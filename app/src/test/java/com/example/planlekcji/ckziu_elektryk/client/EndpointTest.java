package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;

import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

import org.junit.Test;

import java.util.Map;

public class EndpointTest {

    @Test
    public void shouldCreateEndpointWithPlaceholders() {
        Endpoint endpoint = Endpoint.TIMETABLES_CLASS
                .withPlaceholders(Map.of("{school_entry_shortcut}", "filename"));

        assertEquals("timetables/classes/filename", endpoint.getName());
    }

    @Test
    public void shouldCreateEndpointWithPlaceholdersWithReplacingTwice() {
        Endpoint.TIMETABLES_CLASS
                .withPlaceholders(Map.of("{school_entry_shortcut}", "filename"));

        Endpoint endpoint2 = Endpoint.TIMETABLES_CLASS
                .withPlaceholders(Map.of("{school_entry_shortcut}", "filename2"));

        assertEquals("timetables/classes/filename2", endpoint2.getName());
    }
}
