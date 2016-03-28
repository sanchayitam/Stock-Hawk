package com.sam_chordas.android.stockhawk.widget;

/**
 * Created by sanch on 3/22/2016.
 */

import java.util.Random;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.data.QuoteColumns;
import com.sam_chordas.android.stockhawk.data.QuoteProvider;
import com.sam_chordas.android.stockhawk.rest.Utils;

/**
 * This is the service that provides the factory to be bound to the collection service.
 */
/*public class QuoteWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuoteRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}*/

 public class QuoteWidgetRemoteViewsService  extends RemoteViewsService {
    private static final String LOG_TAG = QuoteWidgetRemoteViewsService.class.getSimpleName();
    private Cursor mCursor;
    private int mAppWidgetId;

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {

            @Override
            public void onCreate() {
                // Since we reload the mCursor in onDataSetChanged() which gets called immediately after
                // onCreate(), we do nothing here.
            }

            @Override
            public void onDestroy() {
                if (mCursor != null) {
                    mCursor.close();
                }
            }

            @Override
            public int getCount() {
                return mCursor == null ? 0 : mCursor.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
             //   AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);

                if (position == AdapterView.INVALID_POSITION ||
                        mCursor == null || !mCursor.moveToPosition(position)) {
                    return null;
                }

                RemoteViews remoteViews = new RemoteViews(getPackageName(),
                        R.layout.widget_collection_item);

                remoteViews.setTextViewText(R.id.stock_symbol, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
               // remoteViews.setTextViewText(R.id.bid_price, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.BIDPRICE)));

                if (mCursor.getInt(mCursor.getColumnIndex(QuoteColumns.ISUP)) == 1) {
                    remoteViews.setInt(R.id.change, getResources().getString(R.string.set_background_resource), R.drawable.percent_change_pill_green);

                } else {

                    remoteViews.setInt(R.id.change,getResources().getString(R.string.set_background_resource),
                            R.drawable.percent_change_pill_red);

                }
                if (Utils.showPercent) {
                    remoteViews.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.PERCENT_CHANGE)));
                } else {
                    remoteViews.setTextViewText(R.id.change, mCursor.getString(mCursor.getColumnIndex(QuoteColumns.CHANGE)));
                }

                // Register an onClickListener
                Intent launchIntent = new Intent();
                launchIntent.putExtra(getResources().getString(R.string.str_symbol), mCursor.getString(mCursor.getColumnIndex(QuoteColumns.SYMBOL)));
                remoteViews.setOnClickFillInIntent(R.id.widget_list_item, launchIntent);

                return remoteViews;
            }

            @Override
            public RemoteViews getLoadingView() {
                // We aren't going to return a default loading view in this sample
                return null;
            }

            @Override
            public int getViewTypeCount() {
                // Technically, we have two types of views (the dark and light background views)
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return position;
            }

            public boolean hasStableIds() {
                return true;
            }

            @Override
            public void onDataSetChanged() {

                // Refresh the mCursor
                if (mCursor != null) {
                    mCursor.close();
                }

                //Restore the identity of the incoming IPC back to a previously identity
                // that was returned by {@link #clearCallingIdentity}.
                final long origId = Binder.clearCallingIdentity();

                mCursor = getContentResolver().query(QuoteProvider.Quotes.CONTENT_URI, new String[]{
                                QuoteColumns._ID, QuoteColumns.SYMBOL, QuoteColumns.BIDPRICE,
                                QuoteColumns.PERCENT_CHANGE, QuoteColumns.CHANGE, QuoteColumns.ISUP},
                        QuoteColumns.ISCURRENT + " = ?",
                        new String[]{"1"},
                        null);

                //Reset the identity of the incoming IPC on the current thread.
                Binder.restoreCallingIdentity(origId);
            }
        };
    }
}