package com.xdtech.project.lot.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;

/**
 * 创建一个http请求
 * @author wujy 2019/09/29
 */
@Component
public class RetrofitUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetrofitUtil.class);
    private static String baseUrl;
    private static Retrofit retrofit;

//    @Value("${system.32bitdll.path}")
    public void setBaseUrl(String baseUrl) {
        RetrofitUtil.baseUrl = baseUrl;
    }

    /**
     * 创建Retrofit请求对象
     * @return
     */
    public static Retrofit getRetrofit(){
        if(retrofit != null){
            return retrofit;
        }
        //构建Retrofit实例
        retrofit = new Retrofit.Builder()
                //设置网络请求BaseUrl地址
                .baseUrl(baseUrl)
                //设置数据解析器
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        return retrofit;
    }

    /**
     * 解析请求返回的数据
     * @return
     */
    public static ResponseMsg getJsonResult(Call<ResponseMsg> call)  {
        ResponseMsg responseMsg = null;
        try {
            responseMsg = call.execute().body();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return responseMsg;
    }
}
