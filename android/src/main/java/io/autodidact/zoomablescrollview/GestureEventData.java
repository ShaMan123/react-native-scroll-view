package io.autodidact.zoomablescrollview;

import android.graphics.Matrix;
import android.graphics.PointF;

import androidx.annotation.Nullable;

public class GestureEventData {
    PointF pointer;
    boolean changed;
    Matrix matrix;
    float scale;
    Displacement displacement;
    PointF velocity;

    GestureEventData(PointF pointer, boolean changed, Matrix matrix, float scale, Displacement displacement, PointF velocity){
        this.pointer = pointer;
        this.changed = changed;
        this.matrix = matrix;
        this.scale = scale;
        this.displacement = displacement;
        this.velocity = velocity;
    }

    interface Displacement {
        PointF clamped = null;
        PointF raw = null;
    }

    interface GestureEventListener {
        void onGestureEvent(GestureEventData current, @Nullable GestureEventData previous);
    }
}

