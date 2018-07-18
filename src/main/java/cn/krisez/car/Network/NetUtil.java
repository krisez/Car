package cn.krisez.car.Network;

import com.google.gson.JsonObject;

import java.util.concurrent.TimeUnit;

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

    /**
     * 默认没有CallAdapter
     *
     * @return
     */
    private static void init() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().connectTimeout(3, TimeUnit.SECONDS)
                .readTimeout(3,TimeUnit.SECONDS).build();
        mRetrofit = new Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("http://law.krisez.cn/")
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

    public void create(Observer<JsonObject> observer, String u, String p, boolean isMain){
        NetServiceApi api = mRetrofit.create(NetServiceApi.class);
        createRx(api.getR(u,p),observer,isMain);
    }

    public void create(Observer<?> observer,String u,String p,boolean isMain,int type){
       //TODO:其他网络库处理内容
    }

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
