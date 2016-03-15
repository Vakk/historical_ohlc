package com.vakk.ohlc.api.quandl;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;

import com.google.gson.annotations.SerializedName;
import com.vakk.ohlc.api.Queries;
import com.vakk.ohlc.api.ServerResponseListener;
import com.vakk.ohlc.api.quandl.helpers.QuandlClient;
import com.vakk.ohlc.api.quandl.helpers.ServiceGenerator;
import com.vakk.ohlc.api.quandl.models.DataResponse;
import com.vakk.ohlc.helpers.Ticker;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vakk on 3/11/16.
 */

/**
 * Queries for quandl.com
 */
public class QuandlQueries implements Queries {

    // callback for server response, global because we can cancel request and need for it call object
    private Call<Data> call;

    /**
     * @param listener result listener
     * @param key      user keyword
     * @return pattern to ServerResponseListener obj
     */
    @Override
    public void getOHLC(final ServerResponseListener listener, String key) {
        final QuandlClient client = ServiceGenerator.createService(QuandlClient.class);


        if (call != null) {
            if (call.isExecuted())
                call.cancel();
        }
        call = client.getOHLC(key);
        call.enqueue(new Callback<Data>() {
            @Override
            public void onResponse(Call<Data> call, Response<Data> response) {
                if (response.body() != null)
                    listener.done(response.body().response.getPattern());
                else listener.fail("body is null, wrong answer");
            }

            @Override
            public void onFailure(Call<Data> call, Throwable t) {
                t.printStackTrace();
                listener.fail(t.getMessage());
            }
        });
    }

    /**
     * if user wont - cancel request
     */
    @Override
    public boolean cancelRequest() {
        if (call != null) {
            if (call.isExecuted()) {
                call.cancel();
                return true;
            }
        }
        return false;
    }

    /**
     * Base data class for handle server response
     */
    public static class Data {
        @SerializedName("dataset")
        public DataResponse response;

        public String toString() {
            return response.toString();
        }
    }

    /**
     * read CSV from site
     * @param context base application context
     * @param listener listener for callback
     * @return List <Ticker> from callback
     */
    public static void readCsv(Context context, final ServerResponseListener listener) {

        Thread thread = new Thread(new LoadTickersThread(context, new Handler() {
            @Override
            public void handleMessage(Message msg) {
                List<Ticker> list = (List<Ticker>) msg.obj;
                listener.done(list);
            }
        }));
        thread.start();
    }

    /**
     * Load list of tickers from server in new thread, return List<Ticker>
     */
    private static class LoadTickersThread implements Runnable {

        Handler handler;
        Context context;

        /**
         *
         * @param context application context
         * @param handler handle response, return list
         */
        public LoadTickersThread(Context context, Handler handler) {
            this.context = context;
            this.handler = handler;
        }

        @Override
        public void run() {
            final List<String[]> list = new ArrayList<String[]>();
            AssetManager assetManager = context.getAssets();
            try {
                //InputStream csvStream = assetManager.open("https://s3.amazonaws.com/quandl-static-content/Ticker+CSV%27s/WIKI_tickers.csv");
                InputStream csvStream = new URL("https://s3.amazonaws.com/quandl-static-content/Ticker+CSV%27s/WIKI_tickers.csv").openStream();
                InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
                CSVReader csvReader = new CSVReader(csvStreamReader);

                String[] line;

                // throw away the header
                csvReader.readNext();

                while ((line = csvReader.readNext()) != null) {
                    list.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            parseTickers(list);
        }

        private void parseTickers(List<String[]> list) {
            List<Ticker> tickersList = new ArrayList<>();
            for (String[]strings: list){
                Ticker ticker = new Ticker();
                for (String string:strings){
                    if (string.contains("/")){
                        String [] splitedCode = string.split("/");
                        ticker.setCode(splitedCode[1]);
                    }
                    else ticker.setName(string);
                }
                tickersList.add(ticker);
            }
            handler.obtainMessage(-1, tickersList).sendToTarget();
        }
    }
}