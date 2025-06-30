package com.example.planlekcji.ckziu_elektryk.client.timetable.info;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.google.gson.JsonElement;

import java.util.Optional;

class TimetableInfoServiceImpl extends ClientService implements TimetableInfoService {
    protected TimetableInfoServiceImpl(Config config) {
        super(config);
    }

    @Override
    public Optional<TimetableInfo> getTimetableInfo() {
        APIResponseCall apiResponseCall = getData(Endpoint.TIMETABLE_INFO);

        if (!apiResponseCall.hasResponse())
            return Optional.empty();

        return Optional.ofNullable(apiResponseCall
                .error(printError())
                .success(successResponse -> {
                    JsonElement jsonElement = successResponse.getJsonElement();

                    return new TimetableInfo(
                            TimetableInfo.parseDate(jsonElement.getAsJsonObject().get("apply_at").getAsString()),
                            jsonElement.getAsJsonObject().get("info").getAsString()
                    );
                }));
    }
}
