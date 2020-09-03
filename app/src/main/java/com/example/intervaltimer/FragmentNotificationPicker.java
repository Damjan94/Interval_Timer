package com.example.intervaltimer;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Map;

public class FragmentNotificationPicker extends DialogFragment {

    private final DynamicTheme m_theme;
    private final RingtoneManager m_ringtoneManager;
    private final Map<String, Integer> m_ringtones;
    private final IRingtoneReceiver m_receiver;
    ViewGroup m_container;
    private Ringtone m_selectedRingtone;
    private int m_selectedRingtoneId;

    FragmentNotificationPicker(DynamicTheme theme, RingtoneManager manager, Map<String, Integer> ringtones, IRingtoneReceiver receiver) {
        m_theme = theme;
        m_ringtoneManager = manager;
        m_ringtones = ringtones;
        m_receiver = receiver;
    }

    @Override
    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        m_container = (ViewGroup) inflater.inflate(R.layout.fragment_notification_picker, container, false);
        LinearLayout scroll = m_container.findViewById(R.id.scroll_linear);
        RadioGroup radioGroup = new RadioGroup(scroll.getContext());
        for (String r : m_ringtones.keySet()) {
            RadioButton b = new RadioButton(scroll.getContext());
            b.setText(r);
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String rName = ((RadioButton) (v)).getText().toString();
                    Integer i = m_ringtones.get(rName);
                    if (i == null) {
                        m_selectedRingtone = null;
                        return;
                    }
                    m_selectedRingtone = m_ringtoneManager.getRingtone(i);
                    m_selectedRingtoneId = i;
                    if (m_selectedRingtone != null) {
                        m_selectedRingtone.play();
                    }
                }
            });
            radioGroup.addView(b);
        }
        scroll.addView(radioGroup);
        return m_container;
    }

    @Override
    public void onStart() {
        super.onStart();
        m_theme.apply((ViewGroup) m_container.getParent());
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("Selected ringtone ", m_selectedRingtone.toString());
        m_receiver.ringtoneSelected(m_selectedRingtone, m_selectedRingtoneId);
    }
}
