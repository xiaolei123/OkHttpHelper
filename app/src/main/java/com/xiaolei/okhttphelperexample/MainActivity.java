package com.xiaolei.okhttphelperexample;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xiaolei.okhttphelperexample.Net.DataBean;
import com.xiaolei.okhttphelperexample.Net.Net;
import com.xiaolei.okhttphelperexample.Net.RetrofitBase;

import java.text.SimpleDateFormat;
import java.util.Date;

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
        Call<DataBean> call = net.getIndex("兰州市");
        call.enqueue(new Callback<DataBean>()
        {
            @Override
            public void onResponse(Call<DataBean> call, Response<DataBean> response)
            {
                Date date = new Date();
                text.setText("" + response.body() + "\n"+format.format(date));
            }

            @Override
            public void onFailure(Call<DataBean> call, Throwable t)
            {
                text.setText("出错了");
            }
        });
    }

}
