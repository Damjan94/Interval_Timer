package com.example.intervaltimer;

import android.media.Ringtone;

import androidx.annotation.NonNull;

interface IRingtoneReceiver {
    void ringtoneSelected(@NonNull Ringtone ringtone, int ringtoneId);
}
