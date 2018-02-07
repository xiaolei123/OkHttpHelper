package com.xiaolei.okhttputil.interceptor;

import android.content.Context;

import com.xiaolei.okhttputil.Cache2.DiskCache;
import com.xiaolei.okhttputil.Catch.CacheType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.*;

/**
 * 高级缓存,可以缓存一切数据
 * Created by xiaolei on 2018/2/7.
 */
public class CacheInterceptor2 implements Interceptor
{
    private DiskCache diskCache;

    public CacheInterceptor2(Context context, File cacheDir)
    {
        diskCache = DiskCache.getInstance(cacheDir, context);
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();
        String cacheHead = request.header("cache");
        String cache_control = request.header("Cache-Control");
        if ("true".equals(cacheHead) ||                              // 意思是要缓存
                (cache_control != null && !cache_control.isEmpty())) // 这里还支持WEB端协议的缓存头
        {
            String url = request.url().url().toString();
            String reqBodyStr = getPostParams(request);
            String key = url + "?" + reqBodyStr;//链接就是缓存的key
            try
            {
                // 网络正常，缓存正常数据
                Response response = chain.proceed(request);
                if (response.isSuccessful()) // 正常了，才去缓存数据
                {
                    storeResponse(response, key);

                }
                return response;
            } catch (Exception e)
            {
                // 网络异常，取缓存

                return null;
            }
        } else
        {
            return chain.proceed(request);
        }
    }

    private Response getResponse(String key, Request request)
    {
        InputStream inputStream = null;
        int contentLength = 0;
        try
        {
            contentLength = inputStream.available();
        } catch (IOException e)
        {
            contentLength = 0;
        }
        Source source = Okio.source(inputStream);
        BufferedSource bufferedSource = Okio.buffer(source);
        ResponseBody body = ResponseBody.create(null, contentLength, bufferedSource);
        Response response = new Response.Builder()
                .code(200)
                .body(body)
                .request(request)
                .message(CacheType.DISK_CACHE)
                .protocol(Protocol.HTTP_1_0)
                .build();
        return response;
    }

    /**
     * 缓存返回的数据
     *
     * @param response
     */
    private void storeResponse(Response response, String key)
    {
        ResponseBody responseBody = response.body();
        if (responseBody != null)
        {
            MediaType mediaType = responseBody.contentType();
            String typeStr = "";
            if (mediaType != null)
            {
                typeStr = mediaType.toString();
            }
            long contentLength = responseBody.contentLength();
            Protocol protocol = response.protocol();
            diskCache.put(key, responseBody.byteStream());
            diskCache.put(key + "@:mediaType", typeStr);
            diskCache.put(key + "@:contentLength", contentLength + "");
            diskCache.put(key + "@:protocol", protocol.name() + "");
        }
    }

    /**
     * 获取在Post方式下。向服务器发送的参数
     *
     * @param request
     * @return
     */
    private String getPostParams(Request request)
    {
        String reqBodyStr = "";
        String method = request.method();
        if ("POST".equals(method)) // 如果是Post，则尽可能解析每个参数
        {
            StringBuilder sb = new StringBuilder();
            if (request.body() instanceof FormBody)
            {
                FormBody body = (FormBody) request.body();
                if (body != null)
                {
                    for (int i = 0; i < body.size(); i++)
                    {
                        sb.append(body.encodedName(i)).append("=").append(body.encodedValue(i)).append(",");
                    }
                    sb.delete(sb.length() - 1, sb.length());
                }
                reqBodyStr = sb.toString();
                sb.delete(0, sb.length());
            }
        }
        return reqBodyStr;
    }
}
