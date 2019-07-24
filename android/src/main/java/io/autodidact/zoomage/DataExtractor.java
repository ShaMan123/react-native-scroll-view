package io.autodidact.zoomage;

import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;

import javax.annotation.Nullable;

import static io.autodidact.zoomage.ZoomageViewGroup.TAG;

public class DataExtractor {
    private ScaleDataExtractor lastScaleDataExtractor;
    private TranslateDataExtractor lastTranslateDataExtractor;
    private @Nullable Zoomage handler;
    private long lastTranslateEventTime = -1;
    private long lastTranslateDownTime = -1;

    DataExtractor(Zoomage handler){
        this.handler = handler;
    }

    DataExtractor(){
        this.handler = null;
    }

    public WritableMap extractEventData(ScaleGestureDetector detector){
        ScaleDataExtractor dataExtractor = new ScaleDataExtractor(handler, detector);
        lastScaleDataExtractor = dataExtractor;
        return dataExtractor.extractEventData();
    }

    public WritableMap extractEventData(ScaleGestureDetector detector, ValueAnimator animation){
        ScaleDataExtractor dataExtractor =  new ScaleDataExtractor(handler, detector, animation);
        lastScaleDataExtractor = dataExtractor;
        return dataExtractor.extractEventData();
    }

    public WritableMap extractEventData(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY){
        if(e2.getDownTime() != lastTranslateDownTime){
            lastTranslateDownTime = e2.getDownTime();
            lastTranslateEventTime = -1;
        }
        if(lastTranslateEventTime == -1) lastTranslateEventTime = e2.getEventTime();
        long dt = e2.getEventTime() - lastTranslateEventTime;

        TranslateDataExtractor dataExtractor = new TranslateDataExtractor(handler, e1, e2, distanceX, distanceY, dt);
        lastTranslateDataExtractor = dataExtractor;
        return dataExtractor.extractEventData();
    }

    public WritableMap extractEventData(float scale, PointF translation, PointF velocity){
        JSScrollEventDataExtractor dataExtractor = new JSScrollEventDataExtractor(handler, scale, translation, velocity);
        return dataExtractor.extractEventData();
    }

    public WritableMap extractEventData(){
        JSScrollEventDataExtractor dataExtractor = new JSScrollEventDataExtractor(handler, lastScaleDataExtractor, lastTranslateDataExtractor);
        return dataExtractor.extractEventData();
    }

    public static class DataExtractorBase {
        public WritableMap extractEventData(@Nullable WritableMap eventData){
            if(eventData == null) {
                eventData = Arguments.createMap();
            }
            return eventData;
        }

        public WritableMap extractEventData(){
            return extractEventData(null);
        }
    }

    public static class ScaleDataExtractor extends DataExtractorBase {
        private Zoomage handler;
        private ScaleGestureDetector scaleDetector;
        private @Nullable ValueAnimator animation;
        public ScaleDataExtractor(Zoomage handler, ScaleGestureDetector scaleGestureDetector){
            this.handler = handler;
            scaleDetector = scaleGestureDetector;
            animation = null;
        }

        public ScaleDataExtractor(Zoomage handler, ScaleGestureDetector scaleGestureDetector, ValueAnimator animation){
            this.handler = handler;
            scaleDetector = scaleGestureDetector;
            this.animation = animation;
        }

        public float getScale(){
            return handler.getCurrentScale();
        }

        public float getFocalX(){
            return scaleDetector.getFocusX();
        }

        public float getFocalY(){
            return scaleDetector.getFocusY();
        }

        public float getVelocity(){
            long dt = animation == null ? scaleDetector.getTimeDelta() : animation.getCurrentPlayTime();
            long delta = dt <= 0 ? 1 : dt;
            return (handler.getCurrentScaleFactor() - handler.getPreviousScaleFactor()) / delta;
        }

        @Override
        public WritableMap extractEventData(@Nullable WritableMap eventData){
            eventData = super.extractEventData(eventData);
            eventData.putDouble("scale", getScale());
            eventData.putDouble("velocity", getVelocity());
            eventData.putDouble("focalX", PixelUtil.toDIPFromPixel(getFocalX()));
            eventData.putDouble("focalY", PixelUtil.toDIPFromPixel(getFocalY()));

            return eventData;
        }
    }

    public static class TranslateDataExtractor extends DataExtractorBase {
        private Zoomage handler;
        float translationX = 0;
        float translationY = 0;
        float x = 0;
        float y = 0;
        float absoluteX = 0;
        float absoluteY = 0;
        float velocityX = 0;
        float velocityY = 0;

        public TranslateDataExtractor(Zoomage handler, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, long dt){
            this.handler = handler;
            extractScrollEventData(e1, e2, distanceX, distanceY, dt);
        }

        public PointF getTranslate(){
            if(handler == null) throw new NullPointerException("handler was not provided to TranslateDataExtractor");
            float[] values = new float[9];
            handler.getMatrix().getValues(values);
            return new PointF(values[Matrix.MTRANS_X], values[Matrix.MTRANS_Y]);
        }

        private void extractScrollEventData(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, long dt) {
            PointF translation = handler.getCurrentTranslate();
            translationX = translation.x;    //e2.getX() - e1.getX();
            translationY = translation.y;   //e2.getY() - e1.getY();
            x = e2.getX();
            y = e2.getY();
            absoluteX = e2.getRawX();
            absoluteY = e2.getRawY();

            long delta = dt <= 0 ? 1 : dt;
            velocityX = distanceX / delta;
            velocityY = distanceY / delta;
        }


        @Override
        public WritableMap extractEventData(@Nullable WritableMap eventData) {
            eventData = super.extractEventData(eventData);

            eventData.putDouble("x", PixelUtil.toDIPFromPixel(x));
            eventData.putDouble("y", PixelUtil.toDIPFromPixel(y));
            eventData.putDouble("absoluteX", PixelUtil.toDIPFromPixel(absoluteX));
            eventData.putDouble("absoluteY", PixelUtil.toDIPFromPixel(absoluteY));
            eventData.putDouble("translationX", PixelUtil.toDIPFromPixel(translationX));
            eventData.putDouble("translationY", PixelUtil.toDIPFromPixel(translationY));
            eventData.putDouble("velocityX", PixelUtil.toDIPFromPixel(velocityX));
            eventData.putDouble("velocityY", PixelUtil.toDIPFromPixel(velocityY));

            return eventData;
        }

    }

    public static class JSScrollEventDataExtractor extends DataExtractorBase {
        private Zoomage handler;
        private int target;
        LayoutRect layoutMeasurement;
        LayoutRect contentSize;
        LayoutPoint contentOffset;
        LayoutRect contentInset;
        LayoutPoint velocity;
        float scale;

        JSScrollEventDataExtractor(Zoomage handler, @Nullable ScaleDataExtractor scaleDataExtractor, @Nullable TranslateDataExtractor translateDataExtractor){
            float scale = scaleDataExtractor != null ? scaleDataExtractor.getScale() : 1;
            PointF velocity = new PointF();
            PointF contentOffset = new PointF();
            if(translateDataExtractor != null){
                velocity.set(translateDataExtractor.velocityX, translateDataExtractor.velocityY);
                contentOffset.set(translateDataExtractor.translationX / scale, translateDataExtractor.translationY / scale);
            }
            else {
                if(scaleDataExtractor != null){
                    velocity.set(scaleDataExtractor.getVelocity(), scaleDataExtractor.getVelocity());
                }
                else{
                    velocity.set(0, 0);
                }
                contentOffset.set(0, 0);
            }

            init(handler, scale, contentOffset, velocity);
        }

        JSScrollEventDataExtractor(Zoomage handler, float scale, PointF contentOffset, PointF velocity){
            init(handler, scale, contentOffset, velocity);
        }

        private void init(Zoomage handler, float scale, PointF contentOffset, PointF velocity){
            this.handler = handler;
            ZoomageViewGroup view = handler.getView();
            target = view.getId();
            layoutMeasurement = LayoutRect.toDIPFromPixel(view.getWidth(), view.getHeight());
            contentSize = LayoutRect.toDIPFromPixel(view.getWidth() * scale, view.getHeight() * scale);
            this.contentOffset = new LayoutPoint(contentOffset);
            contentInset = LayoutRect.toDIPFromPixel(0, 0, 0, 0);
            this.scale = scale;
            this.velocity = new LayoutPoint(velocity);
        }

        @Override
        public WritableMap extractEventData(@Nullable WritableMap eventData) {
            eventData = super.extractEventData(eventData);
            eventData.putInt("target", target);
            eventData.putMap("layoutMeasurement", layoutMeasurement.getMap());
            eventData.putMap("contentSize", contentSize.getMap());
            eventData.putMap("contentOffset", contentOffset.getMap());
            eventData.putMap("contentInset", contentInset.getLegacyMap());
            eventData.putMap("velocity", velocity.getMap("velocity"));
            eventData.putDouble("zoomScale", scale);
            return eventData;
        }

        private static class LayoutPoint extends PointF {
            WritableMap getMap(){
                return getMap(null, null);
            }

            WritableMap getMap(String prefix){
                return getMap(prefix, null);
            }

            WritableMap getMap(@Nullable String prefix, @Nullable WritableMap map){
                if(map == null){
                    map = Arguments.createMap();
                }

                String prefixX = prefix == null ? "x" : prefix + "x".toUpperCase();
                String prefixY = prefix == null ? "y" : prefix + "y".toUpperCase();
                map.putDouble(prefixX, x);
                map.putDouble(prefixY, y);

                return map;
            }

            public LayoutPoint(PointF p){
                super(p.x, p.y);
            }
        }
        
        private static class LayoutRect extends RectF {
            static LayoutRect toDIPFromPixel(float width, float height){
                LayoutRect rect = new LayoutRect();
                rect.set(0, 0, PixelUtil.toDIPFromPixel(width), PixelUtil.toDIPFromPixel(height));
                return rect;
            }

            static LayoutRect toDIPFromPixel(float left, float top, float right, float bottom){
                LayoutRect rect = new LayoutRect();
                rect.set(PixelUtil.toDIPFromPixel(left), PixelUtil.toDIPFromPixel(top), PixelUtil.toDIPFromPixel(right), PixelUtil.toDIPFromPixel(bottom));
                return rect;
            }

            WritableMap getMap(){
                return getMap(false, null);
            }

            WritableMap getMap(boolean includePoint, @Nullable WritableMap map){
                if(map == null){
                    map = Arguments.createMap();
                }

                if(includePoint){
                    map.putDouble("x", left);
                    map.putDouble("y", top);
                }

                map.putDouble("width", width());
                map.putDouble("height", height());

                return map;
            }

            WritableMap getLegacyMap(){
                return getLegacyMap(null);
            }

            WritableMap getLegacyMap(@Nullable WritableMap map){
                if(map == null){
                    map = Arguments.createMap();
                }

                map.putDouble("left", left);
                map.putDouble("top", top);
                map.putDouble("right", right);
                map.putDouble("bottom", bottom);

                return map;
            }
        }
    }
}
