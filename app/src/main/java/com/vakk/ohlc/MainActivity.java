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
import android.widget.LinearLayout;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vakk on 3/9/16.
 */
public class MainActivity extends Activity {
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
        ProgressDialog rowProgress = new ProgressDialog(MainActivity.this);
        rowProgress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        rowProgress.setMessage("Creating table...");
        rowProgress.setIndeterminate(true);
        rowProgress.setCancelable(false);
        rowProgress.show();
        // draw table, add values
        int count;
        if (ShowFormat.page * ShowFormat.perPage >= ShowFormat.currentList.size()) {
            count = ShowFormat.currentList.size() % ShowFormat.perPage;
        } else count = ShowFormat.perPage - 1;
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
        if (ShowFormat.page > 0) {
            Button prev = new Button(this);
            prev.setText("<--");
            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowFormat.page -= 1;
                    drawTable(ShowFormat.currentList);
                }
            });
            tableRow.addView(prev, k);
            k++;
        }
        if (ShowFormat.page * ShowFormat.perPage < ShowFormat.currentList.size()) {
            Button next = new Button(this);
            next.setText("-->");
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ShowFormat.page += 1;
                    drawTable(ShowFormat.currentList);
                }
            });

            tableRow.addView(next, k);
            table.addView(tableRow, count);
        }
    }

    /**
     * set progress dialog preferences
     */
    private void addProgressDialog() {
        pDialog = new ProgressDialog(MainActivity.this);
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
                        ShowFormat.currentList = new ArrayList<Data>();
                        ShowFormat.currentList.addAll(pattern.getOhlc());
                        ShowFormat.fullList = pattern.getOhlc();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.show_historical_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.set_range:{
                Button setRange = (Button)findViewById(R.id.set_date_range);
                setRange.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText lowDateText = (EditText)findViewById(R.id.low_date_input);
                        EditText highDateText = (EditText)findViewById(R.id.high_date_input);
                        String lowDate = lowDateText.getText().toString();
                        String highDate = highDateText.getText().toString();
                        if (!ShowFormat.setDateRange(lowDate,highDate)){
                            Toast.makeText(getApplicationContext(),"Format must be like 1997-01-01",Toast.LENGTH_LONG).show();
                        }
                        else {
                            drawTable(ShowFormat.currentList);
                        }
                    }
                });
                break;
            }
            case R.id.order_by:{
                showDialog(0);
                break;
            }
            case R.id.order_rank_by:{
                showDialog(1);
                break;
            }
        }
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
            case 0:{
                return  createOrderAlertDialog();
            }
            case 1:{
                return createRankedAlertDialog();
            }
        }
        return null;
    }

    private Dialog createOrderAlertDialog(){
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
    private Dialog createRankedAlertDialog(){
        AlertDialog.Builder builder;
        final String[] sortTypes = new String[2];
        sortTypes[0]=getResources().getString(R.string.less);
        sortTypes[1]=getResources().getString(R.string.greater);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Ranked by");
        builder.setItems(sortTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (ShowFormat.less) {
                    if (item == 1) {
                        ShowFormat.less = false;
                        Collections.reverse(ShowFormat.currentList);
                    }
                    else {
                        if (item==0){
                            ShowFormat.less = true;
                            Collections.reverse(ShowFormat.currentList);
                        }
                    }
                    drawTable(ShowFormat.currentList);
                }
            }
        });
        builder.setCancelable(false);
        return builder.create();
    }


}
