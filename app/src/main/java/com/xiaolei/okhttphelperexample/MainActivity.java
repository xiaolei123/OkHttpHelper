package com.xiaolei.okhttphelperexample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.xiaolei.okhttphelperexample.Net.DataBean;
import com.xiaolei.okhttphelperexample.Net.Net;
import com.xiaolei.okhttphelperexample.Net.RetrofitBase;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends Activity
{
    RetrofitBase retrofitBase;
    Retrofit retrofit;
    Net net;

    TextView text;
    Button button;
    ScrollView scrollview;
    
    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        retrofitBase = new RetrofitBase(this);
        retrofit = retrofitBase.getRetrofit();
        net = retrofit.create(Net.class);
        
        text = findViewById(R.id.text);
        button = findViewById(R.id.button);
        scrollview = findViewById(R.id.scrollview);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                click();
            }
        });
    }

    private void click()
    {
        Call<ResponseBody> call = net.getIndex("兰州市");
        call.enqueue(new Callback<ResponseBody>()
        {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response)
            {
                Date date = new Date();
                if (response.isSuccessful())
                {
                    InputStream inputStream = response.body().byteStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    Drawable drawable = new BitmapDrawable(bitmap);
                    scrollview.setBackgroundDrawable(drawable);
                }
                text.setText("\n" + format.format(date));
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t)
            {
                t.printStackTrace();
                text.setText("出错了");
            }
        });
    }

}
