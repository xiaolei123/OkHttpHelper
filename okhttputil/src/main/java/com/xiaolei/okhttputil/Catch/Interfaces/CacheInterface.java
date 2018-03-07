package com.xiaolei.okhttputil.Catch.Interfaces;

import java.io.InputStream;


/**
 * 缓存的方式，可能缓存到文件，可能缓存到SQLite，或者其他的地方...
 * Created by xiaolei on 2018/2/9.
 */

public interface CacheInterface
{
    /**
     * 缓存字符串
     *
     * @param key
     * @param value
     */
    public void put(String key, String value);

    /**
     * 缓存流
     *
     * @param key
     * @param inputStream
     */
    public void put(String key, InputStream inputStream);

    /**
     * 追加
     * @param key
     * @param inputStream
     */
    public void append(String key, InputStream inputStream);
    
    /**
     * 获取字符串
     *
     * @param key
     * @return
     */
    public String getString(String key);

    /**
     * 获取流
     *
     * @param key
     * @return
     */
    public InputStream getStream(String key);

    /**
     * 判断key是否被缓存
     *
     * @param key
     * @return
     */
    public boolean containsKey(String key);
}
