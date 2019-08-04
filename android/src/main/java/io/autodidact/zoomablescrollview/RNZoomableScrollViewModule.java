package io.autodidact.zoomablescrollview;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNZoomableScrollViewModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNZoomableScrollViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNZoomableScrollViewModule";
    }
}
