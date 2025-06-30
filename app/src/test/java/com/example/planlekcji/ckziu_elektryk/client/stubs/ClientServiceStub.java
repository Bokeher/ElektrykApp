package com.example.planlekcji.ckziu_elektryk.client.stubs;

import androidx.annotation.NonNull;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

public class ClientServiceStub extends ClientService {
    public ClientServiceStub(Config config) {
        super(config);
    }

    public APIResponseCall getData(@NonNull Endpoint endpoint) {
        return super.getData(endpoint);
    }

    public APIResponseCall getDataWithPagination(@NonNull Endpoint endpoint) {
        return super.getDataWithPagination(endpoint);
    }
}
