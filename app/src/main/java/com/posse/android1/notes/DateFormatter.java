package com.posse.android1.notes;

import android.icu.text.SimpleDateFormat;

import java.util.Date;
import java.util.Locale;

public class DateFormatter {
    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(new Date());
    }
}
