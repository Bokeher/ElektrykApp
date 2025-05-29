package com.example.planlekcji.ckziu_elektryk.client.calendar;

import com.google.gson.annotations.SerializedName;

public record Calendar(@SerializedName("file_name") String fileName, String content) {
}
