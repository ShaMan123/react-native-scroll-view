package io.autodidact.zoomage;

import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import androidx.core.view.ViewCompat;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

import static android.view.MotionEvent.INVALID_POINTER_ID;

public class RNZoomView1 extends ReactViewGroup {
    private static final float AXIS_X_MIN = 0;
    private static final float AXIS_Y_MIN = 0;
    private static final float AXIS_X_MAX = 2000;
    private static final float AXIS_Y_MAX = 2000;
    public static String TAG = "RNZoomageView";
    private ScaleGestureDetector mScaleDetector;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private float mScaleFactor = 1.f;
    private GestureDetector mGestureDetector;

    private RectF mCurrentViewport;
    private Rect mContentRect;
    private ScaleGestureDetector mScaleGestureDetector;

    public RNZoomView1(ThemedReactContext context){
        super(context);
        //mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mScaleGestureDetector = new ScaleGestureDetector(context, mScaleGestureListener);
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                float viewportOffsetX = distanceX * mCurrentViewport.width()
                        / mContentRect.width();
                float viewportOffsetY = -distanceY * mCurrentViewport.height()
                        / mContentRect.height();

                // Updates the viewport, refreshes the display.
                setViewportBottomLeft(
                        mCurrentViewport.left + viewportOffsetX,
                        mCurrentViewport.bottom + viewportOffsetY);


                return super.onScroll(e1, e2, distanceX, distanceY);
            }
        });
    }

    /**
     * Sets the current viewport (defined by mCurrentViewport) to the given
     * X and Y positions. Note that the Y value represents the topmost pixel position,
     * and thus the bottom of the mCurrentViewport rectangle.
     */
    private void setViewportBottomLeft(float x, float y) {
        /*
         * Constrains within the scroll range. The scroll range is simply the viewport
         * extremes (AXIS_X_MAX, etc.) minus the viewport size. For example, if the
         * extremes were 0 and 10, and the viewport size was 2, the scroll range would
         * be 0 to 8.
         */

        float curWidth = mCurrentViewport.width();
        float curHeight = mCurrentViewport.height();
        x = Math.max(AXIS_X_MIN, Math.min(x, AXIS_X_MAX - curWidth));
        y = Math.max(AXIS_Y_MIN + curHeight, Math.min(y, AXIS_Y_MAX));

        mCurrentViewport.set(x, y - curHeight, x + curWidth, y);

        // Invalidates the View to update the display.
        postInvalidateOnAnimation();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "RNZoomView1: ww " + getWidth());

        if(mContentRect == null) {
            mContentRect = new Rect();
            mCurrentViewport = new RectF();
        }
        mContentRect.set(left, top, right, bottom);
        mCurrentViewport.set(left, top, right, bottom);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean retVal = mScaleGestureDetector.onTouchEvent(event);
        retVal = mGestureDetector.onTouchEvent(event) || retVal;
        return retVal || super.onTouchEvent(event);
    }

    /**
     * The scale listener, used for handling multi-finger scale gestures.
     */
    private final ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener
            = new ScaleGestureDetector.SimpleOnScaleGestureListener() {
        /**
         * This is the active focal point in terms of the viewport. Could be a local
         * variable but kept here to minimize per-frame allocations.
         */
        private PointF viewportFocus = new PointF();
        private float lastSpanX;
        private float lastSpanY;

        // Detects that new pointers are going down.
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            lastSpanX = scaleGestureDetector.getCurrentSpanX();
            lastSpanY = scaleGestureDetector.getCurrentSpanY();
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            setScale(scaleGestureDetector.getScaleFactor());
            float spanX = scaleGestureDetector.getCurrentSpanX();
            float spanY = scaleGestureDetector.getCurrentSpanY();

            float newWidth = lastSpanX / spanX * mCurrentViewport.width();
            float newHeight = lastSpanY / spanY * mCurrentViewport.height();

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            /*
            // Makes sure that the chart point is within the chart region.
            // See the sample for the implementation of hitTest().
            hitTest(scaleGestureDetector.getFocusX(),
                    scaleGestureDetector.getFocusY(),
                    viewportFocus);
                    */

            mCurrentViewport.set(
                    viewportFocus.x
                            - newWidth * (focusX - mContentRect.left)
                            / mContentRect.width(),
                    viewportFocus.y
                            - newHeight * (mContentRect.bottom - focusY)
                            / mContentRect.height(),
                    0,
                    0);
            mCurrentViewport.right = mCurrentViewport.left + newWidth;
            mCurrentViewport.bottom = mCurrentViewport.top + newHeight;

            postInvalidateOnAnimation();
            //postInvalidate();

            lastSpanX = spanX;
            lastSpanY = spanY;
            return true;
        }

        public void setScale(float scaleFactor){
            float scale = Math.max(0.75f, Math.min(mScaleFactor * scaleFactor, 3.f));
            mScaleFactor = scale;
        }
    };

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();
        //canvas.ma
        //canvas.translate();
        canvas.scale(mScaleFactor, mScaleFactor);
        // onDraw() code goes here
        Log.d(TAG, "onDraw: " + mScaleFactor);
        //canvas.restore();
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor *= detector.getScaleFactor();

            // Don't let the object get too small or too large.
            mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));

            setScaleX(mScaleFactor);
            setScaleY(mScaleFactor);

            //invalidate();

            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            return;
        }
    }
}
