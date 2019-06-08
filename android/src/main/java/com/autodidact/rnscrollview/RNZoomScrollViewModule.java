package com.autodidact.rnscrollview;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNZoomScrollViewModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNZoomScrollViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNZoomScrollViewModule";
    }
}
