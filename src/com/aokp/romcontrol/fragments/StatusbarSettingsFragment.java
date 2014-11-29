package com.aokp.romcontrol.fragments;

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
import com.aokp.romcontrol.settings.SwitchSetting;

public class StatusbarSettingsFragment extends Fragment implements OnSettingChangedListener {

    private static final String DOUBLE_TAP_SLEEP_GESTURE = "double_tap_sleep_gesture";
    private static final String STATUS_BAR_BRIGHTNESS_CONTROL = "status_bar_brightness_control";
    private static final String STATUS_BAR_QUICK_QS_PULLDOWN = "status_bar_quick_qs_pulldown";
    SwitchSetting mDoubleTapSleepGesture;
    SwitchSetting mStatusBarBrightnessControl;
    SwitchSetting mStatusBarQuickQsPulldown;

    public StatusbarSettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_statusbar_settings, container, false);

        mDoubleTapSleepGesture = (SwitchSetting) v.findViewById(R.id.double_tap_sleep_gesture);
        mStatusBarBrightnessControl = (SwitchSetting) v.findViewById(R.id.status_bar_brightness_control);
        mStatusBarQuickQsPulldown = (SwitchSetting) v.findViewById(R.id.status_bar_quick_qs_pulldown);

        mDoubleTapSleepGesture.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.DOUBLE_TAP_SLEEP_GESTURE, 0) == 1);
        mStatusBarBrightnessControl.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL, 0) == 1);
        mStatusBarQuickQsPulldown.setChecked(Settings.System.getInt(getActivity().getContentResolver(),
                Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN, 0) == 1);

        mDoubleTapSleepGesture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.DOUBLE_TAP_SLEEP_GESTURE,
                        mDoubleTapSleepGesture.isChecked() ? 1 : 0);
            }
        });
        mStatusBarBrightnessControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_BRIGHTNESS_CONTROL,
                        mStatusBarBrightnessControl.isChecked() ? 1 : 0);
            }
        });
        mStatusBarQuickQsPulldown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.System.putInt(getActivity().getContentResolver(),
                        Settings.System.STATUS_BAR_QUICK_QS_PULLDOWN,
                        mStatusBarQuickQsPulldown.isChecked() ? 1 : 0);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDoubleTapSleepGesture.setOnSettingChangedListener(this);
        mStatusBarBrightnessControl.setOnSettingChangedListener(this);
        mStatusBarQuickQsPulldown.setOnSettingChangedListener(this);
    }

    @Override
    public void onSettingChanged(String table, String key, String oldValue, String value) {
    }
}
