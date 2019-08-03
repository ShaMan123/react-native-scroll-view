package io.autodidact.zoomage;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;

import com.facebook.react.uimanager.ThemedReactContext;

public class CombinedGestureDetector implements ScaleGestureDetector.OnScaleGestureListener, IGestureDetector {
    public static String TAG = CombinedGestureDetector.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private float mScale = 1f;
    private PointF displacement = new PointF(0, 0);
    private PointF lastDisplacement = new PointF();
    Matrix matrix = new Matrix();
    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;
    VelocityTracker mVelocityTracker;
    GestureHelper gestureHelper;

    public CombinedGestureDetector(ThemedReactContext context, GestureHelper helper){
        mScaleDetector = new ScaleGestureDetector(context, this){
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return event.getPointerCount() > 1 && super.onTouchEvent(event);
            }
        };
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(doubleTapListener);

        gestureHelper = helper;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;

        pointer.set(ev.getX(index), ev.getY(index));

        if(mGestureDetector.onTouchEvent(ev) || mScaleDetector.onTouchEvent(ev) || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
        }

        if(action == MotionEvent.ACTION_DOWN){ mVelocityTracker = VelocityTracker.obtain(); }
        mVelocityTracker.addMovement(ev);
        mVelocityTracker.computeCurrentVelocity(1);
        if(action == MotionEvent.ACTION_UP) { mVelocityTracker.recycle(); }

        lastDisplacement.set(pointer.x - prevPointer.x, pointer.y - prevPointer.y);
        lastDisplacement.set(gestureHelper.clampOffset(displacement, lastDisplacement));
        matrix.postTranslate(lastDisplacement.x, lastDisplacement.y);
        displacement.offset(lastDisplacement.x, lastDisplacement.y);


        gestureHelper.onChange(matrix);

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            prevPointer = null;
        }


/*
        if(action == MotionEvent.ACTION_UP){
            PointF a = gestureHelper.getTopLeftMaxDisplacement();
            matrix.postTranslate(-a.x, -a.y);

        }
        */
    }

    public void postScale() {
        postScale(mScaleDetector);
    }

    public void postScale(ScaleGestureDetector detector){
        float scaleBy = clampScaleFactor(detector.getScaleFactor());
        mScale *= scaleBy;
        //RectF src = layoutRect(false);
        //src.offset(-viewPort.left, -viewPort.top);
        //matrix.postScale(scaleBy, scaleBy, src.centerX(), src.centerY());
        matrix.postScale(scaleBy, scaleBy, detector.getFocusX(), detector.getFocusY());
    }

    private float clampScaleFactor(float scaleBy){
        return gestureHelper.clampScaleFactor(mScale, scaleBy);
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

    private GestureDetector.OnDoubleTapListener doubleTapListener = new GestureDetector.OnDoubleTapListener() {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            gestureHelper.zoomTo(new RectF(0, 0, e.getX(), e.getY()));
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    };
}
