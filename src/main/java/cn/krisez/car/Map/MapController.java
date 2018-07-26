package cn.krisez.car.Map;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polyline;

import java.util.List;

import cn.krisez.car.R;

public class MapController /*implements AMapLocationListener, LocationSource*/{
    private Context mContext;

    private MapView mMapView;
    private TextureMapView mTextureMapView;
    private AMap mMap;
    private AMapLocationClient aMapLocationClient;
    private MyLocationStyle myLocationStyle;

    public MapController(Context context) {
        this.mContext = context;
    }

    public MapController with(Context context) {
        this.mContext = context;
        return this;
    }

    public MapController map(MapView mapView) {
        this.mMapView = mapView;
        this.mMap = mapView.getMap();
        return this;
    }

    public MapController map(TextureMapView mapView) {
        this.mTextureMapView = mapView;
        this.mMap = mapView.getMap();
        return this;
    }

    private boolean isFirst = true;
    public MapController defaultAmap() {

        //显示定位按钮是否可以点击
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);//指南针
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.5f));
        //定位按钮显示
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.w));
        myLocationStyle.strokeColor(Color.WHITE);
        myLocationStyle.radiusFillColor(Color.argb(100, 20, 12, 20));
        myLocationStyle.anchor(0.5f,0.7f);
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        //显示定位层以及是否定位
        mMap.setMyLocationEnabled(true);

        mMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                if(isFirst) {
                    myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
                    myLocationStyle.interval(2000);
                    mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
                    isFirst = false;
                }
            }
        });
        return this;
    }

    /**
     * marker设置处
     */
    private Marker mMarker;
    public MapController setMarkerOption(MarkerOptions option){
        mMarker = new MapMarker(mMapView).getMarker(option);
        return this;
    }

    public Marker getMarker(){
        return mMarker;
    }

    /**
     * trace 设置处
     */

    private MapTrace mMapTrace;
    public MapController setTrace(int id){
        mMapTrace = new MapTrace(mMapView);
        mMapTrace.startTrace(id);
        return this;
    }

    public Animator getMarkerAnimator(float duration){
        return mMapTrace.setAnimation(mMarker,duration);
    }

    public List<LatLng> getTracePoints(){
        return mMapTrace.getPolyline().getPoints();
    }

    public MapController clearTrace(){
        mMapTrace.getPolyline().remove();
        return this;
    }

    public void onSaveInstanceState(Bundle bundle) {
        if (mMapView != null) {
            mMapView.onSaveInstanceState(bundle);
        } else {
            if (mTextureMapView != null) {
                mTextureMapView.onSaveInstanceState(bundle);
            }
        }
    }

    public void onPause() {
        if (mMapView != null) {
            mMapView.onPause();
        } else {
            if (mTextureMapView != null) {
                mTextureMapView.onPause();
            }
        }
    }

    public void onResume() {
        if (mMapView != null) {
            mMapView.onResume();
        } else {
            if (mTextureMapView != null) {
                mTextureMapView.onResume();
            }
        }
    }

    public void onDestroy() {
        if (mContext != null) {
            mContext = null;
        }
        if (mMapView != null) {
            mMapView.onDestroy();
        } else {
            if (mTextureMapView != null) {
                mTextureMapView.onDestroy();
            }
        }
        if (aMapLocationClient != null) {
            aMapLocationClient.onDestroy();
            aMapLocationClient = null;
        }
    }

    public void create(Bundle savedInstanceState) {
        if (mMapView != null) {
            mMapView.onCreate(savedInstanceState);
        } else {
            if (mTextureMapView != null) {
                mTextureMapView.onCreate(savedInstanceState);
            }
        }
    }


}
