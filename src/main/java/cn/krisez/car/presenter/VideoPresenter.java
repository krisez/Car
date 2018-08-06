package cn.krisez.car.presenter;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.trace.ITraceView;
import cn.krisez.car.trace.IView;

public class VideoPresenter extends Presenter {
    private Context mContext;
    private List<VideoQuery> mList;
    private ITraceView mITraceView;

    public VideoPresenter(IView view, Context context) {
        super(view, context);
        this.mContext = context;
        this.mITraceView = (ITraceView) view;
    }

    @Override
    public void onCreate() {
        mList = new ArrayList<>();
    }

    @Override
    public void onStart() {

    }

    @Override
    public void onStop() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }
}
