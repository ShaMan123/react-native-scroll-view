package com.autodidact.rnscrollview;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.views.scroll.FpsListener;
import com.facebook.react.views.scroll.ReactScrollView;
import com.facebook.react.views.scroll.ReactScrollViewManager;
import com.facebook.react.views.view.ReactViewGroup;
import com.facebook.react.views.view.ReactViewManager;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class RNZoomScrollViewManager extends ReactScrollViewManager {
    protected @Nullable FpsListener mFpsListener = null;
    private ReactScrollViewManager scrollViewManager;
    private ReactScrollView scrollView;
    private ReactViewGroup g;

    @Override
    public String getName() {
        return "RNZoomScrollView";
    }


    public RNZoomScrollViewManager(@Nullable FpsListener fpsListener) {
        super(fpsListener);
        mFpsListener = fpsListener;
    }

    public ZoomableView createViewInstance(ThemedReactContext context) {
        //scrollViewManager.createViewInstance(context);

        g = new ReactViewGroup(context);
        ZoomableView v = new ZoomableView(context);
        v.addView(g);
        return v;
    }

    @Override
    public void addView(ReactScrollView parent, View child, int index) {
        g.addView(child, index);
    }
/*
    @Override
    public void addViews(ReactScrollView parent, List<View> views) {
        Log.d("ScrollV", "addViews: " + parent.toString() + "    child:  " + views.toString());
        super.addViews(g, views);
    }

    /*
                        @Override
                        public ReactScrollView createViewInstance(ThemedReactContext context) {
                            return new ReactZoomScrollView(context);
                        }

                    /*
                        @ReactProp(name = PROPS_MATH_TEXT)
                        public void setMathText(RNMathView viewContainer, String text) {
                            //String r = text.getString(0).replaceAll("###", "\\\\");
                            viewContainer.setText(text);
                        }
                    */

}
