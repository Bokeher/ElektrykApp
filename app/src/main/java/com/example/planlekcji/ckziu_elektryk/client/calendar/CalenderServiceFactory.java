package com.example.planlekcji.ckziu_elektryk.client.calendar;

import com.example.planlekcji.ckziu_elektryk.client.Config;

public final class CalenderServiceFactory {

    public static CalenderService create(Config config) {
        return new CalenderServiceImpl(config);
    }
}
