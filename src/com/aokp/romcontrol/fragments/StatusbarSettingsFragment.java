package com.aokp.romcontrol.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.settings.BaseSetting.OnSettingChangedListener;
import com.aokp.romcontrol.settings.CheckboxSetting;

public class StatusbarSettingsFragment extends Fragment implements OnSettingChangedListener {

    public StatusbarSettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statusbar_settings, container, false);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onSettingChanged(String table, String key, String oldValue, String value) {
    }
}
