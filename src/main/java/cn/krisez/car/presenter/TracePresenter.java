package cn.krisez.car.presenter;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.network.MySubscribe;
import cn.krisez.car.network.NetUtil;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.ui.trace.ITraceView;
import cn.krisez.car.ui.trace.IView;

public class TracePresenter extends Presenter {

    private Context mContext;
    private List<TraceQuery> mList;
    private ITraceView mITraceView;

    public TracePresenter(IView view, Context context) {
        super(view,context);
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
        mList = null;
    }


    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getTraceList(){

        NetUtil.INSTANCE().createTrace(new MySubscribe<List<TraceQuery>>(mITraceView) {

            @Override
            public void onComplete() {
                mITraceView.update(mList);
            }

            @Override
            public void onNext(List<TraceQuery> object) {
                mList.removeAll(mList);
                mList.addAll(object);
            }
        },"trace","",true);
    }
}
