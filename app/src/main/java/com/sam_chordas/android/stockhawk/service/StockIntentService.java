package com.sam_chordas.android.stockhawk.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.TaskParams;
import com.sam_chordas.android.stockhawk.R;

/**
 * Created by sam_chordas on 10/1/15.
 */
public class StockIntentService extends IntentService {

  public StockIntentService(){
    super(StockIntentService.class.getName());
  }

  public StockIntentService(String name) {
    super(name);
  }

  @Override protected void onHandleIntent(Intent intent) {
    Log.d(StockIntentService.class.getSimpleName(), "Stock Intent Service");
    StockTaskService stockTaskService = new StockTaskService(this);
    Bundle args = new Bundle();
    if (intent.getStringExtra(getString(R.string.tag)).equals(getString(R.string.add))) {
      args.putString("symbol", intent.getStringExtra(getString(R.string.str_symbol)));
    }
    // We can call OnRunTask from the intent service to force it to run immediately instead of
    // scheduling a task.
    Handler h = new Handler(getApplicationContext().getMainLooper());
    if (stockTaskService.onRunTask(new TaskParams(intent.getStringExtra(getString(R.string.tag)), args)) == GcmNetworkManager.RESULT_FAILURE) {
      h.post(new Runnable() {
        public void run() {
          Toast.makeText(getApplicationContext(), R.string.invalid_symbol, Toast.LENGTH_LONG).show();
        }
      });
    }

  }
}
