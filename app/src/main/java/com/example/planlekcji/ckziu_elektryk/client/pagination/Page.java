package com.example.planlekcji.ckziu_elektryk.client.pagination;

import java.util.Collections;
import java.util.List;

public record Page<T>(List<T> data, PageLinks links, PageMeta meta) {

    public static <T> Page<T> empty() {
        return new Page<>(Collections.emptyList(), null, null);
    }

}
