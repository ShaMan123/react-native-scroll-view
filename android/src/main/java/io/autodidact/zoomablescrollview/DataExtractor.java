package io.autodidact.zoomablescrollview;

public class DataExtractor {
    /*
    private ScaleDataExtractor lastScaleDataExtractor;
    private TranslateDataExtractor lastTranslateDataExtractor;
    private long lastTranslateEventTime = -1;
    private long lastTranslateDownTime = -1;

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
        JSScrollEventDataExtractor dataExtractor = new JSScrollEventDataExtractor(lastScaleDataExtractor, lastTranslateDataExtractor);
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
        private ScaleGestureDetector scaleDetector;
        private @Nullable ValueAnimator animation;
        public ScaleDataExtractor(ScaleGestureDetector scaleGestureDetector){
            scaleDetector = scaleGestureDetector;
            animation = null;
        }

        public ScaleDataExtractor(ScaleGestureDetector scaleGestureDetector, ValueAnimator animation){
            scaleDetector = scaleGestureDetector;
            this.animation = animation;
        }

        public float getScale(){
            return null;
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
        float translationX = 0;
        float translationY = 0;
        float x = 0;
        float y = 0;
        float absoluteX = 0;
        float absoluteY = 0;
        float velocityX = 0;
        float velocityY = 0;

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
    */
}
