package com.example.intervaltimer;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class AlarmService extends Service {

    private final Object THREAD_LOCK = new Object();
    private final Binder m_binder = new MyBinder(this);
    private final RingtoneManager m_ringtoneManager = new RingtoneManager(this);
    ArrayList<ElapsedTimeCheck> m_checks = null;
    private volatile boolean m_isRunning = false;
    private boolean m_notificationPosted = false;
    private Ringtone m_shortRingtone = null;
    private Ringtone m_longRingtone = null;
    private AlarmInfo m_alarmInfo = null;
    private MainActivity m_activity = null;
    private Thread m_thread = new Thread(() -> {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "IntervalTimer::CountdownLock");

        wakeLock.acquire();
        while (m_isRunning) {
            long nowTime = System.currentTimeMillis();
            long sleepTime = Long.MAX_VALUE;
            int i = 0;
            if (m_activity == null) {
                i = 1;//no need to check the gui update, if the activity isn't available...
            }
            synchronized (THREAD_LOCK) {

                for (; i < m_checks.size(); i++) {
                    ElapsedTimeCheck ec = m_checks.get(i);
                    sleepTime = Math.min(Math.max(0, ec.check(nowTime)), sleepTime);
                }
            }

            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (m_activity != null) {
            m_activity.stopCountdown(m_checks.get(2).m_intervalTime);
        }
        wakeLock.release();
        stopSelf();
    });

    {
        m_ringtoneManager.setType(RingtoneManager.TYPE_NOTIFICATION);
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent i, int flags, int startId) {
        if (m_notificationPosted == false) {
            final String NOTIFICATION_ID = "Interval timer notification";
            NotificationChannel ch = new NotificationChannel(NOTIFICATION_ID, "Interval timer", NotificationManager.IMPORTANCE_HIGH);
            ch.setDescription("The timer is running...");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(ch);
            NotificationCompat.Builder b = new NotificationCompat.Builder(this, NOTIFICATION_ID);
            startForeground(123, b.build());
            m_notificationPosted = true;
        }
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return m_binder;
    }

    public void startCountdown() {
        if (m_alarmInfo == null) {
            return;
        }

        m_longRingtone = m_ringtoneManager.getRingtone(m_ringtoneManager.getRingtonePosition(m_alarmInfo.getLongRingtoneID()));
        setRingtone(m_longRingtone, 1);

        m_shortRingtone = m_ringtoneManager.getRingtone(m_ringtoneManager.getRingtonePosition(m_alarmInfo.getShortRingtoneID()));
        setRingtone(m_shortRingtone, 0.7f);

        long startTime = System.currentTimeMillis();
        ArrayList<ElapsedTimeCheck> checks = new ArrayList<>(3);
        checks.add(new ElapsedTimeCheck(startTime, 300) {
            @Override
            public void run() {
                if (m_activity != null) {
                    m_activity.update(m_checks.get(2).getSecondsUntilFinished());
                }
            }
        });

        checks.add(new ElapsedTimeCheck(startTime, m_alarmInfo.getShortInterval()) {
            @Override
            public void run() {
                if (m_shortRingtone != null) {
                    m_shortRingtone.play();
                }
            }
        });
        checks.add(new ElapsedTimeCheck(startTime, m_alarmInfo.getLongInterval()) {
            @Override
            public void run() {

                if (m_longRingtone != null) {
                    m_longRingtone.play();
                }
                if (m_activity != null) {
                    m_activity.finishedIteration(this.m_iterationCount);
                }
            }
        });

        synchronized (THREAD_LOCK) {
            m_checks = checks;
            if (!m_isRunning) {
                m_isRunning = true;
                m_thread.start();
            }

        }
        if (m_activity != null) {
            m_activity.startCountdown(m_alarmInfo.getLongInterval());
        }
    }

    void setActivity(MainActivity activity) {
        m_activity = activity;
        if (m_activity == null) {
            return;
        }
        setAlarmInfo(activity.getAlarmInfo());
        if (m_isRunning) {
            m_activity.startCountdown(m_checks.get(2).m_intervalTime);
        }
    }

    private void setAlarmInfo(@NonNull AlarmInfo alarmInfo) {
        m_alarmInfo = alarmInfo;
    }

    private void setRingtone(Ringtone ringtone, float volume) {
        AudioAttributes aa = new AudioAttributes.Builder().
                setFlags(AudioAttributes.FLAG_AUDIBILITY_ENFORCED).
                setUsage(AudioAttributes.USAGE_NOTIFICATION_COMMUNICATION_INSTANT).build();
        ringtone.setAudioAttributes(aa);
        ringtone.setVolume(volume);
    }

    public static class MyBinder extends Binder {

        private final AlarmService m_alarm;

        private MyBinder(AlarmService alarmService) {
            m_alarm = alarmService;
        }

        public void setActivity(MainActivity activity) {
            m_alarm.setActivity(activity);
        }

        public void startCountdown() {
            m_alarm.startCountdown();
        }

        public void stopCountdown() {
            m_alarm.m_isRunning = false;
        }

        public boolean isRunning() {
            return m_alarm.m_isRunning;
        }
    }

    private static abstract class ElapsedTimeCheck implements Runnable {

        protected int m_iterationCount = 0;
        private int m_intervalTime;
        private long m_lastRun;

        ElapsedTimeCheck(long startTime, int intervalTime) {
            m_intervalTime = intervalTime;
            m_lastRun = startTime;

        }

        /**
         * @return returns the number of milliseconds needed to sleep for the next action,
         * if return is negative, that means we are late by that amount of time
         */
        long check(long currentTime) {
            long sleepTime = m_intervalTime - (currentTime - m_lastRun);
            if (sleepTime <= 0) {
                this.run();
                m_iterationCount++;
                m_lastRun = currentTime;

                sleepTime = m_intervalTime;
            }
            return sleepTime;
        }

        int getSecondsUntilFinished() {
            long currentTime = System.currentTimeMillis();
            return (int) ((currentTime - m_lastRun) / 1000);
        }
    }
}
