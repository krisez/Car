package cn.krisez.car.Map;

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
import com.amap.api.maps.model.MyLocationStyle;

import cn.krisez.car.R;

public class MapController /*implements AMapLocationListener, LocationSource*/{
    private Context mContext;

    private MapView mMapView;
    private TextureMapView mTextureMapView;
    private AMap mMap;
    private AMapLocationClient aMapLocationClient;
    private MyLocationStyle myLocationStyle;

   // private LocationSource mLocationSource;

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

    public MapController locateSource(LocationSource locationSource) {
       // this.mLocationSource = locationSource;
        return this;
    }

    private boolean isFirst = true;
    public MapController defaultAmap() {
        /*//放在client设置前面
        if (mLocationSource == null) {
            //TODO：理解为什么? 顺序问题？
            mMap.setLocationSource(this);
        } else {
            mMap.setLocationSource(mLocationSource);
        }*/
        //显示定位按钮是否可以点击
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);//指南针
        mMap.moveCamera(CameraUpdateFactory.zoomTo(16.5f));
        //定位按钮显示
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.w));
        myLocationStyle.strokeColor(Color.WHITE);
        myLocationStyle.radiusFillColor(Color.argb(100, 20, 12, 20));
        myLocationStyle.anchor(0.5f,0.7f);
        mMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style



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
        mMap.setOnMyLocationChangeListener(new AMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {

            }
        });
        //显示定位层以及是否定位
        mMap.setMyLocationEnabled(true);
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

    /**
     * map.setLocationSource();
     */
   /* @Override
    public void activate(final OnLocationChangedListener onLocationChangedListener) {
       // mListener = onLocationChangedListener;
        if (aMapLocationClient == null) {
            aMapLocationClient = new AMapLocationClient(mContext);
        }
        // 设置定位监听
        aMapLocationClient.setLocationListener(this);
        if (mLocationOption == null) {
            mLocationOption = new AMapLocationClientOption();
        }
        // 设置为高精度定位模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 只是为了获取当前位置，所以设置为单次定位
        mLocationOption.setOnceLocation(true);
        // 设置定位参数
        aMapLocationClient.setLocationOption(mLocationOption);
        aMapLocationClient.startLocation();

    }*/

  /*  @Override
    public void deactivate() {
        if (aMapLocationClient != null) {
            aMapLocationClient.stopLocation();
            aMapLocationClient.onDestroy();
        }
        aMapLocationClient = null;
    }*/


    /**
     * AMapLocationClient.setLocationChangeListener();
     */

   /* @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(aMapLocation);// 显示系统小蓝点
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": "
                        + aMapLocation.getErrorInfo();
                Log.e("AmapErr", errText);
            }
        }
    }

    private OnLocationChangedListener mListener;
    private AMapLocationClientOption mLocationOption;*/

}
