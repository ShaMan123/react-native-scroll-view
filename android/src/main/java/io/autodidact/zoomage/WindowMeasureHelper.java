package io.autodidact.zoomage;

import android.app.Activity;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;

import com.facebook.react.uimanager.ThemedReactContext;

public class WindowMeasureHelper {
    private DisplayMetrics displayMetrics;
    private Display display;
    public int width;
    public int height;

    public WindowMeasureHelper(View view, boolean includeNavigationBar){
        displayMetrics = new DisplayMetrics();
        display = ((Activity) view.getContext())
                .getWindowManager()
                .getDefaultDisplay();

        display.getMetrics(displayMetrics);

        height = displayMetrics.heightPixels + (includeNavigationBar? getNavigationBarHeight(): 0);
        width = displayMetrics.widthPixels;
    }

    private int getNavigationBarHeight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            int usableHeight = metrics.heightPixels;
            display.getRealMetrics(metrics);
            int realHeight = metrics.heightPixels;
            if (realHeight > usableHeight)
                return realHeight - usableHeight;
            else
                return 0;
        }
        return 0;
    }
}
