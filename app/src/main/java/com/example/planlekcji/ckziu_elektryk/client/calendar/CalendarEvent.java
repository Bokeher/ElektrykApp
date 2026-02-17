package com.example.planlekcji.ckziu_elektryk.client.calendar;

import com.google.gson.annotations.SerializedName;

public record CalendarEvent(@SerializedName("date_raw") String dateRaw, String description) {

}
