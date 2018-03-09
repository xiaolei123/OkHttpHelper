package com.xiaolei.okhttphelperexample.Net;


import com.xiaolei.okhttputil.Catch.CacheHeaders;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;

/**
 * Created by xiaolei on 2017/7/9.
 */

public interface Net
{
    @Headers(CacheHeaders.NORMAL)
    @GET("/")
    public Call<String> getText(@Query("w") String a);

    @Headers(CacheHeaders.CACHE_FIRST)
    @GET("https://pic4.zhimg.com/v2-67ee006ec9f4171121b245c57079bee6_r.jpg")
    Call<ResponseBody> getImg();
    
    @Headers(CacheHeaders.NORMAL)
    @GET("http://192.168.1.111:8080/app/testEmpty")
    Call<String> getEmpty(); 
}