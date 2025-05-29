package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonDetails {

    private final List<String> groups;
    private List<SchoolClass> schoolClasses;
    private List<String> teachers;
    private List<String> classrooms;

    public LessonDetails(List<String> groups) {
        this.groups = groups;
        this.schoolClasses = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.classrooms = new ArrayList<>();
    }

    public List<String> getGroups() {
        return groups;
    }

    public List<SchoolClass> getSchoolClasses() {
        return schoolClasses;
    }

    public List<String> getTeachers() {
        return teachers;
    }

    public List<String> getClassrooms() {
        return classrooms;
    }

    @NonNull
    @Override
    public String toString() {
        return "LessonDetails{" +
                "groups=" + groups +
                ", schoolClasses=" + schoolClasses +
                ", teachers=" + teachers +
                ", classrooms=" + classrooms +
                '}';
    }

    public void addTeacher(String shortcut) {
        this.teachers.add(shortcut);
    }

    public void addClassroom(String shortcut) {
        this.classrooms.add(shortcut);
    }

    public void addSchoolClass(SchoolClass schoolClass) {
        this.schoolClasses.add(schoolClass);
    }

    public void setSchoolClasses(List<SchoolClass> schoolClasses) {
        this.schoolClasses = schoolClasses;
    }

    public void setTeachers(List<String> teachers) {
        this.teachers = teachers;
    }

    public void setClassrooms(List<String> classrooms) {
        this.classrooms = classrooms;
    }

    public Optional<SchoolClass> getSchoolClass() {
        if (this.schoolClasses.isEmpty()) {
            return Optional.empty();
        }

        return Optional.ofNullable(this.schoolClasses.get(0));
    }

    public String getTeacher() {
        return getFirstEntryShortcut(this.teachers);
    }

    public String getClassroom() {
        return getFirstEntryShortcut(this.classrooms);
    }

    private String getFirstEntryShortcut(List<String> entries) {
        if (entries.isEmpty()) {
            return "";
        }

        String firstEntry = entries.get(0);

        return firstEntry == null ? "" : firstEntry;
    }
}
