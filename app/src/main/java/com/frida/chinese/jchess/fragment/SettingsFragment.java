package com.frida.chinese.jchess.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.frida.chinese.jchess.R;


public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.game_settings);
    }
}
