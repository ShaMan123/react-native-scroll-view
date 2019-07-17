package io.autodidact.rnscrollview;

import android.view.MotionEvent;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.views.scroll.FpsListener;
import com.facebook.react.views.scroll.ReactScrollView;

public class ZoomScaleScrollView extends ReactScrollView {
    final static String TAG = "ZoomScaleScrollView";
    private Zoomage gestureHandler;

    public ZoomScaleScrollView(ReactContext context, @Nullable FpsListener fpsListener){
        super(context, fpsListener);
        gestureHandler = new Zoomage(context, this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return gestureHandler.onTouchEvent(ev)? true: super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return gestureHandler.onTouchEvent(ev)? true: super.onInterceptTouchEvent(ev);
    }
}
