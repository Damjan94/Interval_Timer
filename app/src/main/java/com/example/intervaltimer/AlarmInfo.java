package com.example.intervaltimer;

import android.content.SharedPreferences;
import android.net.Uri;


public class AlarmInfo implements IRingtoneReceiver {

    final static String STOP_RUNNING_KEY = "stop running";

    final static String GUI_UPDATE_INTERVAL_KEY = "gui update interval";
    final static String SHORT_INTERVAL_KEY = "short update interval";
    final static String LONG_INTERVAL_KEY = "long update interval";

    final static String RINGTONE_SHORT_INTERVAL_KEY = "ringtone " + SHORT_INTERVAL_KEY;
    final static String RINGTONE_LONG_INTERVAL_KEY = "ringtone " + LONG_INTERVAL_KEY;

    final static int DEFAULT_GUI_UPDATE_INTERVAL = 300;
    final static int DEFAULT_SHORT_UPDATE_INTERVAL = 10 * 1000;
    final static int DEFAULT_LONG_UPDATE_INTERVAL = 20 * 1000;

    private int m_shortInterval;
    private int m_longInterval;

    private boolean m_isRunning = false;

    private Uri m_shortRingtoneID;
    private Uri m_longRingtoneID;

    public void save(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putInt(SHORT_INTERVAL_KEY, m_shortInterval);
        preferencesEditor.putInt(LONG_INTERVAL_KEY, m_longInterval);

        preferencesEditor.putString(RINGTONE_SHORT_INTERVAL_KEY, m_shortRingtoneID.toString());
        preferencesEditor.putString(RINGTONE_LONG_INTERVAL_KEY, m_longRingtoneID.toString());
    }

    public void load(SharedPreferences sharedPreferences) {
        m_shortInterval = sharedPreferences.getInt(SHORT_INTERVAL_KEY, DEFAULT_SHORT_UPDATE_INTERVAL);
        m_longInterval = sharedPreferences.getInt(LONG_INTERVAL_KEY, DEFAULT_LONG_UPDATE_INTERVAL);

        m_shortRingtoneID = Uri.parse(sharedPreferences.getString(RINGTONE_SHORT_INTERVAL_KEY, ""));
        m_longRingtoneID = Uri.parse(sharedPreferences.getString(RINGTONE_LONG_INTERVAL_KEY, ""));


    }


    public int getShortInterval() {
        return m_shortInterval;
    }

    public void setShortInterval(int shortInterval) {
        this.m_shortInterval = shortInterval;
    }

    public int getLongInterval() {
        return m_longInterval;
    }

    public void setLongInterval(int longInterval) {
        this.m_longInterval = longInterval;
    }

    public Uri getShortRingtoneID() {
        return m_shortRingtoneID;
    }

    public void setShortRingtoneID(Uri shortRingtoneID) {
        this.m_shortRingtoneID = shortRingtoneID;

    }

    public Uri getLongRingtoneID() {
        return m_longRingtoneID;
    }

    public void setLongRingtoneID(Uri longRingtoneID) {
        this.m_longRingtoneID = longRingtoneID;
    }

    public void setIsRunning(boolean isRunning) {
        m_isRunning = isRunning;
    }

    public boolean isRunning() {
        return m_isRunning;
    }

    @Override
    public void ringtoneSelected(Uri ringtoneId, String ringtoneType) {
        if (ringtoneType.equals(RINGTONE_SHORT_INTERVAL_KEY)) {
            this.setShortRingtoneID(ringtoneId);
        } else if (ringtoneType.equals(RINGTONE_LONG_INTERVAL_KEY)) {
            this.setLongRingtoneID(ringtoneId);
        }
    }
}
