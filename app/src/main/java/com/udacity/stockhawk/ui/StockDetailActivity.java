package com.udacity.stockhawk.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import yahoofinance.Stock;

import static com.udacity.stockhawk.ui.MainActivity.STOCK_LOADER;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SELECTED_SYMBOL = "SELECTED_SYMBOL";
    public static final String SELECTED_HISTORY = "SELECTED_HISTORY";

    private static final DateFormat sDateFormat = new SimpleDateFormat("YYYY-MM-dd");

    private String mSymbol;
    private LineChart mLineChart;
    private Loader<Cursor> mLoader;
    private Cursor mCursor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_detail);
        Intent intent = getIntent();

        mSymbol = intent.getStringExtra(SELECTED_SYMBOL);
        String historyString = intent.getStringExtra(SELECTED_HISTORY);
        mLineChart = (LineChart) findViewById(R.id.stock_history_chart);

        populateChart(historyString);
    }

    private void populateChart(String historyString) {

        List<Entry> entries = new ArrayList<>();
        List<String> xValues = new ArrayList<>();

        String[] coordinates = historyString.split("\n");

        for (String coordinate : coordinates) {

            String[] coordinateElements = coordinate.split(",");
            int secondsSinceEpoch = (int) Long.parseLong(coordinateElements[0]);
            float value = (float) Double.parseDouble(coordinateElements[1]);

            entries.add(new Entry(value, secondsSinceEpoch));
            xValues.add("M");//String.valueOf(secondsSinceEpoch));

        }

        LineDataSet dataSet = new LineDataSet(entries, "Value");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);

        LineData lineData = new LineData(xValues, dataSet);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.uri,
                Contract.Quote.QUOTE_COLUMNS,
                null, null, Contract.Quote.COLUMN_SYMBOL);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
