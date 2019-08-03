package io.autodidact.zoomage;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import androidx.annotation.RequiresApi;

import com.autodidact.R;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;

public class RNZoomView extends ViewGroup {
    public static String TAG = RNZoomView.class.getSimpleName();
    MatrixGestureDetector matrixGestureDetector;
    Matrix matrix = new Matrix();
    RectF layout = new RectF();
    RectF dst = new RectF();
    Rect mViewPort;

    RNZoomView(ThemedReactContext context){
        super(context);
        matrixGestureDetector = new MatrixGestureDetector(matrix, new MatrixGestureDetector.OnMatrixChangeListener() {
            @Override
            public void onChange(Matrix matrix) {
                matrix.mapRect(dst);
                postInvalidateOnAnimation();
            }
        }).setRotationEnabled(false);

        mViewPort = new MeasureUtility(context).getUsableViewPort();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if(super.onTouchEvent(event)) return true;
        requestDisallowInterceptTouchEvent(true);
        matrixGestureDetector.onTouchEvent(event);

        Log.d(TAG, "onTouchEvent: " + contains());
        return true;
    }

    public boolean contains(){
        return out(true).contains(targetViewPort(false));
    }

    public Matrix absMatrix(){
        Matrix m = new Matrix();
        m.preTranslate(mViewPort.left, mViewPort.top);
        m.preTranslate(layout.left, layout.top);
        m.postConcat(matrix);
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

    public RectF out(boolean relative){
        RectF src = new RectF(absLayout());
        if(relative) src.offsetTo(0, 0);
        absMatrix().mapRect(dst, src);
        return dst;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(new Matrix());
        RectF dst1 = out(true);
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
}
