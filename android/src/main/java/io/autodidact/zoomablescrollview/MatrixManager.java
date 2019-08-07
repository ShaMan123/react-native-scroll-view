package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.LayoutDirection;
import android.view.ScaleGestureDetector;

public class MatrixManager extends Matrix implements IGesture.ScaleHelper, IGesture.TranslateHelper, IGesture.MesaureTransformedView, IGesture.ScrollResponder {
    private static final String TAG = RNZoomableScrollView.class.getSimpleName();

    protected RNZoomableScrollView mView;

    private float mScale = 1f;
    private float minScale = 0.75f;
    private float maxScale = 3f;

    private boolean mAppliedChange;
    private boolean mDidInitScale = false;

    private RectB canOffset = new RectB();
    private MeasureTransformedView mMeasurementProvider;
    private boolean mCenterContent = false;

    /**
     * Common
     */

    MatrixManager(RNZoomableScrollView view){
        super();
        mMeasurementProvider = new MeasureTransformedView(view);
        mView = view;
    }

    private MatrixManager(MatrixManager ancestor){
        super();
        mMeasurementProvider = ancestor.mMeasurementProvider;
        mView = ancestor.mView;
        set(ancestor);
    }

    public MatrixManager test(){
        return new MatrixManager(this);
    }

    public MeasureTransformedView getMeasuringHelper() {
        return mMeasurementProvider;
    }


    public void forceUpdateFromMatrix() {
        float[] values = new float[9];
        getValues(values);
        mScale = clampScale(values[Matrix.MSCALE_X]);

        computeScroll();
    }

    /**
     * Scroll Adapter interface
     *
     */

    /**
     *
     * uses {@link #scrollBy(float, float, boolean)}
     * @param x relative to view
     * @param y relative to view
     * @param animated
     *
     */
    @Override
    public void scrollTo(float x, float y, boolean animated) {
        RectF layoutRect = getAbsoluteLayoutRect();
        RectF transformedRect = getTransformedRect();
        layoutRect.offset(-x, -y);

        scrollBy(transformedRect.left - layoutRect.left, transformedRect.top - layoutRect.top, animated);
    }

    @Override
    public void scrollBy(float x, float y, boolean animated) {
        MatrixAnimationBuilder animationBuilder = new MatrixAnimationBuilder(animated);
        PointF clamped = clampOffset(new PointF(-x, -y));
        postTranslate(clamped.x ,clamped.y);
        animationBuilder.run();
        //mView.postInvalidateOnAnimation();
    }

    /**
     * uses {@link #scrollBy(float, float, boolean)}
     * @param animated
     */
    @Override
    public void scrollToEnd(boolean animated) {
        RectF clippingRect = getClippingRect();
        RectF layoutRect = getAbsoluteLayoutRect();
        RectF transformedRect = getTransformedRect();

        PointF relEnd = new PointF(
                transformedRect.width() - clippingRect.width(),
                transformedRect.height() - clippingRect.height()
        );
        layoutRect.offset(-relEnd.x, -relEnd.y);

        float scrollX = mView.isHorizontal() ?
                getMeasuringHelper().isRTL() ?
                        transformedRect.left - layoutRect.left :
                        transformedRect.right - layoutRect.right :
                0;
        float scrollY = mView.isHorizontal() ? 0 : transformedRect.top - layoutRect.top;
        scrollBy(scrollX, scrollY, animated);
    }

    @Override
    public void zoomToRect(float x, float y, float width, float height, boolean animated) {
        zoomToRect(new RectF(x, y, x + width, y + height), animated);
    }

    @Override
    public void zoomToRect(RectF dst, boolean animated) {
        MatrixAnimationBuilder animationBuilder = new MatrixAnimationBuilder(animated);
        RectF src = getAbsoluteLayoutRect();
        scrollTo(dst.left, dst.top, animated);
        float scale = clampScale(Math.min(src.width() / dst.width(), src.height() / dst.height()));
        RectF absDst = getMeasuringHelper().fromRelativeToAbsolute(dst);
        setScale(scale, scale, absDst.centerX(), absDst.centerY());
        mScale = scale;

        animationBuilder.run();

        //mView.postInvalidateOnAnimation();
    }

    @Override
    public void flashScrollIndicators() {

    }

    /**
     * Measuring Manager
     *
     */


    public boolean isContained(){
        return getTransformedRect().contains(getClippingRect());
    }

    public Matrix getRawViewMatrix(){
        Matrix out = new Matrix();
        //mView.setPivotX(0);
        //mView.setPivotY(0);
        out.preTranslate(-mView.getPivotX(), -mView.getPivotY());
        out.postConcat(mView.getMatrix());
        return out;
/*
        RectF mLayout = getAbsoluteLayoutRect();
        Matrix m = new Matrix();
        //m.preTranslate(mLayout.left, mLayout.top);

        Matrix m1 = new Matrix();
        RectF mapped = new RectF(mLayout);
        mapped.offsetTo(0, 0);
        mView.getMatrix().mapRect(mapped);
        Log.d(TAG, "getAbsoluteMatrix: " + mapped);
        m1.setRectToRect(mLayout, mapped, ScaleToFit.START);
        //m.postConcat(getRawViewMatrix());
        m.postConcat(m1);
        return m;
        */
    }

    public Matrix getAbsoluteMatrix(){
        Matrix m = new Matrix();
        m.postConcat(this);
        return m;
    }

    public RectF getAbsoluteLayoutRect(){
        return mMeasurementProvider.getAbsoluteLayoutRect();
    }

    @Override
    public RectF getClippingRect() {
        return mMeasurementProvider.getClippingRect();
    }

    public RectF getTransformedRect(){
        RectF src = new RectF(getAbsoluteLayoutRect());
        RectF dst = new RectF();
        src.offsetTo(0, 0);
        getAbsoluteMatrix().mapRect(dst, src);
        return dst;
    }


    /**
     * ScaleManager
     *
     */

    private void setInitialScale(float scale){
        if(mDidInitScale) return;
        mDidInitScale = true;
        mScale = clampScale(scale);
        postScale(mScale, mScale, 0, 0);
    }
        
    public float getScale() {
        return mScale;
    }

    @Override
    public float getMinimumScale() {
        return minScale;
    }

    public void setMinimumScale(float minimumScale) {
        minScale = minimumScale;
    }

    @Override
    public float getMaximumScale() {
        return maxScale;
    }

    public void setMaximumScale(float maximumScale) {
        maxScale = maximumScale;
    }

    public void setCenterContent(boolean centerContent) {
        mCenterContent = centerContent;
    }

    @Override
    public float clampScale(float scale) {
        return clamp(getMinimumScale(), scale, getMaximumScale());
    }

    @Override
    public float clampScaleFactor(float currentScale, float scaleBy) {
        return clamp(getMinimumScale() / currentScale, scaleBy, getMaximumScale() / currentScale);
    }

    private float clampScaleFactor(float scaleBy){
        return clampScaleFactor(mScale, scaleBy);
    }

    public void setScale(float scale, float focusX, float focusY, boolean animated){
        postScale(scale / mScale, focusX, focusY);
    }

    public void postScale(ScaleGestureDetector detector){
        float scaleBy = clampScaleFactor(detector.getScaleFactor());
        PointF focal = getFocalPoint(detector.getFocusX(), detector.getFocusY());
        postScale(scaleBy, focal.x, focal.y);
    }

    public PointF getFocalPoint(float x, float y){
        PointF out = new PointF(x, y);
        if(!isContained()) {
            RectF clippingRect = getClippingRect();
            if(mCenterContent){
                out.set(clippingRect.centerX(), clippingRect.centerY());
            }
            else {
                out.set(mMeasurementProvider.getLayoutDirection() == LayoutDirection.RTL ? clippingRect.right : clippingRect.left, clippingRect.top);
            }
        }
        return out;
    }

    public void postScale(float scaleBy, float focusX, float focusY){
        float outScaleBy = clampScaleFactor(scaleBy);
        mScale *= outScaleBy;
        if(outScaleBy != 1) mAppliedChange = true;
        postScale(outScaleBy, outScaleBy, focusX, focusY);
    }

    public void tapToScale(float x, float y){
        float min = Math.max(1, minScale);
        float max = maxScale;
        float m = (min + max) / 2;
        float scaleTo = mScale > m ? max : min;
        setScale(scaleTo, x, y, true);
    }

    /*
     * TranslateManager
     *
     */

    @Override
    public PointF getTopLeftMaxDisplacement() {
        RectF o = getTransformedRect();
        RectF clippingRect = getClippingRect();
        //return new PointF(Math.min(o.left - clippingRect.left, 0), Math.min(o.top - clippingRect.top, 0));
        return new PointF(o.left - clippingRect.left, o.top - clippingRect.top);
    }

    @Override
    public PointF getTopLeftMaxDisplacement(PointF distance) {
        PointF p = getTopLeftMaxDisplacement();
        p.offset(-distance.x, -distance.y);
        return p;
    }

    @Override
    public PointF getBottomRightMaxDisplacement() {
        RectF o = getTransformedRect();
        RectF clippingRect = getClippingRect();
        //return new PointF(Math.max(o.right - clippingRect.right, 0), Math.max(o.bottom - clippingRect.bottom, 0));
        return new PointF(o.right - clippingRect.right, o.bottom - clippingRect.bottom);
    }

    @Override
    public PointF getBottomRightMaxDisplacement(PointF distance) {
        PointF p = getBottomRightMaxDisplacement();
        p.offset(-distance.x, -distance.y);
        return p;
    }

    @Override
    public boolean canScroll(PointF velocity) {
        boolean scrollX = velocity.x == 0 ? true : velocity.x > 0 ? canOffset.left : canOffset.right;
        boolean scrollY = velocity.y == 0 ? true : velocity.y > 0 ? canOffset.top : canOffset.bottom;

        return Math.abs(velocity.x) > Math.abs(velocity.y) ? scrollX : scrollY;
        /*
        return new RectB(
                mVelocity.x >= 0 && canOffset.left,
                mVelocity.y >= 0 && canOffset.top,
                mVelocity.x <= 0 && canOffset.right,
                mVelocity.y <= 0 && canOffset.bottom
        );
        */
    }

    @Override
    public void computeScroll() {
        clampOffset(new PointF(0, 0));
    }

    @Override
    public PointF clampOffset(PointF offset) {
        RectB mCanOffset = new RectB(false);
        PointF out =  new PointF();
        out.set(offset);
        RectF clippingRect = getClippingRect();
        RectF transformed = getTransformedRect();
        transformed.offset(offset.x, offset.y);

        boolean wIsContained = transformed.width() <= clippingRect.width();
        boolean hIsContained = transformed.height() <= clippingRect.height();

        if(transformed.left < clippingRect.left && !wIsContained) mCanOffset.left = true;
        if(transformed.right > clippingRect.right && !wIsContained) mCanOffset.right = true;
        if(transformed.top < clippingRect.top && !hIsContained) mCanOffset.top = true;
        if(transformed.bottom > clippingRect.bottom && !hIsContained) mCanOffset.bottom = true;

        if(wIsContained) out.x = 0;
        else if(transformed.left > clippingRect.left) out.offset(clippingRect.left - transformed.left, 0);
        else if(transformed.right < clippingRect.right) out.offset(clippingRect.right - transformed.right, 0);

        if(hIsContained) out.y = 0;
        else if(transformed.top > clippingRect.top) out.offset(0, clippingRect.top - transformed.top);
        else if(transformed.bottom < clippingRect.bottom) out.offset(0, clippingRect.bottom - transformed.bottom);

        canOffset.set(mCanOffset);
        return out;
    }

    public RectB isOverScrolling() {
        RectB mIsOverscrolling = new RectB(false);
        RectF clippingRect = getClippingRect();
        RectF transformed = getTransformedRect();

        boolean wIsContained = transformed.width() <= clippingRect.width();
        boolean hIsContained = transformed.height() <= clippingRect.height();

        if(transformed.left > clippingRect.left && !wIsContained) mIsOverscrolling.left = true;
        if(transformed.right > clippingRect.right && !wIsContained) mIsOverscrolling.right = true;
        if(transformed.top < clippingRect.top && !hIsContained) mIsOverscrolling.top = true;
        if(transformed.bottom < clippingRect.bottom && !hIsContained) mIsOverscrolling.bottom = true;

        return mIsOverscrolling;
    }




    public static float clamp(float min, float value, float max){
        return Math.max(min, Math.min(value, max));
    }

    public MatrixAnimationBuilder getAnimationBuilder(){
        return new MatrixAnimationBuilder();
    }

    public MatrixAnimationBuilder getAnimationBuilder(boolean animated){
        return new MatrixAnimationBuilder(animated);
    }

    protected class MatrixAnimationBuilder {
        Matrix start;
        Matrix end;
        boolean animated;

        MatrixAnimationBuilder(){
            this(true);
        }

        MatrixAnimationBuilder(boolean animated){
            this.animated = animated;
            fillStart();
        }

        public void fillStart() {
            start = MatrixManager.this;
        }

        public void fillEnd() {
            end = MatrixManager.this;
        }

        public void run(){
            run(animated);
        }

        public void run(boolean animated){
            if(start == null) throw new NullPointerException("start matrix is empty");
            if(end == null) end = MatrixManager.this;
            MatrixAnimation animation = new MatrixAnimation(start, end)
                    .setAnimated(animated);
            mView.startAnimation(animation);
        }
    }

}
