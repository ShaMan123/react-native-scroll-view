package io.autodidact;

import android.content.Context;
import android.util.AttributeSet;

import com.yalantis.ucrop.view.GestureCropImageView;

public class ZoomableImageView extends GestureCropImageView {
    public ZoomableImageView(Context context) {
        super(context);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
