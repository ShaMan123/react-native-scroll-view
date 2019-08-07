package io.autodidact.zoomablescrollview;

import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import androidx.annotation.IntDef;
import androidx.core.view.ViewCompat;

import com.facebook.react.modules.i18nmanager.I18nUtil;
import com.facebook.react.uimanager.ThemedReactContext;

public class MeasureUtility {
    private ThemedReactContext mContext;

    public MeasureUtility(ThemedReactContext context){
        mContext = context;
    }

    public int getLayoutDirection(){
        return isRTL() ? ViewCompat.LAYOUT_DIRECTION_RTL : ViewCompat.LAYOUT_DIRECTION_LTR;
    }

    public boolean isRTL(){
        return I18nUtil.getInstance().isRTL(mContext);
    }

    public Rect getUsableViewPort(){
        Point viewPort = getViewPort();
        //viewPort.offset(0, -getStatusBarHeight());
        return new Rect(0, getStatusBarHeight(), viewPort.x, viewPort.y);
    }


    public int getStatusBarHeight(){
        int result = 0;
        int resourceId = mContext.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = mContext.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public Point getViewPort(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = mContext.getCurrentActivity() .getWindowManager();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return new Point(displayMetrics.widthPixels, displayMetrics.heightPixels);
    }

    public int getNavigationBarHeight(){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        WindowManager windowManager = mContext.getCurrentActivity() .getWindowManager();
        int navigationBarHeight = 0;

        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.getDefaultDisplay().getRealMetrics(realDisplayMetrics);
            boolean hasNavigationBar = realDisplayMetrics.heightPixels > displayMetrics.heightPixels;
            if (hasNavigationBar) navigationBarHeight = realDisplayMetrics.heightPixels - displayMetrics.heightPixels;
        }

        return navigationBarHeight;
    }
    /*
    public Point getViewPort(boolean includeNavigationBar){
        WindowManager windowManager = mContext.getCurrentActivity() .getWindowManager();
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();

        if (Build.VERSION.SDK_INT >= 19 && includeNavigationBar) {
            // include navigation bar
            display.getRealSize(outPoint);
        } else {
            // exclude navigation bar
            display.getSize(outPoint);
        }
        if (outPoint.y > outPoint.x) {
            return new Point(outPoint.x, outPoint.y);
        } else {
            return new Point(outPoint.y, outPoint.x);
        }
    }
    */
}
