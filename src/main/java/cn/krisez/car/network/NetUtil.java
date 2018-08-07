package cn.krisez.car.network;

import android.content.Context;

import java.util.List;
import java.util.concurrent.TimeUnit;

import cn.krisez.car.entity.CarRoute;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.entity.VideoQuery;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetUtil {
    private volatile static NetUtil INSTANCE;
    private static Retrofit mRetrofit;
    private static boolean addCallAdapter = false;
    private Context mContext;

    private NetUtil() {
    }

    public static NetUtil INSTANCE() {
        //先检查实例是否存在，如果不存在才进入下面的同步块
        if (INSTANCE == null) {
            synchronized (NetUtil.class) {
                //再次检查实例是否存在，如果不存在才真正的创建实例
                if (INSTANCE == null) {
                    INSTANCE = new NetUtil();
                    init();
                }
            }
        }

        return INSTANCE;
    }

    public void serC(Context context){
        this.mContext = context;
    }

    /**
     * 默认没有CallAdapter
     *
     * @return
     */
    private static void init() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3,TimeUnit.SECONDS).build();
        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://krisez.cn/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * 添加callAdapter
     * @param factory
     */
    public void addCallAdapter(CallAdapter.Factory factory) {
        mRetrofit = mRetrofit.newBuilder().addCallAdapterFactory(factory).build();
        addCallAdapter = true;
    }

    public void createTrace(Observer<List<TraceQuery>> observer, String type, String traceId, boolean isMain){
        NetServiceApi api = mRetrofit.create(NetServiceApi.class);
        createRx(api.trace(type,traceId),observer,isMain);
    }

    public void createVideo(Observer<List<VideoQuery>> observer, String type, String traceId,int pager, boolean isMain){
        NetServiceApi api = mRetrofit.create(NetServiceApi.class);
        if(traceId==null) traceId="";
        createRx(api.video(type,traceId,pager),observer,isMain);
    }

    public void getPoints(Observer<List<CarRoute>> observer, String id, boolean isMain){
        NetServiceApi api = mRetrofit.create(NetServiceApi.class);
        createRx(api.getRoutes(id),observer,isMain);
    }

   /* public void createTrace(Observer<?> observer, String u, String p, boolean isMain){
       //TODO:其他网络库处理内容
    }*/

    /**
     * 修改上面的数据内容体就可以更换网络框架
     * @param o
     * @param s
     * @param isMainThread
     * @param <T>
     */
    private <T>void createRx(Observable<T> o, Observer<T> s, boolean isMainThread) {
        if (isMainThread) {
            o.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(s);
        }else {
            o.subscribeOn(Schedulers.io()).observeOn(Schedulers.io()).subscribe(s);
        }
    }

    /**
     * 判断是否添加了callAdapter，将根据adapter操作网络请求回调
     * @return
     */
    private static boolean isAddCallAdapter() {
        return addCallAdapter;
    }
}
