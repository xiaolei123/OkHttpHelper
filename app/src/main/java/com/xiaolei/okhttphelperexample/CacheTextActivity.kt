package com.xiaolei.okhttphelperexample

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import com.xiaolei.okhttphelperexample.Net.Net
import com.xiaolei.okhttphelperexample.Net.RetrofitBase
import kotlinx.android.synthetic.main.activity_cache_text.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class CacheTextActivity : Activity()
{
    private val format = SimpleDateFormat("HH:mm:ss")
    private val retrofitBase by lazy { RetrofitBase(this) }
    private val retrofit by lazy { retrofitBase.retrofit }
    private val net by lazy { retrofit.create(Net::class.java) }
    
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cache_text)
        button.setOnClickListener { click() }
        
        val list = LinkedList<Byte>()
        
        list.toByteArray()
        
    }

    private fun click()
    {
        val call = net.getText("兰州市")
        call.enqueue(object : Callback<String>
        {
            override fun onResponse(call: Call<String>, response: Response<String>)
            {
                Toast.makeText(this@CacheTextActivity, "成功", Toast.LENGTH_SHORT).show()
                val date = Date()
                text.text = response.body() + "\n" + format.format(date)
            }
            override fun onFailure(call: Call<String>, t: Throwable)
            {
                t.printStackTrace()
                text.text = "出错了"
            }
        })
    }
}
