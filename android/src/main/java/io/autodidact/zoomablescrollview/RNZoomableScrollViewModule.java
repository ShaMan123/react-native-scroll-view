package io.autodidact.zoomablescrollview;

import android.graphics.RectF;

import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.NativeViewHierarchyManager;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.UIBlock;
import com.facebook.react.uimanager.UIManagerModule;

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

    @ReactMethod
    public void zoomToRect(final int tag, final ReadableMap rect, final boolean animated){
        final ReactApplicationContext context = getReactApplicationContext();
        UIManagerModule uiManager = context.getNativeModule(UIManagerModule.class);
        uiManager.addUIBlock(new UIBlock() {
            public void execute(NativeViewHierarchyManager nvhm) {
                RNZoomableScrollView view = (RNZoomableScrollView) nvhm.resolveView(tag);
                view.getGestureManager().zoomToRect(
                        PixelUtil.toPixelFromDIP(rect.getInt("x")),
                        PixelUtil.toPixelFromDIP(rect.getInt("y")),
                        PixelUtil.toPixelFromDIP(rect.getInt("width")),
                        PixelUtil.toPixelFromDIP(rect.getInt("height")),
                        animated
                );
            }
        });
    }
}
