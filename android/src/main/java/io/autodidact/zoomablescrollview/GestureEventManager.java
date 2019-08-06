package io.autodidact.zoomablescrollview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class GestureEventManager implements IGestureDetector.ScrollResponder {
    public static String TAG = RNZoomableScrollView.class.getSimpleName();
    private IGestureDetector combinedGestureDetector;
    private MatrixManager mMatrix;
    private ViewGroup mView;
    private ScaleGestureHelper scaleGestureHelper;
    private TranslateGestureHelper translateGestureHelper;
    private boolean mAppliedChange;
    private VelocityHelper mVelocityHelper;
    private boolean mIsHorizontal = false;

    GestureEventManager(RNZoomableScrollView view){
        mMatrix = new MatrixManager(view);
        mView = view;
        scaleGestureHelper = new ScaleGestureHelper(view.getReactContext(), mMatrix);
        translateGestureHelper = new TranslateGestureHelper(mMatrix);
        mVelocityHelper = new VelocityHelper();
    }

    public void setHorizontal() {
        mIsHorizontal = true;
    }

    /*
    private void setGestureDetector(ThemedReactContext context, @IGestureDetector.GestureDetectors int detectorType){
        if (detectorType == IGestureDetector.GestureDetectors.MATRIX_GESTURE_DETECTOR){
            combinedGestureDetector = new MatrixGestureDetector(this).setRotationEnabled(false);
        }
        else{
            combinedGestureDetector =
        }
    }
    */

    public ScaleGestureHelper getScaleGestureHelper() {
        return scaleGestureHelper;
    }

    public TranslateGestureHelper getTranslateGestureHelper() {
        return translateGestureHelper;
    }

    public MatrixManager getMatrix() {
        return mMatrix;
    }

    public MeasureTransformedView getMeasuringHelper(){
        return mMatrix.getMeasuringHelper();
    }

    @Override
    public void scrollTo(float x, float y, boolean animated) {
        mView.scrollTo((int) x, (int) y);
        //mMatrix.scrollTo(new PointF(x, y));
        //mView.postInvalidateOnAnimation();
    }

    @Override
    public void scrollBy(float x, float y, boolean animated) {
        mView.scrollBy((int) x, (int) y);
        /*
        mMatrix.scrollBy(new PointF(x, y));
        mView.postInvalidateOnAnimation();
        */
    }

    @Override
    public void scrollToEnd(boolean animated) {
        RectF clippingRect = mMatrix.getClippingRect();
        RectF layoutRect = mMatrix.getAbsoluteLayoutRect();

        Log.d(TAG, "scrollToEnd: " + layoutRect + "  " + clippingRect);
        mView.scrollTo((int) Math.max(layoutRect.width() - clippingRect.width(), 0), (int) Math.max(layoutRect.height() - clippingRect.height(), 0));

        /*
        mMatrix.scrollToEnd(mIsHorizontal);
        mView.postInvalidateOnAnimation();
        */
    }

    @Override
    public void zoomToRect(float x, float y, float width, float height, boolean animated) {
        //mMatrix.zoomToRect(x, y, width, height, animated);
        //mView.postInvalidateOnAnimation();
        zoomToRect(new RectF(x, y, x + width, y + height), animated);
    }

    @Override
    public void zoomToRect(RectF dst, boolean animated) {
        mMatrix.zoomToRect(dst, animated);
        mView.postInvalidateOnAnimation();
    }

    @Override
    public void flashScrollIndicators() {

        //mView.postInvalidateOnAnimation();
    }

    public void onLayout(boolean changed, int l, int t, int r, int b) {
        RectF clip;
        if(getMeasuringHelper().isInitialized()){
            clip = getMeasuringHelper().getClippingRect();
            mMatrix.preTranslate(-clip.left, -clip.top);
        }
        getMeasuringHelper().onLayout(changed, l, t, r, b);
        clip = getMeasuringHelper().getClippingRect();
        mMatrix.preTranslate(clip.left, clip.top);
    }

    protected void onDraw(Canvas canvas) {
        canvas.setMatrix(mMatrix.getAbsoluteMatrix());

        Paint p = new Paint();
        p.setColor(Color.BLUE);
        Rect rel = getMeasuringHelper().getLayout();
        rel.offsetTo(0, 0);
        canvas.drawRect(rel, p);
    }

    public boolean requestDisallowInterceptTouchEvent() {
        return mMatrix.canScroll(mVelocityHelper.getVelocity());
    }

    public boolean onTouchEvent(MotionEvent event) {
        mVelocityHelper.onTouchEvent(event);
        mAppliedChange = false;
        if(scaleGestureHelper.onTouchEvent(event)) {
            translateGestureHelper.resetTouchPointers();
            mAppliedChange = true;
        }
        if(translateGestureHelper.onTouchEvent(event)) {
            mAppliedChange = true;
        }

        return mAppliedChange;
    }

     /*

    public boolean requestDisallowInterceptTouchEvent() {
        return mRequestDisallowInterceptTouchEvent;
    }


    private PointF down = new PointF();
    private PointF crossThreshold = new PointF();
    private boolean mDisallowIntercept = true;
    Point direction = new Point();
    private int minThreshold = 50;
    private boolean mRequestDisallowInterceptTouchEvent = true;

    public boolean onTouchEvent(MotionEvent event) {
        mVelocityHelper.onTouchEvent(event);
        int action = event.getActionMasked();
        PointF pointer = new PointF(event.getX(), event.getY());

        if(action == MotionEvent.ACTION_DOWN) down.set(event.getX(), event.getY());
        boolean disallowIntercept = translateGestureHelper.canScroll(mVelocityHelper.getVelocity());

        mAppliedChange = false;
        if(scaleGestureHelper.onTouchEvent(event)) {
            translateGestureHelper.resetTouchPointers();
            mAppliedChange = true;
        }
        if(translateGestureHelper.onTouchEvent(event)) {
            mAppliedChange = true;
        }

        if(!disallowIntercept && mDisallowIntercept) {
            PointF velocity = mVelocityHelper.getVelocity();
            direction = VelocityHelper.sign(velocity);
            crossThreshold.set(pointer);
            crossThreshold.offset(direction.x * minThreshold, direction.y * minThreshold);
            //event.offsetLocation(down.x, down.y);
            Matrix m = new Matrix();
            m.setTranslate(-pointer.x, -pointer.y);
            event.transform(m);
        }
        else if(!disallowIntercept) {
            crossThreshold.offset(-pointer.x, -pointer.y);
            mRequestDisallowInterceptTouchEvent = !VelocityHelper.sign(crossThreshold).equals(direction);
        }

        mDisallowIntercept = disallowIntercept;
        if(action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mRequestDisallowInterceptTouchEvent = true;
        }

        return mAppliedChange;
    }
    */
}
