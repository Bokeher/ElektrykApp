package com.example.planlekcji.settings;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntry;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.preview.PreviewDataStore;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class SchoolEntriesDownloader implements Runnable {
    private List<SchoolEntry> classesSchoolEntries = new ArrayList<>();
    private List<SchoolEntry> teachersSchoolEntries = new ArrayList<>();
    private List<SchoolEntry> classroomsSchoolEntries = new ArrayList<>();
    private final CKZiUElektrykClient client;

    public SchoolEntriesDownloader(CKZiUElektrykClient client) {
        this.client = client;
    }

    @Override
    public void run() {
        if (Config.getOrCreateConfig().isPreviewMode()) {
            classesSchoolEntries = PreviewDataStore.getSchoolEntries(SchoolEntryType.CLASSES);
            teachersSchoolEntries = PreviewDataStore.getSchoolEntries(SchoolEntryType.TEACHERS);
            classroomsSchoolEntries = PreviewDataStore.getSchoolEntries(SchoolEntryType.CLASSROOMS);

            sortEntries();
            return;
        }

        TimetableService timetableService = client.getTimetableService(SchoolEntryType.CLASSES);
        classesSchoolEntries = timetableService.getList();

        timetableService = client.getTimetableService(SchoolEntryType.TEACHERS);
        teachersSchoolEntries = timetableService.getList();

        timetableService = client.getTimetableService(SchoolEntryType.CLASSROOMS);
        classroomsSchoolEntries = timetableService.getList();

        if (classesSchoolEntries == null) {
            classesSchoolEntries = new ArrayList<>();
        }
        if (teachersSchoolEntries == null) {
            teachersSchoolEntries = new ArrayList<>();
        }
        if (classroomsSchoolEntries == null) {
            classroomsSchoolEntries = new ArrayList<>();
        }

        sortEntries();
    }

    private void sortEntries() {
        classesSchoolEntries.sort(Comparator.comparing(SchoolEntry::shortcut));
        teachersSchoolEntries.sort(Comparator.comparing(SchoolEntry::shortcut, Collator.getInstance(new Locale("pl"))));
        classroomsSchoolEntries.sort(Comparator.comparing(SchoolEntry::shortcut));
    }

    public List<SchoolEntry> getClassesSchoolEntries() {
        return classesSchoolEntries;
    }

    public List<SchoolEntry> getTeachersSchoolEntries() {
        return teachersSchoolEntries;
    }

    public List<SchoolEntry> getClassroomsSchoolEntries() {
        return classroomsSchoolEntries;
    }
}
