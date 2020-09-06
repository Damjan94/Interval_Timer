package com.example.intervaltimer;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
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
    private final String m_ringtoneType;
    ViewGroup m_container;
    private Uri m_selectedRingtoneId;

    FragmentNotificationPicker(DynamicTheme theme, RingtoneManager manager, Map<String, Integer> ringtones, IRingtoneReceiver receiver, String ringtoneType) {
        m_theme = theme;
        m_ringtoneManager = manager;
        m_ringtones = ringtones;
        m_receiver = receiver;
        m_ringtoneType = ringtoneType;
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
                        return;
                    }
                    Ringtone selectedRingtone = m_ringtoneManager.getRingtone(i);
                    m_selectedRingtoneId = m_ringtoneManager.getRingtoneUri(i);
                    if (selectedRingtone != null) {
                        selectedRingtone.play();
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
        m_receiver.ringtoneSelected(m_selectedRingtoneId, m_ringtoneType);
    }
}
