package io.autodidact.zoomage;

import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ScrollView;

import com.facebook.react.uimanager.ThemedReactContext;

public class CustomScrollView extends ScrollView {
    public static String TAG = "CustomScrollView";

    private float mScale = 1f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector gestureDetector;
    private float minScale = 0.75f;
    private float maxScale = 3f;
    private int doubleTapAnimationDuration = 300;
    private PointF translation = new PointF(0, 0);

    CustomScrollView(ThemedReactContext context){
        super(context);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        gestureDetector = new GestureDetector(context, new GestureListener());
        mScaleDetector = new ScaleGestureDetector(context, new ScaleListener()){
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return event.getPointerCount() > 1 && super.onTouchEvent(event);
            }
        };
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        Log.d(TAG, "dispatchTouchEvent: " + mScaleDetector.onTouchEvent(event));
        return mScaleDetector.onTouchEvent(event) ? true : gestureDetector.onTouchEvent(event);
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        public boolean applyScaleAnimation(ScaleGestureDetector detector){
            float prevScale = mScale;
            mScale = clamp(minScale, prevScale * detector.getScaleFactor(), maxScale);
            ScaleAnimation scaleAnimation = new ScaleAnimation(prevScale, mScale, prevScale, mScale, detector.getFocusX(), detector.getFocusY());
            scaleAnimation.setDuration(0);
            scaleAnimation.setFillAfter(true);
            startAnimation(scaleAnimation);

            return scaleAnimation.willChangeBounds();
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            super.onScaleBegin(detector);
            return applyScaleAnimation(detector);
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            super.onScale(detector);
            return applyScaleAnimation(detector);
        }

    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
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
            boolean shouldCatch = super.onScroll(e1, e2, distanceX, distanceY);
            Log.d(TAG, "onScroll: " + shouldCatch);
            //if(shouldCatch == false) return false;

            PointF prev = new PointF(translation.x, translation.y);
            translation.offset(-distanceX * mScale, -distanceY * mScale);
            TranslateAnimation translateAnimation = new TranslateAnimation(prev.x, translation.x, prev.y, translation.y);
            translateAnimation.setDuration(0);
            translateAnimation.setFillAfter(true);
            startAnimation(translateAnimation);
            return translateAnimation.willChangeTransformationMatrix();
        }
    }

    public static float clamp(float min, float value, float max){
        return Math.max(min, Math.min(value, max));
    }

}
