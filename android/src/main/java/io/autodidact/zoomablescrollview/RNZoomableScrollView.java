package io.autodidact.zoomablescrollview;

import android.app.Application;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.OverScroller;
import android.widget.ScrollView;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;

import com.facebook.react.ReactApplication;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIImplementation;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.scroll.ReactScrollView;
import com.facebook.react.views.view.ReactViewGroup;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.SynchronousQueue;


/**
 *
 *  UIManagerModule uiManager = ((ReactApplication) mContext.getCurrentActivity()
 *                 .getApplication())
 *                 .getReactNativeHost()
 *                 .getReactInstanceManager()
 *                 .getCurrentReactContext()
 *                 .getNativeModule(UIManagerModule.class);
 */

public class RNZoomableScrollView extends ViewGroup {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private GestureEventManager mGestureManager;
    private ThemedReactContext mContext;
    private OnGlobalLayoutListener mGlobalLayoutListener;
    private Rect mContentRect = new Rect();

    private static @Nullable Field sScrollerField;
    private static boolean sTriedToGetScrollerField = false;
    //private final OnScrollDispatchHelper mOnScrollDispatchHelper = new OnScrollDispatchHelper();
    private final @Nullable OverScroller mScroller = null;

    RNZoomableScrollView(ThemedReactContext context){
        super(context);
        mContext = context;
        mGestureManager = new GestureEventManager(this);
        //setRemoveClippedSubviews(false);
        //mScroller = getOverScrollerFromParent();
        //setOverScrollMode(ScrollView.OVER_SCROLL_ALWAYS);
    }

    private void initObserver(){
        mGlobalLayoutListener = new OnGlobalLayoutListener();
        mGlobalLayoutListener.addListener();
    }

    public GestureEventManager getGestureManager() {
        return mGestureManager;
    }

    public ThemedReactContext getReactContext() {
        return mContext;
    }

    ConcurrentLinkedQueue q = new ConcurrentLinkedQueue();

    private class OnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener, Runnable {
        private boolean flag = false;
        private int i=0;
        @Override
        public void onGlobalLayout() {
            new Thread(this).start();
            if(flag) return;
            removeListener();
            flag = true;
            Rect clippingRect = new Rect();
            getLocalVisibleRect(clippingRect);
            int[] a = new int[2];
            getLocationOnScreen(a);
            Log.d(TAG, "onGlobalLayout: " + a[0] + "," + a[1] + " " + flag);
            getLocationInWindow(a);
            Log.d(TAG, "onGlobalLayout: " + + a[0] + "," + a[1] + " " + getTop());
            mContentRect.offset(clippingRect.left, clippingRect.top);
            ///Log.d(TAG, "onGlobalLayout: " + clippingRect + "  " + mContentRect);
            mGestureManager.processLayout(clippingRect, mContentRect);
        }

        @Override
        public void run() {
            Log.d(TAG, "run: ");
            q.add(new Runnable() {
                @Override
                public void run() {
                    run1();
                }
            });
        }

        private Object run1() {
            Collection c = new ArrayList();

            Log.d(TAG, "run1: " +  i);
            i++;

            return i;
            /*
            if(flag) return;
            removeListener();
            flag = true;
            Rect clippingRect = new Rect();
            getLocalVisibleRect(clippingRect);
            int[] a = new int[2];
            getLocationOnScreen(a);
            Log.d(TAG, "onGlobalLayout: " + a[0] + "," + a[1] + " " + flag);
            getLocationInWindow(a);
            Log.d(TAG, "onGlobalLayout: " + + a[0] + "," + a[1]);
            mContentRect.offset(clippingRect.left, clippingRect.top);
            ///Log.d(TAG, "onGlobalLayout: " + clippingRect + "  " + mContentRect);
            mGestureManager.processLayout(clippingRect, mContentRect);
            */
        }

        public void addListener(){
            getViewTreeObserver().addOnGlobalLayoutListener(this);
        }

        public void reset(){
            flag = false;
        }

        public synchronized void removeListener(){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            } else {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            }
        }
    }

    protected void destroy(){
        if(mGlobalLayoutListener != null) mGlobalLayoutListener.removeListener();
    }

    @Nullable
    private OverScroller getOverScrollerFromParent() {
        OverScroller scroller;

        if (!sTriedToGetScrollerField) {
            sTriedToGetScrollerField = true;
            try {
                sScrollerField = ScrollView.class.getDeclaredField("mScroller");
                sScrollerField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.w(
                        TAG,
                        "Failed to get mScroller field for ScrollView! "
                                + "This app will exhibit the bounce-back scrolling bug :(");
            }
        }

        if (sScrollerField != null) {
            try {
                Object scrollerValue = sScrollerField.get(this);
                if (scrollerValue instanceof OverScroller) {
                    scroller = (OverScroller) scrollerValue;
                } else {
                    Log.w(
                            TAG,
                            "Failed to cast mScroller field in ScrollView (probably due to OEM changes to AOSP)! "
                                    + "This app will exhibit the bounce-back scrolling bug :(");
                    scroller = null;
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to get mScroller from ScrollView!", e);
            }
        } else {
            scroller = null;
        }

        return scroller;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        getChildAt(0).getDrawingRect(mContentRect);
        mContentRect.offset(l, t);
        mGestureManager.processLayout(new Rect(l, t, r, b), mContentRect);
        //mGlobalLayoutListener.reset();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mGestureManager.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        super.onInterceptTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //if(super.onTouchEvent(event)) return true;
        boolean disallowInterceptTouchEvent = mGestureManager.requestDisallowInterceptTouchEvent();
        requestDisallowInterceptTouchEvent(disallowInterceptTouchEvent);
        mGestureManager.onTouchEvent(event);
        super.onTouchEvent(event);
        postInvalidateOnAnimation();

        return true;
        //return super.onTouchEvent(event);
    }
}
