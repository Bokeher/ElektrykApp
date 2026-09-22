package com.example.planlekcji.notifications;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class LiveUpdateRelayTest {

    private final List<LiveUpdateRelay.LiveUpdateListener> registeredListeners = new ArrayList<>();

    private LiveUpdateRelay.LiveUpdateListener createAndRegisterListener(List<LiveUpdateRelay.UpdateType> receivedEvents) {
        LiveUpdateRelay.LiveUpdateListener listener = receivedEvents::add;
        LiveUpdateRelay.register(listener);
        registeredListeners.add(listener);
        return listener;
    }

    @After
    public void cleanup() {
        for (LiveUpdateRelay.LiveUpdateListener listener : registeredListeners) {
            LiveUpdateRelay.unregister(listener);
        }
        registeredListeners.clear();
    }

    @Test
    public void shouldRegisterAndReceiveAllUpdateTypes() {
        List<LiveUpdateRelay.UpdateType> received = new ArrayList<>();
        createAndRegisterListener(received);

        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.REPLACEMENTS);
        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.TIMETABLE);
        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.ARTICLES);

        assertEquals(3, received.size());
        assertEquals(LiveUpdateRelay.UpdateType.REPLACEMENTS, received.get(0));
        assertEquals(LiveUpdateRelay.UpdateType.TIMETABLE, received.get(1));
        assertEquals(LiveUpdateRelay.UpdateType.ARTICLES, received.get(2));
    }

    @Test
    public void shouldNotReceiveUpdatesAfterUnregister() {
        List<LiveUpdateRelay.UpdateType> received = new ArrayList<>();
        LiveUpdateRelay.LiveUpdateListener listener = createAndRegisterListener(received);

        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.REPLACEMENTS);
        assertEquals(1, received.size());

        LiveUpdateRelay.unregister(listener);
        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.TIMETABLE);

        assertEquals(1, received.size());
    }

    @Test
    public void shouldNotAddDuplicateListeners() {
        List<LiveUpdateRelay.UpdateType> received = new ArrayList<>();
        LiveUpdateRelay.LiveUpdateListener listener = received::add;

        LiveUpdateRelay.register(listener);
        LiveUpdateRelay.register(listener);
        registeredListeners.add(listener);

        LiveUpdateRelay.emit(LiveUpdateRelay.UpdateType.REPLACEMENTS);

        assertEquals(1, received.size());
    }

    @Test
    public void shouldHandleNullGracefully() {
        LiveUpdateRelay.register(null);
        LiveUpdateRelay.unregister(null);
        LiveUpdateRelay.emit((LiveUpdateRelay.UpdateType) null);
        LiveUpdateRelay.emit((LiveUpdateRelay.LiveUpdateEvent) null);
        // No exception expected
    }

    @Test
    public void shouldTrackActiveListenersState() {
        List<LiveUpdateRelay.UpdateType> received = new ArrayList<>();
        LiveUpdateRelay.LiveUpdateListener listener = createAndRegisterListener(received);

        assertTrue(LiveUpdateRelay.hasActiveListeners());

        LiveUpdateRelay.unregister(listener);
        registeredListeners.remove(listener);

        assertFalse(LiveUpdateRelay.hasActiveListeners());
    }

    @Test
    public void shouldReceiveLiveUpdateEvent() {
        List<LiveUpdateRelay.LiveUpdateEvent> receivedEvents = new ArrayList<>();
        LiveUpdateRelay.LiveUpdateListener listener = new LiveUpdateRelay.LiveUpdateListener() {
            @Override
            public void onLiveUpdateReceived(LiveUpdateRelay.UpdateType type) {}

            @Override
            public void onLiveUpdateReceived(LiveUpdateRelay.LiveUpdateEvent event) {
                receivedEvents.add(event);
            }
        };
        LiveUpdateRelay.register(listener);
        registeredListeners.add(listener);

        LiveUpdateRelay.LiveUpdateEvent testEvent = new LiveUpdateRelay.LiveUpdateEvent(
                LiveUpdateRelay.UpdateType.REPLACEMENTS, "Zastępstwa", "Nowe zastępstwa dla 1TP", 1);
        LiveUpdateRelay.emit(testEvent);

        assertEquals(1, receivedEvents.size());
        assertEquals(testEvent, receivedEvents.get(0));
    }
}
