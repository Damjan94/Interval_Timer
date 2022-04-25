package com.example.intervaltimer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.os.IBinder;

import androidx.activity.ComponentActivity;

public class MainActivityLayouts {
    private AlarmInfo m_myAlarmInfo;

    private AlarmService.MyBinder m_binder;
    private ServiceConnection connection;
    private Intent m_alarmServiceIntent;

    public MainActivityLayouts(ComponentActivity mainActivity) {
        m_alarmServiceIntent = new Intent(mainActivity, AlarmService.class);
    }

    private boolean m_didWeStartTheServiceRecently = false;
    protected void onCreate(Bundle savedInstanceState) {

        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                m_binder = (AlarmService.MyBinder) service;

                m_binder.setActivity(MainActivityLayouts.this);
                if (m_didWeStartTheServiceRecently) {
                    m_binder.startCountdown();
                    m_didWeStartTheServiceRecently = false;
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                m_binder = null;
                //MainActivityLayouts.this.stopCountdown(m_myAlarmInfo.getLongInterval());
            }
        };
        m_myAlarmInfo = new AlarmInfo();


    }
/*
    private void setFragmentNotificationPicker(View v, IRingtoneReceiver receiver, String ringtoneType) {
        v.setOnLongClickListener((view) -> {
            RingtoneManager manager = new RingtoneManager(this);
            manager.setType(RingtoneManager.TYPE_NOTIFICATION);
            Cursor c = manager.getCursor();

            Map<String, Integer> ringtones = new TreeMap<>();

            do {
                String title = c.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                ringtones.put(title, c.getPosition());
            } while (c.moveToNext());
            final FragmentNotificationPicker fragmentNotificationPicker = new FragmentNotificationPicker(m_myTheme, manager, ringtones, receiver, ringtoneType);
            getSupportFragmentManager().beginTransaction().add(fragmentNotificationPicker, "Notification picker").commit();
            return true; // we're consuming this click
        });
    }
*/
    protected void onResume(SharedPreferences preferences) {
        m_myAlarmInfo.load(preferences);

        if (m_binder != null) {
            AlarmInfo ai = m_binder.getInfo();
            if (ai != null) {
                m_myAlarmInfo = ai;
            }
            m_binder.setActivity(this);
        }

        /*
        bindService(m_alarmServiceIntent, connection, 0);//bind to it, but don't start countdown until the start button is pressed

        seconds.setText(String.valueOf(m_myAlarmInfo.getLongInterval() / 1000));
        //loop trough the options of our adapter and set the loaded option to the closest option available
        int interval = m_myAlarmInfo.getShortInterval() / 1000;
        int selection = 0;
        for (int i = 1; i < IntervalSpinnerAdapter.INTERVAL_TIME.length; i++) {//we skip the first element, it should be "---"
            if (Integer.parseInt(IntervalSpinnerAdapter.INTERVAL_TIME[i]) == interval) {
                selection = i;

            }
        }
        m_notificationInterval.setSelection(selection);
    */}

    protected void onPause() {
        if (m_binder != null) {
            m_binder.setActivity(null);
        }

/*
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = preferences.edit();
        m_myTheme.save(sharedPreferencesEditor);
        m_myAlarmInfo.save(sharedPreferencesEditor);
        sharedPreferencesEditor.apply();
*/
    }
/*
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (!m_binder.isRunning()) {
            Intent alarmServiceIntent = new Intent(this, AlarmService.class);
            stopService(alarmServiceIntent);
        }
        unbindService(connection);
        connection = null;
    }


    public void stopCountdown(int intervalTime) {
        final String intervalTimeStr = String.valueOf(intervalTime / 1000);
        runOnUiThread(() -> {
            iterationCount.setText(R.string.number_of_iterations);
            progressBar.setProgress(progressBar.getMax());//as we're counting down, make the progress bar "empty" itself
            start.setText(R.string.start);
            seconds.setEnabled(true);
            seconds.setText(intervalTimeStr);
            m_notificationInterval.setEnabled(true);
        });

    }

    public AlarmInfo getAlarmInfo() {
        return m_myAlarmInfo;
    }

    public void startCountdown(int intervalTime) {
        runOnUiThread(() -> {
            progressBar.setMax(intervalTime / 1000);
            start.setText(R.string.cancel);
            seconds.clearFocus();
            seconds.setEnabled(false);
            m_notificationInterval.setEnabled(false);//technically it could be always enabled, but I'm too lazy to code for that
        });
    }

    void update(int secondsUntilFinished) {
        runOnUiThread(() -> {
            progressBar.setProgress(progressBar.getMax() - secondsUntilFinished);
            seconds.setText(String.valueOf(progressBar.getMax() - secondsUntilFinished));
        });

    }

    void finishedIteration(int numberOfIterations) {
        final String str = String.valueOf(numberOfIterations);
        runOnUiThread(() -> {
            iterationCount.setText(str);
        });
    }*/
}
