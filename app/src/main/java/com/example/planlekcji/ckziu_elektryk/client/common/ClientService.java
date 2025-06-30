package com.example.planlekcji.ckziu_elektryk.client.common;

import android.content.Context;

import androidx.annotation.NonNull;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.PaginatedSuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.example.planlekcji.utils.ToastUtils;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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
            String bodyContent = "";

            if (body != null) bodyContent = body.string();

            JsonElement jsonElement = gson.fromJson(bodyContent, JsonElement.class);
            if (!response.isSuccessful()) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                if (jsonObject.has("message")) {
                    bodyContent = jsonObject.get("message").getAsString();
                }

                apiResponseCall.setErrorResponse(new ErrorResponse(response.code(), bodyContent));

                return apiResponseCall;
            }

            apiResponseCall.setSuccessResponse(successResponseBiFunction.apply(response.code(), jsonElement));
        } catch (IOException exception) {
            Context context = MainActivity.getContext();
            String errorMessage = context.getString(R.string.toastErrorMessage_failedApiConnection);

            ToastUtils.showToast(context, errorMessage, true);
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

    protected @NonNull Consumer<ErrorResponse> printError() {
        return errorResponse -> System.err.println(errorResponse.getMessage());
    }
}
