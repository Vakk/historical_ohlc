package com.vakk.ohlc.helpers;

/**
 * Created by vakk on 3/13/16.
 */
public class Ticker {
    private String code;
    private String name;

    public Ticker(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public Ticker(){};

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return code;
    }
}
