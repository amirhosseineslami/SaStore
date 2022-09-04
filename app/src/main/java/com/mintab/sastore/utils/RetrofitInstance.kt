package com.mintab.sastore.utils

import android.util.Log
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val TAG = "RetrofitInstance"
public var BASE_URL
//  = "http://192.168.8.126/miniproject/"
// = "http://mineta.rf.gd/miniproject/"
// = "http://mineta.gigfa.com/miniproject/"
        = "https://minetaproject.000webhostapp.com/miniproject/"

class RetrofitInstance {
    companion object {
        private var retrofitInstance: Retrofit? = null
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

        fun getInstance(whichServer: Int?): Api? {
            var okHttpClient = OkHttpClient.Builder()
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build()
            if (retrofitInstance == null) {
                if (whichServer == 1) {
                    // server 1 is for the anon pic uploader
                    BASE_URL = "https://api.anonfiles.com/"
                    Log.i(TAG, "Base url is: $BASE_URL")
                    okHttpClient = OkHttpClient.Builder()
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build()
                }else if (whichServer == 2){
                    BASE_URL = "https://www.filestackapi.com/api/store/"
                    Log.i(TAG, "Base url is: $BASE_URL")
                    okHttpClient = OkHttpClient.Builder()
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .build()
                }
                else{
                    BASE_URL = "https://minetaproject.000webhostapp.com/miniproject/"
                    Log.i(TAG, "Base url is: $BASE_URL")
                }
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