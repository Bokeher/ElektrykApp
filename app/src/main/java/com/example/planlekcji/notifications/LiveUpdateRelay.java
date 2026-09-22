package com.example.planlekcji.notifications;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LiveUpdateRelay {

    public enum UpdateType {
        REPLACEMENTS,
        TIMETABLE,
        ARTICLES
    }

    public record LiveUpdateEvent(UpdateType type, String title, String message, int targetTab) {}

    public interface LiveUpdateListener {
        void onLiveUpdateReceived(UpdateType type);

        default void onLiveUpdateReceived(LiveUpdateEvent event) {
            if (event != null) {
                onLiveUpdateReceived(event.type());
            }
        }
    }

    private static final List<LiveUpdateListener> listeners = new CopyOnWriteArrayList<>();

    private LiveUpdateRelay() {}

    public static void register(LiveUpdateListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public static void unregister(LiveUpdateListener listener) {
        if (listener != null) {
            listeners.remove(listener);
        }
    }

    public static void emit(UpdateType type) {
        if (type == null) return;
        emit(new LiveUpdateEvent(type, null, null, -1));
    }

    public static void emit(LiveUpdateEvent event) {
        if (event == null) return;
        for (LiveUpdateListener listener : listeners) {
            listener.onLiveUpdateReceived(event);
        }
    }

    public static boolean hasActiveListeners() {
        return !listeners.isEmpty();
    }
}
