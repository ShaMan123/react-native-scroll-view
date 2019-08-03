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
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.OverScroller;

import com.facebook.react.uimanager.ThemedReactContext;

public class RNZoomView1 extends ViewGroup implements ScaleGestureDetector.OnScaleGestureListener {
    public static String TAG = RNZoomView1.class.getSimpleName();
    private ScaleGestureDetector mScaleDetector;
    private float mScale = 1f;
    private float minScale = 0.75f;
    private float maxScale = 3f;
    private PointF displacement = new PointF(0, 0);
    private int doubleTapAnimationDuration = 300;
    RectF layout = new RectF();
    RectF viewPort = new RectF();
    Matrix matrix = new Matrix();
    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;

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
        viewPort.set(0, 0, mViewPort.width(), mViewPort.height());

        requestDisallowInterceptTouchEvent(true);
    }



    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        layout.set(left, top, right, bottom);
        Log.d(TAG, "onLayout: " + top);
        /*
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            setClipBounds(new Rect(0, top, right, bottom));
        }
        */
//super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(matrix);
        super.onDraw(canvas);
    }



    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;
        pointer.set(ev.getX(index), ev.getY(index));

        if(mScaleDetector.onTouchEvent(ev) || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
        }

        matrix.postTranslate(pointer.x - prevPointer.x, pointer.y - prevPointer.y);

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP) prevPointer = null;
        postInvalidateOnAnimation();
        //matrixGestureDetector.onTouchEvent(ev);
        /*
        //if(mScaleDetector.onTouchEvent(ev)) return true;
        if (ev.getPointerCount() > 2) {
            return false;
        }


        rect.set(Math.min(ev.getX(0), ev.getX(ev.getPointerCount() - 1)), Math.min(ev.getY(0), ev.getY(ev.getPointerCount() - 1)), Math.max(ev.getX(0), ev.getX(ev.getPointerCount() - 1)), Math.max(ev.getY(0), ev.getY(ev.getPointerCount() - 1)));
        if(rectPrev == null) rectPrev.set(rect.left, rect.top, rect.right, rect.bottom);
        Log.d(TAG, "rect: " + rect + "  ccc " + rectPrev);
        matrix.setRectToRect(rectPrev, rect, Matrix.ScaleToFit.CENTER);
        rectPrev.set(rect.left, rect.top, rect.right, rect.bottom);
        postInvalidateOnAnimation();


/*

        */
        //matrixGestureDetector.onTouchEvent(ev);
        //mScaleDetector.onTouchEvent(ev);

        Log.d(TAG, "rect: " + getActualLayout());


        return true;
        /*
        mGestureDetector.onTouchEvent(ev);
        mScaleDetector.onTouchEvent(ev);
        startAnimation(animationSet);
        return true;
        */
    }

    public RectF getActualLayout(){
        RectF rect = new RectF();
        matrix.mapRect(rect, layout);
        return rect;
        /*
        RectF actualLayout = new RectF(0, 0, layout.width() * mScale, layout.height() * mScale);
        actualLayout.offset((actualLayout.width() - layout.width()) * -0.5f, (actualLayout.height() - layout.height()) * -0.5f);
        actualLayout.offset(layout.left, layout.top);
        actualLayout.offset(displacement.x, displacement.y);
        return actualLayout;
        */
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
}