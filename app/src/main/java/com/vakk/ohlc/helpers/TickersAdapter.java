package com.vakk.ohlc.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.vakk.ohlc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vakk on 3/13/16.
 */
public class TickersAdapter extends BaseAdapter implements Filterable{
    List<Ticker> list;
    Context context;
    public TickersAdapter(Context context, ArrayList<Ticker> tickers) {
        this.context = context;
        list = tickers;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return list.size();
    }

    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Ticker getItem(int position) {
        return list.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the item within the adapter's data set whose row id we want.
     * @return The id of the item at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (convertView==null){
            convertView=inflater.inflate(R.layout.tickers_dropdown_item,parent,false);
        }
        Ticker ticker = getItem(position);
        ((TextView)convertView.findViewById(R.id.code)).setText(ticker.getCode());
        ((TextView)convertView.findViewById(R.id.name)).setText(ticker.getName());
        return convertView;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint !=null){
                    List<Ticker> tickers = searchTickers(constraint);
                    filterResults.values = tickers;
                    filterResults.count = tickers.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results!=null && results.count>0){
                    list = (List<Ticker>)results.values;
                    notifyDataSetChanged();
                }
                else notifyDataSetInvalidated();
            }
        };
                return filter;
    }
    private List<Ticker> searchTickers(CharSequence code){
        List<Ticker>tickers = new ArrayList<>();
        for (Ticker ticker:list){
            if (ticker.getCode().contains(code)){
                tickers.add(ticker);
            }
        }
        return tickers;
    }
}
