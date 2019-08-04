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
    private PointF prevDisplacement = new PointF(0, 0);
    private PointF rawlastDisplacement = new PointF();
    private PointF lastDisplacement = new PointF();
    Matrix matrix = new Matrix();
    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;
    GestureHelper gestureHelper;
    private boolean mDidChange;
    private RectB mCanOffset = new RectB();


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

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;
        mDidChange = false;

        pointer.set(ev.getX(index), ev.getY(index));

        if(mGestureDetector.onTouchEvent(ev) || mScaleDetector.onTouchEvent(ev) || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
        }

        rawlastDisplacement.set(pointer.x - prevPointer.x, pointer.y - prevPointer.y);
        mCanOffset = gestureHelper.clampOffset(lastDisplacement, displacement, rawlastDisplacement);
        matrix.postTranslate(lastDisplacement.x, lastDisplacement.y);
        displacement.offset(lastDisplacement.x, lastDisplacement.y);
        if(!displacement.equals(prevDisplacement)) mDidChange = true;
        prevDisplacement.set(displacement);

        //if(mDidChange) gestureHelper.onChange(matrix);

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            prevPointer = null;
        }

        return mDidChange;
    }

    public void postScale() {
        postScale(mScaleDetector);
    }

    public void postScale(ScaleGestureDetector detector){
        float scaleBy = clampScaleFactor(detector.getScaleFactor());
        mScale *= scaleBy;
        if(scaleBy != 1) mDidChange = true;
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
            RectF z = new RectF(-1, -1, 1, 1);
            z.offset(e.getX(), e.getY());
            gestureHelper.zoomTo(z);
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            return false;
        }
    };
}
