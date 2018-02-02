package com.xiaolei.okhttputil.Cookie;

import android.content.Context;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.HttpUrl;

/**
 * Created by xiaolei on 2017/3/14.
 */

public class CookieJar implements okhttp3.CookieJar
{
    private Context mContext;
    private PersistentCookieStore cookieStore;
    private LinkedList<String> couldSaveUrlFile = new LinkedList<>();

    /**
     * @param context     上下文
     * @param saveUrlFile 可以保存Cookie的URI 譬如 /app/loginAuto,如果为 null 则所有请求都可以保存cookie
     */
    public CookieJar(Context context, List<URL> saveUrlFile)
    {
        mContext = context;
        cookieStore = new PersistentCookieStore(mContext);
        if (saveUrlFile != null)
        {
            for (URL url : saveUrlFile)
            {
                couldSaveUrlFile.add(url.getFile());
            }
        }

        for (int i = 0; i < couldSaveUrlFile.size(); i++)
        {
            String urlFile = couldSaveUrlFile.get(i);
            if (!urlFile.startsWith("/"))
            {
                couldSaveUrlFile.remove(urlFile);
                couldSaveUrlFile.add("/" + urlFile);
                i -= 1;
            }
        }

    }

    public CookieJar(Context context)
    {
        this(context, null);
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies)
    {
        URL url1 = url.url();
        String urlFile = url1.getFile();
        boolean check = true;

        if (!couldSaveUrlFile.isEmpty())
        {
            check = couldSaveUrlFile.contains(urlFile);
        }

        if (check && cookies != null && cookies.size() > 0)
        {
            for (Cookie item : cookies)
            {
                cookieStore.add(url, item);
            }
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url)
    {
        List<Cookie> cookies = cookieStore.get(url);
        return cookies;
    }

    public PersistentCookieStore getCookieStore()
    {
        return cookieStore;
    }
}