package com.the_canuck.openpodcast.misc_helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

public class ImageHelper {

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels / displayMetrics.density;
        int columns = (int) (width / 137);
        Log.d("width", "Width: " + width);
        return columns;
    }
}
