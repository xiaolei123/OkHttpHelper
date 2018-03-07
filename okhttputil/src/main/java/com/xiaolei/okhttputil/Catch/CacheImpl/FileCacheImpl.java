package com.xiaolei.okhttputil.Catch.CacheImpl;

import android.content.Context;

import com.xiaolei.okhttputil.Catch.DiskCache;
import com.xiaolei.okhttputil.Catch.Interfaces.CacheInterface;
import com.xiaolei.okhttputil.Utils.Util;

import java.io.File;
import java.io.InputStream;

/**
 * 将网络数据，缓存到本地文件的实现方式
 * Created by xiaolei on 2018/2/7.
 */

public class FileCacheImpl implements CacheInterface
{
    private static FileCacheImpl instance;
    private DiskCache diskCache;

    private FileCacheImpl(File cacheDir, Context context)
    {
        if (!cacheDir.exists())
        {
            boolean result = cacheDir.mkdirs();
        }
        diskCache = DiskCache.open(cacheDir, Util.getAppVersion(context) + "");
    }

    public synchronized static FileCacheImpl getInstance(File cacheDir, Context context)
    {
        if (instance == null)
        {
            instance = new FileCacheImpl(cacheDir, context);
        }
        return instance;
    }

    /**
     * 把字符串保存起来
     *
     * @param key
     * @param value
     */
    @Override
    public void put(String key, String value)
    {
        key = Util.encryptMD5(key);
        diskCache.put(key,value);
    }

    /**
     * 把数据流保存起来
     *
     * @param key
     * @param inputStream
     */
    @Override
    public void put(String key, InputStream inputStream)
    {
        key = Util.encryptMD5(key);
        diskCache.put(key,inputStream);
    }

    /**
     * 根据Key获取字符串
     *
     * @param key
     * @return
     */
    @Override
    public String getString(String key)
    {
        key = Util.encryptMD5(key);
        return diskCache.getString(key);
    }

    /**
     * 根据Key获取流
     *
     * @param key
     * @return
     */
    @Override
    public InputStream getStream(String key)
    {
        key = Util.encryptMD5(key);
        return diskCache.get(key);
    }

    /**
     * 数据中是否存在
     *
     * @param key
     * @return
     */
    @Override
    public boolean containsKey(String key)
    {
        key = Util.encryptMD5(key);
        return diskCache.containsKey(key);
    }

}
