package com.vakk.ohlc.api;

/**
 * Created by vakk on 3/9/16.
 */

/**
 * Base query interface
 */
public interface Queries {
    void getOHLC(ServerResponseListener listener, String key);

    boolean cancelRequest();
}
