package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import java.util.ArrayList;
import java.util.List;

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
}
