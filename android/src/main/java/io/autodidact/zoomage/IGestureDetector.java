package io.autodidact.zoomage;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.MotionEvent;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IGestureDetector {
    public Matrix getMatrix();
    public boolean onTouchEvent(MotionEvent motionEvent);

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
        RectB clampOffset(PointF out, PointF distance, PointF offset);
        RectB clampOffset(PointF out, PointF distance);
        RectB canOffset = new RectB();
        boolean canScroll();
        public void requestDisallowInterceptTouchEvent();

        RectF getTransformedRect();
    }

    class RectB {
        boolean left;
        boolean top;
        boolean right;
        boolean bottom;

        RectB() {
            this(true);
        }

        RectB(boolean value){
            this(value, value, value, value);
        }

        RectB(RectB rect) {
            this(rect.left, rect.top, rect.right, rect.bottom);
        }

        RectB(boolean left, boolean top, boolean right, boolean bottom) {
            set(left, top, right, bottom);
        }

        void set() {
            set(true);
        }

        void set(boolean value){
            set(value, value, value, value);
        }

        void set(RectB rect) {
            set(rect.left, rect.top, rect.right, rect.bottom);
        }

        void set(boolean left, boolean top, boolean right, boolean bottom){
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        boolean some() {
            return left || top || right || bottom;
        }

        boolean every(){
            return every(true);
        }

        boolean every(boolean value) {
            return left == value && top == value && right == value && bottom == value;
        }

        @NonNull
        @Override
        public String toString() {
            return "RectB(" + left + ", " + top + ", " + right + ", " + bottom + ")";
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GestureDetectors.COMBINED_GESTURE_DETECTOR, GestureDetectors.MATRIX_GESTURE_DETECTOR})
    public static @interface GestureDetectors {
        int COMBINED_GESTURE_DETECTOR = 0;
        int MATRIX_GESTURE_DETECTOR = 1;
    }
}
