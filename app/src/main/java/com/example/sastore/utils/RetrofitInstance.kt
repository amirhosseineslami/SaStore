package com.example.sastore.utils

import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit


private const val BASE_URL
//  = "http://192.168.8.126/miniproject/"
// = "http://mineta.rf.gd/miniproject/"
// = "http://mineta.gigfa.com/miniproject/"
 = "https://minetaproject.000webhostapp.com/miniproject/"
class RetrofitInstance {

    companion object{
        private var retrofitInstance:Retrofit? = null
/*        fun getInstance(): Api? {
            val gson = GsonBuilder()
                .setLenient()
                .create()

            if (retrofitInstance == null){
               retrofitInstance = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                   .client(OkHttpClient())
                   .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                   .addConverterFactory(GsonConverterFactory.create(gson))
                    .build()
            }
            return retrofitInstance?.create(Api::class.java)
        }*/

        fun getInstance(): Api? {
            val okHttpClient = OkHttpClient.Builder()
                .readTimeout(9, TimeUnit.SECONDS)
                .writeTimeout(9, TimeUnit.SECONDS)
                .connectTimeout(9, TimeUnit.SECONDS)
                .build()
            if (retrofitInstance == null) {
                retrofitInstance = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
            return retrofitInstance?.create(Api::class.java)
        }
    }
}