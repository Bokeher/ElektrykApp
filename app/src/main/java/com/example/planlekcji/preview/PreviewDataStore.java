package com.example.planlekcji.preview;

import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementChange;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntry;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonDetails;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.LessonDuration;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.SchoolClass;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.SingleLesson;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Subject;
import com.example.planlekcji.ckziu_elektryk.client.utils.Time;
import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class PreviewDataStore {
    private static final Map<String, String> TEACHER_NAMES = Map.of(
        "AL", "Anna White",
        "JW", "Catherine Newman",
        "JS", "John Smith",
        "MK", "Michael Wilson",
        "KP", "George Scott",
        "TG", "Thomas Grayson",
        "RM", "Robert Miller",
        "AD", "Adam Dawson"
    );

    private static final List<SchoolEntry> CLASS_ENTRIES = createSchoolEntries(
        "1A", "2B"
    );
    private static final List<SchoolEntry> TEACHER_ENTRIES = createSchoolEntries(
            "AL", "JW", "JS", "MK", "KP", "TG", "RM", "AD"
    );
    private static final List<SchoolEntry> CLASSROOM_ENTRIES = createSchoolEntries(
            "S01", "S02", "S03", "S04", "S05",
            "S06", "S07", "S08", "S09", "S10",
            "S11", "S12", "S13", "S14", "S15",
            "S16", "S17", "S18", "S19", "S20",
            "S21", "S22", "S23", "S24", "S25",
            "S26", "S27", "S28", "S29", "S30"
    );

    private static final String[] LESSON_START_TIMES = {
        "08:00", "08:50", "09:45", "10:50", "11:45", "12:40", "13:35", "14:25"
    };
    private static final String[] LESSON_END_TIMES = {
        "08:45", "09:35", "10:30", "11:35", "12:30", "13:25", "14:20", "15:10"
    };

    private static final List<PreviewLessonSpec> LESSON_SPECS = List.of(
            // SCHOOL_CLASS: 1A
            // MONDAY
            new PreviewLessonSpec(DayOfWeek.MONDAY, 1, "1A", "AL", "S01", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 2, "1A", "JW", "S02", "English"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 3, "1A", "JS", "S03", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 4, "1A", "MK", "S04", "History"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 5, "1A", "KP", "S05", "Chemistry"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 6, "1A", "TG", "S06", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 7, "1A", "RM", "S07", "German"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 8, "1A", "AD", "S08", "Geography"),

            // TUESDAY
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 1, "1A", "AL", "S09", "Physics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 2, "1A", "JS", "S10", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 3, "1A", "JW", "S11", "Polish"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 4, "1A", "MK", "S12", "Economics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 5, "1A", "KP", "S13", "Biology"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 6, "1A", "TG", "S14", "Ethics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 7, "1A", "RM", "S15", "Art"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 8, "1A", "AD", "S16", "Nature"),

            // WEDNESDAY
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 1, "1A", "AL", "S17", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 2, "1A", "JW", "S18", "English"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 3, "1A", "JW", "S19", "Polish"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 4, "1A", "JS", "S20", "Programming"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 5, "1A", "KP", "S21", "Chemistry"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 6, "1A", "TG", "S22", "Mathematics"),

            // THURSDAY
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 1, "1A", "AL", "S23", "Physics"),
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 2, "1A", "MK", "S24", "History"),
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 3, "1A", "RM", "S25", "German"),
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 4, "1A", "AL", "S26", "Mathematics"),

            // FRIDAY
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 1, "1A", "JS", "S27", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 2, "1A", "JS", "S28", "Programming"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 3, "1A", "JW", "S29", "English"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 4, "1A", "AD", "S30", "Geography"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 5, "1A", "TG", "S01", "Mathematics"),

            // SCHOOL_CLASS: 2B
            // MONDAY
            new PreviewLessonSpec(DayOfWeek.MONDAY, 1, "2B", "JS", "S18", "English"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 2, "2B", "AL", "S19", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 3, "2B", "JW", "S20", "English"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 4, "2B", "KP", "S21", "Biology"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 5, "2B", "MK", "S22", "History"),
            new PreviewLessonSpec(DayOfWeek.MONDAY, 6, "2B", "RM", "S23", "German"),

            // TUESDAY
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 1, "2B", "AL", "S10", "Physics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 2, "2B", "JW", "S11", "History"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 3, "2B", "JS", "S12", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 4, "2B", "MK", "S13", "Economics"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 5, "2B", "KP", "S14", "Chemistry"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 6, "2B", "RM", "S15", "Art"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 7, "2B", "AD", "S16", "Geography"),
            new PreviewLessonSpec(DayOfWeek.TUESDAY, 8, "2B", "TG", "S17", "Ethics"),

            // WEDNESDAY
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 1, "2B", "MK", "S02", "History"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 2, "2B", "KP", "S03", "Biology"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 3, "2B", "RM", "S04", "German"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 4, "2B", "AL", "S05", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 5, "2B", "JW", "S06", "German"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 6, "2B", "JS", "S07", "English"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 7, "2B", "AD", "S08", "Nature"),
            new PreviewLessonSpec(DayOfWeek.WEDNESDAY, 8, "2B", "TG", "S09", "Mathematics"),

            // THURSDAY
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 1, "2B", "AL", "S24", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 2, "2B", "AL", "S25", "Physics"),
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 3, "2B", "JS", "S26", "Programming"),
            new PreviewLessonSpec(DayOfWeek.THURSDAY, 4, "2B", "JW", "S27", "Polish"),

            // FRIDAY
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 1, "2B", "AD", "S28", "Geography"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 2, "2B", "KP", "S29", "Chemistry"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 3, "2B", "TG", "S30", "Mathematics"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 4, "2B", "JW", "S01", "English"),
            new PreviewLessonSpec(DayOfWeek.FRIDAY, 5, "2B", "JW", "S02", "Mathematics")
    );


    private PreviewDataStore() {
    }

    public static List<SchoolEntry> getSchoolEntries(SchoolEntryType type) {
        return switch (type) {
            case CLASSES -> new ArrayList<>(CLASS_ENTRIES);
            case TEACHERS -> new ArrayList<>(TEACHER_ENTRIES);
            case CLASSROOMS -> new ArrayList<>(CLASSROOM_ENTRIES);
        };
    }

    public static String getDefaultToken(SchoolEntryType type) {
        List<SchoolEntry> entries = getSchoolEntries(type);
        return entries.isEmpty() ? "" : entries.get(0).shortcut();
    }

    public static Map<DayOfWeek, List<Lesson>> getTimetable(SchoolEntryType type, String token) {
        String resolvedToken = resolveToken(type, token);
        Map<DayOfWeek, List<Lesson>> timetable = new EnumMap<>(DayOfWeek.class);

        for (DayOfWeek dayOfWeek : DayOfWeek.values()) {
            timetable.put(dayOfWeek, new ArrayList<>());
        }

        for (PreviewLessonSpec spec : LESSON_SPECS) {
            if (!matches(type, resolvedToken, spec)) {
                continue;
            }

            Objects.requireNonNull(timetable.get(spec.dayOfWeek())).add(createLesson(spec));
        }

        return timetable;
    }

    public static List<List<Replacement>> getReplacements(SchoolEntryType type, String token) {
        String resolvedToken = resolveToken(type, token);
        List<List<Replacement>> replacements = new ArrayList<>();

        Map<Integer, List<Replacement>> classesData = new HashMap<>();
        classesData.put(0, List.of(
                createReplacement("1A", List.of(
                        new ReplacementChange("1", "Mathematics - Cancelled"),
                        new ReplacementChange("2", "English - Thomas Grayson"),
                        new ReplacementChange("4", "History - Cancelled"),
                        new ReplacementChange("7", "German - Cancelled")
                )),
                createReplacement("2B", List.of(
                        new ReplacementChange("2", "Mathematics - Cancelled"),
                        new ReplacementChange("3", "English - Thomas Grayson"),
                        new ReplacementChange("5", "History - Cancelled"),
                        new ReplacementChange("6", "German - Cancelled")
                ))
        ));
        classesData.put(1, List.of(
                createReplacement("1A", List.of(
                        new ReplacementChange("2", "Mathematics - George Scott"),
                        new ReplacementChange("4", "Economics - Cancelled"),
                        new ReplacementChange("6", "Ethics - Cancelled"),
                        new ReplacementChange("7", "Art - Cancelled")
                )),
                createReplacement("2B", List.of(
                        new ReplacementChange("3", "Mathematics - George Scott"),
                        new ReplacementChange("4", "Economics - Cancelled"),
                        new ReplacementChange("7", "Geography - Catherine Newman"),
                        new ReplacementChange("8", "Ethics - Cancelled")
                ))
        ));
        classesData.put(2, List.of(
                createReplacement("1A", List.of(
                        new ReplacementChange("1", "Mathematics - Cancelled"),
                        new ReplacementChange("4", "Programming - Michael Wilson"),
                        new ReplacementChange("5", "Chemistry - Cancelled"),
                        new ReplacementChange("6", "Mathematics - Cancelled")
                )),
                createReplacement("2B", List.of(
                        new ReplacementChange("2", "Biology - Cancelled"),
                        new ReplacementChange("4", "Mathematics - Cancelled"),
                        new ReplacementChange("7", "Nature - Cancelled"),
                        new ReplacementChange("8", "Mathematics - Cancelled")
                ))
        ));
        classesData.put(3, List.of(
                createReplacement("1A", List.of(
                        new ReplacementChange("1", "Physics - Cancelled"),
                        new ReplacementChange("2", "History - Cancelled"),
                        new ReplacementChange("3", "German - Cancelled"),
                        new ReplacementChange("4", "Mathematics - Cancelled")
                )),
                createReplacement("2B", List.of(
                        new ReplacementChange("1", "Mathematics - Cancelled"),
                        new ReplacementChange("2", "Physics - Cancelled"),
                        new ReplacementChange("3", "Programming - George Scott"),
                        new ReplacementChange("4", "Polish - Cancelled")
                ))
        ));
        classesData.put(4, List.of(
                createReplacement("1A", List.of(
                        new ReplacementChange("1", "Mathematics - Cancelled"),
                        new ReplacementChange("2", "Programming - Cancelled"),
                        new ReplacementChange("3", "English - Robert Miller"),
                        new ReplacementChange("4", "Geography - Cancelled"),
                        new ReplacementChange("5", "Mathematics - Cancelled")
                )),
                createReplacement("2B", List.of(
                        new ReplacementChange("1", "Geography - Cancelled"),
                        new ReplacementChange("2", "Chemistry - Cancelled"),
                        new ReplacementChange("3", "Mathematics - Cancelled"),
                        new ReplacementChange("4", "English - Robert Miller"),
                        new ReplacementChange("5", "Mathematics - Robert Miller")
                ))
        ));

        Map<Integer, List<Replacement>> teachersData = new HashMap<>();
        teachersData.put(0, List.of(
                createReplacement("Anna White", "1-2", "Cancelled"),
                createReplacement("Catherine Newman", "2-3", "Thomas Grayson"),
                createReplacement("Michael Wilson", "4-5", "Cancelled"),
                createReplacement("Robert Miller", "6-7", "Cancelled")
        ));

        teachersData.put(1, List.of(
                createReplacement("John Smith", "2-3", "George Scott"),
                createReplacement("Michael Wilson", "4", "Cancelled"),
                createReplacement("Thomas Grayson", "6-8", "Cancelled"),
                createReplacement("Robert Miller", "7", "Cancelled"),
                createReplacement("Adam Dawson", "7-8", "Catherine Newman")
        ));

        teachersData.put(2, List.of(
                createReplacement("Anna White", "1,4", "Cancelled"),
                createReplacement("John Smith", "4,6", "Michael Wilson"),
                createReplacement("George Scott", "2,5", "Cancelled"),
                createReplacement("Thomas Grayson", "6,8", "Cancelled"),
                createReplacement("Adam Dawson", "7", "Cancelled")
        ));

        teachersData.put(3, List.of(
                createReplacement("Anna White", "1-2,4", "Cancelled"),
                createReplacement("Michael Wilson", "2", "Cancelled"),
                createReplacement("Robert Miller", "3", "Cancelled"),
                createReplacement("John Smith", "3", "George Scott"),
                createReplacement("Catherine Newman", "4", "Cancelled")
        ));

        teachersData.put(4, List.of(
                createReplacement("John Smith", "1-2", "Cancelled"),
                createReplacement("Catherine Newman", "3-5", "Robert Miller"),
                createReplacement("Adam Dawson", "1,4", "Cancelled"),
                createReplacement("Thomas Grayson", "3,5", "Cancelled"),
                createReplacement("George Scott", "2", "Cancelled")
        ));

        for (int dayIndex = 0; dayIndex < 5; dayIndex++) {
            List<Replacement> dayReplacements;

            if (type == SchoolEntryType.CLASSES) {
                dayReplacements = filterByName(Objects.requireNonNull(classesData.getOrDefault(dayIndex, List.of())), resolvedToken);
            } else if (type == SchoolEntryType.TEACHERS || type == SchoolEntryType.CLASSROOMS) {
                dayReplacements = new ArrayList<>(Objects.requireNonNull(teachersData.getOrDefault(dayIndex, List.of())));
            } else {
                dayReplacements = List.of();
            }

            replacements.add(new ArrayList<>(dayReplacements));
        }

        return replacements;
    }

    private static List<Replacement> filterByName(List<Replacement> replacements, String name) {
        List<Replacement> filtered = new ArrayList<>();

        for (Replacement replacement : replacements) {
            if (replacement.name().equals(name)) {
                filtered.add(replacement);
            }
        }

        return filtered;
    }

    private static Replacement createReplacement(String name, List<ReplacementChange> changes) {
        return new Replacement(name, changes);
    }

    private static Replacement createReplacement(String name, String period, String info) {
        return new Replacement(name, List.of(new ReplacementChange(period, info)));
    }

    private static String resolveToken(SchoolEntryType type, String token) {
        if (token == null || token.trim().isEmpty()) {
            return getDefaultToken(type);
        }

        return token.trim();
    }

    private static boolean matches(SchoolEntryType type, String token, PreviewLessonSpec spec) {
        return switch (type) {
            case CLASSES -> spec.schoolClassShortcut().equals(token);
            case TEACHERS -> spec.teacherShortcut().equals(token);
            case CLASSROOMS -> spec.classroomShortcut().equals(token);
        };
    }

    private static Lesson createLesson(PreviewLessonSpec spec) {
        LessonDetails details = new LessonDetails(new Subject(spec.subjectName(), spec.subjectName()), List.of());
        details.addSchoolClass(new SchoolClass(spec.schoolClassShortcut(), spec.schoolClassShortcut()));
        details.addTeacher(spec.teacherShortcut());
        details.addClassroom(spec.classroomShortcut());

        return new SingleLesson(
            List.of(spec.lessonNumber()),
            new LessonDuration(
                Time.of(LESSON_START_TIMES[spec.lessonNumber() - 1]),
                Time.of(LESSON_END_TIMES[spec.lessonNumber() - 1])
            ),
            details
        );
    }

    private static List<SchoolEntry> createSchoolEntries(String... shortcuts) {
        List<SchoolEntry> entries = new ArrayList<>();

        for (String shortcut : shortcuts) {
            SchoolEntry schoolEntry = new SchoolEntry(shortcut, "preview://" + shortcut.toLowerCase());
            schoolEntry.setName(TEACHER_NAMES.getOrDefault(shortcut, "Preview " + shortcut));
            entries.add(schoolEntry);
        }

        return entries;
    }

    private record PreviewLessonSpec(
        DayOfWeek dayOfWeek,
        int lessonNumber,
        String schoolClassShortcut,
        String teacherShortcut,
        String classroomShortcut,
        String subjectName
    ) {
    }
}
