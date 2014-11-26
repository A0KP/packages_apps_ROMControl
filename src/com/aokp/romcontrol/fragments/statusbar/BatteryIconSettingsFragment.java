package com.aokp.romcontrol.fragments.statusbar;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.aokp.romcontrol.R;
import com.aokp.romcontrol.settings.BaseSetting.OnSettingChangedListener;
import com.aokp.romcontrol.settings.CheckboxSetting;
import com.aokp.romcontrol.settings.SwitchSetting;

public class BatteryIconSettingsFragment extends Fragment implements OnSettingChangedListener {

    private static final String STATUS_BAR_SHOW_BATTERY_PERCENT = "status_bar_show_battery_percent";

    SwitchSetting mShowBatteryPercent;

    public BatteryIconSettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statusbar_battery_icon, container, false);

        mShowBatteryPercent = (SwitchSetting) v.findViewById(R.id.status_bar_show_battery_percent);
        mShowBatteryPercent.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT, 0) == 1);

        mShowBatteryPercent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_SHOW_BATTERY_PERCENT,
                        mShowBatteryPercent.isChecked() ? 1 : 0);
            }
        });

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mShowBatteryPercent.setOnSettingChangedListener(this);
    }

    @Override
    public void onSettingChanged(String table, String key, String oldValue, String value) {
    }
}
