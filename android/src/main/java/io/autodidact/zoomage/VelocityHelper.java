package io.autodidact.zoomage;

import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class VelocityHelper {
    VelocityTracker mVelocityTracker;
    private PointF mVelocity = new PointF(0, 0);

    public void onTouchEvent(MotionEvent event) {
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
        }
    }

    public PointF getVelocity() {
        return mVelocity;
    }
}
