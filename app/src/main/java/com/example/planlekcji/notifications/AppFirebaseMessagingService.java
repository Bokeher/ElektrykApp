package com.example.planlekcji.notifications;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.fragments.ViewPagerAdapter;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class AppFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "AppFirebaseMsgService";

    @Override
    @SuppressWarnings("deprecation")
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "New FCM Token generated: " + token);
        NotificationPreferences.setFcmToken(this, token);
        FcmTopicManager.syncSubscriptions(this);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        if (!NotificationPreferences.isNotificationsMasterEnabled(this)) {
            return;
        }

        Map<String, String> data = remoteMessage.getData();
        if (data.isEmpty()) {
            return;
        }

        String type = data.get("type");
        if (type == null) {
            return;
        }

        switch (type.trim().toUpperCase()) {
            case "TIMETABLE" -> handleTimetableMessage(data);
            case "REPLACEMENT", "REPLACEMENTS" -> handleReplacementMessage(data);
            case "ARTICLE", "ARTICLES" -> handleArticleMessage(data);
        }
    }

    // Handles timetable update notifications for the currently selected timetable.
    private void handleTimetableMessage(Map<String, String> data) {
        if (!NotificationPreferences.isNotifyTimetableEnabled(this)) {
            return;
        }

        SchoolEntryType currentType = MainActivity.getTimetableType(this);
        String currentToken = MainActivity.getToken(this, currentType);

        String msgToken = data.get("token");
        String msgEntryType = data.get("entry_type");

        if (msgToken != null && !msgToken.trim().isEmpty()) {
            if (!msgToken.trim().equalsIgnoreCase(currentToken.trim())) {
                return;
            }
        }

        if (msgEntryType != null && !msgEntryType.trim().isEmpty()) {
            if (!msgEntryType.trim().equalsIgnoreCase(currentType.name())) {
                return;
            }
        }

        String displayToken = (currentToken != null && !currentToken.trim().isEmpty()) ? currentToken : msgToken;
        NotificationHelper.showTimetableNotification(this, displayToken);
        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.TIMETABLE);
    }

    // Handles substitution notifications without individual lesson details.
    private void handleReplacementMessage(Map<String, String> data) {
        if (!NotificationPreferences.isNotifyReplacementsEnabled(this)) {
            return;
        }

        SchoolEntryType currentType = MainActivity.getTimetableType(this);
        String currentToken = MainActivity.getToken(this, currentType);

        String targetType = data.get("target_type");
        boolean isTeacherTarget = "TEACHER".equalsIgnoreCase(targetType) || currentType == SchoolEntryType.TEACHERS;

        String classToken = data.get("token");
        if (classToken == null || classToken.trim().isEmpty()) {
            classToken = currentToken;
        }

        String notificationTitle = getString(R.string.notification_replacement_title);
        String notificationBody;
        if (isTeacherTarget) {
            notificationBody = getString(R.string.notification_replacement_teacher_body);
        } else if (classToken != null && !classToken.trim().isEmpty()) {
            notificationBody = getString(R.string.notification_replacement_class_body, classToken.trim());
        } else {
            notificationBody = getString(R.string.notification_replacement_general_body);
        }

        boolean isForeground = LiveUpdateRelay.hasActiveListeners();
        if (!isForeground) {
            if (isTeacherTarget) {
                NotificationHelper.showTeacherReplacementNotification(this);
            } else {
                NotificationHelper.showClassReplacementNotification(this, classToken);
            }
        }

        LiveUpdateRelay.emit(new LiveUpdateRelay.LiveUpdateEvent(
                LiveUpdateRelay.UpdateType.REPLACEMENTS,
                notificationTitle,
                notificationBody,
                ViewPagerAdapter.REPLACEMENTS_TAB_ID
        ));
    }

    // Handles newly published school article notifications.
    private void handleArticleMessage(Map<String, String> data) {
        if (!NotificationPreferences.isNotifyArticlesEnabled(this)) {
            return;
        }

        String title = data.get("title");
        if (title == null || title.trim().isEmpty()) {
            title = data.get("body");
        }

        String notificationTitle = getString(R.string.notification_article_title);
        String notificationBody = (title != null && !title.trim().isEmpty()) ? title.trim() : "";

        boolean isForeground = LiveUpdateRelay.hasActiveListeners();
        if (!isForeground) {
            NotificationHelper.showArticleNotification(this, title);
        }

        LiveUpdateRelay.emit(new LiveUpdateRelay.LiveUpdateEvent(
                LiveUpdateRelay.UpdateType.ARTICLES,
                notificationTitle,
                notificationBody,
                ViewPagerAdapter.ARTICLES_TAB_ID
        ));
    }
}
