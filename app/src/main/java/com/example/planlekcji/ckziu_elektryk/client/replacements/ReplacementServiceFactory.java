package com.example.planlekcji.ckziu_elektryk.client.replacements;

import com.example.planlekcji.ckziu_elektryk.client.Config;

public final class ReplacementServiceFactory {

    public static ReplacementService create(Config config) {
        return new ReplacementServiceImpl(config);
    }
}
