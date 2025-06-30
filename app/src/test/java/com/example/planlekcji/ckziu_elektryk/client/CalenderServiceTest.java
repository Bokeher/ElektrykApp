package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.planlekcji.ckziu_elektryk.client.calendar.Calendar;
import com.example.planlekcji.ckziu_elektryk.client.calendar.CalenderService;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

public class CalenderServiceTest {

    private CalenderService calenderService;

    @Before
    public void init() {
        Config config = mock(Config.class);

        when(config.getAPIUrl()).thenReturn(TestConstants.URL);
        when(config.getToken()).thenReturn(TestConstants.TOKEN);

        CKZiUElektrykClient client = new CKZiUElektrykClient(config);

        this.calenderService = client.getCalenderService();
    }

    @Test
    public void shouldGetCalender() {
        Optional<Calendar> latestCalenderOptional = calenderService.getLatestCalender();

        if (latestCalenderOptional.isPresent()) {
            Calendar calendar = latestCalenderOptional.get();

            assertNotNull(calendar.content());
            assertNotNull(calendar.fileName());
        }
    }
}
