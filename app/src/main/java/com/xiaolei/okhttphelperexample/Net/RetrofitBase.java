package com.xiaolei.okhttphelperexample.Net;

import android.content.Context;

import com.xiaolei.okhttputil.Log.Log;
import com.xiaolei.okhttputil.interceptor.CacheInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by xiaolei on 2017/12/9.
 */

public class RetrofitBase
{
    private Retrofit retrofit;
    private OkHttpClient client;
    private HttpLoggingInterceptor loggingInterceptor;

    public RetrofitBase(Context context)
    {
        loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger()
        {
            @Override
            public void log(String message)
            {
                Log.d("HttpRetrofit", message + "");
            }
        });
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client = new OkHttpClient.Builder()
                .addInterceptor(new CacheInterceptor(context))//添加缓存拦截器，添加缓存的支持
                .addInterceptor(loggingInterceptor)
                .retryOnConnectionFailure(true)//失败重连
                .connectTimeout(30, TimeUnit.SECONDS)//网络请求超时时间单位为秒
                .build();
        retrofit = new Retrofit.Builder()  //01:获取Retrofit对象
                .baseUrl("http://192.168.1.124:8080/") //02采用链式结构绑定Base url
                .addConverterFactory(ScalarsConverterFactory.create())//字符串
                .addConverterFactory(GsonConverterFactory.create())//再将转换成bean
                .client(client)
                .build();//03执行操作
    }

    public Retrofit getRetrofit()
    {
        return retrofit;
    }
}