package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.view.ViewCompat;

public class TranslateGestureHelper implements GestureEventData.Displacement {
    private static final String TAG = RNZoomableScrollView.class.getSimpleName();
    private MatrixManager matrix;

    TranslateGestureHelper(MatrixManager matrix) {
        this.matrix = matrix;
    }

    public PointF raw = new PointF();
    public PointF clamped = new PointF();
    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;
    private boolean mAppliedChange;
    private boolean mResetPrevPointer = false;

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;
        mAppliedChange = false;

        pointer.set(ev.getX(index), ev.getY(index));

        if(mResetPrevPointer || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
            if(mResetPrevPointer) mResetPrevPointer = false;
        }

        raw.set(pointer.x - prevPointer.x, pointer.y - prevPointer.y);
        clamped.set(matrix.clampOffset(raw));
        matrix.postTranslate(clamped.x, clamped.y);
        if(clamped.length() > 0) mAppliedChange = true;

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            prevPointer = null;
        }

        return mAppliedChange;
    }

    public void resetTouchPointers() {
        mResetPrevPointer = true;
    }
}
