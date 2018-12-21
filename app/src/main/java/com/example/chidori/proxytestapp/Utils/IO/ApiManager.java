package com.example.chidori.proxytestapp.Utils.IO;

import com.example.chidori.proxytestapp.Config;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiManager {

    private RetrofitService myApi;
    private static ApiManager sApiManager;


    //get instance of api
    public static ApiManager getInstance() {
        if (sApiManager == null) {
            synchronized (ApiManager.class) {
                if (sApiManager == null) {
                    sApiManager = new ApiManager();
                }
            }
        }
        return sApiManager;
    }


    //request Retrofit service
    public RetrofitService getRetrofitService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpInterceptor())
                .build();
        if (myApi == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Config.baseURL)
                    .client(client)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            myApi = retrofit.create(RetrofitService.class);
        }
        return myApi;
    }
}
