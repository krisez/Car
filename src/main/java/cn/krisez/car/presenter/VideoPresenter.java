package cn.krisez.car.presenter;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.entity.VideoQuery;
import cn.krisez.car.network.MySubscribe;
import cn.krisez.car.network.NetUtil;
import cn.krisez.car._interface.IView;
import cn.krisez.car.ui.video.IVideoView;

public class VideoPresenter extends Presenter {
    private Context mContext;
    private List<VideoQuery> mList;
    private IVideoView mView;

    public VideoPresenter(IView view, Context context) {
        super(view, context);
        this.mContext = context;
        this.mView = (IVideoView) view;
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
        super.onDestroy();
        mList = null;
    }

    private Intent mIntent;

    @Override
    public void attachIncomingIntent(Intent intent) {
        this.mIntent = intent;
    }

    public void getVideoList(int pager,boolean down) {
        NetUtil.INSTANCE().createVideo(new MySubscribe<List<VideoQuery>>(mView) {
            @Override
            public void onNext(List<VideoQuery> objects) {
                    mList.removeAll(mList);
                    mList.addAll(objects);
            }

            @Override
            public void onComplete() {
                if (!down)
                    mView.update(mList);
                else mView.skip(mList);
            }
        }, "video", mIntent.getStringExtra("trace_id"), pager, true);
    }
}
