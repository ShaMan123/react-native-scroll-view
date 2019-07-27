package io.autodidact.zoomage;

import android.animation.ObjectAnimator;
import android.graphics.Path;
import android.graphics.PointF;
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

    public RNZoomView(ThemedReactContext context){
        super(context);
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        mGestureDetector = new GestureDetector(context, new GestureListener());

        animationSet = new AnimationSet(false);
        animationSet.restrictDuration(0);
        animationSet.setDuration(0);
        animationSet.setFillAfter(true);
    }

    // The ‘active pointer’ is the one currently moving our object.
    //private int mActivePointerId = INVALID_POINTER_ID;

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        mGestureDetector.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        startAnimation(animationSet);
        return true;
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {
        public float clamp(float value){
            return clamp(minScale, value, maxScale);
        }

        public float clamp(float min, float value, float max){
            return Math.max(min, Math.min(value, max));
        }

        public ObjectAnimator animation;
        private float previousScaleFactor = 1f;


        public boolean applyScaleAnimation(ScaleGestureDetector detector){
            float prevScale = mScale;
            mScale = clamp(prevScale * detector.getScaleFactor());
            float scaleFactor = detector.getScaleFactor();
            /*

            Path path = new Path();
            path.moveTo(prevScale, prevScale);
            path.lineTo(mScale, mScale);
            if(animation != null) animation.end();
            animation = ObjectAnimator.ofFloat(RNZoomView.this, "scaleX", "scaleY", path);
            animation.setDuration(0);
            animation.start();

            return true;
*/

            ScaleAnimation scaleAnimation = new ScaleAnimation(previousScaleFactor, scaleFactor, previousScaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
            animationSet.addAnimation(scaleAnimation);
            previousScaleFactor = scaleFactor;
            return scaleAnimation.willChangeBounds();
        }

        /*
        @Override
        public boolean onScaleBegin(ScaleGestureDetector scaleGestureDetector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            mScaleFactor = clamp(mScaleFactor * detector.getScaleFactor());
            invalidate();

            return true;
        }
*/



        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            //super.onScaleBegin(detector);
            return applyScaleAnimation(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //super.onScale(detector);
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
            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {

            float prevScale = mScale;
            mScale = mScale == maxScale ? 1 : maxScale;
            ScaleAnimation scaleAnimation = new ScaleAnimation(prevScale, mScale, prevScale, mScale, e.getX(), e.getY());
            scaleAnimation.setDuration(doubleTapAnimationDuration);
            scaleAnimation.setFillAfter(true);
            startAnimation(scaleAnimation);
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.d(TAG, "onScroll: " + " distanceX = " + distanceX + " distanceY = " + distanceY);
            Log.d(TAG, "accum: " + " distanceX = " + translation.x + " distanceY = " + translation.y);
            //boolean shouldCatch = super.onScroll(e1, e2, distanceX, distanceY);
            previousDistance.set(-distanceX, -distanceY);
            translation.offset(-distanceX, -distanceY);
            TranslateAnimation translateAnimation = new TranslateAnimation(previousDistance.x, -distanceX, previousDistance.y, -distanceY);
            animationSet.addAnimation(translateAnimation);
            return translateAnimation.willChangeTransformationMatrix();
        }
    }
}
