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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.intervaltimer.spinner.IntervalSpinner;
import com.example.intervaltimer.spinner.IntervalSpinnerAdapter;

import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private TextView iterationCount;
    private ProgressBar progressBar;
    private EditText seconds;
    private Button start;

    private IntervalSpinner m_notificationInterval;

    private DynamicTheme m_myTheme;
    private AlarmInfo m_myAlarmInfo;

    private AlarmService.MyBinder m_binder;
    private ServiceConnection connection;

    private boolean m_isRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iterationCount = findViewById(R.id.iteration_number);
        start = findViewById(R.id.start_button);
        seconds = findViewById(R.id.seconds_text);
        progressBar = findViewById(R.id.progress_bar);
        m_notificationInterval = findViewById(R.id.notification_interval_spinner);
        start.setOnClickListener((view) -> {

            Intent alarmServiceIntent = new Intent(MainActivity.this, AlarmService.class);

            if (m_binder.isRunning()) {
                m_binder.stopCountdown();
                Thread t = new Thread(() -> {
                    try {
                        Thread.sleep(500);//wait for the service to stop...
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    startService(alarmServiceIntent);
                    bindService(alarmServiceIntent, connection, 0);//bind to it, but don't start countdown until the start button is pressed
                });
                t.start();
                start.setEnabled(false);
            } else {
                m_binder.startCountdown();
            }
        });

        progressBar.setMin(0);

        m_myTheme = new DynamicTheme(this, getWindow());
        m_myTheme.apply((ViewGroup) findViewById(R.id.linear_layout).getParent());

        m_myAlarmInfo = new AlarmInfo();

        ImageView timerImage = findViewById(R.id.timer_image);

        timerImage.setOnLongClickListener((view) -> {
            final FragmentColorPicker fragmentColorPicker = new FragmentColorPicker(m_myTheme);
            getSupportFragmentManager().beginTransaction().add(fragmentColorPicker, "Color picker").commit();
            return true; // we're consuming this click
        });

        IntervalSpinnerAdapter adapter = new IntervalSpinnerAdapter(m_myTheme, m_myAlarmInfo);
        m_notificationInterval.setAdapter(adapter);
        m_notificationInterval.setSelection(0);
        m_notificationInterval.setOnItemSelectedListener(adapter);

        seconds.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int longInterval = -1;
                try {
                    longInterval = Integer.parseInt(s.toString());
                } catch (NumberFormatException e) {
                    seconds.requestFocus();
                }
                if (longInterval > 0) {
                    m_myAlarmInfo.setLongInterval(longInterval * 1000);
                }
            }
        });

        setFragmentNotificationPicker(m_notificationInterval, m_myAlarmInfo, AlarmInfo.RINGTONE_SHORT_INTERVAL_KEY);

        setFragmentNotificationPicker(seconds, m_myAlarmInfo, AlarmInfo.RINGTONE_LONG_INTERVAL_KEY);
    }

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

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        m_myAlarmInfo.load(preferences);
        m_myTheme.load(preferences);

        Intent alarmServiceIntent = new Intent(MainActivity.this, AlarmService.class);
        startService(alarmServiceIntent);//start the service, so it doesn't get killed if we lave the activity
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                m_binder = (AlarmService.MyBinder) service;

                m_binder.setActivity(MainActivity.this);
                start.setEnabled(true);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                m_binder = null;

            }
        };
        bindService(alarmServiceIntent, connection, 0);//bind to it, but don't start countdown until the start button is pressed

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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (m_binder != null) {
            m_binder.setActivity(null);
        }


        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = preferences.edit();
        m_myTheme.save(sharedPreferencesEditor);
        m_myAlarmInfo.save(sharedPreferencesEditor);
        sharedPreferencesEditor.apply();

    }

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
        final String secondsUntilFinishedString = String.valueOf(secondsUntilFinished);
        runOnUiThread(() -> {
            progressBar.setProgress(secondsUntilFinished);
            seconds.setText(secondsUntilFinishedString);
        });

    }

    void finishedIteration(int numberOfIterations) {
        final String str = String.valueOf(numberOfIterations);
        runOnUiThread(() -> {
            iterationCount.setText(str);
        });
    }
}
