package com.example.planlekcji.notifications;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.planlekcji.R;

public final class NotificationPreferences {
    private static final String PREF_NAME = "sharedPrefs";

    private NotificationPreferences() {}

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Checks whether push notifications are globally enabled.
    public static boolean isNotificationsMasterEnabled(Context context) {
        if (context == null) return false;
        return getPrefs(context).getBoolean(context.getString(R.string.prefNotificationsMasterKey), false);
    }

    // Enables or disables push notifications globally.
    public static void setNotificationsMasterEnabled(Context context, boolean enabled) {
        if (context == null) return;
        getPrefs(context).edit().putBoolean(context.getString(R.string.prefNotificationsMasterKey), enabled).apply();
    }

    // Checks whether timetable update notifications are enabled.
    public static boolean isNotifyTimetableEnabled(Context context) {
        if (context == null) return false;
        return getPrefs(context).getBoolean(context.getString(R.string.prefNotifyTimetableKey), true);
    }

    // Enables or disables timetable update notifications.
    public static void setNotifyTimetableEnabled(Context context, boolean enabled) {
        if (context == null) return;
        getPrefs(context).edit().putBoolean(context.getString(R.string.prefNotifyTimetableKey), enabled).apply();
    }

    // Checks whether substitution notifications are enabled.
    public static boolean isNotifyReplacementsEnabled(Context context) {
        if (context == null) return false;
        return getPrefs(context).getBoolean(context.getString(R.string.prefNotifyReplacementsKey), true);
    }

    // Enables or disables substitution notifications.
    public static void setNotifyReplacementsEnabled(Context context, boolean enabled) {
        if (context == null) return;
        getPrefs(context).edit().putBoolean(context.getString(R.string.prefNotifyReplacementsKey), enabled).apply();
    }

    // Checks whether school article notifications are enabled.
    public static boolean isNotifyArticlesEnabled(Context context) {
        if (context == null) return false;
        return getPrefs(context).getBoolean(context.getString(R.string.prefNotifyArticlesKey), true);
    }

    // Enables or disables school article notifications.
    public static void setNotifyArticlesEnabled(Context context, boolean enabled) {
        if (context == null) return;
        getPrefs(context).edit().putBoolean(context.getString(R.string.prefNotifyArticlesKey), enabled).apply();
    }

    // Saves the latest FCM registration token.
    public static void setFcmToken(Context context, String token) {
        if (context == null) return;
        getPrefs(context).edit().putString(context.getString(R.string.prefFcmTokenKey), token != null ? token : "").apply();
    }

}
