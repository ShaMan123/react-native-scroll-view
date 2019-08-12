package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;

import com.autodidact.BuildConfig;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.views.scroll.ScrollEventType;

import javax.annotation.Nullable;

public class GestureManager {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private IGesture combinedGestureDetector;
    private MatrixManager mMatrix;
    private RNZoomableScrollView mView;
    private ScaleGestureHelper scaleGestureHelper;
    private TranslateGestureHelper translateGestureHelper;
    private VelocityHelper mVelocityHelper;
    private MatrixManager.MatrixAnimationBuilder mAnimationBuilder;
    private ScrollEventAdapter.ScrollEventManager scrollEventManager;

    private boolean mAppliedChange;
    private boolean mPersistEventAppliedChange;

    GestureManager(RNZoomableScrollView view){
        mMatrix = new MatrixManager(view);
        mView = view;
        scaleGestureHelper = new ScaleGestureHelper(view.getReactContext(), mMatrix);
        translateGestureHelper = new TranslateGestureHelper(mMatrix);
        mVelocityHelper = new VelocityHelper();
        scrollEventManager = new ScrollEventAdapter.ScrollEventManager();
    }

    /*
    private void setGestureDetector(ThemedReactContext context, @IGesture.GestureDetectors int detectorType){
        if (detectorType == IGesture.GestureDetectors.MATRIX_GESTURE_DETECTOR){
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

    public MatrixManager getMatrix() {
        return mMatrix;
    }

    public MeasureTransformedView getMeasuringHelper(){
        return mMatrix.getMeasuringHelper();
    }

    /*
        Events
     */

    public void requestEvent(ScrollEventType eventType, boolean isRequested){
        scrollEventManager.setRequested(eventType, isRequested);
    }

    /**
     * computes offset/scroll in accordance to {@link #mVelocityHelper}
     * @return true if can scroll in the {@link MotionEvent} direction, false otherwise
     */
    public boolean canScroll(){
        return mMatrix.canScroll(mVelocityHelper.getVelocity());
    }

    public boolean isPointerInBounds(MotionEvent ev){
        return getMeasuringHelper().getClippingRect().contains(ev.getX(), ev.getY());
    }

    public boolean requestDisallowInterceptTouchEvent() {
        return canScroll() || (getTranslateGestureHelper().requestDisallowInterceptTouchEvent() && mPersistEventAppliedChange);
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        getMeasuringHelper().onLayout(changed, l, t, r, b);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            Rect rect = new Rect(l, t, r, b);
            rect.offsetTo(0, 0);
            //mView.setClipBounds(rect);
        }
        if(mMatrix.needsViewMatrixConcat()) mMatrix.DEV_postViewMatrix();
    }

    protected void onDraw(Canvas canvas) {
        canvas.clipRect(getMeasuringHelper().getClippingRect());

        if(BuildConfig.DEBUG){
            Paint p = new Paint();
            p.setColor(Color.BLUE);
            Rect rel = getMeasuringHelper().getLayout();
            rel.offsetTo(0, 0);
            //canvas.drawRect(rel, p);
        }
    }

    public boolean onInterceptTouchEvent(MotionEvent event){
        return true;
    }

    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        PointF p = new PointF(event.getX(), event.getY());
        if(action == MotionEvent.ACTION_DOWN) {
            mPersistEventAppliedChange = false;
        }

        mVelocityHelper.onTouchEvent(event);

        mAppliedChange = false;
        mAnimationBuilder = getMatrix().getAnimationBuilder(true);


        if(scaleGestureHelper.onTouchEvent(event)) {
            translateGestureHelper.resetTouchPointers();
            mAppliedChange = true;
        }
        if(translateGestureHelper.onTouchEvent(event)) {
            mAppliedChange = true;
        }

        if(mAppliedChange) {
            mAnimationBuilder.run();
        }

        if(mView.getParent() != null) mView.getParent().requestDisallowInterceptTouchEvent(requestDisallowInterceptTouchEvent());

        emitEvent(event, false);

        if (mAppliedChange) mPersistEventAppliedChange = true;

        return mPersistEventAppliedChange;
    }

    private void emitEvent(MotionEvent event, boolean fake){
        ScrollEventType eventType = ScrollEventAdapter.getScrollEventType(event, fake);

        if(!scrollEventManager.isRequested(eventType)) return;

        RectF clippingRect = getMeasuringHelper().getClippingRect();
        RectF contentRect = getMeasuringHelper().getContentRect();
        RectF transformedRect = mMatrix.getTransformedRect();
        PointF velocity = mVelocityHelper.getVelocity();
        float scale = mMatrix.getScale();

        ScrollEventAdapter adapter = ScrollEventAdapter.obtain(
                mView.getId(),
                eventType,
                (int) (-transformedRect.left),
                (int) (-transformedRect.top),
                velocity.x,
                velocity.y,
                (int) transformedRect.width(),
                (int) transformedRect.height(),
                (int) clippingRect.width(),
                (int) clippingRect.height(),
                scale
        );

        mView.getReactContext()
                .getNativeModule(UIManagerModule.class)
                .getEventDispatcher()
                .dispatchEvent(adapter);
    }

    /**
     * Command handling from JS
     *
     */









    /**
     * Tries to post {@link #mView} matrix to {@link #mMatrix}.
     * In case layout has not occurred yet the request will be handled after layout by {@link #onLayout(boolean, int, int, int, int)}
     * invoked after {@link RNZoomableScrollViewManager} received props
     */
    protected void tryPostViewMatrixConcat(@Nullable ReadableArray matrix){
        mMatrix.DEV_requestViewMatrixConcat(matrix);
    }
}
