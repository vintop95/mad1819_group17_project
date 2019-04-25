package it.polito.mad1819.group17.deliveryapp.common.utils;

import android.os.AsyncTask;
import android.util.Log;

public class TimeoutOperation extends AsyncTask<String, Void, Void> {

    @Override
    protected Void doInBackground(String... str) {
        try {
            Log.i("TimeoutOperation", "Going to sleep");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        Log.i("TimeoutOperation",
                "This is executed after X seconds " +
                        "and runs on the main thread");
        super.onPostExecute(result);
    }
}