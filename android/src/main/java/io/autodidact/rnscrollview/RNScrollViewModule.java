package io.autodidact.rnscrollview;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;

public class RNScrollViewModule extends ReactContextBaseJavaModule {
    private final ReactApplicationContext reactContext;

    public RNScrollViewModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNScrollViewModule";
    }
}
