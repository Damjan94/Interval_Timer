package com.example.intervaltimer;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;
import java.util.TreeMap;

public class MainActivity extends AppCompatActivity {

    private TextView iterationCount;
    private ProgressBar progressBar;
    private EditText seconds;
    private Button start;
    private AlarmController m_finishedAlarm;
    private AlarmController m_intervalAlarm;
    private Spinner m_notificationInterval;

    private DynamicTheme m_myTheme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iterationCount = findViewById(R.id.iteration_number);
        start = findViewById(R.id.start_button);
        seconds = findViewById(R.id.seconds_text);
        progressBar = findViewById(R.id.progress_bar);
        m_notificationInterval = findViewById(R.id.notification_interval_spinner);
        start.setOnClickListener(new StartOnClick(this));
        progressBar.setMin(0);


        DynamicTheme.Colors colors = new DynamicTheme.Colors();

        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        colors.background = Color.valueOf(typedValue.data);

        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        colors.foreground = Color.valueOf(typedValue.data);

        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        colors.text = Color.valueOf(typedValue.data);

        m_myTheme = new DynamicTheme(colors, getWindow());

        m_myTheme.apply((ViewGroup) findViewById(R.id.linear_layout).getParent());
        ImageView timerImage = findViewById(R.id.timer_image);

        timerImage.setOnLongClickListener((view) -> {
            final FragmentColorPicker fragmentColorPicker = new FragmentColorPicker(m_myTheme);
            getSupportFragmentManager().beginTransaction().add(fragmentColorPicker, "Color picker").commit();
            return true; // we're consuming this click
        });
        // set up the alarm and ring tones
        ActivityAndAlarm aaa = new ActivityAndAlarm(this, (AlarmManager) getSystemService(ALARM_SERVICE));
        RingtoneManager manager = new RingtoneManager(this);

        m_finishedAlarm = new AlarmController(aaa, manager, 0, true, AlarmController.FINISHED_KEY, (float) 1.0);


        m_intervalAlarm = new AlarmController(aaa, manager, 0, false, AlarmController.INTERVAL_KEY, (float) 0.2);

        IntervalSpinnerAdapter adapter = new IntervalSpinnerAdapter(m_myTheme);
        m_notificationInterval.setAdapter(adapter);
        m_notificationInterval.setSelection(0);
        m_notificationInterval.setOnItemSelectedListener(adapter);


        setFragmentNotificationPicker(m_notificationInterval, m_intervalAlarm);

        setFragmentNotificationPicker(seconds, m_finishedAlarm);
    }

    private void setFragmentNotificationPicker(View v, IRingtoneReceiver receiver) {
        v.setOnLongClickListener((view) -> {
            RingtoneManager manager = new RingtoneManager(this);
            manager.setType(RingtoneManager.TYPE_NOTIFICATION);
            Cursor c = manager.getCursor();

            Map<String, Integer> ringtones = new TreeMap<>();

            do {
                String title = c.getString(RingtoneManager.TITLE_COLUMN_INDEX);
                ringtones.put(title, c.getPosition());
            } while (c.moveToNext());
            final FragmentNotificationPicker fragmentNotificationPicker = new FragmentNotificationPicker(m_myTheme, manager, ringtones, receiver);
            getSupportFragmentManager().beginTransaction().add(fragmentNotificationPicker, "Notification picker").commit();
            return true; // we're consuming this click
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        m_finishedAlarm.addActivity(this);
        m_intervalAlarm.addActivity(this);
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        int selection = 0;

        m_finishedAlarm.load(preferences);
        m_intervalAlarm.load(preferences);
        m_myTheme.load(preferences);

        seconds.setText(String.valueOf(m_finishedAlarm.getMillis() / 1000));
        //loop trough the options of our adapter and set the loaded option to the closest option available
        int interval = (int) (m_intervalAlarm.getMillis() / 1000);
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
        m_finishedAlarm.removeActivity();
        m_intervalAlarm.removeActivity();

        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferencesEditor = preferences.edit();
        m_finishedAlarm.save(sharedPreferencesEditor);
        m_intervalAlarm.save(sharedPreferencesEditor);
        m_myTheme.save(sharedPreferencesEditor);
        sharedPreferencesEditor.apply();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        m_finishedAlarm.stop();
        m_finishedAlarm.stopThreads();
        m_intervalAlarm.stop();
        m_intervalAlarm.stopThreads();
    }

    private void stopTimers() {
        m_finishedAlarm.stop();
        m_intervalAlarm.stop();
        stopCountdown();
    }

    private void stopCountdown() {
        iterationCount.setText(R.string.number_of_iterations);
        reset();
    }

    private void reset() {
        progressBar.setProgress(progressBar.getMax());//as we're counting down, make the progress bar "empty" itself
        start.setText(R.string.start);
        seconds.setEnabled(true);
        seconds.setText(String.valueOf(m_finishedAlarm.getMillis() / 1000));
        m_notificationInterval.setEnabled(true);
    }

    private void start() {
        progressBar.setMax((int) m_finishedAlarm.getMillis() / 1000);
        start.setText(R.string.cancel);
        seconds.clearFocus();
        seconds.setEnabled(false);
        m_notificationInterval.setEnabled(false);//technically it could be always enabled, but I'm too lazy to code for that

    }

    void update(int secondsUntilFinished, String secondsUntilFinishedString) {
        runOnUiThread(() -> {
            progressBar.setProgress(secondsUntilFinished);
            seconds.setText(secondsUntilFinishedString);
        });

    }

    void finishedIteration(int numberOfIterations) {
        runOnUiThread(() -> {
            iterationCount.setText(String.valueOf(numberOfIterations));
        });
    }

    private class StartOnClick implements OnClickListener {

        private MainActivity m_mainActivity;

        StartOnClick(MainActivity m) {
            m_mainActivity = m;
        }

        @Override
        public void onClick(View v) {
            if (m_finishedAlarm.isActive()) {
                stopTimers();
                return;
            }

            /*
            Do not Disturb values
            0 - If DnD is off.
            1 - If DnD is on - Priority Only
            2 - If DnD is on - Total Silence
            3 - If DnD is on - Alarms Only
             */
            int doNotDisturbStatus;
            try {
                doNotDisturbStatus = Settings.Global.getInt(getContentResolver(), "zen_mode");
            } catch (Settings.SettingNotFoundException e) {
                doNotDisturbStatus = 0;//we couldn't get the DnD status, so just assume it's off
                Log.w("IntervalTimer", "Couldn't get the Do not Disturb status.");//notify the user
            }
            if (doNotDisturbStatus != 0) {
                Dialog alert = new AlertDialog.Builder(m_mainActivity).setMessage(R.string.warning_dnd)
                        .setPositiveButton(R.string.ok, (dialog, which) -> stopTimers())
                        .setNegativeButton(R.string.cancel, null)
                        .create();
                alert.show();
            }
            long countdown;
            try {
                countdown = Integer.parseInt(seconds.getText().toString()) * 1000;
            } catch (NumberFormatException ex) {
                seconds.requestFocus();
                return;
            }

            String selectedTime = (String) m_notificationInterval.getSelectedItem();
            if (!selectedTime.equals(IntervalSpinnerAdapter.INTERVAL_TIME[0])) {
                int intervalTime = Integer.parseInt(selectedTime) * 1000;
                m_intervalAlarm.start(intervalTime);
            }
            m_finishedAlarm.start(countdown);
            start();
        }
    }
}
