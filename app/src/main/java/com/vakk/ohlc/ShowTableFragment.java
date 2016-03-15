package com.vakk.ohlc;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.vakk.ohlc.api.Queries;
import com.vakk.ohlc.api.ServerResponseListener;
import com.vakk.ohlc.api.quandl.QuandlQueries;
import com.vakk.ohlc.api.quandl.models.Data;
import com.vakk.ohlc.api.quandl.models.OHLCPattern;
import com.vakk.ohlc.helpers.Ticker;
import com.vakk.ohlc.helpers.TickersAdapter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by vakk on 3/9/16.
 */
public class ShowTableFragment extends Activity {
    // for site quandl.com
    Queries query = new QuandlQueries();
    // for response waiting
    ProgressDialog pDialog;
    // keyword
    EditText keyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        keyword = (EditText) findViewById(R.id.keyword);
        addProgressDialog();
        addListener();
        //pickDate();
        keyword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void drawTable(List<Data> datas) {
        // get table layout object
        TableLayout table = (TableLayout) findViewById(R.id.table_response);
        // get rectangle for table cells from resources
        Drawable shapes = getResources().getDrawable(R.drawable.shape);
        // clean table
        table.removeAllViews();
        // dialog for wait
        ProgressDialog rowProgress = new ProgressDialog(ShowTableFragment.this);
        rowProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        rowProgress.setMessage("Creating table...");
        rowProgress.setIndeterminate(true);
        rowProgress.setCancelable(false);
        rowProgress.show();
        // draw table, add values
        int count;
        if (ShowFormat.page*ShowFormat.perPage>=ShowFormat.currentList.size()){
            count = ShowFormat.currentList.size()%ShowFormat.perPage;
        }
        else count = ShowFormat.perPage;
        for (int i = 0; i < count; i++) {
            TableRow tableRow = new TableRow(getApplicationContext());
            tableRow.setBackground(shapes);

            tableRow.setLayoutParams(
                    new LayoutParams(
                            LayoutParams.WRAP_CONTENT,
                            LayoutParams.WRAP_CONTENT)
            );
            for (int j = 0; j < 5; j++) {

                TextView textView = new TextView(this);
                textView.setBackground(shapes);
                textView.setGravity(Gravity.CENTER);
                switch (j) {
                    case 0: {
                        textView.setText(datas.get(ShowFormat.perPage * ShowFormat.page + i).getDate());
                        break;
                    }
                    case 1: {
                        textView.setText(Double.toString(datas.get(ShowFormat.perPage * ShowFormat.page + i).getOpen()));
                        break;
                    }
                    case 2: {
                        textView.setText(Double.toString(datas.get(ShowFormat.perPage * ShowFormat.page + i).getHigh()));
                        break;
                    }
                    case 3: {
                        textView.setText(Double.toString(datas.get(ShowFormat.perPage * ShowFormat.page + i).getLow()));
                        break;
                    }
                    case 4: {
                        textView.setText(Double.toString(datas.get(ShowFormat.perPage * ShowFormat.page + i).getClose()));
                        break;
                    }


                }
                tableRow.addView(textView, j);
            }

            table.addView(tableRow, i);
        }
        rowProgress.cancel();
        int k = 0;
        TableRow tableRow = new TableRow(getApplicationContext());
        if (ShowFormat.page>0) {
            Button prev = new Button(this);
            prev.setText("<--");
            tableRow.addView(prev, k);
            k++;
        }
        if (ShowFormat.page<ShowFormat.currentList.size()/ShowFormat.perPage);
        Button next = new Button(this);
        next.setText("-->");
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowFormat.page+=1;
                drawTable(ShowFormat.currentList);
            }
        });
        tableRow.addView(next,k);
        table.addView(tableRow,ShowFormat.perPage);
    }

    /**
     * set progress dialog preferences
     */
    private void addProgressDialog() {
        pDialog = new ProgressDialog(ShowTableFragment.this);
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pDialog.setMessage("Waiting for response...");
        pDialog.setIndeterminate(true);
        pDialog.setCancelable(true);
        pDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                query.cancelRequest();
            }
        });
    }

    /**
     * add on click listener on send button
     */
    private void addListener() {
        Button send = (Button) findViewById(R.id.send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pDialog.show();
                query.cancelRequest();
                query.getOHLC(new ServerResponseListener() {
                    @Override
                    public void fail(Object obj) {
                        Toast.makeText(getApplicationContext(), obj.toString(), Toast.LENGTH_LONG).show();
                        pDialog.cancel();
                    }

                    @Override
                    public void done(Object obj) {
                        OHLCPattern pattern = (OHLCPattern) obj;
                        TextView name = (TextView) findViewById(R.id.name);
                        name.setVisibility(View.VISIBLE);
                        name.setText(pattern.getName());
                        TableRow tableRow = (TableRow) findViewById(R.id.table_header);
                        tableRow.setVisibility(View.VISIBLE);
                        pDialog.cancel();
                        ShowFormat.currentList=pattern.getOhlc();
                        drawTable(ShowFormat.currentList);
                    }
                }, keyword.getText().toString());
            }
        });
        // set tickers adapter
        final AutoCompleteTextView keyword = (AutoCompleteTextView)findViewById(R.id.keyword);

        QuandlQueries.readCsv(this, new ServerResponseListener() {
            @Override
            public void fail(Object obj) {

            }

            @Override
            public void done(Object obj) {
                List<Ticker> list = (List<Ticker>) obj;
                TickersAdapter adapter = new TickersAdapter(getApplicationContext(), (ArrayList<Ticker>) list);
                keyword.setAdapter(adapter);
            }
        });
        keyword.setThreshold(1);

    }


    @Override
    protected void onResume() {
        if (ShowFormat.currentList!=null){
            drawTable(ShowFormat.currentList);
        }
        super.onResume();
    }

    private void pickDate(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Toast.makeText(getApplication(),"ondataset",Toast.LENGTH_LONG).show();
            }
        },1997,10,13);
        datePickerDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_historical_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.set_range:{

                break;
            }
            case R.id.order_by:{
                showDialog(0);
            }
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case 0:{
                AlertDialog.Builder builder;
                final String[] sortTypes = getResources().getStringArray(R.array.sort_by);

                builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose your order type");

                builder.setItems(sortTypes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                       ShowFormat.orderBy(item);
                    drawTable(ShowFormat.currentList);}
                });
                builder.setCancelable(false);
                return builder.create();

            }
        }
        return null;
    }

    private static class ShowFormat{
        public static List<Data> fullList;
        public static List<Data> currentList;
        public static int perPage=10;
        public static int page=0;
        // sort type
        public static final int SORT_BY_DATE=0;
        public static final int SORT_BY_OPEN=1;
        public static final int SORT_BY_HIGH=2;
        public static final int SORT_BY_LOW=3;
        public static final int SORT_BY_CLOSE=4;
        // order
        public static boolean less = true;
        //date range
        public static String lowRange;
        public static String highRange;

        public boolean setDateRange(Context context,String lowRange,String highRange){
            /*
            String string = "January 2, 2010";
            DateFormat format = new SimpleDateFormat("yyyy-dd-mm", Locale.ENGLISH);
            Date date = null;
            try {
                date = format.parse(lowRange);
            } catch (ParseException e) {
                Toast.makeText(context,"parse exception",Toast.LENGTH_LONG).show();

                return false;
            }
            Toast.makeText(context,Integer.toString(date.getYear()),Toast.LENGTH_LONG).show();
            */
            return true;
        }
        public static void orderBy(int param){
            page=0;

            switch (param){
                case SORT_BY_DATE:{
                    Collections.sort(currentList, new Comparator<Data>() {
                        @Override
                        public int compare(Data lhs, Data rhs) {
                            return lhs.getDate().compareTo(rhs.getDate());

                        }
                    });
                    if (!less){
                        Collections.reverse(currentList);
                    }
                    break;
                }

                case SORT_BY_OPEN:{
                       Collections.sort(currentList, new Comparator<Data>() {
                           @Override
                           public int compare(Data lhs, Data rhs) {
                               if (lhs.getOpen()>rhs.getOpen()){
                                   return 1;
                               }
                               if (lhs.getOpen()<rhs.getOpen()){
                                    return -1;
                               }
                               return 0;
                           }
                       });
                    if (!less){
                        Collections.reverse(currentList);
                    }
                    break;
                }

                case SORT_BY_HIGH:{
                    Collections.sort(currentList, new Comparator<Data>() {
                        @Override
                        public int compare(Data lhs, Data rhs) {
                            if (lhs.getHigh()>rhs.getHigh()){
                                return 1;
                            }
                            if (lhs.getHigh()<rhs.getHigh()){
                                return -1;
                            }
                            return 0;
                        }
                    });
                    if (!less){
                        Collections.reverse(currentList);
                    }
                    break;
                }

                case SORT_BY_LOW:{
                    Collections.sort(currentList, new Comparator<Data>() {
                        @Override
                        public int compare(Data lhs, Data rhs) {
                            if (lhs.getLow()>rhs.getLow()){
                                return 1;
                            }
                            if (lhs.getLow()<rhs.getLow()){
                                return -1;
                            }
                            return 0;
                        }
                    });
                    if (!less){
                        Collections.reverse(currentList);
                    }
                    break;
                }

                case SORT_BY_CLOSE:{
                    Collections.sort(currentList, new Comparator<Data>() {
                        @Override
                        public int compare(Data lhs, Data rhs) {
                            if (lhs.getClose()>rhs.getClose()){
                                return 1;
                            }
                            if (lhs.getClose()<rhs.getClose()){
                                return -1;
                            }
                            return 0;
                        }
                    });
                    if (!less){
                        Collections.reverse(currentList);
                    }
                    break;
                }
            }

        }
    }
}
