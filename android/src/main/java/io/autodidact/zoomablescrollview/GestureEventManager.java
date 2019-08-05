package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.facebook.react.uimanager.ThemedReactContext;

public class GestureEventManager implements IGestureDetector.ScrollResponder {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private IGestureDetector combinedGestureDetector;
    private Matrix mMatrix = new Matrix();
    MeasureTransformedView measureTransformedView;
    private ViewGroup mView;
    private ScaleGestureHelper scaleGestureHelper;
    private TranslateGestureHelper translateGestureHelper;
    private boolean mAppliedChange;
    private VelocityHelper mVelocityHelper;
    private boolean mIsHorizontal = false;

    GestureEventManager(ThemedReactContext context, ViewGroup view){
        measureTransformedView = new MeasureTransformedView(context, mMatrix);
        mView = view;
        scaleGestureHelper = new ScaleGestureHelper(context, mMatrix, measureTransformedView);
        translateGestureHelper = new TranslateGestureHelper(mMatrix, measureTransformedView);
        mVelocityHelper = new VelocityHelper();
    }

    public void setHorizontal() {
        mIsHorizontal = true;
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

    public ScaleGestureHelper getScaleGestureHelper() {
        return scaleGestureHelper;
    }

    public TranslateGestureHelper getTranslateGestureHelper() {
        return translateGestureHelper;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    @Override
    public void scrollTo(float x, float y, boolean animated) {
        translateGestureHelper.scrollTo(new PointF(x, y));
        mView.postInvalidateOnAnimation();
    }

    @Override
    public void scrollBy(float x, float y, boolean animated) {
        translateGestureHelper.scrollBy(new PointF(x, y));
        mView.postInvalidateOnAnimation();
    }

    @Override
    public void scrollToEnd(boolean animated) {
        translateGestureHelper.scrollToEnd(mIsHorizontal);
        mView.postInvalidateOnAnimation();
    }

    @Override
    public void zoomToRect(float x, float y, float width, float height, boolean animated) {
        zoomToRect(new RectF(x, y, x + width, y + height), animated);
    }

    @Override
    public void zoomToRect(RectF dst, boolean animated) {
        mMatrix.setRectToRect(dst, measureTransformedView.getAbsoluteLayoutRect(), Matrix.ScaleToFit.CENTER);
        forceUpdateFromMatrix();
        mView.postInvalidateOnAnimation();
    }

    protected void forceUpdateFromMatrix() {
        scaleGestureHelper.forceUpdateFromMatrix();
        translateGestureHelper.forceUpdateFromMatrix();
    }

    @Override
    public void flashScrollIndicators() {

        //mView.postInvalidateOnAnimation();
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

    public boolean requestDisallowInterceptTouchEvent() {
        return translateGestureHelper.canScroll(mVelocityHelper.getVelocity());
    }

    public boolean onTouchEvent(MotionEvent event) {
        mVelocityHelper.onTouchEvent(event);
        mAppliedChange = false;
        if(scaleGestureHelper.onTouchEvent(event)) {
            translateGestureHelper.resetTouchPointers();
            mAppliedChange = true;
        }
        if(translateGestureHelper.onTouchEvent(event)) {
            mAppliedChange = true;
        }

        return mAppliedChange;
    }

     /*

    public boolean requestDisallowInterceptTouchEvent() {
        return mRequestDisallowInterceptTouchEvent;
    }


    private PointF down = new PointF();
    private PointF crossThreshold = new PointF();
    private boolean mDisallowIntercept = true;
    Point direction = new Point();
    private int minThreshold = 50;
    private boolean mRequestDisallowInterceptTouchEvent = true;

    public boolean onTouchEvent(MotionEvent event) {
        mVelocityHelper.onTouchEvent(event);
        int action = event.getActionMasked();
        PointF pointer = new PointF(event.getX(), event.getY());

        if(action == MotionEvent.ACTION_DOWN) down.set(event.getX(), event.getY());
        boolean disallowIntercept = translateGestureHelper.canScroll(mVelocityHelper.getVelocity());

        mAppliedChange = false;
        if(scaleGestureHelper.onTouchEvent(event)) {
            translateGestureHelper.resetTouchPointers();
            mAppliedChange = true;
        }
        if(translateGestureHelper.onTouchEvent(event)) {
            mAppliedChange = true;
        }

        if(!disallowIntercept && mDisallowIntercept) {
            PointF velocity = mVelocityHelper.getVelocity();
            direction = VelocityHelper.sign(velocity);
            crossThreshold.set(pointer);
            crossThreshold.offset(direction.x * minThreshold, direction.y * minThreshold);
            //event.offsetLocation(down.x, down.y);
            Matrix m = new Matrix();
            m.setTranslate(-pointer.x, -pointer.y);
            event.transform(m);
        }
        else if(!disallowIntercept) {
            crossThreshold.offset(-pointer.x, -pointer.y);
            mRequestDisallowInterceptTouchEvent = !VelocityHelper.sign(crossThreshold).equals(direction);
        }

        mDisallowIntercept = disallowIntercept;
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mRequestDisallowInterceptTouchEvent = true;
        }

        return mAppliedChange;
    }
    */
}
