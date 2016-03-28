package com.sam_chordas.android.stockhawk.ui;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.LoaderManager;
import android.content.ContentProviderOperation;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.graphics.Color;
import android.os.Bundle;
import android.app.Activity;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.view.ViewGroup;
import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.service.StockTaskService;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyStocksDetail extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MyStocksDetail.class.getSimpleName();
    private static final int CURSOR_LOADER_ID = 0;
    private Cursor mCursor;
    private XYMultipleSeriesRenderer mRenderer;
    private GraphicalView mChart;
    XYMultipleSeriesDataset mSeriesDataset;
    private XYSeriesRenderer mSeriesRenderer;
    int minRange, maxRange = 0;
    private LinearLayout chartContainer;
    ArrayList<Float> mArrayList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        // Get a reference of the layout
        chartContainer = (LinearLayout) findViewById(R.id.linechart);

        if (mChart == null) {
            // Initialize the chart
            initializeChart();
        } else {
            // Refresh the chart
            mChart.repaint();
        }

        Intent intent = getIntent();
        Bundle args = new Bundle();
        args.putString(getResources().getString(R.string.str_symbol), intent.getStringExtra(getResources().getString(R.string.str_symbol)));
        getLoaderManager().initLoader(CURSOR_LOADER_ID, args, this);

    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, QuoteProvider.Quotes.CONTENT_URI,
                new String[]{ QuoteColumns.BIDPRICE},
                QuoteColumns.SYMBOL + " = ?",
                new String[]{args.getString(getResources().getString(R.string.str_symbol))},
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursor = data;
        populateSeries();
    }

    void populateSeries(){
        // Initialize series
        XYSeries xySeries = new XYSeries("Stock Market Bid Price");

   /*     mCursor.moveToFirst();
        for (int i = 0; i < mCursor.getCount(); i++){
            float price = Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));
            xySeries.add( i, price);
            mCursor.moveToNext();
        }
*/
        findRange();
        for(int i = 0; i < mArrayList.size(); i++) {

            xySeries.add( i + 1, mArrayList.get(i));
        }

        // Creating a dataset to hold each series
        mSeriesDataset= new XYMultipleSeriesDataset();
        mSeriesDataset.addSeries(xySeries);

     //   findRange();

        // Create XYMultipleSeriesRenderer
        mRenderer = new XYMultipleSeriesRenderer();
        // Add renderers to XYMultipleSeriesRenderer
        mRenderer.addSeriesRenderer(mSeriesRenderer);
        // We want to avoid black border
        mRenderer.setMarginsColor(Color.argb(0x00, 0xff, 0x00, 0x00)); // transparent margins

       // Disable Pan on two axis
        mRenderer.setPanEnabled(false, false);
        mRenderer.setZoomButtonsVisible(true);
        mRenderer.setZoomLimits(new double[]{-10, 40, -10, 100});
        mRenderer.setYAxisMax(maxRange);
        mRenderer.setYAxisMin(minRange);
        mRenderer.setShowGrid(true); // we show the grid

        //remove any views before u paint the chart
        chartContainer.removeAllViews();

        mChart = ChartFactory.getLineChartView(this, mSeriesDataset, mRenderer);

        // Get a reference of the layout
        chartContainer = (LinearLayout)findViewById(R.id.linechart);
        // Add chart to the layout
        chartContainer.addView(mChart);

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void initializeChart(){
        // Now we create the renderer
        mSeriesRenderer = new XYSeriesRenderer();
        mSeriesRenderer.setLineWidth(2);
        mSeriesRenderer.setColor(Color.RED);

      // Include low and max value
        mSeriesRenderer.setDisplayBoundingPoints(true);
      // we add point markers
        mSeriesRenderer.setPointStyle(PointStyle.CIRCLE);
        mSeriesRenderer.setPointStrokeWidth(3);
    }

    public void findRange() {

        mArrayList = new ArrayList<Float>();
        for (mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
            // The Cursor is now set to the right position
            mArrayList.add(Float.parseFloat(mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE))));
        }

        maxRange = Math.round(Collections.max(mArrayList));
        minRange = Math.round(Collections.min(mArrayList));
    }
}

