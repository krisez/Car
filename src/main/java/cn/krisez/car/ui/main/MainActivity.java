package cn.krisez.car.ui.main;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.R;
import cn.krisez.car.base.CheckPermissionsActivity;
import cn.krisez.car.base.LoadFragment;
import cn.krisez.car.enevt.TraceEvent;
import cn.krisez.car.entity.CarRoute;
import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.map.MapController;
import cn.krisez.car.map.MarkerInfoWindow;
import cn.krisez.car.ui.trace.TraceHistoryActivity;
import cn.krisez.car.ui.video.VideoDetailActivity;
import cn.krisez.car.utils.Const;

public class MainActivity extends CheckPermissionsActivity
        implements NavigationView.OnNavigationItemSelectedListener, IMainView {
    private MapController controller;
    private MapView mMapView;
    private TextView tvShowSpeed;
    private ConstraintLayout mBottom;
    private SeekBar mSeekBar;
    private ImageButton mLocateCar;

    private AMap mAMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMapView = findViewById(R.id.mv_show);
        tvShowSpeed = findViewById(R.id.tv_show_speed);
        mBottom = findViewById(R.id.bottom);
        mSeekBar = findViewById(R.id.pb_fraction);
        mLocateCar = findViewById(R.id.ib_locate_car);

        controller = new MapController(this);
        controller.map(mMapView).view(this).defaultAmap().create(savedInstanceState);
        //可以获得AMap 继续对其另外的操作
        mAMap = mMapView.getMap();
        MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
        myTrafficStyle.setSeriousCongestedColor(Color.parseColor("#790000"));
        myTrafficStyle.setCongestedColor(Color.parseColor("#ff0000"));
        myTrafficStyle.setSlowColor(Color.parseColor("#a9c433"));
        myTrafficStyle.setSmoothColor(Color.parseColor("#5c71fc71"));
        mAMap.setMyTrafficStyle(myTrafficStyle);
        mAMap.setTrafficEnabled(true);

        mMarkerInfoWindow = new MarkerInfoWindow(this);
        mAMap.setInfoWindowAdapter(mMarkerInfoWindow);

        mAMap.setOnMarkerClickListener(marker -> {
            Toast.makeText(MainActivity.this, "" + marker.getTitle(), Toast.LENGTH_SHORT).show();
            return true;
        });

        mAMap.setOnInfoWindowClickListener(marker -> VideoDetailActivity.intentTo(this, mVideos.get(index)));
    }

    Marker marker;
    private MarkerInfoWindow mMarkerInfoWindow;

    public void setMarker() {
        mSwitch = findViewById(R.id.map_switch);

        List<LatLng> list = controller.getTracePoints();
        MarkerOptions options = new MarkerOptions();
        LatLng latLng1 = list.get(0);
        LatLng latLng2 = list.get(1);
        float angle = getAngle(latLng1, latLng2);
        options.setFlat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car))
                .position(latLng1)
                .title("car").snippet("animation")
                .rotateAngle(angle)
                .anchor(0.5f, 0.5f);
        marker = controller.setMarkerOption(options).getMarker();

        mSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                follow = false;
                LatLngBounds.Builder builder = LatLngBounds.builder();
                for (int i = 0; i < list.size(); i++) {
                    builder.include(list.get(i));
                }
                mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
            } else follow = true;
        });
    }

    private float getAngle(LatLng latLng1, LatLng latLng2) {
        float angle = 0;
        if (latLng2.longitude - latLng1.longitude == 0) {
            angle = latLng1.latitude > latLng2.latitude ? 180 : 0;
        } else if (latLng2.latitude - latLng1.latitude == 0) {
            angle = latLng1.longitude < latLng2.longitude ? 90 : 270;
        } else {
            double a = (latLng2.latitude - latLng1.latitude) / (latLng2.longitude - latLng1.longitude);
            if (latLng2.longitude > latLng1.longitude) {
                angle = (float) (Math.atan(a) * 180 / Math.PI) - 90;
            } else {
                angle = (float) (Math.atan(a) * 180 / Math.PI) + 90;
            }
        }
        return angle;
    }

    ValueAnimator animator;

    private Switch mSwitch;
    private boolean follow = false;

    private int index = -1;

    /**
     * 开始动画
     * 初始化以及监听添加
     *
     * @param view
     */
    public void startAnimation(View view) {
        if (animator != null && animator.isStarted()) {
            animator.end();
            animator.removeAllUpdateListeners();
        }
        mSwitch.setVisibility(View.VISIBLE);
        marker.showInfoWindow();
        if (tvShowSpeed.getVisibility() == View.GONE) {
            tvShowSpeed.setVisibility(View.VISIBLE);
        }
        animator = (ValueAnimator) controller.getMarkerAnimator(30000);
        animator.addUpdateListener(animation -> {
            if (follow) {
                CarRoute carRoute = (CarRoute) animation.getAnimatedValue();
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(carRoute.getLatLng()).bearing(-(float) carRoute.getBearing()).zoom(18).tilt(45).build();
                mAMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
            mSeekBar.setProgress((int) (animation.getAnimatedFraction() * 100));
            if (animation.getAnimatedFraction() == 1) {
                tvShowSpeed.setVisibility(View.GONE);
            }
            if (requestImg) {
                index = (int) (mVideos.size() * animation.getAnimatedFraction());
                if (index != mVideos.size()) {
                    mMarkerInfoWindow.setImg(mVideos.get(index).getThumb());
                }
            }
        });

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (!animator.isPaused() && animator.isRunning()) {
                    error("请先暂停,再操作");
                }
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                int s = seekBar.getProgress();
                s *= 300;
                if (animator.isPaused() && animator.isRunning()) {
                    animator.setCurrentPlayTime(s);
                }
                if (animator != null && !animator.isRunning()) {
                    animator.start();
                    animator.setCurrentPlayTime(s);
                    animator.pause();
                }
            }
        });
        moveCamera();
    }

    private void moveCamera() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<LatLng> a = controller.getTracePoints();
        for (int i = 0; i < a.size(); i++) {
            builder.include(a.get(i));
        }
        if(!builder.build().contains(mAMap.getCameraPosition().target)){
            mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 0));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pause(View view) {
        if (animator != null && animator.isStarted() && !animator.isPaused()) {
            animator.pause();
            return;
        }
        if (animator != null && animator.isPaused()) {
            animator.resume();
            moveCamera();
        }
    }

    public void clear(View view) {
        clear();
    }

    private void clear() {
        controller = controller.clearTrace();
        if (animator != null && animator.isStarted()) {
            animator.end();
            animator.removeAllUpdateListeners();
        }
        if (marker != null) {
            marker.remove();
        }
        mSwitch.setVisibility(View.GONE);
        mBottom.setVisibility(View.GONE);
        mSeekBar.setVisibility(View.GONE);
        mLocateCar.setVisibility(View.GONE);
    }

    public void locateCar(View view) {
        mAMap.animateCamera(CameraUpdateFactory.changeLatLng(marker.getPosition()));
    }

    @Override
    public void traceOver() {
        if(marker != null) {
            marker.remove();
        }
        setMarker();
        mBottom.setVisibility(View.VISIBLE);
        mSeekBar.setVisibility(View.VISIBLE);
        mSeekBar.setProgress(0);
        mLocateCar.setVisibility(View.VISIBLE);
    }

    @Override
    public void speed(String v) {
        tvShowSpeed.setText("当前时速：" + v + "km");
    }

    List<VideoQuery> mVideos = new ArrayList<>();
    private boolean requestImg = false;

    @Override
    public void requestVideo(List<VideoQuery> list) {
        mVideos.clear();
        mVideos.addAll(list);
        requestImg = true;
    }

    @Override
    public void error(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTrace(TraceEvent event) {
        controller = controller.setTrace(event.getId());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_people_center) {
            error("无");
            //startActivity(new Intent(MainActivity.this, PeopleActivity.class));
        } else if (id == R.id.nav_trace_history) {
            startActivity(new Intent(MainActivity.this, TraceHistoryActivity.class));
        } else if (id == R.id.nav_video_history) {
            startActivity(new Intent(Const.ACTION_VIDEO_LIST));
        } else if (id == R.id.nav_setting) {
            startActivity(new Intent(MainActivity.this, LoadFragment.class).putExtra("cls", "set"));
        } else if (id == R.id.nav_about) {
            startActivity(new Intent(MainActivity.this, LoadFragment.class).putExtra("cls", "about"));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        controller.onSaveInstanceState(outState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        controller.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        controller.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.onDestroy();
        if (animator != null && animator.isStarted()) {
            animator.end();
            animator.removeAllUpdateListeners();
        }
        EventBus.getDefault().unregister(this);
    }
}
