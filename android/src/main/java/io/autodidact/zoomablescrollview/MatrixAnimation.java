package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.view.animation.Animation;
import android.view.animation.Transformation;

public class MatrixAnimation extends Animation {
    private PointF scaleStart;
    private PointF scaleEnd;
    private PointF translateStart;
    private PointF translateEnd;
    private boolean animated = true;

    MatrixAnimation(Matrix startMatrix, Matrix endMatrix){
        this(startMatrix, endMatrix, true);
    }

    MatrixAnimation(Matrix startMatrix, Matrix endMatrix, boolean animated){
        float[] a = new float[9];
        float[] b = new float[9];

        startMatrix.getValues(a);
        endMatrix.getValues(b);

        scaleStart = new PointF(a[Matrix.MSCALE_X], a[Matrix.MSCALE_Y]);
        scaleEnd =  new PointF(b[Matrix.MSCALE_X], b[Matrix.MSCALE_Y]);
        translateStart = new PointF(a[Matrix.MTRANS_X], a[Matrix.MTRANS_Y]);
        translateEnd = new PointF(b[Matrix.MTRANS_X], b[Matrix.MTRANS_Y]);

        setFillAfter(true);

        setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // TODO: 07/08/2019
                //dispatch event onMomentumStart
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO: 07/08/2019
                //dispatch event onMomentumEnd, if (animated) onScrollAnimationEnd
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        //setInterpolator(new DecelerateInterpolator());
    }

    protected MatrixAnimation setAnimated(boolean animated){
        this.animated = animated;
        setDuration(animated ? 300 : 0);
        return this;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        final Matrix matrix = t.getMatrix();
        PointF sFactor = new PointF(
                scaleEnd.x * interpolatedTime / scaleStart.x + 1 - interpolatedTime,
                scaleEnd.y * interpolatedTime / scaleStart.y + 1 - interpolatedTime);
        PointF tFactor = new PointF(
                (translateEnd.x - translateStart.x) * interpolatedTime,
                (translateEnd.y - translateStart.y) * interpolatedTime);

        //Log.d(TAG, "applyTransformation: " + sFactor + " " + tFactor);

        matrix.postScale(scaleStart.x, scaleStart.y, 0, 0);
        matrix.postScale(sFactor.x, sFactor.y, 0, 0);
        matrix.postTranslate(translateStart.x, translateStart.y);
        matrix.postTranslate(tFactor.x, tFactor.y);
    }

    @Override
    public void start() {
        setAnimated(true);
        super.start();
    }

    public void start(boolean animated){
        setAnimated(animated);
        super.start();
    }
}
