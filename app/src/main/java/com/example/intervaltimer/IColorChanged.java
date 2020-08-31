package com.example.intervaltimer;

interface IColorChanged {
    void backgroundColorChanged(int color);

    void foregroundColorChanged(int color);

    void textColorChanged(int color);

    int getBackgroundColor();

    int getForegroundColor();

    int getTextColor();
}
