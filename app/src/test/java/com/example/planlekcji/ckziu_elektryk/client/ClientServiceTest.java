package com.example.planlekcji.ckziu_elektryk.client;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.PaginatedSuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.stubs.ClientServiceStub;
import com.example.planlekcji.ckziu_elektryk.client.stubs.TestConstants;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

public class ClientServiceTest {

    private Config config;

    @Before
    public void init() {
        config = mock(Config.class);
    }

    private void setCorrectAPIUrl() {
        given(config.getAPIUrl()).willReturn(TestConstants.URL);
    }

    @Test
    public void shouldSendRequestWhenTokenIsCorrect() {
        setCorrectAPIUrl();
        given(config.getToken()).willReturn(TestConstants.TOKEN);

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);

        JsonElement jsonElement = clientServiceStub.getData(Endpoint.TIMETABLE_INFO)
                .success(SuccessResponse::getJsonElement);

        assertNotNull(jsonElement);
    }

    @Test
    public void shouldSendRequestWhenTokenIsNotCorrect() {
        setCorrectAPIUrl();
        given(config.getToken()).willReturn("123");

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);

        AtomicReference<ErrorResponse> responseAtomicReference = new AtomicReference<>();

        JsonElement jsonElement = clientServiceStub.getData(Endpoint.TIMETABLE_INFO)
                .error(responseAtomicReference::set)
                .success(SuccessResponse::getJsonElement);

        ErrorResponse errorResponse = responseAtomicReference.get();

        assertEquals("Unauthorized", errorResponse.getMessage());
        assertEquals(401, errorResponse.getHttpStatus());
        assertNull(jsonElement);
    }

    @Test
    public void shouldSendRequestWhenTokenIsEmpty() {
        setCorrectAPIUrl();
        given(config.getToken()).willReturn("");

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);

        AtomicReference<ErrorResponse> responseAtomicReference = new AtomicReference<>();

        JsonElement jsonElement = clientServiceStub.getData(Endpoint.TIMETABLE_INFO)
                .error(responseAtomicReference::set)
                .success(SuccessResponse::getJsonElement);

        ErrorResponse errorResponse = responseAtomicReference.get();

        assertEquals("Unauthorized", errorResponse.getMessage());
        assertEquals(401, errorResponse.getHttpStatus());
        assertNull(jsonElement);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldSendRequestWhenAPIURLIsEmpty() {
        given(config.getToken()).willReturn(TestConstants.TOKEN);
        given(config.getAPIUrl()).willReturn("");

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);

        AtomicReference<ErrorResponse> responseAtomicReference = new AtomicReference<>();

        clientServiceStub.getData(Endpoint.TIMETABLE_INFO)
                .error(responseAtomicReference::set)
                .success(SuccessResponse::getJsonElement);

    }

    @Test
    public void shouldSendRequestWhenAPIURLIsNotCorrect() {
        given(config.getToken()).willReturn(TestConstants.TOKEN);
        given(config.getAPIUrl()).willReturn("http://localhost:8000/api/v");

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);

        AtomicReference<ErrorResponse> responseAtomicReference = new AtomicReference<>();

        JsonElement jsonElement = clientServiceStub.getData(Endpoint.TIMETABLE_INFO)
                .error(responseAtomicReference::set)
                .success(SuccessResponse::getJsonElement);

        ErrorResponse errorResponse = responseAtomicReference.get();

        assertTrue(errorResponse.getMessage().contains("could not be found"));
        assertEquals(404, errorResponse.getHttpStatus());
        assertNull(jsonElement);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldSendRequestWhenAPIURLHasNotCorrectServerPortOrIP() {
        given(config.getToken()).willReturn(TestConstants.TOKEN);
        given(config.getAPIUrl()).willReturn("http://localhost:8001/api/v1");

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);

        AtomicReference<ErrorResponse> responseAtomicReference = new AtomicReference<>();

        JsonElement jsonElement = clientServiceStub.getData(Endpoint.TIMETABLE_INFO)
                .error(responseAtomicReference::set)
                .success(SuccessResponse::getJsonElement);

        ErrorResponse errorResponse = responseAtomicReference.get();

        assertNull(errorResponse);
        assertNull(jsonElement);
    }

    @Test
    public void shouldRespondObjectWithPagination() {
        setCorrectAPIUrl();
        given(config.getToken()).willReturn(TestConstants.TOKEN);

        ClientServiceStub clientServiceStub = new ClientServiceStub(config);
        Page<LinkedTreeMap<String, Object>> page = clientServiceStub.getDataWithPagination(Endpoint.ARTICLES_LIST)
                .success(successResponse -> {
                    PaginatedSuccessResponse paginatedSuccessResponse = (PaginatedSuccessResponse) successResponse;

                    return paginatedSuccessResponse.getPage();
                });

        assertNotNull(page);
    }
}
