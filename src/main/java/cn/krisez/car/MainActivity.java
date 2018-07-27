package cn.krisez.car;

import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyTrafficStyle;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;
import java.util.Locale;

import cn.krisez.car.Map.MapController;
import cn.krisez.car.Map.MapTrace;
import cn.krisez.car.Map.MarkerInfoWindow;
import cn.krisez.car.base.BasePermissionsActivity;
import cn.krisez.car.entity.CarRoute;
import cn.krisez.car.entity.SpeedEvent;

public class MainActivity extends BasePermissionsActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private MapController controller;
    private MapView mMapView;
    private TextView tvShowSpeed;

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

        controller = new MapController(this);
        controller.map(mMapView).defaultAmap().create(savedInstanceState);
        //可以获得AMap 继续对其另外的操作
        AMap aMap = mMapView.getMap();
        MyTrafficStyle myTrafficStyle = new MyTrafficStyle();
        myTrafficStyle.setSeriousCongestedColor(Color.parseColor("#790000"));
        myTrafficStyle.setCongestedColor(Color.parseColor("#ff0000"));
        myTrafficStyle.setSlowColor(Color.parseColor("#a9c433"));
        myTrafficStyle.setSmoothColor(Color.parseColor("#5c71fc71"));
        aMap.setMyTrafficStyle(myTrafficStyle);
        aMap.setTrafficEnabled(true);

        aMap.setInfoWindowAdapter(new MarkerInfoWindow(this));

        aMap.setOnMarkerClickListener(new AMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MainActivity.this, "点击Marker", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    Marker marker;

    public void trace(View view) {
        controller = controller.setTrace(1);
    }

    public void setMarker(View view) {
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

    public void startAnimation(View view) {
        final SeekBar progressBar = findViewById(R.id.pb_fraction);
        if(tvShowSpeed.getVisibility()==View.GONE){
            tvShowSpeed.setVisibility(View.VISIBLE);
        }
        animator = (ValueAnimator) controller.getMarkerAnimator(46000);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                marker.showInfoWindow();
                progressBar.setProgress((int) (animation.getAnimatedFraction()*100));
                if(animation.getAnimatedFraction()==1){
                    tvShowSpeed.setVisibility(View.GONE);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void pause(View view) {
        if (animator != null && animator.isStarted() && !animator.isPaused()) {
            animator.pause();
            return;
        }
        if (animator != null && animator.isPaused()) {
            animator.resume();
        }
    }

    public void clear(View view) {
        controller = controller.clearTrace();
        marker.remove();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSpeed(SpeedEvent event) {
        String speed = String.format(Locale.CHINA,"%.2f", event.getSpeed());
        tvShowSpeed.setText("当前时速：" + speed + "km");
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

        if (id == R.id.nav_camera) {
            startActivity(new Intent(MainActivity.this, PeopleActivity.class));
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
    }

}
