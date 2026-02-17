package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertNotNull;

import com.example.planlekcji.ckziu_elektryk.client.calendar.Calendar;
import com.example.planlekcji.ckziu_elektryk.client.calendar.CalenderService;
import com.example.planlekcji.ckziu_elektryk.client.stubs.CKZiUElektrykClientStubFactory;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class CalenderServiceTest {

    private CalenderService calenderService;

    @Before
    public void init() throws IOException {
        CKZiUElektrykClient client = CKZiUElektrykClientStubFactory.createClient();

        this.calenderService = client.getCalenderService();
    }

    @Test
    public void shouldGetCalender() {
        Optional<Calendar> latestCalenderOptional = calenderService.getLatestCalender();

        if (latestCalenderOptional.isPresent()) {
            Calendar calendar = latestCalenderOptional.get();

            assertNotNull(calendar.sections());
            assertNotNull(calendar.fileName());
        }
    }
}
