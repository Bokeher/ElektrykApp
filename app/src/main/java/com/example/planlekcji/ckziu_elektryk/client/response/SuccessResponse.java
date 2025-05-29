package com.example.planlekcji.ckziu_elektryk.client.response;

import com.google.gson.JsonElement;

public class SuccessResponse extends APIResponse {

    private final JsonElement jsonElement;

    public SuccessResponse(int httpStatus, JsonElement jsonElement) {
        super(httpStatus);
        this.jsonElement = jsonElement;
    }

    public JsonElement getJsonElement() {
        return jsonElement;
    }
}
