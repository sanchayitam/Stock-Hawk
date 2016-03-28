package com.sam_chordas.android.stockhawk.widget;

import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.appwidget.AppWidgetProviderInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.sam_chordas.android.stockhawk.R;
import com.sam_chordas.android.stockhawk.ui.MyStocksActivity;

/**
 * Implementation of App Widget functionality.
  */
public class QuoteWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            //Enabling app widget for the locked home screen
   //     Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetIds[i]);
   //      int category = options.getInt(AppWidgetManager.OPTION_APPWIDGET_HOST_CATEGORY, -1);
    //     boolean isLockScreen = category == AppWidgetProviderInfo.WIDGET_CATEGORY_KEYGUARD;

           // updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

            // Create intent to launch MainActivity
            Intent intent = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context,
                    0, intent,0);
            remoteViews.setOnClickPendingIntent(R.id.widget, pendingIntent);

            //remote views adapter which connects to a RemoteViewsService through the specified intent.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                setRemoteAdapter(context,remoteViews);
            } else {
                setRemoteAdapterV11(context, remoteViews);
            }

            // Set up collection items
            Intent clickIntentTemplate = new Intent(context, MyStocksActivity.class);
            PendingIntent pendingIntentTemplate = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(clickIntentTemplate)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, pendingIntentTemplate);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    //added
    @Override
    public void onReceive( Context context,  Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

     /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param context the context used to launch the intent
     * @param views RemoteViews to set the RemoteAdapter
     */
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    private void setRemoteAdapter(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(R.id.widget_list,
                new Intent(context, QuoteWidgetRemoteViewsService.class));
    }

    /**
     * Sets the remote adapter used to fill in the list items
     *
     * @param context the context to launch the intent
     * @param views RemoteViews to set the RemoteAdapter
     */
    @SuppressWarnings("deprecation")
    private void setRemoteAdapterV11(Context context, @NonNull final RemoteViews views) {
        views.setRemoteAdapter(0, R.id.widget_list,
                new Intent(context, QuoteWidgetRemoteViewsService.class));
    }
}

