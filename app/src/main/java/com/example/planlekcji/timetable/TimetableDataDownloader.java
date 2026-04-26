package com.example.planlekcji.timetable;

import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.TimetableService;
import com.example.planlekcji.ckziu_elektryk.client.timetable.info.TimetableInfo;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.listener.TimetableDownloadCompleteListener;
import com.example.planlekcji.preview.PreviewDataStore;
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
        SchoolEntryType schoolEntryType = MainActivity.getTimetableType();
        String token = MainActivity.getToken(schoolEntryType).replaceAll(" ", "");

        if (Config.getOrCreateConfig().isPreviewMode()) {
            listener.onDownloadComplete(PreviewDataStore.getTimetable(schoolEntryType, token));
            return;
        }

        Optional<TimetableInfo> timetableInfoOptional = client.getTimetableInfo();

        if (!timetableInfoOptional.isPresent()) return;

        if (token.isEmpty()) {
            Log.e("Error", "Token is empty");
            return;
        }

        Log.d("token: ", token);
        TimetableService timetableService = client.getTimetableService(schoolEntryType);

        Map<DayOfWeek, List<Lesson>> map = timetableService.getTimetable(token);
        if (map != null) Log.d("timetable_map", map.toString());

        listener.onDownloadComplete(map);
    }

}
