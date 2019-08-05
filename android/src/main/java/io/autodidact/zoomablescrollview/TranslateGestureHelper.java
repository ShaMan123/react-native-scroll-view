package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;

import androidx.core.view.ViewCompat;

public class TranslateGestureHelper implements IGestureDetector.TranslateHelper, GestureEventData.Displacement {
    private static final String TAG = RNZoomableScrollView.class.getSimpleName();
    private RectB canOffset = new RectB();
    private Matrix matrix;
    private MeasureTransformedView measureTransformedView;

    TranslateGestureHelper(Matrix matrix, MeasureTransformedView measureTransformedView) {
        this.matrix = matrix;
        this.measureTransformedView = measureTransformedView;
    }

    @Override
    public PointF getTopLeftMaxDisplacement() {
        RectF o = measureTransformedView.getTransformedRect();
        RectF clippingRect = measureTransformedView.getClippingRect();
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
        RectF o = measureTransformedView.getTransformedRect();
        RectF clippingRect = measureTransformedView.getClippingRect();
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
    public boolean canScroll(PointF velocity) {
        boolean scrollX = velocity.x == 0 ? true : velocity.x > 0 ? canOffset.left : canOffset.right;
        boolean scrollY = velocity.y == 0 ? true : velocity.y > 0 ? canOffset.top : canOffset.bottom;

        return Math.abs(velocity.x) > Math.abs(velocity.y) ? scrollX : scrollY;
        /*
        return new RectB(
                mVelocity.x >= 0 && canOffset.left,
                mVelocity.y >= 0 && canOffset.top,
                mVelocity.x <= 0 && canOffset.right,
                mVelocity.y <= 0 && canOffset.bottom
        );
        */
    }

    @Override
    public void forceUpdateFromMatrix() {
        computeScroll();
    }

    @Override
    public void computeScroll() {
        clampOffset(new PointF(0, 0));
    }

    @Override
    public PointF clampOffset(PointF offset) {
        RectB mCanOffset = new RectB(false);
        PointF out =  new PointF();
        out.set(offset);
        RectF clippingRect = measureTransformedView.getClippingRect();
        RectF transformed = measureTransformedView.getTransformedRect();
        transformed.offset(offset.x, offset.y);

        boolean wIsContained = transformed.width() <= clippingRect.width();
        boolean hIsContained = transformed.height() <= clippingRect.height();

        if(transformed.left < clippingRect.left && !wIsContained) mCanOffset.left = true;
        if(transformed.right > clippingRect.right && !wIsContained) mCanOffset.right = true;
        if(transformed.top < clippingRect.top && !hIsContained) mCanOffset.top = true;
        if(transformed.bottom > clippingRect.bottom && !hIsContained) mCanOffset.bottom = true;

        if(wIsContained) out.x = 0;
        else if(transformed.left > clippingRect.left) out.offset(clippingRect.left - transformed.left, 0);
        else if(transformed.right < clippingRect.right) out.offset(clippingRect.right - transformed.right, 0);

        if(hIsContained) out.y = 0;
        else if(transformed.top > clippingRect.top) out.offset(0, clippingRect.top - transformed.top);
        else if(transformed.bottom < clippingRect.bottom) out.offset(0, clippingRect.bottom - transformed.bottom);

        canOffset.set(mCanOffset);
        return out;
    }

    public boolean scrollTo(PointF scrollTo) {
        PointF p = new PointF();
        RectF transformedRect = measureTransformedView.getTransformedRect();
        p.set(scrollTo);
        p.offset(-transformedRect.left, -transformedRect.top);
        return scrollBy(p);
    }

    public boolean scrollBy(PointF scrollBy) {
        PointF out = clampOffset(scrollBy);
        matrix.postTranslate(out.x, out.y);
        return out.length() > 0;
    }

    public void scrollToEnd(boolean horizontal) {
        PointF p = getBottomRightMaxDisplacement();
        if(horizontal){
            p.x = 0;
        }
        else {
            p.y = 0;
            if(measureTransformedView.getLayoutDirection() == ViewCompat.LAYOUT_DIRECTION_RTL){
                p.negate();
            }
        }
        scrollBy(p);
    }

    public PointF raw = new PointF();
    public PointF clamped = new PointF();
    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;
    private boolean mAppliedChange;
    private boolean mResetPrevPointer = false;

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;
        mAppliedChange = false;

        pointer.set(ev.getX(index), ev.getY(index));

        if(mResetPrevPointer || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
            if(mResetPrevPointer) mResetPrevPointer = false;
        }

        raw.set(pointer.x - prevPointer.x, pointer.y - prevPointer.y);
        clamped.set(clampOffset(raw));
        matrix.postTranslate(clamped.x, clamped.y);
        if(clamped.length() > 0) mAppliedChange = true;

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            prevPointer = null;
        }

        return mAppliedChange;
    }

    public void resetTouchPointers() {
        mResetPrevPointer = true;
    }
}
