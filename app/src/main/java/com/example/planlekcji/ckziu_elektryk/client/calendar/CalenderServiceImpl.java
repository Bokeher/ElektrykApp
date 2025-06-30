package com.example.planlekcji.ckziu_elektryk.client.calendar;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;

import java.util.Optional;

class CalenderServiceImpl extends ClientService implements CalenderService {
    protected CalenderServiceImpl(Config config) {
        super(config);
    }

    @Override
    public Optional<Calendar> getLatestCalender() {
        APIResponseCall apiResponseCall = getData(Endpoint.LATEST_CALENDER);

        if (!apiResponseCall.hasResponse()) return Optional.empty();

        return Optional.ofNullable(apiResponseCall
                .error(e -> System.err.println("Error occurred: " + e.getMessage()))
                .success(successResponse -> gson.fromJson(successResponse.getJsonElement(), Calendar.class))
        );
    }
}
