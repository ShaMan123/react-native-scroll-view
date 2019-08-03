package io.autodidact.zoomage;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.VelocityTracker;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.OverScroller;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

public class RNZoomView1 extends ViewGroup implements ScaleGestureDetector.OnScaleGestureListener {
    public static String TAG = RNZoomView1.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private float mScale = 1f;
    private float minScale = 0.75f;
    private float maxScale = 3f;
    private PointF displacement = new PointF(0, 0);
    private PointF lastDisplacement = new PointF();
    private int doubleTapAnimationDuration = 300;
    RectF layout = new RectF();
    RectF viewPort = new RectF();
    RectF actualViewPort = new RectF();
    Matrix matrix = new Matrix();
    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;
    VelocityTracker mVelocityTracker;

    GestureDetector gestureDetector;
    GestureListener gestureListener;

    public RNZoomView1(ThemedReactContext context){
        super(context);
        setClipChildren(false);
        //setLayerType(LAYER_TYPE_SOFTWARE, null);

        mScaleDetector = new ScaleGestureDetector(context, this){
            @Override
            public boolean onTouchEvent(MotionEvent event) {
                return event.getPointerCount() > 1 && super.onTouchEvent(event);
            }
        };

        Rect mViewPort = new MeasureUtility(context).getUsableViewPort();
        //viewPort.set(0, 0, mViewPort.width(), mViewPort.height());
        viewPort.set(mViewPort);
        matrix.preTranslate(mViewPort.left, mViewPort.top);

        gestureListener = new GestureListener();
        gestureDetector = new GestureDetector(context, gestureListener);

    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        //super.onLayout(changed, left, top, right, bottom);
        layout.set(left, top, right, bottom);
        if(changed) matrix.preTranslate(-actualViewPort.left, -actualViewPort.top);
        actualViewPort.set(targetViewPort());
        matrix.preTranslate(actualViewPort.left, actualViewPort.top);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setClipBounds(new Rect(0, top, right, bottom));
        }
        */
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(matrix);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        requestDisallowInterceptTouchEvent(true);

        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;

        /*
        if(action == MotionEvent.ACTION_DOWN) {
            gestureListener.reset();
        }
        gestureDetector.onTouchEvent(ev);
        if(gestureListener.isFling()){
            return false;
        }
        */

        pointer.set(ev.getX(index), ev.getY(index));

        if(mScaleDetector.onTouchEvent(ev) || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
        }

        if(action == MotionEvent.ACTION_DOWN){ mVelocityTracker = VelocityTracker.obtain(); }
        mVelocityTracker.addMovement(ev);
        mVelocityTracker.computeCurrentVelocity(1);
        if(action == MotionEvent.ACTION_UP) { mVelocityTracker.recycle(); }

        lastDisplacement.set(pointer.x - prevPointer.x, pointer.y - prevPointer.y);
        matrix.postTranslate(lastDisplacement.x, lastDisplacement.y);

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) {
            prevPointer = null;
        }

        postInvalidateOnAnimation();

        Log.d(TAG, "isInBounds: " + isInBounds(lastDisplacement.x, lastDisplacement.y) + "  " + lastDisplacement);


        return true;
    }

    public RectF drawingRect(){
        RectF rect = new RectF();
        matrix.mapRect(rect, layout);
        return rect;
    }

    public RectF drawingViewPort(){
        RectF rect = new RectF();
        matrix.mapRect(rect, actualViewPort);
        return rect;
    }

    public RectF targetViewPort(){
        return new RectF(Math.max(viewPort.left, layout.left), Math.max(viewPort.top, layout.top), Math.min(viewPort.right, layout.right), Math.min(viewPort.bottom, layout.bottom));
    }

    public boolean isInBounds(float dx, float dy){
        RectF actualLayout = drawingRect();
        RectF targetViewPort = targetViewPort();

        actualLayout.offset(dx, dy);

        Log.d(TAG, "drawingViewPort: " + drawingViewPort());
        Log.d(TAG, "isInBounds B: " + actualLayout);
        boolean xInBounds = actualLayout.left <= targetViewPort.left && actualLayout.right >= targetViewPort.right;
        boolean yInBounds = actualLayout.top <= targetViewPort.top && actualLayout.bottom >= targetViewPort.bottom;

        return xInBounds && yInBounds;
    }

    public static float clamp(float min, float value, float max){
        return Math.max(min, Math.min(value, max));
    }

    public void postScale() {
        postScale(mScaleDetector);
    }

    public void postScale(ScaleGestureDetector detector){
        float scaleBy = clampScaleFactor(detector.getScaleFactor());
        mScale *= scaleBy;
        matrix.postScale(scaleBy, scaleBy, detector.getFocusX(), detector.getFocusY());
    }

    private float clampScaleFactor(float scaleBy){
        return clampScaleFactor(mScale, scaleBy);
    }

    private float clampScaleFactor(float scale, float scaleBy){
        return clamp(minScale / scale, scaleBy, maxScale / scale);
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

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private boolean mDidFling = false;

        public boolean isFling(){
            return mDidFling;
        }

        public void reset(){
            mDidFling = false;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            float viewportPercetageToFling = 0.5f;
            boolean flingX = Math.abs(e2.getX() - e1.getX()) > viewPort.width() * viewportPercetageToFling;
            boolean flingY = Math.abs(e2.getY() - e1.getY()) > viewPort.height() * viewportPercetageToFling;
            mDidFling = mDidFling || flingX || flingY;
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }
    }
}