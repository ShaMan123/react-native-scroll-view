package com.autodidact.rnscrollview;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;

import com.yalantis.ucrop.view.GestureCropImageView;

public class ZoomableImageView extends GestureCropImageView {
    public ZoomableImageView(Context context) {
        super(context);
        setImageURI(Uri.parse("https://cdn.lynda.com/course/483230/483230-636529267515404332-16x9.jpg"));
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }



}
