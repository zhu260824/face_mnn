package com.zl.demo.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public abstract class RectView extends View {
    public RectView(Context context) {
        super(context);
    }

    public RectView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public RectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public abstract void init();

    public abstract void setRect(int left, int top, int right, int bottom);

    public abstract void setRect(Rect rect);

    public abstract void setTip(String tip);
}
