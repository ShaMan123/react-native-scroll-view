package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.view.MotionEvent;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public interface IGestureDetector {
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
        void zoomTo(RectF dst);
    }

    interface TranslateHelper {
        PointF getTopLeftMaxDisplacement();
        PointF getTopLeftMaxDisplacement(PointF distance);
        PointF getBottomRightMaxDisplacement();
        PointF getBottomRightMaxDisplacement(PointF distance);
        RectB clampOffset(PointF out, PointF distance, PointF offset);
        RectB clampOffset(PointF out, PointF distance);
        RectB canOffset = new RectB();
        boolean canScroll(PointF velocity);
    }

    interface GestureHelper extends OnMatrixChangeListener {


        //

        public void requestDisallowInterceptTouchEvent();


    }



    @Retention(RetentionPolicy.SOURCE)
    @IntDef({GestureDetectors.COMBINED_GESTURE_DETECTOR, GestureDetectors.MATRIX_GESTURE_DETECTOR})
    public static @interface GestureDetectors {
        int COMBINED_GESTURE_DETECTOR = 0;
        int MATRIX_GESTURE_DETECTOR = 1;
    }

/*
    class ScrollDirection {
        public static @interface Directions {
            Point LEFT = new Point(-1, 0);
            Point RIGHT = new Point(1, 0);
            Point UP = new Point(0, -1);
            Point DOWN = new Point(0, 1);
        }

        static Point merge(@Directions Point x, @Directions Point y){
            return new Point(x.x, y.y);
        }

        void g(){
            Directions.
        }
    }

*/
}
