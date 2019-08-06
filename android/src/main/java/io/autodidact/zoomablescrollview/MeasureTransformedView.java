package io.autodidact.zoomablescrollview;

import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

public class MeasureTransformedView {
    private Rect mContentRect = new Rect();
    private Rect mClippingRect = new Rect();
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

    public void processLayout(Rect clippingRect, Rect LayoutRect){
        mClippingRect.set(clippingRect);
        mContentRect.set(LayoutRect);
        if(!mInitialized) mInitialized = true;
    }

    public Rect getViewPort(){
        return mViewPort;
    }

    public Rect getContentRect(){
        return mContentRect;
    }

    public RectF getAbsoluteContentRect(){
        validateState();
        return new RectF(mContentRect);
        //return fromRelativeToViewPortToAbsolute(new RectF(mContentRect));
    }

    public RectF getClippingRect(){
        //Log.d("ZoomableScroll", "getClippingRect: " + mClippingRect);
        return new RectF(mClippingRect);
        //return fromRelativeToViewPortToAbsolute(new RectF(mClippingRect));

    }

    public int getLayoutDirection(){
        return mLayoutDirection;
    }

    protected boolean isInitialized(){
        return mInitialized;
    }

    protected void validateState() {
        if(!mInitialized) throw new IllegalStateException("MeasureTransformedView has not been initialized yet");
    }


/*
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
*/
    public RectF fromRelativeToAbsolute(RectF src){
        validateState();
        RectF dst = new RectF(src);
        dst.offset(mViewPort.left, mViewPort.top);
        dst.offset(mContentRect.left, mContentRect.top);
        return dst;
    }

    public RectF fromRelativeToViewPortToAbsolute(RectF src){
        validateState();
        RectF dst = new RectF(src);
        dst.offset(mViewPort.left, mViewPort.top);
        return dst;
    }
}
