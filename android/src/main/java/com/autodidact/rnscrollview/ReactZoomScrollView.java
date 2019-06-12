package com.autodidact.rnscrollview;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.views.scroll.ReactScrollView;
import com.facebook.react.views.view.ReactViewGroup;

public class ReactZoomScrollView extends ReactScrollView {
    private ZoomableView wrapper;
    private ThemedReactContext mContext;
    public ReactZoomScrollView(ThemedReactContext context){
        super(context);
        mContext = context;
        wrapper = new ZoomableView(context);

        ReactViewGroup container = (ReactViewGroup)(super.getChildAt(0));
        if(container != null) container.addView(wrapper);
        else super.addView(wrapper);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return super.onTouchEvent(ev);
    }

    /*
    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        // This will get called for every overload of addView so there is no need to override every method.
        //ZoomableView wrapper = new ZoomableView(mContext);
        wrapper.addView(child, index, params);
        //super.addView(wrapper, index, params);
    }

    @Override
    public void onChildViewAdded(View parent, View child) {
        Log.d("ScrollV", "onChildViewAdded: " + child.toString() + "   " + parent.toString());
        super.onChildViewAdded(parent, child);
    }

    @Override
    public void removeView(View view) {
        wrapper.removeView(view);
    }

    @Override
    public void removeViewAt(int index) {
        wrapper.removeViewAt(index);
    }
/*
    @Override
    public void addView(View child) {
        wrapper.addView(child);
    }

    @Override
    public void addView(View child, int index) {
        wrapper.addView(child, index);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        wrapper.addView(child, params);
    }

    @Override
    public void addView(View child, int width, int height) {
        wrapper.addView(child, width, height);
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        wrapper.addView(child, index, params);
    }

    @Override
    public void removeAllViews() {
        wrapper.removeAllViews();
    }

    @Override
    public void removeView(View view) {
        wrapper.removeView(view);
    }
    */
}
