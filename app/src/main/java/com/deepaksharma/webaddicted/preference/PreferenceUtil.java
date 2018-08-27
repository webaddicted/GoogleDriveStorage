package com.deepaksharma.webaddicted.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class PreferenceUtil {

    private static SharedPreferences sharedPreferences;
    private static PreferenceUtil preferenceUtil;


    public static void init(Context appContext) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext);
        preferenceUtil = new PreferenceUtil();
    }

    public static PreferenceUtil getInstance() {
        if (preferenceUtil == null)
            preferenceUtil = new PreferenceUtil();
        return preferenceUtil;
    }

    /**
     * Get data from preferenceUtil with key {key} & of type {obj}
     *
     * @param key
     * @param defautlValue
     * @param <T>
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getPreference(String key, T defautlValue) {
        try {
            if (defautlValue instanceof String) {
                return (T) sharedPreferences.getString(key, (String) defautlValue);
            } else if (defautlValue instanceof Integer) {
                return (T) (Integer) sharedPreferences.getInt(key, (Integer) defautlValue);
            } else if (defautlValue instanceof Boolean) {
                return (T) (Boolean) sharedPreferences.getBoolean(key, (Boolean) defautlValue);
            } else if (defautlValue instanceof Float) {
                return (T) (Float) sharedPreferences.getFloat(key, (Float) defautlValue);
            } else if (defautlValue instanceof Long) {
                return (T) (Long) sharedPreferences.getLong(key, (Long) defautlValue);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Save data to preferenceUtil with key {key} & of type {obj}
     *
     * @param key
     * @param value
     * @param <T>
     * @return
     */
    public <T> void setPreference(String key, T value) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            if (value instanceof String) {
                editor.putString(key, (String) value);
            } else if (value instanceof Integer) {
                editor.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                editor.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                editor.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                editor.putLong(key, (Long) value);
            }
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * clear preference when required
     */
    public void clearAllPreferences() {
        if (sharedPreferences != null)
            sharedPreferences.edit().clear().commit();
    }

    /**
     * Clear all Preference accept keyToBeSaved
     *
     * @param keyToBeSaved
     */
    public void clearAllPreferences(String[] keyToBeSaved) {
        if (sharedPreferences != null) {
            Map<String, Object> map = new ConcurrentHashMap<>(sharedPreferences.getAll());
            for (String stringObjectEntry : map.keySet()) {
                if (!Arrays.asList(keyToBeSaved).contains(stringObjectEntry)) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove(stringObjectEntry).commit();
                }
            }
        }
    }
}