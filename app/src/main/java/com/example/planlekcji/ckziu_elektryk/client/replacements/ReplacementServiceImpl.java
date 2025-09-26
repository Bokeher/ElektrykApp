package com.example.planlekcji.ckziu_elektryk.client.replacements;

import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.common.APIResponseCall;
import com.example.planlekcji.ckziu_elektryk.client.common.ClientService;
import com.example.planlekcji.ckziu_elektryk.client.common.Endpoint;
import com.example.planlekcji.ckziu_elektryk.client.response.SuccessResponse;
import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;
import com.google.gson.JsonElement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class ReplacementServiceImpl extends ClientService implements ReplacementService {

    public ReplacementServiceImpl(Config config) {
        super(config);
    }

    @Override
    public List<Replacement> getReplacements(ReplacementType replacementType, Date date) {
        APIResponseCall apiResponseCall = getData(Endpoint.LATEST_REPLACEMENTS
                .withPlaceholders(Map.of("{mode}", replacementType.getMode(), "{date}", DateUtil.formatDate(ReplacementRequest.REPLACEMENT_DATE_PATTERN, date))));

        if (!apiResponseCall.hasResponse()) return Collections.emptyList();

        return apiResponseCall
                .error(e -> System.err.println("Error occurred: " + e.getMessage()))
                .success(this::createReplacements);
    }

    private List<Replacement> createReplacements(SuccessResponse successResponse) {
        JsonElement jsonElement = successResponse.getJsonElement();

        List<Replacement> replacements = new ArrayList<>();

        jsonElement.getAsJsonArray().forEach(obj -> {
            List<ReplacementChange> changes = obj.getAsJsonObject().get("changes").getAsJsonArray()
                    .asList().stream()
                    .map(jsonElement1 -> new ReplacementChange(jsonElement1.getAsJsonObject().get("period").getAsString(),
                            jsonElement1.getAsJsonObject().get("info").getAsString())).collect(Collectors.toList());

            replacements.add(new Replacement(obj.getAsJsonObject().get("name").getAsString(), changes));
        });

        return replacements;
    }
}
