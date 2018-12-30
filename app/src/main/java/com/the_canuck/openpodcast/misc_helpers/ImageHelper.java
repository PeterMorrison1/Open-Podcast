package com.the_canuck.openpodcast.misc_helpers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class ImageHelper {

    public static final String COLUMN_NUM_MAP = "column_num";
    public static final String IMAGE_SIZE_MAP = "image_size";

    // This solution worked on my phone but not on others, leaving for now though
//    public static int calculateNoOfColumns(Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        float width = displayMetrics.widthPixels / displayMetrics.density;
//        int columns = (int) (width / 137);
//        Log.d("width", "Widthcalc: " + width + " widthpx: " + displayMetrics.widthPixels);
//        return columns;
//    }

    /**
     * Calculates the perfect image pixel size and column count for library fragment based
     * on screen width.
     *
     * @param context the fragment/application/activity context
     * @return a Map with String keys which are final values in ImageHelper and values of the image
     * size and column count
     */
    public static Map<String, Integer> calculateImageSizes(Context context) {
        Map<String, Integer> imageMap = new HashMap<>();

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float width = displayMetrics.widthPixels;

        int size = 0;

        int i = 12;
        while (size < 300 || size > 600) {
            size = (int) width / i;
            i--;
        }

        imageMap.put(COLUMN_NUM_MAP, i + 1);
        imageMap.put(IMAGE_SIZE_MAP, size);
        return imageMap;
    }

}
