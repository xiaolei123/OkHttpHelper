package com.xiaolei.okhttphelperexample.Net;

import com.xiaolei.okhttputil.Catch.CacheHeaders;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by xiaolei on 2017/7/9.
 */

public interface Net
{
//    @Headers(CacheHeaders.NORMAL)
    @GET("http://mm.chinasareview.com/wp-content/uploads/2017a/08/01/01.jpg")
    public Call<ResponseBody> getIndex(@Query("w") String a);
}