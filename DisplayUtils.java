package com.example.mypermissionsapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.ViewConfiguration;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Stanimir Spasov from MentorMate.
 */
public class DisplayUtils {
    // ---------------------------------------------------------------------------------------------
    // Constants
    // ---------------------------------------------------------------------------------------------
    private static final String TAG = DisplayUtils.class.getSimpleName();
    private static final String INCH_FORMAT = "#0.0";

    // ---------------------------------------------------------------------------------------------
    // Fields
    // ---------------------------------------------------------------------------------------------
    private static DisplayMetrics displayMetrics;

    private static int width;
    private static int height;
    private static float xdpi;
    private static float ydpi;
    private static double screenInches;

    private static float density;
    private static int densityDpi;
    private static float scaledDensity;

    // ---------------------------------------------------------------------------------------------
    // Public methods
    // ---------------------------------------------------------------------------------------------

    /**
     * Method that calculate device screen inches.
     * WARNING: May not be 100% accurate on some devices.
     *
     * @param activity The activity witch will call this method. If it is from activity pass "this".
     *                 If it is from fragment - "getActivity()".
     * @return Calculated screen inches.
     */
    public static double getScreenInches(Activity activity) {
        displayMetrics = new DisplayMetrics();
        Display display = activity.getWindowManager()
            .getDefaultDisplay();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealMetrics(displayMetrics);
            height = displayMetrics.heightPixels;
        } else {
            display.getMetrics(displayMetrics);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                if (ViewConfiguration.get(activity)
                    .hasPermanentMenuKey()) {
                    height = displayMetrics.heightPixels;
                } else {
                    height = displayMetrics.heightPixels + getNavigationBarHeight(activity);
                }
            } else {
                height = displayMetrics.heightPixels;
            }
        }

        width = displayMetrics.widthPixels;
        xdpi = displayMetrics.xdpi;
        ydpi = displayMetrics.ydpi;

        density = displayMetrics.density;
        densityDpi = displayMetrics.densityDpi;
        scaledDensity = displayMetrics.scaledDensity;

        double x = Math.pow(width / xdpi, 2);
        double y = Math.pow(height / ydpi, 2);
        screenInches = Math.sqrt(x + y);

        NumberFormat formatter = new DecimalFormat(INCH_FORMAT);
        screenInches = Double.valueOf(formatter.format(screenInches));

        log();

        return screenInches;
    }

    /**
     * Method that calculate status bar height.
     *
     * @param context The activity witch will call this method. If it is from activity pass "this".
     *                If it is from fragment - "getActivity()".
     * @return Calculated status bar height in pixels.
     */
    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
            .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources()
                .getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "statusBarHeight = " + result);
        return result;
    }

    /**
     * Method that calculate navigation bar height.
     *
     * @param context The activity witch will call this method. If it is from activity pass "this".
     *                If it is from fragment - "getActivity()".
     * @return Calculated navigation bar height in pixels.
     */
    public static int getNavigationBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources()
            .getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources()
                .getDimensionPixelSize(resourceId);
        }
        Log.i(TAG, "navigationBarHeight = " + result);
        return result;
    }

    /**
     * Method that shows device screen size name.
     *
     * @param context The activity witch will call this method. If it is from activity pass "this".
     *                If it is from fragment - "getActivity()".
     * @return One of screen names "small", "normal", "large", "xlarge", or ""undefined".
     */
    public static String getScreenSizeName(Context context) {
        int screenLayout = context.getResources()
            .getConfiguration().screenLayout;
        screenLayout &= Configuration.SCREENLAYOUT_SIZE_MASK;
        String sizeName;
        switch (screenLayout) {
            case Configuration.SCREENLAYOUT_SIZE_SMALL:
                sizeName = "small";
                break;
            case Configuration.SCREENLAYOUT_SIZE_NORMAL:
                sizeName = "normal";
                break;
            case Configuration.SCREENLAYOUT_SIZE_LARGE:
                sizeName = "large";
                break;
            case Configuration.SCREENLAYOUT_SIZE_XLARGE:
                sizeName = "xlarge";
                break;
            default:
                sizeName = "undefined";
        }
        Log.i(TAG, "screenSizeName = " + sizeName);
        return sizeName;
    }

    /**
     * Method that shows device screen density in dpi.
     *
     * @param activity The activity witch will call this method. If it is from activity pass "this".
     *                 If it is from fragment - "getActivity()".
     * @return Screen density in dpi.
     */
    public static int getScreenDensityDpi(Activity activity) {
        displayMetrics = new DisplayMetrics();
        activity.getWindowManager()
            .getDefaultDisplay()
            .getMetrics(displayMetrics);
        densityDpi = displayMetrics.densityDpi;

        switch (densityDpi) {
            case DisplayMetrics.DENSITY_LOW:
                Log.i(TAG, "DENSITY_LOW... Density is " + String.valueOf(densityDpi));
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Log.i(TAG, "DENSITY_MEDIUM... Density is " + String.valueOf(densityDpi));
                break;
            case DisplayMetrics.DENSITY_HIGH:
                Log.i(TAG, "DENSITY_HIGH... Density is " + String.valueOf(densityDpi));
                break;
            case DisplayMetrics.DENSITY_XHIGH:
                Log.i(TAG, "DENSITY_XHIGH... Density is " + String.valueOf(densityDpi));
                break;
            case DisplayMetrics.DENSITY_XXHIGH:
                Log.i(TAG, "DENSITY_XXHIGH... Density is " + String.valueOf(densityDpi));
                break;
            case DisplayMetrics.DENSITY_XXXHIGH:
                Log.i(TAG, "DENSITY_XXXHIGH... Density is " + String.valueOf(densityDpi));
                break;
            default:
                Log.i(TAG, "DENSITY_UNKNOWN... Density is " + String.valueOf(densityDpi));
        }
        return densityDpi;
    }

    /**
     * Method that decides if the device is tablet or phone.
     * WARNING: May not be 100% accurate on some devices.
     *
     * @param activity The activity witch will call this method. If it is from activity pass "this".
     *                 If it is from fragment - "getActivity()".
     * @return true or false.
     */
    public static boolean isDeviceTablet(Activity activity) {
        return (getScreenSizeName(activity).contains("large") ||
                getScreenSizeName(activity).contains("xlarge")) && getScreenInches(activity) >= 7.0;
    }

    // ---------------------------------------------------------------------------------------------
    // Private methods
    // ---------------------------------------------------------------------------------------------
    private static void log() {
        Log.i(TAG, "screenWidth = " + width);
        Log.i(TAG, "screenXdpi = " + xdpi);
        Log.i(TAG, "screenHeight = " + height);
        Log.i(TAG, "screenYdpi = " + ydpi);
        Log.i(TAG, "density = " + density);
        Log.i(TAG, "densityDpi = " + densityDpi);
        Log.i(TAG, "scaledDensity = " + scaledDensity);

        Log.i(TAG, "screenInches = " + screenInches);
    }
}
