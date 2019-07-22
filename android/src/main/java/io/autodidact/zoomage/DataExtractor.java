package io.autodidact.zoomage;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.PixelUtil;

import javax.annotation.Nullable;

import static io.autodidact.zoomage.ZoomageViewGroup.TAG;

public class DataExtractor {
    public static WritableMap extractEventData(Zoomage handler, ScaleGestureDetector detector){
        return new ScaleDataExtractor(handler, detector).extractEventData();
    }

    public static WritableMap extractEventData(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, long dt){
        return new TranslateDataExtractor(e1, e2, distanceX, distanceY, dt).extractEventData();
    }

    public static class ScaleDataExtractor {
        private Zoomage handler;
        private ScaleGestureDetector scaleDetector;
        public ScaleDataExtractor(Zoomage handler, ScaleGestureDetector scaleGestureDetector){
            this.handler = handler;
            scaleDetector = scaleGestureDetector;
        }

        public float getScale(){
            float[] values = new float[9];
            handler.getMatrix().getValues(values);
            return values[Matrix.MSCALE_X];
        }

        public float getFocalX(){
            return scaleDetector.getFocusX();
        }

        public float getFocalY(){
            return scaleDetector.getFocusY();
        }

        public float getVelocity(){
            long dt = scaleDetector.getTimeDelta();
            long delta = dt <= 0 ? 1 : dt;
            return (handler.getCurrentScaleFactor() - handler.getPreviousScaleFactor()) / delta;
        }

        public WritableMap extractEventData(){
            return extractEventData(null);
        }

        public WritableMap extractEventData(@Nullable WritableMap eventData){
            if(eventData == null) {
                eventData = Arguments.createMap();
            }
            eventData.putDouble("scale", getScale());
            eventData.putDouble("velocity", getVelocity());
            eventData.putDouble("focalX", PixelUtil.toDIPFromPixel(getFocalX()));
            eventData.putDouble("focalY", PixelUtil.toDIPFromPixel(getFocalY()));

            Log.d(TAG, "extractEventData: " + eventData.toString());

            return eventData;
        }
    }

    public static class TranslateDataExtractor {
        private Zoomage handler;
        float translationX;
        float translationY;
        float x;
        float y;
        float absoluteX;
        float absoluteY;
        float velocityX;
        float velocityY;

        public TranslateDataExtractor(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, long dt){
            this.handler = null;
            extractScrollEventData(e1, e2, distanceX, distanceY, dt);
        }

        public TranslateDataExtractor(@Nullable Zoomage handler, MotionEvent e1, MotionEvent e2, float distanceX, float distanceY, long dt){
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
            translationX = e2.getX() - e1.getX();
            translationY = e2.getY() - e1.getY();
            x = e2.getX();
            y = e2.getY();
            absoluteX = e2.getRawX();
            absoluteY = e2.getRawY();

            long delta = dt <= 0 ? 1 : dt;
            velocityX = distanceX / delta;
            velocityY = distanceY / delta;
        }

        public WritableMap extractEventData(){
            return extractEventData(null);
        }

        public WritableMap extractEventData(@Nullable WritableMap eventData) {
            if(eventData == null) {
                eventData = Arguments.createMap();
            }

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
}
