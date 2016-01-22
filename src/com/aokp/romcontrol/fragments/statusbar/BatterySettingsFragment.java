/*
* Copyright (C) 2015 The Android Open Kang Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.aokp.romcontrol.fragments.statusbar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.UserHandle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import cyanogenmod.providers.CMSettings;
import org.cyanogenmod.internal.logging.CMMetricsLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import net.margaritov.preference.colorpicker.ColorPickerPreference;

import com.aokp.romcontrol.R;
import com.aokp.romcontrol.widgets.SeekBarPreference;

public class BatterySettingsFragment extends Fragment {

    public BatterySettingsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_battery_settings_main, container, false);

        Resources res = getResources();

        super.onCreate(savedInstanceState);

        getActivity().getFragmentManager().beginTransaction()
                .replace(R.id.battery_settings_main, new BatterySettingsPreferenceFragment())
                .commit();

        return v;
    }



    public static class BatterySettingsPreferenceFragment extends PreferenceFragment
            implements OnPreferenceChangeListener {

        public BatterySettingsPreferenceFragment() {

        }

        private static final String TAG = "BatterySettings";

        private static final String PREF_CAT_CIRCLE_OPTIONS =
                "battery_status_cat_circle_options";
        private static final String PREF_CAT_COLORS =
                "battery_status_cat_colors";
        private static final String PREF_STYLE =
                "battery_status_style";
        private static final String PREF_PERCENT_STYLE =
                "battery_status_percent_style";
        private static final String PREF_CHARGE_ANIMATION_SPEED =
                "battery_status_charge_animation_speed";
        private static final String PREF_CIRCLE_DOT_LENGTH =
                "battery_status_circle_dot_length";
        private static final String PREF_CIRCLE_DOT_INTERVAL =
                "battery_status_circle_dot_interval";
        private static final String PREF_BATTERY_COLOR =
                "battery_status_battery_color";
        private static final String PREF_TEXT_COLOR =
                "battery_status_text_color";

        private static final int DEFAULT_BATTERY_COLOR = 0xffffffff;
        private static final int DEFAULT_TEXT_COLOR = 0xff000000;

        private static final int MENU_RESET = Menu.FIRST;
        private static final int DLG_RESET = 0;

        private static final int BATTERY_STATUS_PORTRAIT = 0;
        private static final int BATTERY_STATUS_LANDSCAPE = 5;
        private static final int BATTERY_STATUS_CIRCLE = 2;
        private static final int BATTERY_STATUS_CIRCLE_DOTTED = 3;
        private static final int BATTERY_STATUS_TEXT = 6;
        private static final int BATTERY_STATUS_HIDDEN = 4;

        private ListPreference mStyle;
        private ListPreference mPercentStyle;
        private ListPreference mChargeAnimationSpeed;
        private ListPreference mCircleDotLength;
        private ListPreference mCircleDotInterval;
        private ColorPickerPreference mBatteryColor;
        private ColorPickerPreference mTextColor;

        private ContentResolver mResolver;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            createCustomView();
        }

        protected void removePreference(String key) {
            Preference pref = findPreference(key);
            if (pref != null) {
                getPreferenceScreen().removePreference(pref);
            }
        }

        public void createCustomView() {
            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.fragment_battery_settings);
            PreferenceScreen prefs = getPreferenceScreen();
            if (prefs != null) {
                prefs.removeAll();
            }
            mResolver = getActivity().getContentResolver();

            int intColor = 0xffffffff;
            String hexColor = String.format("#%08x", (0xffffffff & 0xffffffff));

            mStyle = (ListPreference) findPreference(PREF_STYLE);
            int style = CMSettings.System.getInt(mResolver,
                   CMSettings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 0);
            mStyle.setValue(String.valueOf(style));
            mStyle.setSummary(mStyle.getEntry());
            mStyle.setOnPreferenceChangeListener(this);

            boolean batteryStatusVisible = style != BATTERY_STATUS_HIDDEN;
            boolean isCircle = style == BATTERY_STATUS_CIRCLE;
            boolean isTextOnly = style == BATTERY_STATUS_TEXT;
            boolean isCircleDotted = style == BATTERY_STATUS_CIRCLE_DOTTED;

            PreferenceCategory catCircleOptions =
                    (PreferenceCategory) findPreference(PREF_CAT_CIRCLE_OPTIONS);
            PreferenceCategory catColors =
                    (PreferenceCategory) findPreference(PREF_CAT_COLORS);
            mCircleDotLength =
                    (ListPreference) findPreference(PREF_CIRCLE_DOT_LENGTH);
            mCircleDotInterval =
                    (ListPreference) findPreference(PREF_CIRCLE_DOT_INTERVAL);
            mBatteryColor =
                (ColorPickerPreference) findPreference(PREF_BATTERY_COLOR);
            mTextColor =
                    (ColorPickerPreference) findPreference(PREF_TEXT_COLOR);

            if (batteryStatusVisible && !isTextOnly) {
                mPercentStyle =
                        (ListPreference) findPreference(PREF_PERCENT_STYLE);
                int percentStyle = CMSettings.System.getInt(mResolver,
                       CMSettings.System.STATUS_BAR_BATTERY_STATUS_PERCENT_STYLE, 2);
                mPercentStyle.setValue(String.valueOf(percentStyle));
                mPercentStyle.setSummary(mPercentStyle.getEntry());
                mPercentStyle.setOnPreferenceChangeListener(this);

                mChargeAnimationSpeed =
                        (ListPreference) findPreference(PREF_CHARGE_ANIMATION_SPEED);
                int chargeAnimationSpeed = CMSettings.System.getInt(mResolver,
                       CMSettings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, 3);
                mChargeAnimationSpeed.setValue(String.valueOf(chargeAnimationSpeed));
                mChargeAnimationSpeed.setSummary(mChargeAnimationSpeed.getEntry());
                mChargeAnimationSpeed.setOnPreferenceChangeListener(this);
            } else {
                removePreference(PREF_PERCENT_STYLE);
                removePreference(PREF_CHARGE_ANIMATION_SPEED);
            }

            if (batteryStatusVisible && isCircleDotted) {
                int circleDotLength = CMSettings.System.getInt(mResolver,
                       CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, 3);
                mCircleDotLength.setValue(String.valueOf(circleDotLength));
                mCircleDotLength.setSummary(mCircleDotLength.getEntry());
                mCircleDotLength.setOnPreferenceChangeListener(this);

                int circleDotInterval = CMSettings.System.getInt(mResolver,
                       CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, 2);
                mCircleDotInterval.setValue(String.valueOf(circleDotInterval));
                mCircleDotInterval.setSummary(mCircleDotInterval.getEntry());
                mCircleDotInterval.setOnPreferenceChangeListener(this);
            } else {
                catCircleOptions.removePreference(mCircleDotLength);
                catCircleOptions.removePreference(mCircleDotInterval);
                removePreference(PREF_CAT_CIRCLE_OPTIONS);
            }

            if (batteryStatusVisible && !isTextOnly) {
                intColor = CMSettings.System.getInt(mResolver,
                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                        DEFAULT_BATTERY_COLOR);
                mBatteryColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mBatteryColor.setSummary(hexColor);
                mBatteryColor.setOnPreferenceChangeListener(this);
            } else {
                catColors.removePreference(mBatteryColor);
            }

            if (batteryStatusVisible) {
                intColor = CMSettings.System.getInt(mResolver,
                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR,
                        DEFAULT_BATTERY_COLOR);
                mTextColor.setNewPreviewColor(intColor);
                hexColor = String.format("#%08x", (0xffffffff & intColor));
                mTextColor.setSummary(hexColor);
                mTextColor.setOnPreferenceChangeListener(this);
            } else {
                catColors.removePreference(mTextColor);
            }

            if (!batteryStatusVisible) {
                removePreference(PREF_CAT_COLORS);
            }

            setHasOptionsMenu(true);
        }

        @Override
        public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
            menu.add(0, MENU_RESET, 0, R.string.reset)
                    .setIcon(R.drawable.ic_settings_reset)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
                case MENU_RESET:
                    showDialogInner(DLG_RESET);
                    return true;
                 default:
                    return super.onContextItemSelected(item);
            }
        }

        @Override
        public void onPause() {
            super.onPause();
        }

        @Override
        public void onResume() {
            super.onResume();
        }

        protected int getMetricsCategory() {
            // todo add a constant in MetricsLogger.java
            return CMMetricsLogger.DONT_LOG;
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            int intValue, index, intHex;
            String hex;

            if (preference == mStyle) {
                intValue = Integer.valueOf((String) newValue);
                index = mStyle.findIndexOfValue((String) newValue);
                CMSettings.System.putInt(mResolver,
                    CMSettings.System.STATUS_BAR_BATTERY_STATUS_STYLE, intValue);
                mStyle.setSummary(mStyle.getEntries()[index]);
                createCustomView();
                return true;
            } else if (preference == mPercentStyle) {
                intValue = Integer.valueOf((String) newValue);
                index = mPercentStyle.findIndexOfValue((String) newValue);
                CMSettings.System.putInt(mResolver,
                    CMSettings.System.STATUS_BAR_BATTERY_STATUS_PERCENT_STYLE, intValue);
                mPercentStyle.setSummary(mPercentStyle.getEntries()[index]);
                createCustomView();
                return true;
            } else if (preference == mChargeAnimationSpeed) {
                intValue = Integer.valueOf((String) newValue);
                index = mChargeAnimationSpeed.findIndexOfValue((String) newValue);
                CMSettings.System.putInt(mResolver,
                    CMSettings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, intValue);
                mChargeAnimationSpeed.setSummary(mChargeAnimationSpeed.getEntries()[index]);
                return true;
            } else if (preference == mCircleDotLength) {
                intValue = Integer.valueOf((String) newValue);
                index = mCircleDotLength.findIndexOfValue((String) newValue);
                CMSettings.System.putInt(mResolver,
                    CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, intValue);
                mCircleDotLength.setSummary(mCircleDotLength.getEntries()[index]);
                return true;
            } else if (preference == mCircleDotInterval) {
                intValue = Integer.valueOf((String) newValue);
                index = mCircleDotInterval.findIndexOfValue((String) newValue);
                CMSettings.System.putInt(mResolver,
                    CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, intValue);
                mCircleDotInterval.setSummary(mCircleDotInterval.getEntries()[index]);
                return true;
            } else if (preference == mBatteryColor) {
                hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                intHex = ColorPickerPreference.convertToColorInt(hex);
                CMSettings.System.putInt(mResolver,
                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                        intHex);
                preference.setSummary(hex);
                return true;
            } else if (preference == mTextColor) {
                hex = ColorPickerPreference.convertToARGB(
                        Integer.valueOf(String.valueOf(newValue)));
                intHex = ColorPickerPreference.convertToColorInt(hex);
                CMSettings.System.putInt(mResolver,
                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR, intHex);
                preference.setSummary(hex);
                return true;
            }
            return false;
        }

       private void showDialogInner(int id) {
            DialogFragment newFragment = MyAlertDialogFragment.newInstance(id);
            newFragment.setTargetFragment(this, 0);
            newFragment.show(getFragmentManager(), "dialog " + id);
        }

        public static class MyAlertDialogFragment extends DialogFragment {

            public static MyAlertDialogFragment newInstance(int id) {
                MyAlertDialogFragment frag = new MyAlertDialogFragment();
                Bundle args = new Bundle();
                args.putInt("id", id);
                frag.setArguments(args);
                return frag;
            }

            BatterySettingsFragment.BatterySettingsPreferenceFragment getOwner() {
                return (BatterySettingsFragment.BatterySettingsPreferenceFragment) getTargetFragment();
            }

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                int id = getArguments().getInt("id");
                switch (id) {
                    case DLG_RESET:
                        return new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.reset)
                        .setMessage(R.string.dlg_reset_values_message)
                        .setNegativeButton(R.string.cancel, null)
                        .setNeutralButton(R.string.dlg_reset_android,
                            new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 0);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_PERCENT_STYLE, 2);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, 0);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_SHOW_CIRCLE_DOTTED, 0);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, 3);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, 2);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                            DEFAULT_BATTERY_COLOR);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR,
                                            DEFAULT_BATTERY_COLOR);
                                getOwner().createCustomView();
                            }
                        })
                        .setPositiveButton(R.string.dlg_reset_android,
                            new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_STYLE, 2);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_PERCENT_STYLE, 0);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_CHARGING_ANIMATION_SPEED, 3);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_SHOW_CIRCLE_DOTTED, 1);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_LENGTH, 3);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_CIRCLE_DOT_INTERVAL, 2);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_COLOR,
                                            0xff33b5e5);
                                CMSettings.System.putInt(getOwner().mResolver,
                                        CMSettings.System.STATUS_BAR_BATTERY_STATUS_TEXT_COLOR,
                                            DEFAULT_BATTERY_COLOR);
                                getOwner().createCustomView();
                            }
                        })
                        .create();
                }
                throw new IllegalArgumentException("unknown id " + id);
            }

            @Override
            public void onCancel(DialogInterface dialog) {

            }
        }
    }
}