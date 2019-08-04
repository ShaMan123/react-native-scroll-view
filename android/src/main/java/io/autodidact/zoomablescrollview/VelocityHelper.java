package io.autodidact.zoomablescrollview;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class VelocityHelper {
    VelocityTracker mVelocityTracker;
    protected PointF mVelocity = new PointF(0, 0);
    protected PointF prevVelocity = new PointF();

    public void onTouchEvent(MotionEvent event) {
        prevVelocity.set(mVelocity);
        int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_DOWN){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1);
        mVelocity.set(mVelocityTracker.getXVelocity(), mVelocityTracker.getYVelocity());
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mVelocityTracker.recycle();
            mVelocity.set(0, 0);
            prevVelocity.set(mVelocity);
        }
    }

    public PointF getVelocity() {
        return mVelocity;
    }
}