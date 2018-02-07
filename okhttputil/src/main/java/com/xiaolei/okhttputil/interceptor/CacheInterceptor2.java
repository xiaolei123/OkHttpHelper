package com.xiaolei.okhttputil.interceptor;

import android.content.Context;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 高级缓存
 * Created by xiaolei on 2018/2/7.
 */
public class CacheInterceptor2 implements Interceptor
{
    public CacheInterceptor2(Context context)
    {
        
    }
    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        return chain.proceed(request);
    }
}
