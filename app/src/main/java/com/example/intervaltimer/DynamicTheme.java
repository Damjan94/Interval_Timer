package com.example.intervaltimer;

import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import top.defaults.colorpicker.ColorPickerView;

class DynamicTheme implements IColorChanged {

    private static final String BACKGROUND_COLOR_KEY = "background_color";
    private static final String TEXT_COLOR_KEY = "text_color";
    private static final String FOREGROUND_COLOR_KEY = "button_color";

    private Colors m_colors;
    private Window m_window;

    private HashMap<ViewParent, LinkedList<View>> m_viewMap;

    DynamicTheme(Colors defaultColors, Window window) {

        m_viewMap = new HashMap<>();

        m_colors = defaultColors;
        m_window = window;
    }

    private void populateViews(@NonNull ViewGroup container, @NonNull LinkedList<View> coloredView) {
        int childCount = container.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            View child = container.getChildAt(i);
            if (coloredView.contains(child)) {
                continue;
            }
            coloredView.add(child);
            if (child instanceof ColorPickerView) {
                continue; // as a special case, we don't want to colorize the color picker
            }

            if (child instanceof ViewGroup) {
                populateViews((ViewGroup) child, coloredView);
            }
        }
    }

    void apply(ViewGroup container) {
        if (container == null) {
            return;
        }

        LinkedList<View> coloredView = m_viewMap.get(container);
        if (coloredView == null) {
            coloredView = new LinkedList<>();
            m_viewMap.put(container, coloredView);
        }

        populateViews(container, coloredView);

        colorize();
    }

    void remove(View view) {
        if (view == null) {
            return;
        }

        ViewParent parent;
        if (view instanceof ViewParent) {
            parent = (ViewParent) view;
        } else {
            parent = view.getParent();
        }

        if (parent == null) {
            return;
        }
        m_viewMap.remove(parent);
    }

    void save(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(BACKGROUND_COLOR_KEY, m_colors.background.toArgb());
        preferencesEditor.putInt(FOREGROUND_COLOR_KEY, m_colors.foreground.toArgb());
        preferencesEditor.putInt(TEXT_COLOR_KEY, m_colors.text.toArgb());
    }

    void load(SharedPreferences preferences) {
        m_colors.background = Color.valueOf(preferences.getInt(BACKGROUND_COLOR_KEY, m_colors.background.toArgb()));
        m_colors.foreground = Color.valueOf(preferences.getInt(FOREGROUND_COLOR_KEY, m_colors.foreground.toArgb()));
        m_colors.text = Color.valueOf(preferences.getInt(TEXT_COLOR_KEY, m_colors.text.toArgb()));
    }

    private void colorize() {
        m_window.setStatusBarColor(m_colors.background.toArgb());
        for (Map.Entry<ViewParent, LinkedList<View>> item : m_viewMap.entrySet()) {
            LinkedList<View> coloredView = item.getValue();

            for (View view : coloredView) {

                if (view instanceof Button) {
                    Button button = (Button) view;
                    button.setBackgroundColor(m_colors.foreground.toArgb());
                    button.setHighlightColor(m_colors.background.toArgb());
                    button.setTextColor(m_colors.text.toArgb());

                } else if (view instanceof ProgressBar) {
                    ProgressBar progressBar = (ProgressBar) view;
                    progressBar.setProgressTintList(ColorStateList.valueOf(m_colors.foreground.toArgb()));

                } else if (view instanceof TextView) {
                    TextView textView = (TextView) view;
                    textView.setTextColor(m_colors.text.toArgb());
                } else {
                    view.setBackgroundColor(m_colors.background.toArgb());
                }
            }
        }
    }

    @Override
    public void backgroundColorChanged(int color) {
        m_colors.background = Color.valueOf(color);
        colorize();
    }

    @Override
    public void foregroundColorChanged(int color) {
        m_colors.foreground = Color.valueOf(color);
        colorize();
    }

    @Override
    public void textColorChanged(int color) {
        m_colors.text = Color.valueOf(color);
        colorize();
    }

    @Override
    public int getBackgroundColor() {
        return m_colors.background.toArgb();
    }

    @Override
    public int getForegroundColor() {
        return m_colors.foreground.toArgb();
    }

    @Override
    public int getTextColor() {
        return m_colors.text.toArgb();
    }

    static class Colors {
        Color background;
        Color text;
        Color foreground;
    }
}
