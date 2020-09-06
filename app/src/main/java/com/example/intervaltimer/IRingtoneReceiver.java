package com.example.intervaltimer;

import android.net.Uri;

interface IRingtoneReceiver {
    void ringtoneSelected(Uri ringtoneId, String ringtoneType);
}
