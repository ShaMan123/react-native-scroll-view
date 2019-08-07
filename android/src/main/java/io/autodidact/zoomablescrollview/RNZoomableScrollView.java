package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.EdgeEffect;
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
        //mGestureManager.onDraw(canvas);
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

    /*

    private class EdgeEffects {
        //https://android.googlesource.com/platform/frameworks/base/+/jb-release/core/java/android/widget/ScrollView.java
        private EdgeEffect mEdgeGlowTop;
        private EdgeEffect mEdgeGlowBottom;
        private int delta = 0;

        public void onTouchEvent(MotionEvent event){
            if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                final int pulledToY = oldY + deltaY;
                if (pulledToY < 0) {
                    mEdgeGlowTop.onPull((float) deltaY / getHeight());
                    if (!mEdgeGlowBottom.isFinished()) {
                        mEdgeGlowBottom.onRelease();
                    }
                } else if (pulledToY > range) {
                    mEdgeGlowBottom.onPull((float) deltaY / getHeight());
                    if (!mEdgeGlowTop.isFinished()) {
                        mEdgeGlowTop.onRelease();
                    }
                }
                if (mEdgeGlowTop != null
                        && (!mEdgeGlowTop.isFinished() || !mEdgeGlowBottom.isFinished())) {
                    postInvalidateOnAnimation();
                }
            }
        }

        public void draw(Canvas canvas) {
            if (mEdgeGlowTop != null) {
                final int scrollY = mScrollY;
                if (!mEdgeGlowTop.isFinished()) {
                    final int restoreCount = canvas.save();
                    final int width = getWidth() - mPaddingLeft - mPaddingRight;
                    canvas.translate(mPaddingLeft, Math.min(0, scrollY));
                    mEdgeGlowTop.setSize(width, getHeight());
                    if (mEdgeGlowTop.draw(canvas)) {
                        postInvalidateOnAnimation();
                    }
                    canvas.restoreToCount(restoreCount);
                }
                if (!mEdgeGlowBottom.isFinished()) {
                    final int restoreCount = canvas.save();
                    final int width = getWidth() - mPaddingLeft - mPaddingRight;
                    final int height = getHeight();
                    canvas.translate(-width + mPaddingLeft,
                            Math.max(getScrollRange(), scrollY) + height);
                    canvas.rotate(180, width, 0);
                    mEdgeGlowBottom.setSize(width, height);
                    if (mEdgeGlowBottom.draw(canvas)) {
                        postInvalidateOnAnimation();
                    }
                    canvas.restoreToCount(restoreCount);
                }
            }
        }
    }
    */
}
