package io.autodidact.zoomablescrollview;

import android.view.View;

import androidx.annotation.StringDef;
import androidx.core.util.Pools;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Event extends com.facebook.react.uimanager.events.Event {
    public static interface OnScaleEvent {
        public void onScale(WritableMap eventData);
        public void onScaleBegin(WritableMap eventData);
        public void onScaleEnd(WritableMap eventData);
        public void onScroll(WritableMap eventData);
        //public WritableMap extractEventData(ScaleGestureDetector.OnScaleGestureListener scaleGestureListener);
    }

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({EventNames.ON_SCALE, EventNames.ON_SCALE_BEGIN, EventNames.ON_SCALE_END, EventNames.ON_SCROLL})
    public static @interface EventNames {
        String ON_SCALE_BEGIN = "onScrollBegin";
        String ON_SCALE = "onScroll";
        String ON_SCALE_END = "onScrollEnd";
        String ON_SCROLL = "onScroll";
    }

    private static final int TOUCH_EVENTS_POOL_SIZE = 7; // magic

    private static final Pools.SynchronizedPool<Event> EVENTS_POOL =
            new Pools.SynchronizedPool<>(TOUCH_EVENTS_POOL_SIZE);

    public static Event obtain(View view, @EventNames String eventName, WritableMap dataExtractor) {
        Event event = EVENTS_POOL.acquire();
        if (event == null) {
            event = new Event(eventName);
        }
        event.init(view, dataExtractor);
        return event;
    }

    private Event(@EventNames String eventName) {
        mEventName = eventName;
    }

    private WritableMap mExtraData;
    private String mEventName;
    private ReactContext mContext;

    private void init(View view, WritableMap dataExtractor) {
        super.init(view.getId());
        if (dataExtractor == null) {
            dataExtractor = Arguments.createMap();
        }
        dataExtractor.putInt("handlerTag", getViewTag());
        mExtraData = dataExtractor;
        mContext = ((ReactContext) view.getContext());
    }

    @Override
    public void onDispose() {
        mExtraData = null;
        EVENTS_POOL.release(this);
    }

    @Override
    public String getEventName() {
        return mEventName;
    }

    @Override
    public boolean canCoalesce() {
        // TODO: coalescing
        return false;
    }

    @Override
    public short getCoalescingKey() {
        // TODO: coalescing
        return 0;
    }

    @Override
    public void dispatch(RCTEventEmitter rctEventEmitter) {
        rctEventEmitter.receiveEvent(getViewTag(), getEventName(), mExtraData);
    }

    public void dispatch(){
        mContext
                .getJSModule(RCTEventEmitter.class)
                .receiveEvent(getViewTag(), getEventName(), mExtraData);
    }
}
