package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import com.autodidact.BuildConfig;
import com.facebook.react.uimanager.ThemedReactContext;

import static io.autodidact.zoomablescrollview.RNZoomableScrollView.TAG;

public class MeasureTransformedView {
    private Rect mLayout = new Rect();
    private Rect mClipLayout = new Rect();
    private Rect mViewPort;
    private boolean mInitialized = false;
    private int mLayoutDirection;
    private RNZoomableScrollView mView;
    MeasureUtility measureUtility;

    MeasureTransformedView(RNZoomableScrollView view){
        measureUtility = new MeasureUtility(view.getReactContext());
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
        return measureUtility.getLayoutDirection();
    }

    public boolean isRTL() {
        return measureUtility.isRTL();
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        mClipLayout.set(l, t, r, b);

        Rect out = new Rect();
        mView.getChildAt(0).getDrawingRect(out);
        out.offset(l, t);
        mLayout.set(out);

        mInitialized = true;

        if(BuildConfig.DEBUG){
            //Log.d(TAG, "onLayout: clip " + mClipLayout + ", layout " + mLayout);
        }
    }

    protected boolean isInitialized(){
        return mInitialized;
    }

    protected void validateState() {
        if(!mInitialized) throw new IllegalStateException("MeasureTransformedView has not been initialized yet");
    }

    /**
     * very touchy function
     * all logic uses this
     * @return
     */
    public RectF getContentRect(){
        validateState();
        RectF rect = new RectF(mLayout);
        rect.offsetTo(0, 0);
        return rect;
    }

    /**
     * very touchy function
     * all logic uses this
     * @return
     */
    public RectF getClippingRect() {
        validateState();
        RectF rect = new RectF(mClipLayout);
        rect.offsetTo(0, 0);
        return new RectF(rect);
    }

    @Deprecated
    public RectF fromRelativeToAbsolute(RectF src){
        validateState();
        RectF dst = new RectF(src);
        dst.offset(mViewPort.left, mViewPort.top);
        dst.offset(mLayout.left, mLayout.top);
        return dst;
    }

    @Deprecated
    public RectF fromRelativeToViewPortToAbsolute(RectF src){
        validateState();
        RectF dst = new RectF(src);
        dst.offset(mViewPort.left, mViewPort.top);
        return dst;
    }
}
