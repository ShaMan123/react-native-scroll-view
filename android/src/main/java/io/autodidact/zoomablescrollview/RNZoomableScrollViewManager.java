package io.autodidact.zoomablescrollview;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RNZoomableScrollViewManager extends ViewGroupManager<RNZoomableScrollView> {
    @Nonnull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    public RNZoomableScrollViewManager(){
        super();
    }

    @Nonnull
    @Override
    protected RNZoomableScrollView createViewInstance(@Nonnull ThemedReactContext reactContext) {
        return new RNZoomableScrollView(reactContext);
    }

    @ReactProp(name = "zoomScale", defaultFloat = 1f)
    public void setZoomScale(RNZoomableScrollView view, float value){
        //view.getGestureManager().getScaleGestureHelper().setInitialScale(value);
    }

    @ReactProp(name = "minimumZoomScale", defaultFloat = 0.75f)
    public void setMinimumZoomScale(RNZoomableScrollView view, float value){
        view.getGestureManager().getScaleGestureHelper().setMinimumScale(value);
    }

    @ReactProp(name = "maximumZoomScale", defaultFloat = 3.0f)
    public void setMaximumZoomScale(RNZoomableScrollView view, @Nullable float value){
        view.getGestureManager().getScaleGestureHelper().setMaximumScale(value);
    }

    @ReactProp(name = "dispatchScrollEvents", defaultBoolean = true)
    public void setEventType(RNZoomableScrollView view, boolean value){
        //view.setDispatchScrollEvents(value);
    }

    @Override
    public @Nullable
    Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                Event.EventNames.ON_SCALE,
                MapBuilder.of("registrationName", Event.EventNames.ON_SCALE),
                Event.EventNames.ON_SCALE_BEGIN,
                MapBuilder.of("registrationName", Event.EventNames.ON_SCALE_BEGIN),
                Event.EventNames.ON_SCALE_END,
                MapBuilder.of("registrationName", Event.EventNames.ON_SCALE_END)
        );
    }
}
