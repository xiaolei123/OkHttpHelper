package com.xiaolei.okhttputil.Cache2;

import android.content.Context;

import com.jakewharton.disklrucache.DiskLruCache;
import com.xiaolei.okhttputil.Utils.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by xiaolei on 2018/2/7.
 */

public class DiskCache
{
    private static DiskCache instance;
    private DiskLruCache diskLruCache;
    private int maxSize = 1024 * 1024 * 60;

    private DiskCache(File cacheDir, Context context)
    {
        if (!cacheDir.exists())
        {
            boolean result = cacheDir.mkdirs();
        }
        try
        {
            diskLruCache = DiskLruCache.open(cacheDir, Util.getAppVersion(context), 1, maxSize);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized static DiskCache getInstance(File cacheDir, Context context)
    {
        if (instance == null)
        {
            instance = new DiskCache(cacheDir, context);
        }
        return instance;
    }

    /**
     * 把字符串保存起来
     *
     * @param key
     * @param value
     */
    public void put(String key, String value)
    {
        key = Util.encryptMD5(key);
        if (diskLruCache != null)
        {
            DiskLruCache.Editor editor = null;
            try
            {
                editor = diskLruCache.edit(key);
                OutputStream outputStream = editor.newOutputStream(0);
                outputStream.write(value.getBytes());
                outputStream.flush();
                outputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                if (editor != null)
                {
                    try
                    {
                        editor.commit();
                    } catch (IOException e)
                    {
                        editor.abortUnlessCommitted();
                    }
                }
            }
        }
    }

    /**
     * 把数据流保存起来
     *
     * @param key
     * @param inputStream
     */
    public void put(String key, InputStream inputStream)
    {
        key = Util.encryptMD5(key);
        if (diskLruCache != null)
        {
            DiskLruCache.Editor editor = null;
            try
            {
                editor = diskLruCache.edit(key);
                OutputStream outputStream = editor.newOutputStream(0);
                byte buff[] = new byte[1024];
                int len = 0;
                while ((len = inputStream.read(buff)) > 0)
                {
                    outputStream.write(buff, 0, len);
                }
                inputStream.close();
                outputStream.flush();
                outputStream.close();
            } catch (IOException e)
            {
                e.printStackTrace();
            } finally
            {
                if (editor != null)
                {
                    try
                    {
                        editor.commit();
                    } catch (IOException e)
                    {
                        editor.abortUnlessCommitted();
                    }
                }
            }
        }
    }

    /**
     * 根据Key获取字符串
     *
     * @param key
     * @return
     */
    public String getString(String key)
    {
        key = Util.encryptMD5(key);
        if (diskLruCache != null)
        {
            try
            {
                DiskLruCache.Snapshot snapshot = diskLruCache.get(key);
                return snapshot.getString(0);
            } catch (IOException e)
            {
                return null;
            }
        } else
        {
            return null;
        }
    }

    /**
     * 根据Key获取流
     *
     * @param key
     * @return
     */
    public InputStream getStream(String key)
    {
        key = Util.encryptMD5(key);
        if (diskLruCache != null)
        {
            DiskLruCache.Snapshot snapshot = null;
            try
            {
                snapshot = diskLruCache.get(key);
                return snapshot.getInputStream(0);
            } catch (IOException e)
            {
                return null;
            }
        } else
        {
            return null;
        }
    }
}
