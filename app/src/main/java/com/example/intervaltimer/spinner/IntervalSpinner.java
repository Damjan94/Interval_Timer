package com.example.intervaltimer.spinner;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class IntervalSpinner extends androidx.appcompat.widget.AppCompatSpinner {

    public IntervalSpinner(@NonNull Context context) {
        super(context);
    }

    public IntervalSpinner(@NonNull Context context, int mode) {
        super(context, mode);
    }

    public IntervalSpinner(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public IntervalSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public IntervalSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode) {
        super(context, attrs, defStyleAttr, mode);
    }

    public IntervalSpinner(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int mode, Resources.Theme popupTheme) {
        super(context, attrs, defStyleAttr, mode, popupTheme);
    }

    @Override
    public Object getSelectedItem() {
        int position = getSelectedItemPosition();
        if (position == INVALID_POSITION || position == 0) {
            return null;
        }
        return Integer.parseInt(IntervalSpinnerAdapter.INTERVAL_TIME[position]);
    }
}
