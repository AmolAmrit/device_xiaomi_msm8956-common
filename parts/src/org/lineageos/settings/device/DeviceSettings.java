/*
 * Copyright (C) 2018 The Xiaomi-SDM660 Project
 * Copyright (C) 2019-2020 mhkjahromi <m.h.k.jahromi@gmail.com>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package org.lineageos.settings.device;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.preference.PreferenceFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceCategory;

import org.lineageos.settings.device.kcal.KCalSettingsActivity;
import org.lineageos.settings.device.preferences.SecureSettingCustomSeekBarPreference;
import org.lineageos.settings.device.preferences.SecureSettingListPreference;
import org.lineageos.settings.device.preferences.SecureSettingSwitchPreference;
import org.lineageos.settings.device.preferences.VibrationSeekBarPreference;

public class DeviceSettings extends PreferenceFragment implements
        Preference.OnPreferenceChangeListener {

    // Buttons
    private static final String CATEGORY_BUTTONS = "buttons";
    public static final String PREF_SWAP_BUTTONS = "swapbuttons";
    public static final String SWAP_BUTTONS_PATH = "/proc/touchpanel/reversed_keys_enable";

    // Fingerprint options
    private static final String CATEGORY_FINGERPRINT_OPTIONS = "fp_options";
    public static final String PREF_FPWAKEUP = "fpwakeup";
    public static final String FPWAKEUP_PATH = "/sys/devices/soc/soc:fpc_fpc1020/enable_wakeup";
    private static final String CATEGORY_FP_HOME = "fp_home";
    public static final String PREF_FPHOME = "fphome";
    public static final String FPHOME_PATH = "/sys/devices/soc/soc:fpc_fpc1020/enable_key_events";
    private static final String CATEGORY_FP_POCKET = "fp_pocket";
    public static final String PREF_FPPOCKET = "fppocket";
    public static final String FPPOCKET_PATH = "/sys/devices/soc/soc:fpc_fpc1020/proximity_state";

    // Gestures
    private static final String CATEGORY_GESTURES= "gestures";
    public static final String PREF_DT2W = "dt2w";
    public static final String DT2W_PATH = "/proc/touchpanel/double_tap_enable";

    // USB Fastcharge
    private static final String CATEGORY_USB= "usb";
    public static final String PREF_USB_FASTCHARGE = "usbfastcharge";
    public static final String USB_FASTCHARGE_PATH = "/sys/kernel/fast_charge/force_fast_charge";

    // Torch
    public static final String PREF_TORCH_BRIGHTNESS = "torch_brightness";
    public static final String TORCH_1_BRIGHTNESS_PATH = "/sys/devices/soc/400f000.qcom," +
            "spmi/spmi-0/spmi0-03/400f000.qcom,spmi:qcom,pmi8994@3:qcom,leds@d300/leds/led:torch_0/max_brightness";
    public static final String TORCH_2_BRIGHTNESS_PATH = "/sys/devices/soc/400f000.qcom," +
            "spmi/spmi-0/spmi0-03/400f000.qcom,spmi:qcom,pmi8994@3:qcom,leds@d300/leds/led:torch_1/max_brightness";

    // Haptic feedback
    public static final String PREF_VIBRATION_STRENGTH = "vibration_strength";
    public static final String VIBRATION_STRENGTH_PATH = "/sys/class/timed_output/vibrator/vtg_level";
    public static final int MIN_VIBRATION = 12;
    public static final int MAX_VIBRATION = 127;

    // Display
    private static final String CATEGORY_DISPLAY = "display";
    private static final String PREF_DEVICE_DOZE = "device_doze";
    private static final String PREF_DEVICE_KCAL = "device_kcal";
    private static final String DEVICE_DOZE_PACKAGE_NAME = "org.lineageos.settings.doze";

    // Spectrum
    public static final String PREF_SPECTRUM = "spectrum";
    public static final String SPECTRUM_SYSTEM_PROPERTY = "persist.spectrum.profile";

    private SecureSettingListPreference mSPECTRUM;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences_xiaomi_parts, rootKey);

        String device = FileUtils.getStringProp("ro.build.product", "unknown");

        SecureSettingCustomSeekBarPreference TorchBrightness = (SecureSettingCustomSeekBarPreference) findPreference(PREF_TORCH_BRIGHTNESS);
        TorchBrightness.setEnabled(FileUtils.fileWritable(TORCH_1_BRIGHTNESS_PATH) &&
                FileUtils.fileWritable(TORCH_2_BRIGHTNESS_PATH));
        TorchBrightness.setOnPreferenceChangeListener(this);

        VibrationSeekBarPreference vibrationStrength = (VibrationSeekBarPreference) findPreference(PREF_VIBRATION_STRENGTH);
        vibrationStrength.setEnabled(FileUtils.fileWritable(VIBRATION_STRENGTH_PATH));
        vibrationStrength.setOnPreferenceChangeListener(this);

        PreferenceCategory displayCategory = (PreferenceCategory) findPreference(CATEGORY_DISPLAY);
        if (isAppNotInstalled(DEVICE_DOZE_PACKAGE_NAME)) {
            displayCategory.removePreference(findPreference(PREF_DEVICE_DOZE));
        }

        Preference kcal = findPreference(PREF_DEVICE_KCAL);

        kcal.setOnPreferenceClickListener(preference -> {
            Intent intent = new Intent(getActivity().getApplicationContext(), KCalSettingsActivity.class);
            startActivity(intent);
            return true;
        });

        mSPECTRUM = (SecureSettingListPreference) findPreference(PREF_SPECTRUM);
        mSPECTRUM.setValue(FileUtils.getStringProp(SPECTRUM_SYSTEM_PROPERTY, "0"));
        mSPECTRUM.setSummary(mSPECTRUM.getEntry());
        mSPECTRUM.setOnPreferenceChangeListener(this);

        if (FileUtils.fileWritable(SWAP_BUTTONS_PATH)) {
            SecureSettingSwitchPreference swapbuttons = (SecureSettingSwitchPreference) findPreference(PREF_SWAP_BUTTONS);
            swapbuttons.setChecked(FileUtils.getFileValueAsBoolean(SWAP_BUTTONS_PATH, false));
            swapbuttons.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_BUTTONS));
        }

        if (FileUtils.fileWritable(FPWAKEUP_PATH)) {
            SecureSettingSwitchPreference fpwakeup = (SecureSettingSwitchPreference) findPreference(PREF_FPWAKEUP);
            fpwakeup.setChecked(FileUtils.getFileValueAsBoolean(FPWAKEUP_PATH, false));
            fpwakeup.setOnPreferenceChangeListener(this);

            FileUtils.fileWritable(FPHOME_PATH);
            SecureSettingSwitchPreference fphome = (SecureSettingSwitchPreference) findPreference(PREF_FPHOME);
            fphome.setChecked(FileUtils.getFileValueAsBoolean(FPHOME_PATH, false));
            fphome.setOnPreferenceChangeListener(this);
        
            FileUtils.fileWritable(FPPOCKET_PATH);
            SecureSettingSwitchPreference fppocket = (SecureSettingSwitchPreference) findPreference(PREF_FPPOCKET);
            fppocket.setChecked(FileUtils.getFileValueAsBoolean(FPPOCKET_PATH, false));
            fppocket.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_FINGERPRINT_OPTIONS));
        }

        if (FileUtils.fileWritable(DT2W_PATH)) {
            SecureSettingSwitchPreference dt2w = (SecureSettingSwitchPreference) findPreference(PREF_DT2W);
            dt2w.setChecked(FileUtils.getFileValueAsBoolean(DT2W_PATH, false));
            dt2w.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_GESTURES));
        }

        if (FileUtils.fileWritable(USB_FASTCHARGE_PATH)) {
            SecureSettingSwitchPreference usbfastcharge = (SecureSettingSwitchPreference) findPreference(PREF_USB_FASTCHARGE);
            usbfastcharge.setChecked(FileUtils.getFileValueAsBoolean(USB_FASTCHARGE_PATH, false));
            usbfastcharge.setOnPreferenceChangeListener(this);
        } else {
            getPreferenceScreen().removePreference(findPreference(CATEGORY_USB));
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object value) {
        final String key = preference.getKey();
        switch (key) {
            case PREF_TORCH_BRIGHTNESS:
                FileUtils.setValue(TORCH_1_BRIGHTNESS_PATH, (int) value);
                FileUtils.setValue(TORCH_2_BRIGHTNESS_PATH, (int) value);
                break;

            case PREF_VIBRATION_STRENGTH:
                double vibrationValue = (int) value / 100.0 * (MAX_VIBRATION - MIN_VIBRATION) + MIN_VIBRATION;
                FileUtils.setValue(VIBRATION_STRENGTH_PATH, vibrationValue);
                break;

            case PREF_SPECTRUM:
                mSPECTRUM.setValue((String) value);
                mSPECTRUM.setSummary(mSPECTRUM.getEntry());
                FileUtils.setStringProp(SPECTRUM_SYSTEM_PROPERTY, (String) value);
                break;

            case PREF_SWAP_BUTTONS:
                FileUtils.setValue(SWAP_BUTTONS_PATH, (boolean) value);
                break;

            case PREF_FPWAKEUP:
                FileUtils.setValue(FPWAKEUP_PATH, (boolean) value);
                break;

            case PREF_FPHOME:
                FileUtils.setValue(FPHOME_PATH, (boolean) value);
                break;

            case PREF_FPPOCKET:
                FileUtils.setValue(FPPOCKET_PATH, (boolean) value);
                break;

            case PREF_DT2W:
                FileUtils.setValue(DT2W_PATH, (boolean) value);
                break;

            case PREF_USB_FASTCHARGE:
                FileUtils.setValue(USB_FASTCHARGE_PATH, (boolean) value);
                break;

            default:
                break;
        }
        return true;
    }

    private boolean isAppNotInstalled(String uri) {
        PackageManager packageManager = getContext().getPackageManager();
        try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return false;
        } catch (PackageManager.NameNotFoundException e) {
            return true;
        }
    }
}
