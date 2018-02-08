package com.xiaolei.okhttphelperexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void cacheImgFile(View view)
    {
        startActivity(new Intent(this,CacheImgFileActivity.class));
    }
    
    public void cacheText(View view)
    {
        startActivity(new Intent(this,CacheTextActivity.class));
    }
}
