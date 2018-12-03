package com.dev.eivs.openweathermapapp.storage;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Storage {

    public static final String APP_ID = "302b123c89648137da76b6a143380e4d";
    public static final String APP_IDD = "da377f1956b964677cfd21f4c9110043";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formatted = sdf.format(date);
        return formatted;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String formatted = sdf.format(date);
        return formatted;
    }
}
