package io.autodidact.zoomablescrollview;

import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.util.Pools;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.uimanager.BaseViewManager;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.views.scroll.ScrollEventType;


import androidx.annotation.Nullable;
import androidx.core.util.Pools;
import com.facebook.infer.annotation.Assertions;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;
import com.facebook.react.uimanager.events.Event;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.annotation.Nonnull;

/** A event dispatched from a ScrollView scrolling. */
public class ScrollEventAdapter extends ScrollEvent {
    private float mZoomScale;

    public static class ScrollEventManager {
        private boolean shouldEmitOnScrollBeginDrag = false;
        private boolean shouldEmitOnScroll = false;
        private boolean shouldEmitOnScrollEndDrag = false;
        private boolean shouldEmitOnScrollMomentumBegin = false;
        private boolean shouldEmitOnScrollMomentumEnd = false;

        static ScrollEventManager obtain(){
            return new ScrollEventManager();
        }

        public boolean isRequested(ScrollEventType eventType) {
            switch (eventType){
                case SCROLL: return shouldEmitOnScroll;
                case BEGIN_DRAG: return shouldEmitOnScrollBeginDrag;
                case END_DRAG: return shouldEmitOnScrollEndDrag;
                case MOMENTUM_BEGIN: return shouldEmitOnScrollMomentumBegin;
                case MOMENTUM_END: return shouldEmitOnScrollMomentumEnd;
                default: throw new IllegalArgumentException("invalid ScrollEventType");
            }
        }

        public void setRequested(ScrollEventType eventType, boolean value) {
            switch (eventType){
                case SCROLL:
                    shouldEmitOnScroll = value;
                    break;
                case BEGIN_DRAG:
                    shouldEmitOnScrollBeginDrag = value;
                    break;
                case END_DRAG:
                    shouldEmitOnScrollEndDrag = value;
                    break;
                case MOMENTUM_BEGIN:
                    shouldEmitOnScrollMomentumBegin = value;
                    break;
                case MOMENTUM_END:
                    shouldEmitOnScrollMomentumEnd = value;
                    break;
                default: throw new IllegalArgumentException("invalid ScrollEventType");
            }
        }
    }

    private static final Pools.SynchronizedPool<ScrollEventAdapter> EVENTS_POOL =
            new Pools.SynchronizedPool<>(3);

    public static ScrollEventType getScrollEventType(MotionEvent event, boolean fake){
        int action = event.getActionMasked();
        if(fake){
            switch (action){
                case MotionEvent.ACTION_DOWN: return ScrollEventType.MOMENTUM_BEGIN;
                case MotionEvent.ACTION_UP: return ScrollEventType.MOMENTUM_END;
            }
        } else {
            switch (action){
                case MotionEvent.ACTION_DOWN: return ScrollEventType.BEGIN_DRAG;
                case MotionEvent.ACTION_MOVE: return ScrollEventType.SCROLL;
                case MotionEvent.ACTION_UP: return ScrollEventType.END_DRAG;
            }
        }
        return ScrollEventType.SCROLL;
    }

    public static ScrollEventAdapter obtain(
            int viewTag,
            ScrollEventType scrollEventType,
            int scrollX,
            int scrollY,
            float xVelocity,
            float yVelocity,
            int contentWidth,
            int contentHeight,
            int scrollViewWidth,
            int scrollViewHeight,
            float zoomScale
    ) {
        ScrollEventAdapter event = EVENTS_POOL.acquire();
        if (event == null) {
            event = new ScrollEventAdapter();
        }
        event.init(
                viewTag,
                scrollEventType,
                scrollX,
                scrollY,
                xVelocity,
                yVelocity,
                contentWidth,
                contentHeight,
                scrollViewWidth,
                scrollViewHeight,
                zoomScale);
        return event;
    }

    private void init(
            int viewTag,
            ScrollEventType scrollEventType,
            int scrollX,
            int scrollY,
            float xVelocity,
            float yVelocity,
            int contentWidth,
            int contentHeight,
            int scrollViewWidth,
            int scrollViewHeight,
            float zoomScale
    ) {
        super.init(
                viewTag,
                scrollEventType,
                scrollX,
                scrollY,
                xVelocity,
                yVelocity,
                contentWidth,
                contentHeight,
                scrollViewWidth,
                scrollViewHeight
        );

        mZoomScale = zoomScale;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), serializeEventData());
    }

    @Override
    protected WritableMap serializeEventData() {
        WritableMap map = super.serializeEventData();
        map.putDouble("zoomScale", mZoomScale);
        return map;
    }

    /*
    protected @Nullable WritableMap serializeEventData(){
        try {
            Method m = ScrollEvent.class.getDeclaredMethod("serializeEventData");
            m.setAccessible(true);
            WritableMap map = ((WritableMap) m.invoke(event));
            map.putDouble("zoomScale", mZoomScale);
            return map;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
    */
}