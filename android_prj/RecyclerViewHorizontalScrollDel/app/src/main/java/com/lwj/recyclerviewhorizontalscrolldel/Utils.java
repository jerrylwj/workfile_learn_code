package com.lwj.recyclerviewhorizontalscrolldel;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;

import android.view.WindowManager;


public class Utils {
    private static final String TAG = "Utils";
    public static int getDeviceWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        Log.d(TAG, "getDeviceWidth = " + dm.widthPixels);
        return dm.widthPixels;
    }
}
