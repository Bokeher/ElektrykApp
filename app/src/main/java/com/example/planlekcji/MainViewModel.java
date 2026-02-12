package com.example.planlekcji;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.listener.TimetableDownloadCompleteListener;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;
import com.example.planlekcji.replacements.ReplacementDataDownloader;
import com.example.planlekcji.timetable.model.DayOfWeek;
import com.example.planlekcji.utils.RetryHandler;
import com.example.planlekcji.timetable.TimetableDataDownloader;
import com.example.planlekcji.utils.ToastUtils;

import java.util.List;
import java.util.Map;

public class MainViewModel extends ViewModel {
    private final CKZiUElektrykClient client;

    // Downloaded data
    private final MutableLiveData<List<List<Replacement>>> replacements = new MutableLiveData<>();
    private final MutableLiveData<Map<DayOfWeek, List<Lesson>>> timetable = new MutableLiveData<>();

    // ProgressBar state
    private final MutableLiveData<Boolean> isLoadingReplacements = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingTimetable = new MutableLiveData<>(false);

    // Retry handlers
    private final RetryHandler replaceRetryHandler = new RetryHandler(this::startReplacementDownload);
    private final RetryHandler timetableRetryHandler = new RetryHandler(this::startTimetableDownload);

    public MainViewModel() {
        client = new CKZiUElektrykClient();
        client.setFailedApiConnectionCallback(e -> {
            Context context = MainActivity.getContext();
            String errorMessage = context.getString(R.string.toastErrorMessage_failedApiConnection);

            ToastUtils.showToast(context, errorMessage, true);
        });

        client.setFailedRouteRespondCallback(errorResponse -> {
            System.err.println("Error occurred: " + errorResponse.getMessage());
            String errMess = MainActivity.getContext().getString(R.string.toast_errorMessage);
            ToastUtils.showToast(MainActivity.getContext(), errMess, false);
        });
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
        isLoadingReplacements.postValue(true);
        ReplacementDataDownloader downloader = new ReplacementDataDownloader(client, new ReplacementsDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(List<List<Replacement>> replacementList) {
                replacements.postValue(replacementList);
                isLoadingReplacements.postValue(false);
            }

            @Override
            public void onDownloadFailed() {
                isLoadingReplacements.postValue(false);
                replaceRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    private void startTimetableDownload() {
        isLoadingTimetable.postValue(true);
        TimetableDataDownloader downloader = new TimetableDataDownloader(client, new TimetableDownloadCompleteListener() {
            @Override
            public void onDownloadComplete(Map<DayOfWeek, List<Lesson>> timetableMap) {
                timetable.postValue(timetableMap);
                isLoadingTimetable.postValue(false);
            }

            @Override
            public void onDownloadFailed() {
                isLoadingTimetable.postValue(false);
                timetableRetryHandler.handleRetry();
            }
        });
        new Thread(downloader).start();
    }

    // LiveData getters
    public LiveData<Map<DayOfWeek, List<Lesson>>> getTimetableLiveData() {
        return timetable;
    }

    public LiveData<List<List<Replacement>>> getReplacementsLiveData() {
        return replacements;
    }

    public LiveData<Boolean> getIsLoadingReplacements() {
        return isLoadingReplacements;
    }

    public LiveData<Boolean> getIsLoadingTimetable() {
        return isLoadingTimetable;
    }

    public CKZiUElektrykClient getClient() {
        return client;
    }
}
