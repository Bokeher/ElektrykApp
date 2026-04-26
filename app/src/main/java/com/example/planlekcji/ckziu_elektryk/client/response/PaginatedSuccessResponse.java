package com.example.planlekcji.ckziu_elektryk.client.response;

import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.google.gson.JsonElement;
import com.google.gson.internal.LinkedTreeMap;

public class PaginatedSuccessResponse extends SuccessResponse {

    private final Page<LinkedTreeMap<String, Object>> page;

    public PaginatedSuccessResponse(int httpStatus, JsonElement jsonElement, Page<LinkedTreeMap<String, Object>> jsonElementPage) {
        super(httpStatus, jsonElement);

        this.page = jsonElementPage;
    }

    public Page<LinkedTreeMap<String, Object>> getPage() {
        return page;
    }
}
