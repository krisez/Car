package cn.krisez.car.map;

import android.content.Context;
import android.view.View;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;

import cn.krisez.car.R;

public class MarkerInfoWindow implements AMap.ImageInfoWindowAdapter {

    private Context mContext;

    public MarkerInfoWindow(Context context) {
        mContext = context;
    }

    @Override
    public long getInfoWindowUpdateTime() {
        return 0;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return View.inflate(mContext, R.layout.map_marker_info_window,null);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
