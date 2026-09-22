package com.example.planlekcji.notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Locale;

public final class FcmTopicManager {
    private static final String TAG = "FcmTopicManager";

    public static final String TOPIC_ARTICLES = "articles";
    public static final String TOPIC_REPLACEMENTS_TEACHERS = "replacements_teachers";

    private static final String PREF_LAST_TIMETABLE_TOPIC = "fcm_last_timetable_topic";
    private static final String PREF_LAST_REPLACEMENTS_TOPIC = "fcm_last_replacements_topic";

    private FcmTopicManager() {}

    private static SharedPreferences getPrefs(Context context) {
        return context.getApplicationContext().getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);
    }

    // Sanitizes topic names by trimming and converting to uppercase for FCM topic compatibility.
    public static String sanitizeTopicName(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "DEFAULT";
        }
        return input.trim().toUpperCase(Locale.ROOT);
    }

    public static String getTimetableTopic(SchoolEntryType type, String token) {
        String prefix;
        if (type == SchoolEntryType.CLASSES) {
            prefix = "timetable_class_";
        } else if (type == SchoolEntryType.TEACHERS) {
            prefix = "timetable_teacher_";
        } else {
            prefix = "timetable_classroom_";
        }
        return prefix + sanitizeTopicName(token);
    }

    public static String getReplacementsTopic(SchoolEntryType type, String token) {
        if (type == SchoolEntryType.CLASSES) {
            return "replacements_class_" + sanitizeTopicName(token);
        } else if (type == SchoolEntryType.TEACHERS) {
            return TOPIC_REPLACEMENTS_TEACHERS;
        } else {
            return "replacements_classrooms";
        }
    }

    // Synchronizes all FCM topic subscriptions with the current user preferences and selected entry.
    public static void syncSubscriptions(Context context) {
        if (context == null) return;

        boolean masterEnabled = NotificationPreferences.isNotificationsMasterEnabled(context);
        boolean timetableEnabled = NotificationPreferences.isNotifyTimetableEnabled(context);
        boolean replacementsEnabled = NotificationPreferences.isNotifyReplacementsEnabled(context);
        boolean articlesEnabled = NotificationPreferences.isNotifyArticlesEnabled(context);

        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
        SharedPreferences prefs = getPrefs(context);

        String lastTimetableTopic = prefs.getString(PREF_LAST_TIMETABLE_TOPIC, null);
        String lastReplacementsTopic = prefs.getString(PREF_LAST_REPLACEMENTS_TOPIC, null);

        // Articles topic subscription
        if (masterEnabled && articlesEnabled) {
            fcm.subscribeToTopic(TOPIC_ARTICLES)
                    .addOnCompleteListener(t -> Log.d(TAG, "Subscribed to " + TOPIC_ARTICLES));
        } else {
            fcm.unsubscribeFromTopic(TOPIC_ARTICLES)
                    .addOnCompleteListener(t -> Log.d(TAG, "Unsubscribed from " + TOPIC_ARTICLES));
        }

        // Timetable topic subscription is currently omitted
        SchoolEntryType currentType = MainActivity.getTimetableType(context);
        String currentToken = MainActivity.getToken(context, currentType);

        if (lastTimetableTopic != null) {
            fcm.unsubscribeFromTopic(lastTimetableTopic)
                    .addOnCompleteListener(t -> {
                        Log.d(TAG, "Unsubscribed from timetable: " + lastTimetableTopic);
                        prefs.edit().remove(PREF_LAST_TIMETABLE_TOPIC).apply();
                    });
        }

        // Substitutions topic subscription for class or teachers
        String desiredReplacementsTopic = getReplacementsTopic(currentType, currentToken);

        if (masterEnabled && replacementsEnabled && (!currentToken.isEmpty() || currentType == SchoolEntryType.TEACHERS)) {
            if (lastReplacementsTopic != null && !lastReplacementsTopic.equals(desiredReplacementsTopic)) {
                fcm.unsubscribeFromTopic(lastReplacementsTopic)
                        .addOnCompleteListener(t -> Log.d(TAG, "Unsubscribed old replacements: " + lastReplacementsTopic));
            }
            fcm.subscribeToTopic(desiredReplacementsTopic)
                    .addOnCompleteListener(t -> {
                        Log.d(TAG, "Subscribed to replacements topic: " + desiredReplacementsTopic);
                        prefs.edit().putString(PREF_LAST_REPLACEMENTS_TOPIC, desiredReplacementsTopic).apply();
                    });
        } else {
            if (lastReplacementsTopic != null) {
                fcm.unsubscribeFromTopic(lastReplacementsTopic)
                        .addOnCompleteListener(t -> {
                            Log.d(TAG, "Unsubscribed from replacements: " + lastReplacementsTopic);
                            prefs.edit().remove(PREF_LAST_REPLACEMENTS_TOPIC).apply();
                        });
            }
        }
    }

    // Unsubscribes from all topics when master notifications are disabled.
    public static void unsubscribeFromAll(Context context) {
        if (context == null) return;

        FirebaseMessaging fcm = FirebaseMessaging.getInstance();
        SharedPreferences prefs = getPrefs(context);

        fcm.unsubscribeFromTopic(TOPIC_ARTICLES);

        String lastTimetable = prefs.getString(PREF_LAST_TIMETABLE_TOPIC, null);
        if (lastTimetable != null) {
            fcm.unsubscribeFromTopic(lastTimetable);
            prefs.edit().remove(PREF_LAST_TIMETABLE_TOPIC).apply();
        }

        String lastReplacements = prefs.getString(PREF_LAST_REPLACEMENTS_TOPIC, null);
        if (lastReplacements != null) {
            fcm.unsubscribeFromTopic(lastReplacements);
            prefs.edit().remove(PREF_LAST_REPLACEMENTS_TOPIC).apply();
        }
    }
}
