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
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class StockDetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SELECTED_SYMBOL = "SELECTED_SYMBOL";
    public static final String SELECTED_HISTORY = "SELECTED_HISTORY";

    private String mSymbol;
    private LineChart mLineChart;
    private Calendar mNow;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stock_detail);
        Intent intent = getIntent();

        mSymbol = intent.getStringExtra(SELECTED_SYMBOL);
        String historyString = intent.getStringExtra(SELECTED_HISTORY);
        mLineChart = (LineChart) findViewById(R.id.stock_history_chart);
        mNow = Calendar.getInstance();

        setTitle(mSymbol);

        populateChart(historyString);
    }

    private void populateChart(String historyString) {

        List<Entry> entries = new ArrayList<>();

        String[] coordinates = historyString.split("\n");

        for (String coordinate : coordinates) {

            String[] coordinateElements = coordinate.split(",");
            long secondsSinceEpoch = Long.parseLong(coordinateElements[0]);
            int timeDelta = -(int) ((mNow.getTimeInMillis() - secondsSinceEpoch) / (1000 * 60 * 60 * 24 * 7));

            float value = (float) Double.parseDouble(coordinateElements[1]);

            entries.add(new Entry(timeDelta, value));
        }

        Collections.sort(entries, new EntryXComparator());

        LineDataSet dataSet = new LineDataSet(entries, "Value");
        dataSet.setColor(Color.RED);
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);

        LineData lineData = new LineData(dataSet);
        XAxis xAxis = mLineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        Description chartDescription = new Description();
        chartDescription.setPosition(1000, 100);
        chartDescription.setText("Stock History in weeks");
        chartDescription.setTextSize(40f);
        mLineChart.setDescription(chartDescription);
        mLineChart.setData(lineData);
        mLineChart.invalidate();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                Contract.Quote.URI,
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
