package com.vakk.ohlc.api.quandl.models;



import java.util.List;

/**
 * Created by vakk on 3/12/16.
 */
public class OHLCPattern {

    /**
     * @see DataResponse
     */
    private int id;
    private String name;
    private String code;
    private String description;

    // historical values
    public List<Data> ohlc;

    public OHLCPattern(int id, String name, String code, String description, List<Data> ohlc) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.description = description;
        this.ohlc = ohlc;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public List<Data> getOhlc(){
        return ohlc;
    }

    @Override
    public String toString() {
        return "OHLCPattern{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", ohlc=" + ohlc +
                '}';
    }
}
