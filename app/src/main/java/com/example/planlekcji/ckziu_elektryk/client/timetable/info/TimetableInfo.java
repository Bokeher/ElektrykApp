package com.example.planlekcji.ckziu_elektryk.client.timetable.info;

import com.example.planlekcji.ckziu_elektryk.client.utils.DateUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public record TimetableInfo(Date applyAt, String info) {

    public static final SimpleDateFormat APPLY_AT_FORMATTER = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);

    public static Date parseDate(String text) {
        return DateUtil.parseDate(APPLY_AT_FORMATTER, text);
    }
}
