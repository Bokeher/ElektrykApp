package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertNotNull;

import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStubFactory;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfo;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class TimetableInfoServiceTest {

    private CKZiUElektrykClient client;

    @Before
    public void init() throws IOException {
        client = CKZiUElektrykClientStubFactory.createClient();
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
