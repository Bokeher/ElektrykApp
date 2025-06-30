package com.example.planlekcji.ckziu_elektryk.client.timetable;

public class SchoolEntry {


    private final String shortcut;
    private final String url;
    private String name;

    public SchoolEntry(String shortcut, String url) {
        this.shortcut = shortcut;
        this.url = url;
        this.name = "";
    }

    public String shortcut() {
        return shortcut;
    }

    public String url() {
        return url;
    }

    public String name() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "SchoolEntry{" +
                "shortcut='" + shortcut + '\'' +
                ", url='" + url + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
