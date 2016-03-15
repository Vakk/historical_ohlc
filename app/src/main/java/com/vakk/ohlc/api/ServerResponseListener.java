package com.vakk.ohlc.api;

/**
 * Created by vakk on 3/10/16.
 */
public interface ServerResponseListener {
    void fail(Object obj);
    void done(Object obj);
}
