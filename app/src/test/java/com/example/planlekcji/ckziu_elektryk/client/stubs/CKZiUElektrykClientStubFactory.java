package com.example.planlekcji.ckziu_elektryk.client.stubs;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;

import java.io.IOException;

public final class CKZiUElektrykClientStubFactory {

    public static CKZiUElektrykClient createClient() throws IOException {
        ConfigStub config = new ConfigStub();

        CKZiUElektrykClientStub ckZiUElektrykClientStub = new CKZiUElektrykClientStub(config);

        config.setFailedApiConnectionCallback(ex -> System.err.println(ex.getMessage()));
        config.setFailedRouteRespondCallback(e -> System.err.println(e.getMessage()));

        return ckZiUElektrykClientStub;
    }
}
