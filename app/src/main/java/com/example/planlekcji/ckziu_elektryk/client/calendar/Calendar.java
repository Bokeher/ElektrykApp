package com.example.planlekcji.ckziu_elektryk.client.calendar;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public record Calendar(@SerializedName("file_name") String fileName,
                       @SerializedName("content") List<CalendarSection> sections) {
}
