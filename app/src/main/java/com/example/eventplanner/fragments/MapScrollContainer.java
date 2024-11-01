package com.example.eventplanner.fragments;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class MapScrollContainer extends FrameLayout {

    public MapScrollContainer(Context context) {
        super(context);
    }

    public MapScrollContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapScrollContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // When the user touches the map, request the parent to not intercept touch events
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        return super.dispatchTouchEvent(ev);
    }
}

