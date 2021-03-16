package com.xdtech.project.lot.request;

import com.xdtech.project.lot.util.ResponseMsg;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MJJRequest {
    //打开电源
    @GET("MJJ/openPower")
    Call<ResponseMsg> openPower(@Query("deviceId") String deviceId, @Query("qNumber") int qNumber);
    //关闭电源
    @GET("MJJ/shutDown")
    Call<ResponseMsg> shutDown(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
    //禁止移动
    @GET("MJJ/fobiddenExercise")
    Call<ResponseMsg> fobiddenExercise(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
    //停止移动
    @GET("MJJ/stopExercise")
    Call<ResponseMsg> stopExercise(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
    //解除禁止
    @GET("MJJ/cleanFobiddenExercise")
    Call<ResponseMsg> cleanFobiddenExercise(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
    //通风
    @GET("MJJ/ventilation")
    Call<ResponseMsg> ventilation(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
    //打开指定列
    @GET("MJJ/openAssginColumn")
    Call<ResponseMsg> openAssginColumn(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber,@Query("col") int col);
    //关闭已打开的列
    @GET("MJJ/closeColumn")
    Call<ResponseMsg> closeColumn(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
    //获取状态数据
    @GET("MJJ/getStatusData")
    Call<ResponseMsg> getStatus(@Query("deviceId") String deviceId,@Query("qNumber") int qNumber);
}
