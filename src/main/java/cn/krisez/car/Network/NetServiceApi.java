package cn.krisez.car.Network;

import com.google.gson.JsonObject;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NetServiceApi {
    @GET("login.la")
    Observable<JsonObject> getR(@Query("u") String u,@Query("p") String p);
}
