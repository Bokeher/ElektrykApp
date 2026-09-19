package com.example.planlekcji.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.fragments.ViewPagerAdapter;

public final class NotificationHelper {
    private static final String TAG = "NotificationHelper";

    public static final String CHANNEL_TIMETABLE = "channel_timetable";
    public static final String CHANNEL_REPLACEMENTS = "channel_replacements";
    public static final String CHANNEL_ARTICLES = "channel_articles";

    public static final int NOTIFICATION_ID_TIMETABLE = 1001;
    public static final int NOTIFICATION_ID_REPLACEMENTS = 1002;
    public static final int NOTIFICATION_ID_ARTICLES = 1003;

    private static final int ACCENT_COLOR = Color.parseColor("#FFC107");

    private NotificationHelper() {}

    // Initializes Android Notification Channels (API 26+).
    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationManager manager = context.getSystemService(NotificationManager.class);
        if (manager == null) {
            return;
        }

        NotificationChannel timetableChannel = new NotificationChannel(
                CHANNEL_TIMETABLE,
                context.getString(R.string.notification_channel_timetable),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        timetableChannel.enableLights(true);
        timetableChannel.setLightColor(ACCENT_COLOR);

        NotificationChannel replacementsChannel = new NotificationChannel(
                CHANNEL_REPLACEMENTS,
                context.getString(R.string.notification_channel_replacements),
                NotificationManager.IMPORTANCE_HIGH
        );
        replacementsChannel.enableLights(true);
        replacementsChannel.setLightColor(ACCENT_COLOR);
        replacementsChannel.enableVibration(true);

        NotificationChannel articlesChannel = new NotificationChannel(
                CHANNEL_ARTICLES,
                context.getString(R.string.notification_channel_articles),
                NotificationManager.IMPORTANCE_DEFAULT
        );
        articlesChannel.enableLights(true);
        articlesChannel.setLightColor(ACCENT_COLOR);

        manager.createNotificationChannel(timetableChannel);
        manager.createNotificationChannel(replacementsChannel);
        manager.createNotificationChannel(articlesChannel);
    }

    private static boolean hasNotificationPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private static PendingIntent createPendingIntent(Context context, int targetTab, int requestCode) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra(MainActivity.EXTRA_TARGET_TAB, targetTab);
        return PendingIntent.getActivity(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
    }

    private static Bitmap getAppIconBitmap(Context context) {
        try {
            Drawable drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher_round);
            if (drawable == null) {
                drawable = ContextCompat.getDrawable(context, R.mipmap.ic_launcher);
            }
            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable != null) {
                int width = drawable.getIntrinsicWidth() > 0 ? drawable.getIntrinsicWidth() : 192;
                int height = drawable.getIntrinsicHeight() > 0 ? drawable.getIntrinsicHeight() : 192;
                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                drawable.draw(canvas);
                return bitmap;
            }
        } catch (Exception e) {
            Log.e(TAG, "Cannot load app icon for notification", e);
        }
        return null;
    }

    private static NotificationCompat.Builder createBaseNotificationBuilder(Context context, String channelId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setColor(ACCENT_COLOR)
                .setAutoCancel(true);

        Bitmap appIcon = getAppIconBitmap(context);
        if (appIcon != null) {
            builder.setLargeIcon(appIcon);
        }

        return builder;
    }

    // Displays a timetable update notification for the given token.
    public static void showTimetableNotification(Context context, String token) {
        if (!hasNotificationPermission(context)) {
            return;
        }

        String title = context.getString(R.string.notification_timetable_title);
        String body = (token != null && !token.trim().isEmpty()) ? token.trim() : "";

        PendingIntent pendingIntent = createPendingIntent(context, ViewPagerAdapter.TIMETABLE_TAB_ID, NOTIFICATION_ID_TIMETABLE);

        NotificationCompat.Builder builder = createBaseNotificationBuilder(context, CHANNEL_TIMETABLE)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_TIMETABLE, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Cannot show timetable notification without permission", e);
        }
    }

    // Displays a substitution notification for the specified class.
    public static void showClassReplacementNotification(Context context, String classToken) {
        if (!hasNotificationPermission(context)) {
            return;
        }

        String title = context.getString(R.string.notification_replacement_title);
        String tokenStr = (classToken != null && !classToken.trim().isEmpty()) ? classToken.trim() : "";
        String body = tokenStr.isEmpty()
                ? context.getString(R.string.notification_replacement_general_body)
                : context.getString(R.string.notification_replacement_class_body, tokenStr);

        PendingIntent pendingIntent = createPendingIntent(context, ViewPagerAdapter.REPLACEMENTS_TAB_ID, NOTIFICATION_ID_REPLACEMENTS);

        NotificationCompat.Builder builder = createBaseNotificationBuilder(context, CHANNEL_REPLACEMENTS)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REPLACEMENTS, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Cannot show replacement notification without permission", e);
        }
    }

    // Displays a general substitution notification for teachers.
    public static void showTeacherReplacementNotification(Context context) {
        if (!hasNotificationPermission(context)) {
            return;
        }

        String title = context.getString(R.string.notification_replacement_title);
        String body = context.getString(R.string.notification_replacement_teacher_body);

        PendingIntent pendingIntent = createPendingIntent(context, ViewPagerAdapter.REPLACEMENTS_TAB_ID, NOTIFICATION_ID_REPLACEMENTS);

        NotificationCompat.Builder builder = createBaseNotificationBuilder(context, CHANNEL_REPLACEMENTS)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_REPLACEMENTS, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Cannot show teacher replacement notification without permission", e);
        }
    }

    // Displays a new school article notification.
    public static void showArticleNotification(Context context, String articleTitle) {
        if (!hasNotificationPermission(context)) {
            return;
        }

        String title = context.getString(R.string.notification_article_title);
        String body = (articleTitle != null && !articleTitle.trim().isEmpty()) ? articleTitle.trim() : "";

        PendingIntent pendingIntent = createPendingIntent(context, ViewPagerAdapter.ARTICLES_TAB_ID, NOTIFICATION_ID_ARTICLES);

        NotificationCompat.Builder builder = createBaseNotificationBuilder(context, CHANNEL_ARTICLES)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_ARTICLES, builder.build());
        } catch (SecurityException e) {
            Log.e(TAG, "Cannot show article notification without permission", e);
        }
    }
}
