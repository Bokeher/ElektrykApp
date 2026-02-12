package com.example.planlekcji.ckziu_elektryk.client.common;

import androidx.annotation.NonNull;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.PaginatedSuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class ClientService {

    private final OkHttpClient httpClient;
    protected final Gson gson;
    private final Config config;

    protected ClientService(Config config) {
        this.config = config;
        this.httpClient = new OkHttpClient();
        this.gson = new Gson();
    }

    protected APIResponseCall getData(@NonNull Endpoint endpoint, BiFunction<Integer, JsonElement, SuccessResponse> successResponseBiFunction) {
        Request request = new Request.Builder()
                .addHeader("Authorization", config.getToken())
                .url(config.getAPIUrl() + "/" + endpoint.getName())
                .get()
                .build();

        APIResponseCall apiResponseCall = new APIResponseCall();

        try (Response response = this.httpClient.newCall(request).execute()) {
            ResponseBody body = response.body();

            if (body == null) {
                apiResponseCall.setErrorResponse(new ErrorResponse(response.code(), "Empty response"));
                return apiResponseCall;
            }

            JsonElement jsonElement;
            try {
                jsonElement = gson.fromJson(body.charStream(), JsonElement.class);
            } catch (JsonSyntaxException e) {
                apiResponseCall.setErrorResponse(new ErrorResponse(response.code(), "Parsing error: " + e.getMessage()));
                return apiResponseCall;
            }

            if (!response.isSuccessful()) {
                String errorMessage = "Unknown error";
                try {
                    if (jsonElement.isJsonObject() && jsonElement.getAsJsonObject().has("message")) {
                        errorMessage = jsonElement.getAsJsonObject().get("message").getAsString();
                    }
                } catch (Exception e) {
                    errorMessage = "Error code: " + response.code();
                }

                apiResponseCall.setErrorResponse(new ErrorResponse(response.code(), errorMessage));
                return apiResponseCall;
            }

            apiResponseCall.setSuccessResponse(successResponseBiFunction.apply(response.code(), jsonElement));
        } catch (IOException exception) {
            config.getFailedApiConnectionCallback().accept(exception);
        }

        return apiResponseCall;
    }

    protected APIResponseCall getData(@NonNull Endpoint endpoint) {
        return getData(endpoint, SuccessResponse::new);
    }

    @SuppressWarnings("unchecked")
    protected APIResponseCall getDataWithPagination(@NonNull Endpoint endpoint) {
        return getData(endpoint, (code, jsonElement) -> {
            Page<LinkedTreeMap<String, Object>> page = gson.fromJson(jsonElement, Page.class);

            return new PaginatedSuccessResponse(code, jsonElement, page);
        });
    }

    protected @NonNull Consumer<ErrorResponse> handleError() {
        return config.getFailedRouteRespondCallback();
    }
}
