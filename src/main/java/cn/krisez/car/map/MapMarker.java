package cn.krisez.car.map;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import cn.krisez.car.R;

public class MapMarker {
    private Marker mMarker;
    private MarkerOptions mOptions;
    private AMap mAMap;

    MapMarker(MapView mapView){
        this.mAMap = mapView.getMap();
    }

    public Marker getMarker(MarkerOptions options) {
        mOptions = new MarkerOptions();
        mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
        mOptions.position(new LatLng(29.617103,106.499397));
        mOptions.title("car").snippet("animate").setFlat(true);
        mOptions.anchor(0.5f,0.5f);
        mOptions = options !=null ? options:mOptions;
        mMarker = mAMap.addMarker(mOptions);
        return mMarker;
    }


}
