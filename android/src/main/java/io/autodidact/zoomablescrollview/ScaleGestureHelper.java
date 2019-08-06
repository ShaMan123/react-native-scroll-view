package io.autodidact.zoomablescrollview;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.facebook.react.uimanager.ThemedReactContext;

public class ScaleGestureHelper implements ScaleGestureDetector.OnScaleGestureListener, GestureDetector.OnDoubleTapListener {
    private static final String TAG = ScaleGestureHelper.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private GestureDetector mGestureDetector;
    private MatrixManager matrix;
    private boolean mAppliedChange;
    private boolean mDidInitScale = false;

    ScaleGestureHelper(ThemedReactContext context, MatrixManager matrix) {
        mScaleDetector = new ScaleGestureDetector(context, this){
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return event.getPointerCount() > 1 && super.onTouchEvent(event);
            }
        };
        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener());
        mGestureDetector.setOnDoubleTapListener(this);

        this.matrix = matrix;
    }

    public boolean onTouchEvent(MotionEvent event) {
        mAppliedChange = false;
        return mGestureDetector.onTouchEvent(event) ||  mScaleDetector.onTouchEvent(event);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        matrix.postScale(detector);
        return true;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        matrix.postScale(detector);
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
        matrix.tapToScale(e.getX(), e.getY());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }
}
