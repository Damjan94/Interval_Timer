package com.example.intervaltimer;

import android.app.AlarmManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

class ActivityAndAlarm {
    @Nullable
    MainActivity m_ma;
    @NonNull
    AlarmManager m_am;

    ActivityAndAlarm(MainActivity ma, AlarmManager am) {
        m_ma = ma;
        m_am = am;
    }
}