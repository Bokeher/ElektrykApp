package com.example.planlekcji.ckziu_elektryk.client.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

    private DateUtil() {}

    public static Date parsedDate(SimpleDateFormat simpleDateFormat, String text) {
        try {
            return simpleDateFormat.parse(text);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}
