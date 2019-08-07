package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.scroll.ReactScrollView;
import com.facebook.react.views.view.ReactViewGroup;

public class RNZoomableScrollView extends ViewGroup {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private GestureEventManager mGestureManager;
    private ThemedReactContext mContext;
    private boolean mIsHorizontal = false;

    RNZoomableScrollView(ThemedReactContext context){
        super(context);
        mContext = context;
        mGestureManager = new GestureEventManager(this);
        setClipChildren(false);
        //setRemoveClippedSubviews(false);
    }

    public ThemedReactContext getReactContext() {
        return mContext;
    }

    public GestureEventManager getGestureManager() {
        return mGestureManager;
    }

    public MatrixManager getMatrix() {
        return mGestureManager.getMatrix();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mGestureManager.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        mGestureManager.onLayout(changed, l, t, r, b);
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
        postInvalidateOnAnimation();

        return true;
        //return super.onTouchEvent(event);
    }

    public boolean isHorizontal(){
        return mIsHorizontal;
    }

    public void setHorizontal(boolean value) {
        mIsHorizontal = value;
    }
}
