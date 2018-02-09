package com.xiaolei.okhttphelperexample;

import com.facebook.stetho.Stetho;

/**
 * Created by xiaolei on 2018/2/9.
 */

public class Applications extends android.app.Application
{
    @Override
    public void onCreate()
    {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
