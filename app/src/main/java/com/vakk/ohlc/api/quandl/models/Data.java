package com.vakk.ohlc.api.quandl.models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vakk on 3/12/16.
 */
public class Data {
    // date
    @SerializedName("Date")
    private String date;

    // OHLC values
    @SerializedName("Open")
    private double open;
    @SerializedName("High")
    private double high;
    @SerializedName("Low")
    private double low;
    @SerializedName("Close")
    private double close;

    public Data(String date, double open, double high, double low, double close) {
        this.date = date;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
    }

    public String getDate() {
        return date;
    }

    public double getOpen() {
        return open;
    }

    public double getHigh() {
        return high;
    }

    public double getLow() {
        return low;
    }

    public double getClose() {
        return close;
    }

    @Override
    public String toString() {
        return "Data{" +
                "date='" + date + '\'' +
                ", open=" + open +
                ", high=" + high +
                ", low=" + low +
                ", close=" + close +
                '}';
    }
}
