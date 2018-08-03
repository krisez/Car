package cn.krisez.car.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import cn.krisez.car.Network.MySubscribe;
import cn.krisez.car.Network.NetUtil;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.ui.ITraceView;
import cn.krisez.car.ui.IView;

public class TracePresenter  implements Presenter{

    private Context mContext;
    private List<TraceQuery> mList;

    public TracePresenter(Context context) {
        mContext = context;
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

    private ITraceView mITraceView;

    @Override
    public void attachView(IView view) {
        mITraceView = (ITraceView) view;
    }

    @Override
    public void attachIncomingIntent(Intent intent) {

    }

    public void getTraceList(){
        NetUtil.INSTANCE().create(new MySubscribe<List<TraceQuery>>() {

            @Override
            public void onComplete() {
                mITraceView.update(mList);
            }

            @Override
            public void onNext(List<TraceQuery> object) {
                mList.addAll(object);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                mITraceView.error(e.getMessage());
            }
        },"trace","",true);
    }
}
