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

public class BatteryIconSettingsFragment extends Fragment implements OnSettingChangedListener {

    CheckboxSetting mBatteryIndicator, mBatteryIndicatorPlugged;

    public BatteryIconSettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery_icon_settings, container, false);

        mBatteryIndicator = (CheckboxSetting) v.findViewById(R.id.status_bar_show_battery_percent);
        mBatteryIndicatorPlugged = (CheckboxSetting) v.findViewById(R.id.battery_percentage_indicator_plugged);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBatteryIndicator.setOnSettingChangedListener(this);
    }

    @Override
    public void onSettingChanged(String table, String key, String oldValue, String value) {
    }
}
