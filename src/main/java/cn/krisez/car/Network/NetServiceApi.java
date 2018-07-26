package cn.krisez.car.Network;

import com.google.gson.JsonObject;

import java.util.List;

import cn.krisez.car.entity.CarRoute;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface NetServiceApi {
    @GET("http://krisez.cn/map/gdy-hlw.json")
    Observable<JsonObject> getR(@Query("u") String u,@Query("p") String p);

    @GET("http://krisez.cn/map/gdy-hlw.json")
    Observable<List<CarRoute>> getRoutes();

    @GET("http://krisez.cn/map/gdy-hlw2.json")
    Observable<List<CarRoute>> getRoutes2();
}
