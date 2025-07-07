package com.example.planlekcji;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.listener.TimetableDownloadCompleteListener;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;
import com.example.planlekcji.replacements.ReplacementDataProcessor;
import com.example.planlekcji.replacements.ReplacementDataDownloader;
import com.example.planlekcji.timetable.model.DayOfWeek;
import com.example.planlekcji.utils.RetryHandler;
import com.example.planlekcji.timetable.TimetableDataDownloader;

import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {
    private final CKZiUElektrykClient client;

    // downloaded data
    private final MutableLiveData<List<String>> replacements = new MutableLiveData<>();
    private final MutableLiveData<Map<DayOfWeek, List<Lesson>>> timetableMap = new MutableLiveData<>();

    // retry handlers
    private final RetryHandler replaceRetryHandler = new RetryHandler(this::startReplacementDownload);
    private final RetryHandler timetableRetryHandler = new RetryHandler(this::startReplacementDownload);

    public MainViewModel() {
        // Initialize the client
        client = new CKZiUElektrykClient();
    }

    public void fetchData() {
        startReplacementDownload();
        startTimetableDownload();
    }

    public void fetchReplacements() {
        startReplacementDownload();
    }

    public void fetchTimetable() {
        startTimetableDownload();
    }

    private void startReplacementDownload() {
        ReplacementDataDownloader downloader = new ReplacementDataDownloader(client, new ReplacementsDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(String rawReplacements) {
                if(rawReplacements.isEmpty()) {
                    replacements.postValue(null);
                }

                // Process replacement data
                ReplacementDataProcessor replacementDataProcessor = new ReplacementDataProcessor(rawReplacements);
                replacementDataProcessor.process();

                // Update LiveData
                replacements.postValue(replacementDataProcessor.getReplacements());
            }

            @Override
            public void onDownloadFailed() {
                replaceRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    private void startTimetableDownload() {
        TimetableDataDownloader downloader = new TimetableDataDownloader(client, new TimetableDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(Map<DayOfWeek, List<Lesson>> timetableMap) {
                // Update LiveData
                MainViewModel.this.timetableMap.postValue(timetableMap);
            }

            @Override
            public void onDownloadFailed() {
                timetableRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    public LiveData<Map<DayOfWeek, List<Lesson>>> getTimetableLiveData() {
        return timetableMap;
    }

    public LiveData<List<String>> getReplacementsLiveData() {
        return replacements;
    }

    public CKZiUElektrykClient getClient() {
        return client;
    }
}
