package com.example.planlekcji.ckziu_elektryk.client;

import android.content.Context;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.response.ErrorResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

public class Config {
    private static final String MAIN_PROPERTIES_FILE = "app.properties";
    private static final String EXAMPLE_PROPERTIES_FILE = "app.example.properties";

    private static Config instance;

    private final Properties properties;
    private Consumer<IOException> failedApiConnectionCallback;
    private Consumer<ErrorResponse> failedRouteRespondCallback;

    protected Config() throws IOException {
        properties = new Properties();

        Context context = MainActivity.getContext();

        if (context != null) {
            try (InputStream inputStream = openPropertiesFile(context)) {
                properties.load(inputStream);
            }
        }

    }

    private InputStream openPropertiesFile(Context context) throws IOException {
        try {
            return context.getAssets().open(MAIN_PROPERTIES_FILE);
        } catch (IOException ignored) {
            return context.getAssets().open(EXAMPLE_PROPERTIES_FILE);
        }
    }

    public static Config getOrCreateConfig() {
        if (instance == null) {
            try {
                instance = new Config();
            } catch (IOException e) {
                Log.e("error", e.getMessage(), e);
            }
        }

        return instance;
    }

    protected String getValue(String key) {
        if (key == null || key.isEmpty()) {
            throw new IllegalArgumentException("Key can not be null or empty");
        }

        return properties.getProperty(key, "");
    }

    public String getToken() {
        return getValue("token");
    }

    public String getAPIUrl() {
        return getValue("rest_api_url");
    }

    public boolean isPreviewMode() {
        return Boolean.parseBoolean(getValue("preview_mode"));
    }

    public void setFailedApiConnectionCallback(Consumer<IOException> failedApiConnectionCallback) {
        this.failedApiConnectionCallback = failedApiConnectionCallback;
    }

    public Consumer<IOException> getFailedApiConnectionCallback() {
        return failedApiConnectionCallback == null ? (e) -> System.err.println(e.getMessage()) : failedApiConnectionCallback;
    }

    public Consumer<ErrorResponse> getFailedRouteRespondCallback() {
        return failedRouteRespondCallback == null ? (e) -> System.err.println(e.getMessage()) : failedRouteRespondCallback;
    }

    public void setFailedRouteRespondCallback(Consumer<ErrorResponse> failedRouteRespondCallback) {
        this.failedRouteRespondCallback = failedRouteRespondCallback;
    }
}
