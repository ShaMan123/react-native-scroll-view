package io.autodidact.zoomablescrollview;

import android.util.Log;

import androidx.annotation.IntDef;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RNZoomableScrollViewManager extends ViewGroupManager<RNZoomableScrollView> {
    public static final String TAG = RNZoomableScrollView.class.getSimpleName();
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
        //view.getGestureManager().getMatrix().setInitialScale(value);
    }

    @ReactProp(name = "minimumZoomScale", defaultFloat = 0.75f)
    public void setMinimumZoomScale(RNZoomableScrollView view, float value){
        view.getGestureManager().getMatrix().setMinimumScale(value);
    }

    @ReactProp(name = "maximumZoomScale", defaultFloat = 3.0f)
    public void setMaximumZoomScale(RNZoomableScrollView view, @Nullable float value){
        view.getGestureManager().getMatrix().setMaximumScale(value);
    }

    @ReactProp(name = "centerContent", defaultBoolean = false)
    public void setCenterContent(RNZoomableScrollView view, boolean value){
        view.getGestureManager().getMatrix().setCenterContent(value);
    }

    @ReactProp(name = "horizontal", defaultBoolean = false)
    public void setHorizontal(RNZoomableScrollView view, boolean value){
        view.setHorizontal(value);
    }

    @ReactProp(name = "dispatchScrollEvents", defaultBoolean = true)
    public void setEventType(RNZoomableScrollView view, boolean value){
        //view.setDispatchScrollEvents(value);

    }

    @IntDef({Commands.SCROLL_TO, Commands.SCROLL_BY, Commands.ZOOM_TO_RECT, Commands.SCROLL_TO_END, Commands.FLASH_SCROLL_INDICATORS})
    @Retention(RetentionPolicy.SOURCE)
    @interface Commands {
        final int SCROLL_TO = 0;
        final int SCROLL_BY = 1;
        final int ZOOM_TO_RECT = 2;
        final int SCROLL_TO_END = 3;
        final int FLASH_SCROLL_INDICATORS = 4;
    }

    @Nullable
    @Override
    public Map<String, Integer> getCommandsMap() {
        return MapBuilder.of(
                "scrollTo", Commands.SCROLL_TO,
                "scrollBy", Commands.SCROLL_BY,
                "scrollToRect", Commands.ZOOM_TO_RECT,
                "scrollToEnd", Commands.SCROLL_TO_END,
                "flashScrollIndicators", Commands.FLASH_SCROLL_INDICATORS
        );
    }

    @Override
    public void receiveCommand(@Nonnull RNZoomableScrollView root, @Commands int commandId, @Nullable ReadableArray args) {
        float x, y, width, height;
        boolean animated = args.size() > 0 && args.getType(args.size() - 1) == ReadableType.Boolean ? animated = args.getBoolean(args.size() - 1) : true;
        Log.d(TAG, "receiveCommand: " + commandId + "  args: " + args);
        switch (commandId){
            case Commands.SCROLL_TO:
                x = PixelUtil.toPixelFromDIP(args.getDouble(0));
                y = PixelUtil.toPixelFromDIP(args.getDouble(1));
                root.getGestureManager().scrollTo(x, y, animated);
                break;
            case Commands.SCROLL_BY:
                x = PixelUtil.toPixelFromDIP(args.getDouble(0));
                y = PixelUtil.toPixelFromDIP(args.getDouble(1));
                root.getGestureManager().scrollBy(x, y, animated);
                break;
            case Commands.ZOOM_TO_RECT:
                Log.d(TAG, "ZOOM_TO_RECT: " + args.getType(0).name());
                if(args.getType(0) == ReadableType.Number) {
                    x = PixelUtil.toPixelFromDIP(args.getDouble(0));
                    y = PixelUtil.toPixelFromDIP(args.getDouble(1));
                    width = PixelUtil.toPixelFromDIP(args.getDouble(2));
                    height = PixelUtil.toPixelFromDIP(args.getDouble(3));
                }
                else if(args.getType(0) == ReadableType.Map) {
                    ReadableMap rect = args.getMap(0);
                    x = PixelUtil.toPixelFromDIP(rect.getDouble("x"));
                    y = PixelUtil.toPixelFromDIP(rect.getDouble("y"));
                    width = PixelUtil.toPixelFromDIP(rect.getDouble("width"));
                    height = PixelUtil.toPixelFromDIP(rect.getDouble("height"));
                }
                else throw new IllegalArgumentException("received bad args for zoomToRect");

                root.getGestureManager().zoomToRect(x, y, width, height, animated);
                break;
            case Commands.SCROLL_TO_END:
                root.getGestureManager().scrollToEnd(animated);
                break;
            case Commands.FLASH_SCROLL_INDICATORS:
                root.getGestureManager().flashScrollIndicators();
                break;
            default: throw new IllegalArgumentException(getName() + ": Command " + commandId + " not found");
        }
    }

    @Override
    public @Nullable
    Map getExportedCustomDirectEventTypeConstants() {
        return MapBuilder.of(
                Event.EventNames.ON_SCALE, MapBuilder.of("registrationName", Event.EventNames.ON_SCALE),
                Event.EventNames.ON_SCALE_BEGIN, MapBuilder.of("registrationName", Event.EventNames.ON_SCALE_BEGIN),
                Event.EventNames.ON_SCALE_END, MapBuilder.of("registrationName", Event.EventNames.ON_SCALE_END)
        );
    }
}
