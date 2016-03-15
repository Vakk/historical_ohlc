package com.vakk.ohlc.api.quandl.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by vakk on 3/13/16.
 */

/**
 * Base data response class, handle and parse received values from server
 */
public class DataResponse {
    // company id
    private int id;
    // name
    private String name;
    // WIKI code
    @SerializedName("dataset_code")
    private String code;
    // company description
    private String description;
    // reload frequency
    private String frequency;
    // get all historical values
    @SerializedName("data")
    public List<List<Object>> dataList;

    public DataResponse(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    /**
     * Form OHLC pattern
     * @return pattern with historical values
     */
    public OHLCPattern getPattern(){
        List<Data> list = new ArrayList<>();
        for (Object obj:dataList){
            List<Object> data = (List<Object>)obj;
            list.add(new Data(
                    data.get(0).toString(),
                    Double.parseDouble(data.get(1).toString()),
                    Double.parseDouble(data.get(2).toString()),
                    Double.parseDouble(data.get(3).toString()),
                    Double.parseDouble(data.get(4).toString())
            ));
        }
        return new OHLCPattern(id,name,code,description,list);
    }
}
