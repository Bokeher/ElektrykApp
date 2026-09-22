package com.example.planlekcji.notifications;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public final class LiveUpdateRelay {

    public enum UpdateType {
        REPLACEMENTS,
        TIMETABLE,
        ARTICLES
    }

    public interface LiveUpdateListener {
        void onLiveUpdateReceived(UpdateType type);
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
        for (LiveUpdateListener listener : listeners) {
            listener.onLiveUpdateReceived(type);
        }
    }

    public static boolean hasActiveListeners() {
        return !listeners.isEmpty();
    }
}
