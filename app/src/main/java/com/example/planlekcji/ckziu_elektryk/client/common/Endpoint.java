package com.example.planlekcji.ckziu_elektryk.client.common;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class Endpoint {

    public static final Endpoint LATEST_REPLACEMENTS = new Endpoint("replacements?mode={mode}&date={date}");
    public static final Endpoint TIMETABLE_INFO = new Endpoint("timetables/info");
    public static final Endpoint TIMETABLES_CLASSROOMS = new Endpoint("timetables/classrooms");
    public static final Endpoint TIMETABLES_CLASSROOM = new Endpoint("timetables/classrooms/{school_entry_shortcut}");
    public static final Endpoint TIMETABLES_TEACHERS = new Endpoint("timetables/teachers");
    public static final Endpoint TIMETABLES_TEACHER = new Endpoint("timetables/teachers/{school_entry_shortcut}");
    public static final Endpoint TIMETABLES_CLASSES = new Endpoint("timetables/classes");
    public static final Endpoint TIMETABLES_CLASS = new Endpoint("timetables/classes/{school_entry_shortcut}");
    public static final Endpoint LATEST_CALENDER = new Endpoint("calenders");
    public static final Endpoint ARTICLES_LIST = new Endpoint("articles?page={page}");
    public static final Endpoint ARTICLE_BY_ID = new Endpoint("articles/{id}?photo_size={photo_size}");

    private String name;

    private Endpoint(String name) {
        this.name = name;
    }

    private Endpoint(Endpoint endpoint) {
        this.name = endpoint.name;
    }

    public Endpoint withPlaceholders(@NotNull Map<String, String> placeholders) {
        Endpoint clonedEndpoint = new Endpoint(this);

        for (Map.Entry<String, String> keyValueEntry : placeholders.entrySet()) {
            clonedEndpoint.name = clonedEndpoint.name.replace(keyValueEntry.getKey(), keyValueEntry.getValue());
        }

        return clonedEndpoint;
    }

    public String getName() {
        return name;
    }
}
