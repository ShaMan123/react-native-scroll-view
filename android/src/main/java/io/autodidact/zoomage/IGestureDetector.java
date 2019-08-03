package io.autodidact.zoomage;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IGestureDetector {
    public Matrix getMatrix();
    public void onTouchEvent(MotionEvent motionEvent);

    interface OnMatrixChangeListener {
        void onChange(Matrix matrix);
    }

    interface GestureHelper extends OnMatrixChangeListener {
        float getMinimumScale();
        float getMaximumScale();
        float clampScaleFactor(float currentScale, float scaleBy);
        float clampScale(float scale);
        void zoomTo(RectF dst);

        RectF getClippingRect();
        PointF getTopLeftMaxDisplacement();
        PointF getTopLeftMaxDisplacement(PointF distance);
        PointF getBottomRightMaxDisplacement();
        PointF getBottomRightMaxDisplacement(PointF distance);
        PointF clampOffset(PointF distance, PointF offset);
        PointF clampOffset(PointF distance);

        RectF getTransformedRect();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GestureDetectors.COMBINED_GESTURE_DETECTOR, GestureDetectors.MATRIX_GESTURE_DETECTOR})
    public static @interface GestureDetectors {
        int COMBINED_GESTURE_DETECTOR = 0;
        int MATRIX_GESTURE_DETECTOR = 1;
    }
}
