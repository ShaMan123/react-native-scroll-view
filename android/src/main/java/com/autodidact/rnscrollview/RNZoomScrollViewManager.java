package com.autodidact.rnscrollview;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.Map;

public class RNZoomScrollViewManager extends SimpleViewManager<ZoomableView> {
    private static final String PROPS_MATH_TEXT = "text";
    private static final String PROPS_MATH_ENGINE = "mathEngine";
    private static final String PROPS_NATIVE_PROPS = "nativeProps";
    private static final String PROPS_VERTICAL_SCROLL = "verticalScroll";
    private static final String PROPS_HORIZONTAL_SCROLL = "horizontalScroll";
    private static final String PROPS_SHOW_SCROLLBAR_DELAY = "showScrollBarDelay";
    private static final String PROPS_FONT_COLOR = "fontColor";
    private static final String PROPS_FONT_SHRINK = "fontShrink";
    private static final String PROPS_FLEX_WRAP = "flexWrap";
    private static final String PROPS_SCAELS_TO_FIT = "scalesToFit";

    private static final String PROPS_MATH_ENGINE_KATEX = "KATEX";
    private static final String PROPS_MATH_ENGINE_MATHJAX = "MATHJAX";
    private static final String PROPS_FLEX_WRAP_WRAP = "wrap";

    @Override
    public String getName() {
        return "RNZoomScrollView";
    }

    @Override
    protected ZoomableView createViewInstance(ThemedReactContext context) {
        ZoomableView view = new ZoomableView(context);
        return view;
    }

/*
    @ReactProp(name = PROPS_MATH_TEXT)
    public void setMathText(RNMathView viewContainer, String text) {
        //String r = text.getString(0).replaceAll("###", "\\\\");
        viewContainer.setText(text);
    }
*/
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "topChange",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onChange")))
                .build();
    }
}
