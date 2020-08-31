package com.example.intervaltimer;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import top.defaults.colorpicker.ColorObserver;
import top.defaults.colorpicker.ColorPickerView;


public class FragmentColorPicker extends DialogFragment implements ColorObserver {

    private final IColorChanged m_callback;
    private final DynamicTheme m_theme;
    private ViewGroup m_container;

    private Button m_backgroundColorButton;
    private Button m_foregroundColorButton;
    private Button m_textColorButton;

    private ColorPickerView m_colorPicker;

    private @ColorInt
    int m_selectedColor;

    FragmentColorPicker(IColorChanged callback) {
        m_callback = callback;
        m_theme = (DynamicTheme) m_callback;
        m_selectedColor = -1;
    }

    @Override
    public void onColor(int color, boolean fromUser, boolean shouldPropagate) {
        m_selectedColor = color;
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return m_container = (ViewGroup) inflater.inflate(R.layout.fragment_color_picker, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        m_theme.apply((ViewGroup) m_container.getParent());

        m_backgroundColorButton = m_container.findViewById(R.id.background_color_button);
        m_foregroundColorButton = m_container.findViewById(R.id.foreground_color_button);
        m_textColorButton = m_container.findViewById(R.id.text_color_button);


        m_colorPicker = m_container.findViewById(R.id.color_picker);
        m_colorPicker.subscribe(this);

        {
            m_backgroundColorButton.setOnClickListener((view) -> {
                m_callback.backgroundColorChanged(m_selectedColor);
            });

            m_backgroundColorButton.setOnLongClickListener((view) -> {
                m_colorPicker.setInitialColor(m_callback.getBackgroundColor());
                m_colorPicker.reset();
                return true;
            });
        }

        {
            m_foregroundColorButton.setOnClickListener((view) -> {
                m_callback.foregroundColorChanged(m_selectedColor);
            });

            m_foregroundColorButton.setOnLongClickListener((view) -> {
                m_colorPicker.setInitialColor(m_callback.getForegroundColor());
                m_colorPicker.reset();
                return true;
            });
        }

        {
            m_textColorButton.setOnClickListener((view) -> {
                m_callback.textColorChanged(m_selectedColor);
            });

            m_textColorButton.setOnLongClickListener((view) -> {
                m_colorPicker.setInitialColor(m_callback.getTextColor());
                m_colorPicker.reset();
                return true;
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        m_theme.remove((ViewGroup) m_container.getParent());
        m_backgroundColorButton.setOnClickListener(null);
        m_foregroundColorButton.setOnClickListener(null);
        m_textColorButton.setOnClickListener(null);
        m_colorPicker.unsubscribe(this);
    }
}
