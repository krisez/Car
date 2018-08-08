package cn.krisez.car.network;

import java.util.List;

import cn.krisez.car.entity.CarRoute;
import cn.krisez.car.entity.TraceQuery;
import cn.krisez.car.entity.VideoQuery;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

interface NetServiceApi {
    /*@GET("map/query.ca")
    Observable<List<TraceQuery>> trace(@Query("type") String type, @Query("trace_id") String id);

    @GET("map/query.ca")
    Observable<List<VideoQuery>> video(@Query("type") String type, @Query("trace_id") String id,@Query("pager")int pager);*/


    @GET("query.ca")
    Observable<List<TraceQuery>> trace(@Query("type") String type, @Query("trace_id") String id);

    @GET("query.ca")
    Observable<List<VideoQuery>> video(@Query("type") String type, @Query("trace_id") String id,@Query("pager")int pager);


    @GET("map")
    Observable<List<CarRoute>> getRoutes(@Query("id") String id);
}

