package com.example.planlekcji.timetable;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.R;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfo;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.listener.TimetableDownloadCompleteListener;
import com.example.planlekcji.timetable.model.DayOfWeek;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TimetableDataDownloader implements Runnable {
    private final TimetableDownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public TimetableDataDownloader(CKZiUElektrykClient client, TimetableDownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        Optional<TimetableInfo> timetableInfoOptional = client.getTimetableInfo();

        if (!timetableInfoOptional.isPresent()) return;

        SchoolEntryType schoolEntryType = getTimetableType();
        String token = getToken(schoolEntryType).replaceAll(" ", "");

        Log.d("token: ", token);
        TimetableService timetableService = client.getTimetableService(schoolEntryType);

        Map<DayOfWeek, List<Lesson>> map = timetableService.getTimetable(token);

        Log.d("map: ", map.toString());


        listener.onDownloadComplete(map);
    }

    private SchoolEntryType getTimetableType() {
        Context context = MainActivity.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

        // 0 - classes, 1 - teachers, 2 - classrooms
        int typeOfTimetable = sharedPreferences.getInt("selectedTypeOfTimetable", 0);

        return SchoolEntryType.values()[typeOfTimetable];
    }

    private String getToken(SchoolEntryType timetableType) {
        Context context = MainActivity.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

        String tokenType;
        if (timetableType == SchoolEntryType.CLASSES) {
            tokenType = context.getString(R.string.classTokenKey);
        } else if(timetableType == SchoolEntryType.TEACHERS) {
            tokenType = context.getString(R.string.teacherTokenKey);
        } else {
            tokenType = context.getString(R.string.classroomTokenKey);
        }

        return sharedPreferences.getString(tokenType, "");
    }
}
