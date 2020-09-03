package com.example.intervaltimer;

import android.app.AlarmManager;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.HandlerThread;

import androidx.annotation.NonNull;


public class AlarmController implements AlarmManager.OnAlarmListener, IRingtoneReceiver {

    static final String INTERVAL_KEY = "timer interval";
    static final String FINISHED_KEY = "timer finished";
    static final String RINGTONE_ID = "ringtone";
    private final static String ALARM_NAME = "interval timer alarm";
    private final static String THREAD_NAME = "alarm thread";
    private static ActivityAndAlarm activityAndAlarm = null;
    private final RingtoneManager m_ringtoneManager;
    private final Handler m_handler;

    private final float m_volume;
    private String m_saveLoadKey;
    private boolean m_isActive;
    private long m_millisUntilAlarm;
    private boolean m_updateGui;
    private Ringtone m_ringtone;
    private int m_ringtoneId;
    private CountDownTimer m_countDown;
    private int m_numberOfIterations;
    private HandlerThread m_handlerThread;

    /**
     * @param aaa              am the alarm manager to be used to reset the alarm
     *                         ma main activity to be called periodically to update the gui and to play a notification
     * @param millisUntilAlarm after calling start, the alarm will sound every millisUntilAlarm
     */
    AlarmController(@NonNull ActivityAndAlarm aaa, @NonNull RingtoneManager ringtoneManager, long millisUntilAlarm, boolean updateGui, String saveLoadKey, float volume) {
        activityAndAlarm = aaa;
        m_millisUntilAlarm = millisUntilAlarm;
        m_ringtoneManager = ringtoneManager;
        m_updateGui = updateGui;
        m_handlerThread = new HandlerThread(THREAD_NAME);
        m_handlerThread.start();
        m_handler = new Handler(m_handlerThread.getLooper());
        m_saveLoadKey = saveLoadKey;
        m_volume = volume;
    }

    void start(long millisUntilAlarm) {
        stop();
        m_millisUntilAlarm = millisUntilAlarm;
        startAlarm(m_millisUntilAlarm + System.currentTimeMillis());
        m_isActive = true;
    }

    void stop() {
        if (!m_isActive) {
            return;//not currently running
        }
        if (activityAndAlarm == null)
            return;//nothing to stop
        activityAndAlarm.m_am.cancel(this);
        if (m_countDown != null) {
            m_countDown.cancel();
            m_countDown = null;
        }
        m_isActive = false;
        m_numberOfIterations = 0;
    }

    long getMillis() {
        return m_millisUntilAlarm;
    }

    void addActivity(MainActivity activity) {
        activityAndAlarm.m_ma = activity;
    }

    void removeActivity() {
        if (m_countDown != null)
            m_countDown.cancel();
        activityAndAlarm.m_ma = null;
    }

    private void startAlarm(long nextTrigger) {
        activityAndAlarm.m_am.setExact(AlarmManager.RTC_WAKEUP,
                nextTrigger,
                ALARM_NAME,
                this,
                m_handler);//the background alarm
        if (m_updateGui) {
            m_countDown = new CountDownTimer(m_millisUntilAlarm, 500/*update the ui twice every second*/) {
                @Override
                public void onTick(long millisUntilFinished) {
                    if (m_updateGui && (activityAndAlarm.m_ma != null)) {
                        int secondsUntilFinished = (int) (millisUntilFinished / 1000);
                        String secondsUntilFinishedStr = String.valueOf(secondsUntilFinished);//do this here, while on another thread
                        activityAndAlarm.m_ma.update(secondsUntilFinished, secondsUntilFinishedStr);
                    }
                }

                @Override
                public void onFinish() {
                    //do nothing, the alarm will take care of it.
                }
            };
        }
        if (m_updateGui && (activityAndAlarm.m_ma != null))//no point in starting it if there is no main activity
            m_countDown.start();//the ui updating alarm
    }

    boolean isActive() {
        return m_isActive;
    }

    @Override
    public void onAlarm() {
        m_ringtone.play();
        m_numberOfIterations++;
        if (m_updateGui && (activityAndAlarm.m_ma != null)) {
            activityAndAlarm.m_ma.finishedIteration(m_numberOfIterations);
        }
        if (m_isActive) {
            startAlarm(System.currentTimeMillis() + m_millisUntilAlarm);
        }
    }


    //TODO make save and load functions comply with savedInstanceState
    void load(SharedPreferences preferences) {
        m_millisUntilAlarm = preferences.getLong(m_saveLoadKey, 0);
        int id = preferences.getInt(m_saveLoadKey + RINGTONE_ID, 1);
        Ringtone r = m_ringtoneManager.getRingtone(id);
        this.ringtoneSelected(r, id);
    }

    void save(SharedPreferences.Editor preferencesEditor) {
        preferencesEditor.putLong(m_saveLoadKey, m_millisUntilAlarm).apply();
        preferencesEditor.putInt(m_saveLoadKey + RINGTONE_ID, m_ringtoneId);
    }

    /**
     * this method must be called in order to stop the threads. after calling this method this class is not usable anymore
     */
    void stopThreads() {
        m_handlerThread.quitSafely();
        m_handlerThread = null;
    }

    @Override
    public void ringtoneSelected(@NonNull Ringtone ringtone, int ringtoneId) {
        AudioAttributes aa = new AudioAttributes.Builder().
                setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).
                setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build();
        ringtone.setAudioAttributes(aa);
        ringtone.setVolume(m_volume);
        m_ringtone = ringtone;
        m_ringtoneId = ringtoneId;
    }
}
