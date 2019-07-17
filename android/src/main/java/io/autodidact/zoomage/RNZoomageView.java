package io.autodidact.zoomage;

import android.view.ScaleGestureDetector;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.UIManagerModule;
import com.facebook.react.uimanager.events.EventDispatcher;
import com.jsibbold.zoomage.ZoomageView;

public class RNZoomageView extends ZoomageView {
    private float minScale = 0.75f;
    private float maxScale = 3f;

    public RNZoomageView(final ReactContext context){
        super(context);
        setRestrictBounds(true);
        setScaleRange(minScale, maxScale);
    }

    public float getMinimumScale(){
        return minScale;
    }

    public void setMinimumScale(float minimumScale){
        setScaleRange(minimumScale, maxScale);
    }

    public float getMaximumScale(){
        return maxScale;
    }

    public void setMaximumScale(float maximumScale){
        setScaleRange(minScale, maximumScale);
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {

        return super.onScaleBegin(detector);
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return super.onScale(detector);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        super.onScaleEnd(detector);
    }


    public class JSScaleEventPoster {
        String EVENT_NAME_ONSCALEBEGIN = "onScaleBegin";
        String EVENT_NAME_ONSCALE = "onScale";
        String EVENT_NAME_ONSCALEEND = "onScaleEnd";

        String mEventName;
        public JSScaleEventPoster(String eventName){
            mEventName = eventName;
        }

        private WritableMap getJSEvent(ScaleGestureDetector detector){
            WritableMap e = Arguments.createMap();
            e.putDouble("scale", detector.getScaleFactor());
            e.putDouble("velocity", (detector.getCurrentSpan() - detector.getPreviousSpan()) / detector.getTimeDelta());
            e.putDouble("focalX", detector.getFocusX());
            e.putDouble("focalY", detector.getFocusY());
            return e;
        }

        private boolean shouldPostEvent(){
            return true;
        }
/*
        public void handlePost(ScaleGestureDetector detector){
            if(shouldPostEvent()) {
                public void dispatch(RCTEventEmitter rctEventEmitter) {
                    rctEventEmitter.receiveEvent(getViewTag(), EVENT_NAME, mExtraData);
                }
                getJSEvent(detector)
            }
            EventDispatcher eventDispatcher = getReactApplicationContext()
                    .getNativeModule(UIManagerModule.class)
                    .getEventDispatcher();
        }
        */
    }

}
