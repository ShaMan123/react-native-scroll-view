package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.facebook.react.uimanager.ThemedReactContext;

public class ScaleGestureHelper implements IGestureDetector.ScaleHelper, ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = RNZoomableScrollView.class.getSimpleName() + ":" + ScaleGestureHelper.class.getSimpleName();
    protected float mScale = 1f;
    private float minScale = 0.75f;
    private float maxScale = 3f;
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private Matrix matrix;
    private boolean mAppliedChange;
    MeasureTransformedView measureTransformedView;
    private boolean mDidInitScale = false;

    ScaleGestureHelper(ThemedReactContext context, Matrix matrix, MeasureTransformedView measureTransformedView) {
        mScaleDetector = new ScaleGestureDetector(context, this){
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return event.getPointerCount() > 1 && super.onTouchEvent(event);
            }
        };

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(this);

        this.matrix = matrix;
        this.measureTransformedView = measureTransformedView;
    }
/*
    public void setInitialScale(float scale){
        if(mDidInitScale) return;
        mDidInitScale = true;
        mScale = clampScale(scale);
        matrix.postScale(mScale, mScale, 0, 0);
    }
*/
    public float getScale() {
        return mScale;
    }

    @Override
    public void forceUpdateFromMatrix() {
        float[] values = new float[9];
        matrix.getValues(values);
        mScale = values[Matrix.MSCALE_X];
    }

    @Override
    public float getMinimumScale() {
        return minScale;
    }

    public void setMinimumScale(float minimumScale) {
        minScale = minimumScale;
    }

    @Override
    public float getMaximumScale() {
        return maxScale;
    }

    public void setMaximumScale(float maximumScale) {
        maxScale = maximumScale;
    }

    public float clamp(float min, float value, float max){
        return Math.max(min, Math.min(value, max));
    }

    @Override
    public float clampScale(float scale) {
        return clamp(getMinimumScale(), scale, getMaximumScale());
    }

    @Override
    public float clampScaleFactor(float currentScale, float scaleBy) {
        return clamp(getMinimumScale() / currentScale, scaleBy, getMaximumScale() / currentScale);
    }

    private float clampScaleFactor(float scaleBy){
        return clampScaleFactor(mScale, scaleBy);
    }

    public void postScale() {
        postScale(mScaleDetector);
    }

    public void postScale(ScaleGestureDetector detector){
        float scaleBy = clampScaleFactor(detector.getScaleFactor());
        postScale(scaleBy, detector.getFocusX(), detector.getFocusY());
    }

    public void setScale(float scale, float focusX, float focusY, boolean animated){
        postScale(scale / mScale, focusX, focusY);
    }

    public void postScale(float scaleBy, float focusX, float focusY){
        float outScaleBy = clampScaleFactor(scaleBy);
        mScale *= outScaleBy;
        if(outScaleBy != 1) mAppliedChange = true;
        matrix.postScale(outScaleBy, outScaleBy, focusX, focusY);
    }

    public boolean onTouchEvent(MotionEvent event) {
        mAppliedChange = false;
        return mGestureDetector.onTouchEvent(event) ||  mScaleDetector.onTouchEvent(event);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        postScale(detector);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        postScale(detector);
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        float min = Math.max(1, minScale);
        float max = maxScale;
        float m = (min + max) / 2;
        float scaleTo = mScale > m ? max : min;
        setScale(scaleTo, e.getX(), e.getY(), true);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
