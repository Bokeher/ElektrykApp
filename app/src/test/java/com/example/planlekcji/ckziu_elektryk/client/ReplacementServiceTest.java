package com.example.planlekcji.ckziu_elektryk.client;


import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementRequest;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementType;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;
import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class ReplacementServiceTest {

    private ReplacementService replacementService;

    @Before
    public void init() {
        Config config = mock(Config.class);

        when(config.getAPIUrl()).thenReturn(TestConstants.URL);
        when(config.getToken()).thenReturn(TestConstants.TOKEN);

        CKZiUElektrykClient client = new CKZiUElektrykClient(config);

        replacementService = client.getReplacementService();
    }

    @Test
    public void shouldGetLatestReplacement() {
        List<Replacement> replacements = replacementService.getLatestReplacements();

        assertNotNull(replacements);
    }

    @Test
    public void shouldGetLatestReplacementWithModeClasses() {
        List<Replacement> replacements = replacementService.getLatestReplacements(ReplacementType.CLASSES);

        assertNotNull(replacements);
    }

    @Test
    public void shouldGetLatestReplacementWithModeClassesAndDate() {
        List<Replacement> replacements = replacementService.getReplacements(ReplacementType.CLASSES, DateUtil.parseDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, "2025-09-09"));

        assertNotNull(replacements);
    }
}
