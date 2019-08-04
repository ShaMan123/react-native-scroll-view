package io.autodidact.zoomablescrollview;

import android.graphics.PointF;
import android.view.MotionEvent;

public class ScrollHelper extends VelocityHelper {
    private PointF a;
    private PointF b;
    private  boolean mTracking = false;
    private boolean mInitOnNextEvent = false;

    public void startTracking() {
        mTracking = true;
        mInitOnNextEvent = true;
        a = new PointF();
        b = new PointF();
    }

    @Override
    public void onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if(mInitOnNextEvent) {
            a.set(event.getX(), event.getY());
            mInitOnNextEvent = false;
        }
        if(mTracking) b.set(event.getX(), event.getY());
    }

}
