package io.autodidact.zoomage;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.RequiresApi;

import com.autodidact.R;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

public class RNZoomView extends ReactViewGroup {
    public static String TAG = RNZoomView.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private GestureListener mGestureListener;
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private float mScaleFactor = 1f;
    private float mScale = 1f;
    private float minScale = 0.75f;
    private float maxScale = 3f;
    private PointF displacement = new PointF(0, 0);
    private int doubleTapAnimationDuration = 300;
    AnimationSet animationSet;
    static float minMovementToTranslate = 0;
    RectF layout = new RectF();
    RectF viewPort = new RectF();

    public RNZoomView(ThemedReactContext context){
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener()){
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return event.getPointerCount() > 1 ? super.onTouchEvent(event) : false;
            }
        };

        Point mViewPort = new MeasureUtility(context).getUsableViewPort();
        viewPort.set(0, 0, mViewPort.x, mViewPort.y);

        Log.d(TAG, "viewPort: " + viewPort.toString());

        mGestureListener = new GestureListener();
        mGestureDetector = new GestureDetector(context, mGestureListener);

        animationSet = new AnimationSet(true);
        animationSet.restrictDuration(0);
        animationSet.setDuration(0);
        animationSet.setFillAfter(true);
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layout.set(left, top, right, bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        startAnimation(animationSet);
        return true;
    }

    public RectF getActualLayout(){
        RectF actualLayout = new RectF(layout.left, layout.top, layout.width() * mScale, layout.height() * mScale);
        actualLayout.offset((actualLayout.width() - layout.width()) * -0.5f, (actualLayout.height() - layout.height()) * -0.5f);
        actualLayout.offset(-displacement.x, -displacement.y);
        return actualLayout;
    }

    public static float clamp(float min, float value, float max){
        return Math.max(min, Math.min(value, max));
    }

    public static float clampBottom(float value){
        return clampBottom(0, value);
    }

    public static float clampBottom(float min, float value){
        return Math.max(min, value);
    }

    public static float clampTop(float top, float value){
        return Math.min(top, value);
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        public float clamp(float value){
            return RNZoomView.clamp(minScale, value, maxScale);
        }

        public float clampScaleFactor(float scaleFactor){
            return RNZoomView.clamp(minScale / mScale, scaleFactor, maxScale / mScale);
        }

        private float previousScaleFactor = 1f;
        private float clampedScaleFactor = 1f;


        public boolean applyScaleAnimation(ScaleGestureDetector detector){
            float prevScale = mScale;
            float clampedScaleFactor = clampScaleFactor(detector.getScaleFactor());
            mScale *= clampedScaleFactor;
            RectF actualLayout = getActualLayout();
            ScaleAnimation scaleAnimation = new ScaleAnimation(previousScaleFactor, clampedScaleFactor, previousScaleFactor, clampedScaleFactor, actualLayout.centerX(), actualLayout.centerY());
            animationSet.addAnimation(scaleAnimation);
            previousScaleFactor = clampedScaleFactor;
            return scaleAnimation.willChangeBounds();
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return applyScaleAnimation(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return applyScaleAnimation(detector);
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector scaleGestureDetector) {
            return;
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private PointF previousDistance = new PointF(0, 0);
        @Override
        public boolean onDown(MotionEvent e) {
            super.onDown(e);
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
/*
            float prevScale = mScale;
            mScale = mScale == maxScale ? 1 : maxScale;
            ScaleAnimation scaleAnimation = new ScaleAnimation(prevScale, mScale, prevScale, mScale, e.getX(), e.getY());
            scaleAnimation.setDuration(doubleTapAnimationDuration);
            scaleAnimation.setFillAfter(true);
            startAnimation(scaleAnimation);
            return true;
            */


            return false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return super.onFling(e1, e2, velocityX, velocityY);

        }

        private float clampRawDistance(float distance){
            return minMovementToTranslate > Math.abs(distance) ? 0: distance;
        }

        public PointF clampedDistance(float distanceX, float distanceY){

            PointF d = new PointF(clampRawDistance(distanceX), clampRawDistance(distanceY));
            RectF actualLayout = getActualLayout();
            RectF displacementBounds = new RectF(actualLayout.left - layout.left, actualLayout.top - layout.top, actualLayout.right - layout.right,actualLayout.bottom - layout.bottom);
            Log.d(TAG, "    layout:  "+ layout.toString() + "    actual:  " + actualLayout.toString());

            /*
            //
            PointF center = new PointF(actualLayout.centerX(), actualLayout.centerY());
            PointF viewPortCenter = new PointF(layout.centerX(), layout.centerY());
            center.offset(-viewPortCenter.x, -viewPortCenter.y);
            RectF displacementBounds = new RectF(actualLayout.left - layout.left, actualLayout.top - layout.top, actualLayout.right - layout.right,actualLayout.bottom - layout.bottom);
            displacementBounds.offset(translation.x, translation.y);
            Log.d(TAG, "center: " + center.toString() + " tt: " + translation.toString());
            //*/
            //Log.d(TAG, "rects: " + displacementBounds.toString() + "    layout:  "+ layout.toString() + "    actual:  " + actualLayout.toString());

            float displacementLeft = -actualLayout.left;
            float displacementRight = actualLayout.right - layout.right;
            float displacementTop = -actualLayout.top;
            float displacementBottom = actualLayout.height() - actualLayout.top;
/*
            boolean xInBounds = actualLayout.left < 0 && actualLayout.right - layout.width() > 0;
            boolean yInBounds = actualLayout.top < 0 && actualLayout.bottom - layout.height() > 0;
*/
            PointF center = new PointF(actualLayout.centerX(), actualLayout.centerY());
            PointF viewPortCenter = new PointF(layout.centerX(), layout.centerY());

            boolean xInViewBounds = actualLayout.left - layout.left <= 0 && actualLayout.right - layout.right >= 0;
            boolean yInViewBounds = actualLayout.top - layout.top <= 0 && actualLayout.bottom - layout.bottom >= 0;

            boolean xInViewPort = actualLayout.left - layout.left <= 0 && actualLayout.right - viewPort.right >= 0;
            boolean yInViewPort = actualLayout.top - layout.top <= 0 && actualLayout.bottom - viewPort.bottom >= 0;

            boolean xInBounds = actualLayout.left - Math.max(layout.left, viewPort.left) <= 0 && actualLayout.right - Math.min(layout.right, viewPort.right) >= 0;
            boolean yInBounds = actualLayout.top - Math.max(layout.top, viewPort.top) <= 0 && actualLayout.bottom - Math.min(layout.bottom, viewPort.bottom) >= 0;

            Log.d(TAG, "x in bounds: " + xInBounds);
            Log.d(TAG, "y in bounds: " + yInBounds);
/*
            float lowerBoundX = displacementRight;
            float upperBoundX = displacementLeft;
            float lowerBoundY = displacementBottom;
            float upperBoundY = displacementTop;
            Log.d(TAG, "x: " + lowerBoundX + " " + distanceX + " " + upperBoundX);
            */
            //Log.d(TAG, "x: " + displacementLeft + " " + distanceX + " " + displacementRight);
            //Log.d(TAG, "y: " + displacementTop + " " + distanceY + " " + displacementBottom);
            //d.set(RNZoomView.clamp(lowerBoundX, distanceX, upperBoundX), RNZoomView.clamp(lowerBoundY, distanceY, upperBoundY));
            return d;
        }

        public void animate(){
            RectF actualLayout = new RectF(0, 0, layout.width() * mScale, layout.height() * mScale);
            actualLayout.offset((actualLayout.width() - layout.width()) * -0.5f, (actualLayout.height() - layout.height()) * -0.5f);
            actualLayout.offset(-displacement.x, -displacement.y);
            TranslateAnimation translateAnimation = new TranslateAnimation(previousDistance.x, actualLayout.left - layout.left, previousDistance.y, actualLayout.top - layout.top);
            animationSet.addAnimation(translateAnimation);
            previousDistance.set(actualLayout.left - layout.left, actualLayout.top - layout.top);
            startAnimation(animationSet);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            super.onScroll(e1, e2, distanceX, distanceY);
            //Log.d(TAG, "onScroll: " + " distanceX = " + distanceX + " distanceY = " + distanceY);
            //Log.d(TAG, "accum: " + " distanceX = " + translation.x + " distanceY = " + translation.y);
            //boolean shouldCatch = super.onScroll(e1, e2, distanceX, distanceY);





            PointF d = clampedDistance(distanceX, distanceY);

            TranslateAnimation translateAnimation = new TranslateAnimation(-previousDistance.x, -d.x, -previousDistance.y, -d.y);
            animationSet.addAnimation(translateAnimation);

            previousDistance.set(d);
            displacement.offset(d.x, d.y);
            
            return translateAnimation.willChangeTransformationMatrix();
        }
    }
}
