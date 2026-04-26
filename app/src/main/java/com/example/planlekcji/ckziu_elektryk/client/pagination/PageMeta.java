package com.example.planlekcji.ckziu_elektryk.client.pagination;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record PageMeta(@SerializedName("current_page") int currentPage,
                       @SerializedName("last_page") int lastPage,
                       @SerializedName("per_page") int perPage,
                       @SerializedName("total") int totalRecords,
                       List<PageMetaLink> links) {
}
