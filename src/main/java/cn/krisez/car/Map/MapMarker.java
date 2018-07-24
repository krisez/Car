package cn.krisez.car.Map;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;

import cn.krisez.car.R;

public class MapMarker {
    private Marker mMarker;
    private MarkerOptions mOptions;
    private AMap mAMap;

    public MapMarker(MapView mapView,Context context){
        this.mAMap = mapView.getMap();
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(),R.drawable.icon_car));

    }

    public Marker getMarker(MarkerOptions options) {
        mOptions = new MarkerOptions();
        mOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.icon_car));
        mOptions.position(new LatLng(29.617103,106.499397));
        mOptions.title("demo").snippet("demo").setFlat(true);
        mMarker = mAMap.addMarker(options == null ? mOptions : options);
        return mMarker;
    }
}
