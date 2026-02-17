package com.example.planlekcji.ckziu_elektryk.client.stubs;

import com.example.planlekcji.ckziu_elektryk.client.Config;

import java.io.IOException;

public final class ConfigStub extends Config {

    public ConfigStub() throws IOException {
        super();
    }

    @Override
    protected String getValue(String key) {
        if (key.equalsIgnoreCase("token")) {
            return TestConstants.TOKEN;
        }

        if (key.equalsIgnoreCase("rest_api_url")) {
            return TestConstants.URL;
        }

        return "";
    }
}
