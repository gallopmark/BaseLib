package pony.xcode.utils;

import android.text.TextUtils;

public class ParseUtils {
    public static double parseDouble(String source) {
        return parseDouble(source, 0.0);
    }

    public static double parseDouble(String source, double defaultValue) {
        if (TextUtils.isEmpty(source)) return defaultValue;
        try {
            return Double.parseDouble(source);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static int parseInt(String source) {
        return parseInt(source, 0);
    }

    public static int parseInt(String source, int defaultValue) {
        if (TextUtils.isEmpty(source)) return defaultValue;
        try {
            return Integer.parseInt(source);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static float parseFloat(String source) {
        return parseFloat(source, 0f);
    }

    public static float parseFloat(String source, float defaultValue) {
        if (TextUtils.isEmpty(source)) return defaultValue;
        try {
            return Float.parseFloat(source);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static boolean parseBoolean(String source) {
        return parseBoolean(source, false);
    }

    public static boolean parseBoolean(String source, boolean defaultValue) {
        if (TextUtils.isEmpty(source)) return defaultValue;
        try {
            return Boolean.parseBoolean(source);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    public static long parseLong(String source) {
        return parseLong(source, 0L);
    }

    public static long parseLong(String source, long defaultValue) {
        if (TextUtils.isEmpty(source)) return defaultValue;
        try {
            return Long.parseLong(source);
        } catch (Exception e) {
            return defaultValue;
        }
    }
}
