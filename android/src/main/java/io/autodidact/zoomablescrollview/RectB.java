package io.autodidact.zoomablescrollview;

import androidx.annotation.NonNull;

public class RectB {
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
