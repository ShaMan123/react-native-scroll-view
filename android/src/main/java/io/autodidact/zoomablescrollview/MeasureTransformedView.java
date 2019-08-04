package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;

import com.facebook.react.uimanager.ThemedReactContext;

public class MeasureTransformedView implements IGestureDetector.MesaureTransformedView {
    private RectF mLayout = new RectF();
    private Rect mViewPort;
    Matrix matrix;

    interface MatrixProvider {
        Matrix getMatrix();
    }

    MeasureTransformedView(ThemedReactContext context, Matrix matrix){
        mViewPort = new MeasureUtility(context).getUsableViewPort();
        this.matrix = matrix;
    }

    public void setLayout(int l, int t, int r, int b){
        mLayout.set(l, t, r, b);
    }

    public boolean contains(){
        return getTransformedRect().contains(getClippingRect(false));
    }

    public Matrix getAbsoluteMatrix(){
        Matrix m = new Matrix();
        m.preTranslate(mViewPort.left, mViewPort.top);
        m.preTranslate(mLayout.left, mLayout.top);
        m.postConcat(matrix);
        return m;
    }

    public RectF getAbsoluteLayoutRect(){
        RectF out = new RectF(mLayout);
        out.offset(mViewPort.left, mViewPort.top);
        return out;
    }

    @Override
    public RectF getClippingRect() {
        return getClippingRect(false);
    }

    public RectF getClippingRect(boolean relative){
        RectF layoutRect = getAbsoluteLayoutRect();
        RectF out = new RectF(
                Math.max(mViewPort.left, layoutRect.left),
                Math.max(mViewPort.top, layoutRect.top),
                Math.min(mViewPort.right, layoutRect.right),
                Math.min(mViewPort.bottom, layoutRect.bottom)
        );
        if(relative) out.offsetTo(0, 0);
        return out;
    }

    public RectF getTransformedRect(/*boolean relative*/){
        RectF src = new RectF(getAbsoluteLayoutRect());
        RectF dst = new RectF();
        /*if(relative)*/ src.offsetTo(0, 0);
        getAbsoluteMatrix().mapRect(dst, src);
        return dst;
    }
}
