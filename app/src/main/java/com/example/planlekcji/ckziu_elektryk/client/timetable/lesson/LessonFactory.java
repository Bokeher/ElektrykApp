package com.example.planlekcji.ckziu_elektryk.client.timetable.lesson;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.stream.Collectors;

public sealed class LessonFactory permits SingleLessonFactory, GroupLessonFactory {

    public static Lesson createLesson(JsonObject lessonJsonObject) {
        String startTime = lessonJsonObject.get("startTime").getAsString();
        String endTime = lessonJsonObject.get("endTime").getAsString();
        List<Integer> lessonsNumbers = getLessonNumbers(lessonJsonObject);
        Subject subject = getSubject(lessonJsonObject);

        Lesson lesson = new Lesson(subject, lessonsNumbers, startTime, endTime);

        if (lessonJsonObject.has("lessons")) {
            return GroupLessonFactory.createLesson(lessonJsonObject, lesson);
        }

        return SingleLessonFactory.createLesson(lessonJsonObject, lesson);
    }

    protected static List<String> getGroups(JsonObject lessonJsonObject) {
        return lessonJsonObject.get("groups").getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .collect(Collectors.toList());
    }

    private static Subject getSubject(JsonObject lessonJsonObject) {
        JsonObject subjctJsonObject = lessonJsonObject.get("subject").getAsJsonObject();

        return new Subject(
                subjctJsonObject.get("name").getAsString(),
                subjctJsonObject.get("shortcut").getAsString()
        );
    }

    private static List<Integer> getLessonNumbers(JsonObject lessonJsonObject) {
        return lessonJsonObject.get("lesson_numbers").getAsJsonArray()
                .asList()
                .stream()
                .map(JsonElement::getAsString)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
    }

    private static List<String> getEntriesShortcuts(JsonObject lessonJsonObject, String entryName) {
        return lessonJsonObject.getAsJsonArray(entryName).asList()
                .stream()
                .map(JsonElement::getAsJsonObject)
                .map(obj -> obj.get("shortcut").getAsString())
                .collect(Collectors.toList());
    }

    private static String getEntryShortcut(JsonObject lessonJsonObject, String entryName) {
        return lessonJsonObject.getAsJsonObject(entryName).get("shortcut").getAsString();
    }

    protected static void setTeacher(JsonObject lessonJsonObject, LessonDetails lessonDetails) {
        if (lessonJsonObject.has("teacher")) {
            String teacher = getEntryShortcut(lessonJsonObject, "teacher");

            lessonDetails.addTeacher(teacher);
        } else if (lessonJsonObject.has("teachers")) {
            List<String> teachers = getEntriesShortcuts(lessonJsonObject, "teachers");

            lessonDetails.setTeachers(teachers);
        }
    }

    protected static void setClassroom(JsonObject lessonJsonObject, LessonDetails lessonDetails) {
        if (lessonJsonObject.has("classroom")) {
            String classroom = getEntryShortcut(lessonJsonObject, "classroom");

            lessonDetails.addClassroom(classroom);
        } else if (lessonJsonObject.has("classrooms")) {
            List<String> classrooms = getEntriesShortcuts(lessonJsonObject, "classrooms");

            lessonDetails.setClassrooms(classrooms);
        }
    }

    private static SchoolClass getSchoolClass(JsonObject object) {
        return new SchoolClass(object.get("name").getAsString(), object.get("shortcut").getAsString());
    }

    protected static void setSchoolClass(JsonObject lessonJsonObject, LessonDetails lessonDetails) {
        if (lessonJsonObject.has("class")) {
            JsonObject classJsonObject = lessonJsonObject.getAsJsonObject("class");
            lessonDetails.addSchoolClass(getSchoolClass(classJsonObject));
        } else if (lessonJsonObject.has("classes")) {
            JsonArray jsonArray1 = lessonJsonObject.getAsJsonArray("classes");

            List<SchoolClass> schoolClasses = jsonArray1.asList()
                    .stream()
                    .map(JsonElement::getAsJsonObject)
                    .map(LessonFactory::getSchoolClass)
                    .collect(Collectors.toList());

            lessonDetails.setSchoolClasses(schoolClasses);
        }
    }
}
