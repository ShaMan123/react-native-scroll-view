package io.autodidact.zoomage;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNZoomageModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNZoomageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNZoomageModule";
    }
}
