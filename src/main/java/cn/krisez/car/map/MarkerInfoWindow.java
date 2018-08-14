package cn.krisez.car.map;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.bumptech.glide.Glide;

import cn.krisez.car.R;

public class MarkerInfoWindow implements AMap.InfoWindowAdapter {

    private Context mContext;
    private ImageView mImageView;
    private View mView;

    public MarkerInfoWindow(Context context) {
        mContext = context;
        mView = View.inflate(mContext, R.layout.map_marker_info_window, null);
        mImageView = mView.findViewById(R.id.map_info_img);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return mView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }

    public void setImg(String url) {
        Glide.with(mContext).load(url).into(mImageView);
    }
}
