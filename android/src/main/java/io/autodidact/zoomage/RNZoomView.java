package io.autodidact.zoomage;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.facebook.react.uimanager.ThemedReactContext;

public class RNZoomView extends ViewGroup implements IGestureDetector.GestureHelper {
    public static String TAG = RNZoomView.class.getSimpleName();
    IGestureDetector combinedGestureDetector;
    RectF layout = new RectF();
    Rect mViewPort;

    RNZoomView(ThemedReactContext context){
        this(context, IGestureDetector.GestureDetectors.COMBINED_GESTURE_DETECTOR);
    }

    RNZoomView(ThemedReactContext context, @IGestureDetector.GestureDetectors int detectorType){
        super(context);
        setGestureDetector(context, detectorType);
        mViewPort = new MeasureUtility(context).getUsableViewPort();
    }

    private void setGestureDetector(ThemedReactContext context, @IGestureDetector.GestureDetectors int detectorType){
        if (detectorType == IGestureDetector.GestureDetectors.MATRIX_GESTURE_DETECTOR){
            combinedGestureDetector = new MatrixGestureDetector(this).setRotationEnabled(false);
        }
        else{
            combinedGestureDetector = new CombinedGestureDetector(context, this);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if(super.onTouchEvent(event)) return true;
        requestDisallowInterceptTouchEvent(true);
        combinedGestureDetector.onTouchEvent(event);
        return true;
    }

    public boolean contains(){
        return out().contains(targetViewPort(false));
    }

    public Matrix absMatrix(){
        Matrix m = new Matrix();
        m.preTranslate(mViewPort.left, mViewPort.top);
        m.preTranslate(layout.left, layout.top);
        m.postConcat(combinedGestureDetector.getMatrix());
        return m;
    }

    public RectF absLayout(){
        RectF out = new RectF(layout);
        out.offset(mViewPort.left, mViewPort.top);
        return out;
    }

    public RectF targetViewPort(boolean relative){
        RectF layoutRect = absLayout();
        RectF out = new RectF(Math.max(mViewPort.left, layoutRect.left), Math.max(mViewPort.top, layoutRect.top), Math.min(mViewPort.right, layoutRect.right), Math.min(mViewPort.bottom, layoutRect.bottom));
        if(relative) out.offsetTo(0, 0);
        return out;
    }

    public RectF out(/*boolean relative*/){
        RectF src = new RectF(absLayout());
        RectF dst = new RectF();
        /*if(relative)*/ src.offsetTo(0, 0);
        absMatrix().mapRect(dst, src);
        return dst;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(new Matrix());
        RectF dst1 = out();
        Paint p = new Paint();
        p.setColor(Color.BLUE);
        canvas.drawRect(dst1, p);

        canvas.setMatrix(absMatrix());
        super.onDraw(canvas);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layout.set(l,t,r,b);
    }


    /*
     * IGestureDetector.GestureHelper
     *
     */

    private float minScale = 0.75f;
    private float maxScale = 3f;

    @Override
    public void onChange(Matrix matrix) {
        postInvalidateOnAnimation();
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

    @Override
    public RectF getClippingRect() {
        return targetViewPort(false);
    }

    @Override
    public RectF getTransformedRect() {
        return out();
    }

    @Override
    public PointF getTopLeftMaxDisplacement() {
        RectF o = getTransformedRect();
        RectF clippingRect = getClippingRect();
        Log.d(TAG, "getTopLeftMaxDisplacement: " + o + "  " + clippingRect);
        //return new PointF(Math.min(o.left - clippingRect.left, 0), Math.min(o.top - clippingRect.top, 0));
        return new PointF(o.left - clippingRect.left, o.top - clippingRect.top);
    }

    @Override
    public PointF getTopLeftMaxDisplacement(PointF distance) {
        PointF p = getTopLeftMaxDisplacement();
        p.offset(-distance.x, -distance.y);
        return p;
    }

    @Override
    public PointF getBottomRightMaxDisplacement() {
        RectF o = getTransformedRect();
        RectF clippingRect = getClippingRect();
        //return new PointF(Math.max(o.right - clippingRect.right, 0), Math.max(o.bottom - clippingRect.bottom, 0));
        return new PointF(o.right - clippingRect.right, o.bottom - clippingRect.bottom);
    }

    @Override
    public PointF getBottomRightMaxDisplacement(PointF distance) {
        PointF p = getBottomRightMaxDisplacement();
        p.offset(-distance.x, -distance.y);
        return p;
    }

    @Override
    public PointF clampOffset(PointF distance) {
        PointF topLeft = getTopLeftMaxDisplacement();
        PointF bottomRight = getBottomRightMaxDisplacement();

        return new PointF();
    }

    @Override
    public PointF clampOffset(PointF distance, PointF offset) {
        PointF out = new PointF(offset.x, offset.y);
        RectF clippingRect = getClippingRect();
        RectF transformed = getTransformedRect();
        transformed.offset(offset.x, offset.y);

        if(transformed.left > clippingRect.left) out.offset(clippingRect.left - transformed.left, 0);
        if(transformed.top > clippingRect.top) out.offset(0, clippingRect.top - transformed.top);
        if(transformed.right < clippingRect.right) out.offset(clippingRect.right - transformed.right, 0);
        if(transformed.bottom < clippingRect.bottom) out.offset(0, clippingRect.bottom - transformed.bottom);
        return out;
    }
}
