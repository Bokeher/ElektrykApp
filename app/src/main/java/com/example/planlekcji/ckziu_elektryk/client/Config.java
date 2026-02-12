package com.example.planlekcji.ckziu_elektryk.client;

import android.content.Context;
import android.util.Log;

import com.example.planlekcji.MainActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.function.Consumer;

public class Config {

    private static Config instance;

    private final Properties properties;
    private Consumer<IOException> failedApiConnectionCallback;

    private Config() throws IOException {
        properties = new Properties();

        Context context = MainActivity.getContext();

        if (context != null) {
            InputStream inputStream = context.getAssets().open("app.properties");
            properties.load(inputStream);
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

    private String getValue(String key) {
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

    public void setFailedApiConnectionCallback(Consumer<IOException> failedApiConnectionCallback) {
        this.failedApiConnectionCallback = failedApiConnectionCallback;
    }

    public Consumer<IOException> getFailedApiConnectionCallback() {
        return failedApiConnectionCallback;
    }
}
