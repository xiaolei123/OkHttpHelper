package com.xiaolei.okhttphelperexample

import android.app.Activity
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import com.xiaolei.okhttphelperexample.Net.Net
import com.xiaolei.okhttphelperexample.Net.RetrofitBase
import kotlinx.android.synthetic.main.activity_cache_img_file.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.concurrent.thread

class CacheImgFileActivity : Activity()
{
    private val retrofitBase by lazy { RetrofitBase(this) }
    private val retrofit by lazy { retrofitBase.retrofit }
    private val net by lazy { retrofit.create(Net::class.java) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cache_img_file)
        button.setOnClickListener { click() }
    }

    private fun click()
    {
        val call = net.img
        call.enqueue(object : Callback<ResponseBody>
        {
            override fun onResponse(call: Call<ResponseBody>?, response: Response<ResponseBody>?)
            {
                Toast.makeText(this@CacheImgFileActivity, "成功", Toast.LENGTH_SHORT).show()
                response?.let {
                    thread {
                        val bitmap = BitmapFactory.decodeStream(it.body()?.byteStream())
                        runOnUiThread {
                            imageview.setImageBitmap(bitmap)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>?, t: Throwable?)
            {

            }
        })
    }
}
