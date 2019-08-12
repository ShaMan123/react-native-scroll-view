package io.autodidact.zoomablescrollview;

import android.graphics.PointF;
import android.util.Log;
import android.view.MotionEvent;

public class TranslateGestureHelper {
    private static final String TAG = RNZoomableScrollView.class.getSimpleName();
    private MatrixManager matrix;

    TranslateGestureHelper(MatrixManager matrix) {
        this.matrix = matrix;
    }

    private PointF pointer = new PointF();
    private PointF prevPointer = new PointF();
    private int prevPointerId = -1;
    private boolean mAppliedChange;
    private boolean mResetPrevPointer = false;

    private PointF mMovement = new PointF();
    private PointF mTranslation = new PointF();
    private PointF mPersistEventTotalMovement = new PointF();
    private PointF mPersistEventTotalAbsMovement = new PointF();
    private PointF mPersistEventTotalTranslation = new PointF();
    private boolean mPersistEventAppliedChange;

    public static float DEGREE_BORDER = 15;

    public static double fromPointToDegrees(PointF p){
        return p.x == 0 ? 0 : Math.atan(p.y / p.x) * 180 / Math.PI;
    }

    public double degree(){
        return Math.abs(fromPointToDegrees(mPersistEventTotalMovement));
    }

    public double offsetDegree(){
        return Math.abs(fromPointToDegrees(mPersistEventTotalTranslation));
    }

    public boolean isHorizontal(){
        return degree() < DEGREE_BORDER;
    }

    public boolean isVertical(){
        return degree() > 90 - DEGREE_BORDER;
    }

    public boolean isOffsettingHorizontally(){
        return offsetDegree() < DEGREE_BORDER && Math.abs(mPersistEventTotalTranslation.x) > 0;
    }

    public boolean isOffsettingVertically(){
        return offsetDegree() > 90 - DEGREE_BORDER && Math.abs(mPersistEventTotalTranslation.y) > 0;
    }

    /**
     *
     * use in combination with {@link #isInConsistent()}
     * @return whether the event's translation is similar to it's movement, good to understand if the event should be handled
     */
    public boolean isConsistent(){
        return (isHorizontal() && isOffsettingHorizontally()) || (isVertical() && isOffsettingVertically());
    }

    /**
     *
     * use in combination with {@link #isConsistent()}
     * @return whether the event is moving around inconsistently
     */
    public boolean isInConsistent(){
        return mPersistEventTotalAbsMovement.length() > mPersistEventTotalMovement.length();
    }

    public boolean requestDisallowInterceptTouchEvent() {
        return isConsistent() || isInConsistent();
    }

    public boolean isChangeApplied() {
        return mPersistEventAppliedChange;
    }

    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        int index = ev.getActionIndex();
        int pointerId = ev.getPointerId(index);
        if(prevPointerId == -1) prevPointerId = pointerId;
        mAppliedChange = false;

        pointer.set(ev.getX(index), ev.getY(index));

        if(action == MotionEvent.ACTION_DOWN){
            mPersistEventTotalMovement.set(0, 0);
            mPersistEventTotalAbsMovement.set(0, 0);
            mPersistEventTotalTranslation.set(0, 0);
            mPersistEventAppliedChange = false;
        }

        if(mResetPrevPointer || action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL || prevPointer == null || prevPointerId != pointerId) {
            if(prevPointer == null) prevPointer = new PointF();
            prevPointer.set(pointer);
            if(mResetPrevPointer) mResetPrevPointer = false;
        }

        mMovement.set(pointer.x - prevPointer.x, pointer.y - prevPointer.y);
        mAppliedChange = matrix.postTranslate(mTranslation, mMovement);
        mPersistEventTotalMovement.offset(mMovement.x, mMovement.y);
        mPersistEventTotalAbsMovement.offset(Math.abs(mMovement.x), Math.abs(mMovement.y));
        mPersistEventTotalTranslation.offset(mTranslation.x, mTranslation.y);

        prevPointer.set(pointer);
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP || action == MotionEvent.ACTION_CANCEL) {
            prevPointer = null;
        }

        if (mAppliedChange) mPersistEventAppliedChange = true;

        return mAppliedChange;
    }

    public void resetTouchPointers() {
        mResetPrevPointer = true;
    }
}
