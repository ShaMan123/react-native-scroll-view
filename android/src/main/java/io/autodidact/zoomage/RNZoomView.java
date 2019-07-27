package io.autodidact.zoomage;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
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
    private float mLastTouchX;
    private float mLastTouchY;
    private float mPosX;
    private float mPosY;
    private float mScaleFactor = 1f;
    private float mScale = 1f;
    private float minScale = 0.75f;
    private float maxScale = 3f;
    private PointF translation = new PointF(0, 0);
    private int doubleTapAnimationDuration = 300;
    AnimationSet animationSet;
    static float minMovementToTranslate = 0;
    RectF layout = new RectF();

    public RNZoomView(ThemedReactContext context){
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());

        animationSet = new AnimationSet(false);
        animationSet.restrictDuration(0);
        animationSet.setDuration(0);
        animationSet.setFillAfter(true);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(changed) layout.set(left, top, right, bottom);
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        startAnimation(animationSet);
        return true;
    }

    public static float clamp(float min, float value, float max){
        return Math.max(min, Math.min(value, max));
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        public float clamp(float value){
            return RNZoomView.clamp(minScale, value, maxScale);
        }

        public float clampScaleFactor(float scaleFactor){
            return RNZoomView.clamp(minScale / mScale, scaleFactor, maxScale / mScale);
        }

        public ObjectAnimator animation;
        private float previousScaleFactor = 1f;
        private float clampedScaleFactor = 1f;


        public boolean applyScaleAnimation(ScaleGestureDetector detector){
            float prevScale = mScale;
            float clampedScaleFactor = clampScaleFactor(detector.getScaleFactor());
            mScale *= clampedScaleFactor;
            ScaleAnimation scaleAnimation = new ScaleAnimation(previousScaleFactor, clampedScaleFactor, previousScaleFactor, clampedScaleFactor, detector.getFocusX(), detector.getFocusY());
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
        // event when double tap occurs
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

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            super.onScroll(e1, e2, distanceX, distanceY);
            //Log.d(TAG, "onScroll: " + " distanceX = " + distanceX + " distanceY = " + distanceY);
            //Log.d(TAG, "accum: " + " distanceX = " + translation.x + " distanceY = " + translation.y);
            //boolean shouldCatch = super.onScroll(e1, e2, distanceX, distanceY);



            PointF d = new PointF(minMovementToTranslate > Math.abs(distanceX) ? 0: -distanceX, minMovementToTranslate > Math.abs(distanceY) ? 0: -distanceY);



            RectF actualLayout = new RectF(layout.left * mScale, layout.top * mScale, layout.right * mScale, layout.bottom * mScale);
            actualLayout.offset(translation.x, translation.y);
            PointF center = new PointF(actualLayout.centerX(), actualLayout.centerY());
            RectF displacementBounds = new RectF(actualLayout.left - layout.left, actualLayout.top - layout.top, actualLayout.right - layout.right, actualLayout.bottom - layout.bottom);
            Log.d(TAG, "actualLayout: " + actualLayout.toString());

            d.set(RNZoomView.clamp(-displacementBounds.left, d.x, displacementBounds.right), RNZoomView.clamp(-displacementBounds.top, d.x, displacementBounds.bottom));
            Log.d(TAG, "d: " + d.toString());
            previousDistance.set(d);
            translation.offset(d.x, d.y);

            TranslateAnimation translateAnimation = new TranslateAnimation(previousDistance.x, d.x, previousDistance.y, d.y);
            animationSet.addAnimation(translateAnimation);
            return translateAnimation.willChangeTransformationMatrix();
        }
    }
}
