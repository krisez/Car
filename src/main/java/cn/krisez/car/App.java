package cn.krisez.car;

import android.app.Application;
import android.content.Context;


import cn.krisez.car.network.NetUtil;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class App extends Application {

    private static Context sContext;
    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.leakcanary_config) {
            /*if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);*/
        }
        // Normal app init code...
        sContext = getApplicationContext();
        NetUtil.INSTANCE().addCallAdapter(RxJava2CallAdapterFactory.create());
    }

    public static Context getContext() {
        return sContext;
    }
}
