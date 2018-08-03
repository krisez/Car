package cn.krisez.car.Network;

import java.util.List;

import cn.krisez.car.entity.CarRoute;
import cn.krisez.car.entity.TraceQuery;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface NetServiceApi {
    @GET("map/query.ca")
    Observable<List<TraceQuery>> query(@Query("type") String type, @Query("trace_id") String id);

    @GET("map")
    Observable<List<CarRoute>> getRoutes(@Query("id") String id);
}
