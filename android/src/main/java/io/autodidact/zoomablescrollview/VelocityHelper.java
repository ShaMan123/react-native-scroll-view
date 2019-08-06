package io.autodidact.zoomablescrollview;

import android.graphics.Point;
import android.graphics.PointF;
import android.view.MotionEvent;
import android.view.VelocityTracker;

public class VelocityHelper {
    private VelocityTracker mVelocityTracker;
    protected PointF mVelocity = new PointF(0, 0);
    protected PointF prevVelocity = new PointF();

    public void onTouchEvent(MotionEvent event) {
        prevVelocity.set(mVelocity);
        int action = event.getActionMasked();
        if(action == MotionEvent.ACTION_DOWN || mVelocityTracker == null){
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);
        mVelocityTracker.computeCurrentVelocity(1);
        mVelocity.set(mVelocityTracker.getXVelocity(), mVelocityTracker.getYVelocity());
        if(action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
            mVelocity.set(0, 0);
            prevVelocity.set(mVelocity);
        }
    }

    public PointF getVelocity() {
        return mVelocity;
    }

    public static PointF sign(Point p) {
        return sign(new PointF(p));
    }

    public static PointF sign(PointF p) {
        return new PointF(Math.signum(p.x), Math.signum(p.y));
    }
}
