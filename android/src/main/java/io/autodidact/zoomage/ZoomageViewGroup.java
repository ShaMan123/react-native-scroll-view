package io.autodidact.zoomage;

import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.view.ReactViewGroup;
import com.jsibbold.zoomage.AutoResetMode;

public class ZoomageViewGroup extends ReactViewGroup implements Event.OnScaleEvent {
    public static String TAG = "RNZoomageView";
    private Zoomage transformHandler;
    private ThemedReactContext mContext;
    private boolean dispatchScrollEvents = true;
    /*
    public RNZoomageManager viewManager;

    public ZoomageViewGroup(ThemedReactContext context, RNZoomageManager viewManager){
        this(context);
        this.viewManager = viewManager;
    }
*/
    public ZoomageViewGroup(ThemedReactContext context){
        super(context);
        mContext = context;
        transformHandler = new Zoomage(context, this);
        transformHandler.setZoomable(true);
        transformHandler.setDoubleTapToZoom(true);
        transformHandler.setTranslatable(true);
        transformHandler.setAutoResetMode(AutoResetMode.NEVER);
        transformHandler.setRestrictBounds(true);

        //setLayerType(LAYER_TYPE_SOFTWARE, null);

        //ViewConfiguration configuration = ViewConfiguration.get(context);
    }

    public Zoomage getTransformHandler() {
        return transformHandler;
    }

    @Override
    public void onScale(WritableMap eventData) {
        dispatchEvent(Event.EventNames.ON_SCALE, eventData);
    }

    @Override
    public void onScaleBegin(WritableMap eventData) {
        dispatchEvent(Event.EventNames.ON_SCALE_BEGIN, eventData);
    }

    @Override
    public void onScaleEnd(WritableMap eventData) {
        dispatchEvent(Event.EventNames.ON_SCALE_END, eventData);
    }

    @Override
    public void onScroll(WritableMap eventData) {
        dispatchEvent(Event.EventNames.ON_SCROLL, eventData);
    }

    public void setDispatchScrollEvents(boolean dispatchScrollEvents) {
        this.dispatchScrollEvents = dispatchScrollEvents;
    }

    private void dispatchEvent(@Event.EventNames String eventName, WritableMap eventData){
        WritableMap e = dispatchScrollEvents ? transformHandler.getDataExtractor().extractEventData() : eventData;
        Event ev = Event.obtain(this, eventName, e);
        ev.dispatch();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //return transformHandler.onTouchEvent(ev)? true: super.onTouchEvent(ev);
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return transformHandler.onTouchEvent(ev)? true: super.onTouchEvent(ev);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(transformHandler.getMatrix());
        super.onDraw(canvas);
    }
}
