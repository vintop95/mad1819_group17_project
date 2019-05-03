package it.polito.mad1819.group17.deliveryapp.common.utils;

import android.widget.TextView;

public class TimeHelper {
    public static String getTimeAsString(int hourOfDay, int minute){
        String timestamp = "";

        if (hourOfDay < 10)
            timestamp += "0";
        timestamp += hourOfDay + ":";

        if (minute < 10)
            timestamp += "0";
        timestamp += minute;

        return timestamp;
    }
}
