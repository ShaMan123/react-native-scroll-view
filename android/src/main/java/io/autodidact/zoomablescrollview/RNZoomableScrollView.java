package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import com.facebook.react.ReactRootView;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

import java.util.ArrayList;

public class RNZoomableScrollView extends ViewGroup {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private GestureManager mGestureManager;
    private ThemedReactContext mContext;
    private boolean mIsHorizontal = false;
    private float mTouchSlop;

    RNZoomableScrollView(ThemedReactContext context){
        super(context);
        mContext = context;
        mGestureManager = new GestureManager(this);
        ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        //setOverflow("hidden");
        //setRemoveClippedSubviews(false);
    }

    public ThemedReactContext getReactContext() {
        return mContext;
    }

    public GestureManager getGestureManager() {
        return mGestureManager;
    }

    public MatrixManager getMatrix() {
        return mGestureManager.getMatrix();
    }

    public boolean isHorizontal(){
        return mIsHorizontal;
    }

    public void setHorizontal(boolean value) {
        mIsHorizontal = value;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mGestureManager.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mGestureManager.onLayout(changed, l, t, r, b);
    }

/*
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        mTouch.set(ev.getX(), ev.getY());
        final int action = ev.getActionMasked();

        switch (action) {
            case MotionEvent.ACTION_DOWN:{
                mIsScrolling = false;
                mTouchDown.set(mTouch);
                mTotalTouch.set(0, 0);
            }
            case MotionEvent.ACTION_MOVE: {
                mTotalTouch.offset(Math.abs(mTouch.x - mTouchDown.x), Math.abs(mTouch.y - mTouchDown.y));
                Log.d(TAG, "onInterceptTouchEvent: " + mTotalTouch + "  " + mTouchSlop + "  " + mIsScrolling);

                if (mTotalTouch.x > mTouchSlop || mTotalTouch.y > mTouchSlop || ev.getPointerCount() == 2) {
                    mIsScrolling = true;

                }
                break;
            }
        }

        return super.dispatchTouchEvent(ev);

    }
*/
    private boolean mIsScrolling = true;
    private boolean mInvertedEvent = false;
    private PointF mTouchDown = new PointF();
    private PointF mTouch = new PointF();
    private PointF mTotalTouch = new PointF();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        Log.d(TAG, "dispatchTouchEvent: " + ev);
        int action = ev.getActionMasked();
        mTouch.set(ev.getX(), ev.getY());

        if(action == MotionEvent.ACTION_DOWN){
            mTouchDown.set(mTouch);
        }
        else if(action == MotionEvent.ACTION_MOVE){
            mTotalTouch.set(mTouch.x - mTouchDown.x, mTouch.y - mTouchDown.y);
            Log.d(TAG, "in?: " + mTotalTouch.length() + "  " + mTouchSlop);
            if(mTotalTouch.length() > mTouchSlop){
                mIsScrolling = true;
                return true;
            }
        }
        else if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mIsScrolling = false;

        }

        ViewGroup container = (ViewGroup) getChildAt(0);
        View c;
        Rect out = new Rect();
        RectF t = new RectF();
        float x = ev.getX();
        float y = ev.getY();
        for (int i = container.getChildCount() - 1; i >= 0; i--) {
            c = container.getChildAt(i);
            c.getHitRect(out);
            t.set(getMatrix().transformRect(out));
            if(t.contains(x, y) && c.onTouchEvent(ev)){
                /*
                Matrix m = new Matrix();
                getMatrix().invert(m);
                ev.transform(m);
                mInvertedEvent = true;
                */
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(mInvertedEvent){
            event.transform(getMatrix());
            mInvertedEvent = false;
        }
        mGestureManager.onTouchEvent(event);
        //postInvalidateOnAnimation();

        return true;
        //return super.onTouchEvent(event);
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
