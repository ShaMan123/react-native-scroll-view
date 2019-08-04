package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.ViewGroup;

import com.facebook.react.uimanager.ThemedReactContext;

public class RNZoomableScrollView extends ViewGroup {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    GestureEventManager mGestureManager;

    RNZoomableScrollView(ThemedReactContext context){
        super(context);
        mGestureManager = new GestureEventManager(context, this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mGestureManager.onDraw(canvas);
        super.onDraw(canvas);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //super.onLayout(changed, l, t, r, b);
        /*
        Rect out = new Rect();
        for (int i = 0; i < getChildCount() - 1; i++) {
            getChildAt(i).getHitRect(out);
            Log.d(TAG, "onChildLayout: " + i + "  " + out.toString());
        }
        */
        mGestureManager.setLayoutRect(l, t, r, b);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //if(super.onTouchEvent(event)) return true;
        mGestureManager.onTouchEvent(event);
        postInvalidateOnAnimation();
        return true;
    }
}
