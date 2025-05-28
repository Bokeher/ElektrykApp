package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStub;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class TimetableInfoServiceTest {

    private CKZiUElektrykClient client;

    @Before
    public void init() {
        Config config = mock(Config.class);

        when(config.getAPIUrl()).thenReturn(TestConstants.URL);
        when(config.getToken()).thenReturn(TestConstants.TOKEN);

        client = new CKZiUElektrykClientStub(config);
    }

    @Test
    public void shouldGetTimetableInfo() {
        Optional<TimetableInfo> timetableInfoOptional = client.getTimetableInfo();

        if (timetableInfoOptional.isPresent()) {
            TimetableInfo timetableInfo = timetableInfoOptional.get();

            assertNotNull(timetableInfo.applyAt());
            assertNotNull(timetableInfo.info());
        }
    }
}
