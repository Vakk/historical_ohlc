package com.vakk.ohlc.api.quandl.helpers;

import com.vakk.ohlc.api.quandl.QuandlQueries;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by vakk on 3/11/16.
 */
public interface QuandlClient {
    @GET("/api/v3/datasets/WIKI/{key}.json")
    Call<QuandlQueries.Data> getOHLC(@Path("key") String key);
}