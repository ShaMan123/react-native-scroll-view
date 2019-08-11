package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import androidx.annotation.IntDef;

import com.facebook.react.uimanager.ThemedReactContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IGesture {
    public Matrix getMatrix();
    public boolean onTouchEvent(MotionEvent motionEvent);

    interface OnMatrixChangeListener {
        void onChange(Matrix matrix);
    }

    interface MesaureTransformedView {
        RectF getClippingRect();
        RectF getTransformedRect();
    }

    interface ScaleHelper {
        float getMinimumScale();
        float getMaximumScale();
        float clampScaleFactor(float currentScale, float scaleBy);
        float clampScale(float scale);
    }

    interface TranslateHelper {
        PointF getTopLeftMaxDisplacement();
        PointF getTopLeftMaxDisplacement(PointF distance);
        PointF getBottomRightMaxDisplacement();
        PointF getBottomRightMaxDisplacement(PointF distance);
        PointF clampOffset(PointF offset);
        void computeScroll();
        RectB canOffset = new RectB();
        boolean canScroll(PointF velocity);
    }

    interface ScrollResponder {
        void scrollTo(float x, float y, boolean animated);
        void scrollBy(float x, float y, boolean animated);
        void scrollToEnd(boolean animated);
        void zoomToRect(float x, float y, float width, float height, boolean animated);
        void zoomToRect(RectF rect, boolean animated);
        void flashScrollIndicators();
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GestureDetectors.COMBINED_GESTURE_DETECTOR, GestureDetectors.MATRIX_GESTURE_DETECTOR})
    public static @interface GestureDetectors {
        int COMBINED_GESTURE_DETECTOR = 0;
        int MATRIX_GESTURE_DETECTOR = 1;
    }

}
