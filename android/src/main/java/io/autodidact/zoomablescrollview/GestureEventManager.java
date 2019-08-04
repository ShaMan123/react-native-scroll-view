package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.facebook.react.uimanager.ThemedReactContext;

public class GestureEventManager {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private IGestureDetector combinedGestureDetector;
    private Matrix mMatrix = new Matrix();
    MeasureTransformedView measureTransformedView;
    private ViewGroup mView;
    private ScaleGestureHelper scaleGestureHelper;
    private TranslateGestureHelper translateGestureHelper;
    private boolean mAppliedChange;
    private VelocityHelper mVelocityHelper;

    GestureEventManager(ThemedReactContext context, ViewGroup view){
        measureTransformedView = new MeasureTransformedView(context, mMatrix);
        mView = view;
        scaleGestureHelper = new ScaleGestureHelper(context, mMatrix, measureTransformedView);
        translateGestureHelper = new TranslateGestureHelper(mMatrix, measureTransformedView);
        mVelocityHelper = new VelocityHelper();
    }

    /*
    private void setGestureDetector(ThemedReactContext context, @IGestureDetector.GestureDetectors int detectorType){
        if (detectorType == IGestureDetector.GestureDetectors.MATRIX_GESTURE_DETECTOR){
            combinedGestureDetector = new MatrixGestureDetector(this).setRotationEnabled(false);
        }
        else{
            combinedGestureDetector =
        }
    }
    */

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setLayoutRect(int l, int t, int r, int b){
        measureTransformedView.setLayout(l, t, r, b);
    }

    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(new Matrix());
        RectF dst1 = measureTransformedView.getTransformedRect();
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        canvas.drawRect(dst1, p);

        canvas.setMatrix(measureTransformedView.getAbsoluteMatrix());
    }


    //@Override
    public void requestDisallowInterceptTouchEvent() {
        mView.requestDisallowInterceptTouchEvent(translateGestureHelper.canScroll(mVelocityHelper.getVelocity()));
    }

    public boolean onTouchEvent(MotionEvent event) {
        //if(super.onTouchEvent(event)) return true;
        mVelocityHelper.onTouchEvent(event);
        requestDisallowInterceptTouchEvent();
        mAppliedChange = false;
        if(scaleGestureHelper.onTouchEvent(event)) {
            translateGestureHelper.resetTouchPointers();
            mAppliedChange = true;
        }
        if(translateGestureHelper.onTouchEvent(event)) {
            mAppliedChange = true;
        }

        mView.postInvalidateOnAnimation();

        return mAppliedChange;
    }

}
