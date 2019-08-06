package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.facebook.react.uimanager.ThemedReactContext;

import static io.autodidact.zoomablescrollview.RNZoomableScrollView.TAG;

public class MeasureTransformedView {
    private Rect mLayout = new Rect();
    private Rect mClipLayout = new Rect();
    private Rect mViewPort;
    private boolean mInitialized = false;
    private int mLayoutDirection;
    private RNZoomableScrollView mView;

    MeasureTransformedView(RNZoomableScrollView view){
        MeasureUtility measureUtility = new MeasureUtility(view.getReactContext());
        mViewPort = measureUtility.getUsableViewPort();
        mLayoutDirection = measureUtility.getLayoutDirection();
        mView = view;
    }

    public Rect getViewPort(){
        return mViewPort;
    }

    public Rect getLayout(){
        return mLayout;
    }

    public Rect getClipLayout(){
        return mClipLayout;
    }

    public int getLayoutDirection(){
        return mLayoutDirection;
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        mClipLayout.set(l, t, r, b);

        Rect out = new Rect();
        mView.getChildAt(0).getDrawingRect(out);
        out.offset(l, t);
        mLayout.set(out);

        mInitialized = true;

        Log.d(TAG, "onLayout: clip " + mClipLayout + ", layout " + mLayout);
    }

    protected boolean isInitialized(){
        return mInitialized;
    }

    protected void validateState() {
        if(!mInitialized) throw new IllegalStateException("MeasureTransformedView has not been initialized yet");
    }

    public RectF getAbsoluteLayoutRect(){
        validateState();
        return fromRelativeToViewPortToAbsolute(new RectF(mLayout));
    }

    public RectF getClippingRect() {
        validateState();
        RectF mRect = fromRelativeToViewPortToAbsolute(new RectF(mClipLayout));

        RectF out = new RectF(
                Math.max(mViewPort.left, mRect.left),
                Math.max(mViewPort.top, mRect.top),
                Math.min(mViewPort.right, mRect.right),
                Math.min(mViewPort.bottom, mRect.bottom)
        );
        return out;
    }

    public RectF fromRelativeToAbsolute(RectF src){
        validateState();
        RectF dst = new RectF(src);
        dst.offset(mViewPort.left, mViewPort.top);
        dst.offset(mLayout.left, mLayout.top);
        return dst;
    }

    public RectF fromRelativeToViewPortToAbsolute(RectF src){
        validateState();
        RectF dst = new RectF(src);
        dst.offset(mViewPort.left, mViewPort.top);
        return dst;
    }
}
