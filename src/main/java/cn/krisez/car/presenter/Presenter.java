package cn.krisez.car.presenter;

import android.content.Intent;

import cn.krisez.car.ui.IView;

/**
 * Created by Krisez on 2017-12-13.
 */

public interface Presenter {
    void onCreate();

    void onStart();//暂时没用到

    void onStop();

    void onPause();//暂时没用到

    void attachView(IView view);

    void attachIncomingIntent(Intent intent);//暂时没用到
}
