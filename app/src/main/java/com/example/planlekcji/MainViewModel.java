package com.example.planlekcji;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.planlekcji.articles.ArticleDataDownloader;
import com.example.planlekcji.calendar.CalendarDataDownloader;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.article.Article;
import com.example.planlekcji.ckziu_elektryk.client.calendar.Calendar;
import com.example.planlekcji.ckziu_elektryk.client.pagination.Page;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.timetable.lesson.Lesson;
import com.example.planlekcji.replacements.DayReplacements;
import com.example.planlekcji.listener.ArticlesDownloadCompleteListener;
import com.example.planlekcji.listener.CalendarDownloadCompleteListener;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;
import com.example.planlekcji.listener.TimetableDownloadCompleteListener;
import com.example.planlekcji.replacements.ReplacementDataDownloader;
import com.example.planlekcji.timetable.TimetableDataDownloader;
import com.example.planlekcji.utils.DayOfWeek;
import com.example.planlekcji.utils.RefreshCooldownManager;
import com.example.planlekcji.utils.RefreshDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MainViewModel extends ViewModel {
    private final CKZiUElektrykClient client;
    private final ExecutorService executorService = Executors.newFixedThreadPool(4);

    private Future<?> replacementsTask;
    private Future<?> timetableTask;
    private Future<?> articlesTask;
    private Future<?> calendarTask;

    // Downloaded data
    private final MutableLiveData<List<DayReplacements>> replacements = new MutableLiveData<>();
    private final MutableLiveData<Map<DayOfWeek, List<Lesson>>> timetable = new MutableLiveData<>();
    private final MutableLiveData<List<Article>> articles = new MutableLiveData<>();
    private final MutableLiveData<Calendar> calendar = new MutableLiveData<>();

    // Error notification LiveData (string resource ID)
    private final MutableLiveData<Integer> toastErrorMessage = new MutableLiveData<>();

    // ProgressBar state
    private final MutableLiveData<Boolean> isLoadingReplacements = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingTimetable = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingArticles = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isLoadingCalendar = new MutableLiveData<>(false);

    // Article pagination state
    private int currentArticlesPage = 1;
    private final MutableLiveData<Boolean> isLoadingMoreArticles = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> canLoadMoreArticles = new MutableLiveData<>(true);

    public MainViewModel() {
        client = CKZiUElektrykClient.getInstance();
        client.setFailedApiConnectionCallback(e -> toastErrorMessage.postValue(R.string.toastErrorMessage_failedApiConnection));

        client.setFailedRouteRespondCallback(errorResponse -> {
            Log.e("FAILED_ROUTE_RESPOND_CALLBACK", errorResponse.getMessage());
            toastErrorMessage.postValue(R.string.toast_errorMessage);
        });
    }

    public void fetchReplacements() {
        startReplacementDownload();
    }

    public void fetchTimetable() {
        startTimetableDownload();
    }

    public void fetchArticles() {
        startArticlesDownload();
    }

    public void forceFetchArticles() {
        Context context = MainActivity.getContext();
        if (context != null) {
            RefreshCooldownManager.getInstance(context).invalidate(RefreshDataType.ARTICLES);
        }
        startArticlesDownload();
    }

    public void fetchCalendar() {
        startCalendarDownload();
    }

    public void forceFetchCalendar() {
        Context context = MainActivity.getContext();
        if (context != null) {
            RefreshCooldownManager.getInstance(context).invalidate(RefreshDataType.CALENDAR);
        }
        startCalendarDownload();
    }

    private void startReplacementDownload() {
        if (replacementsTask != null && !replacementsTask.isDone()) {
            replacementsTask.cancel(true);
        }
        isLoadingReplacements.postValue(true);
        ReplacementDataDownloader downloader = new ReplacementDataDownloader(client, new ReplacementsDownloadCompleteListener() {
            @Override
            public void onCacheLoaded(List<DayReplacements> replacementList) {
                replacements.postValue(replacementList);
            }

            @Override
            public void onDownloadComplete(List<DayReplacements> replacementList) {
                if (!Objects.equals(replacements.getValue(), replacementList)) {
                    replacements.postValue(replacementList);
                }
                isLoadingReplacements.postValue(false);
            }

            @Override
            public void onDownloadFailed() {
                isLoadingReplacements.postValue(false);
            }
        });
        replacementsTask = executorService.submit(downloader);
    }

    private void startTimetableDownload() {
        if (timetableTask != null && !timetableTask.isDone()) {
            timetableTask.cancel(true);
        }
        isLoadingTimetable.postValue(true);
        TimetableDataDownloader downloader = new TimetableDataDownloader(client, new TimetableDownloadCompleteListener() {
            @Override
            public void onCacheLoaded(Map<DayOfWeek, List<Lesson>> timetableMap) {
                timetable.postValue(timetableMap);
            }

            @Override
            public void onDownloadComplete(Map<DayOfWeek, List<Lesson>> timetableMap) {
                if (timetable.getValue() != timetableMap && !Objects.equals(timetable.getValue(), timetableMap)) {
                    timetable.postValue(timetableMap);
                }
                isLoadingTimetable.postValue(false);
            }

            @Override
            public void onDownloadFailed() {
                isLoadingTimetable.postValue(false);
            }
        });
        timetableTask = executorService.submit(downloader);
    }

    private void startArticlesDownload() {
        if (articlesTask != null && !articlesTask.isDone()) {
            articlesTask.cancel(true);
        }
        currentArticlesPage = 1;
        canLoadMoreArticles.postValue(true);
        isLoadingArticles.postValue(true);
        ArticleDataDownloader downloader = new ArticleDataDownloader(client, new ArticlesDownloadCompleteListener() {
            @Override
            public void onCacheLoaded(List<Article> articleList) {
                articles.postValue(articleList);
            }

            @Override
            public void onDownloadComplete(List<Article> articleList) {
                if (!Objects.equals(articles.getValue(), articleList)) {
                    articles.postValue(articleList);
                }
                isLoadingArticles.postValue(false);
            }

            @Override
            public void onDownloadFailed() {
                isLoadingArticles.postValue(false);
            }
        });
        articlesTask = executorService.submit(downloader);
    }

    private void startCalendarDownload() {
        if (calendarTask != null && !calendarTask.isDone()) {
            calendarTask.cancel(true);
        }
        isLoadingCalendar.postValue(true);
        CalendarDataDownloader downloader = new CalendarDataDownloader(client, new CalendarDownloadCompleteListener() {
            @Override
            public void onCacheLoaded(Calendar calendarData) {
                calendar.postValue(calendarData);
            }

            @Override
            public void onDownloadComplete(Calendar calendarData) {
                if (!Objects.equals(calendar.getValue(), calendarData)) {
                    calendar.postValue(calendarData);
                }
                isLoadingCalendar.postValue(false);
            }

            @Override
            public void onDownloadFailed() {
                isLoadingCalendar.postValue(false);
            }
        });
        calendarTask = executorService.submit(downloader);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (replacementsTask != null) {
            replacementsTask.cancel(true);
        }
        if (timetableTask != null) {
            timetableTask.cancel(true);
        }
        if (articlesTask != null) {
            articlesTask.cancel(true);
        }
        if (calendarTask != null) {
            calendarTask.cancel(true);
        }
        executorService.shutdownNow();
    }

    // LiveData getters
    public LiveData<Map<DayOfWeek, List<Lesson>>> getTimetableLiveData() {
        return timetable;
    }

    public LiveData<List<DayReplacements>> getReplacementsLiveData() {
        return replacements;
    }

    public LiveData<List<Article>> getArticlesLiveData() {
        return articles;
    }

    public LiveData<Calendar> getCalendarLiveData() {
        return calendar;
    }

    public LiveData<Boolean> getIsLoadingReplacements() {
        return isLoadingReplacements;
    }

    public LiveData<Boolean> getIsLoadingTimetable() {
        return isLoadingTimetable;
    }

    public LiveData<Boolean> getIsLoadingArticles() {
        return isLoadingArticles;
    }

    public LiveData<Boolean> getIsLoadingMoreArticles() {
        return isLoadingMoreArticles;
    }

    public LiveData<Boolean> getCanLoadMoreArticles() {
        return canLoadMoreArticles;
    }

    public void loadMoreArticles() {
        if (Boolean.TRUE.equals(isLoadingMoreArticles.getValue()) || Boolean.FALSE.equals(canLoadMoreArticles.getValue())) {
            return;
        }
        isLoadingMoreArticles.postValue(true);
        executorService.submit(() -> {
            try {
                int nextPage = currentArticlesPage + 1;
                Page<Article> page = client.getArticleService().getArticles(nextPage);
                if (page != null && page.data() != null && !page.data().isEmpty()) {
                    List<Article> newArticles = page.data();
                    List<Article> currentArticles = articles.getValue();
                    List<Article> updatedList = new ArrayList<>();
                    if (currentArticles != null) {
                        updatedList.addAll(currentArticles);
                    }
                    for (Article newArt : newArticles) {
                        boolean exists = false;
                        for (Article curr : updatedList) {
                            if (curr.id() == newArt.id()) {
                                exists = true;
                                break;
                            }
                        }
                        if (!exists) {
                            updatedList.add(newArt);
                        }
                    }
                    currentArticlesPage = nextPage;
                    articles.postValue(updatedList);

                    if (page.meta() != null && nextPage >= page.meta().lastPage()) {
                        canLoadMoreArticles.postValue(false);
                    } else if (page.links() != null && page.links().next() == null) {
                        canLoadMoreArticles.postValue(false);
                    }
                } else {
                    canLoadMoreArticles.postValue(false);
                }
            } catch (Exception e) {
                Log.e("MainViewModel", "Failed to load more articles", e);
                toastErrorMessage.postValue(R.string.toast_errorMessage);
            } finally {
                isLoadingMoreArticles.postValue(false);
            }
        });
    }

    public LiveData<Boolean> getIsLoadingCalendar() {
        return isLoadingCalendar;
    }

    public LiveData<Integer> getToastErrorMessage() {
        return toastErrorMessage;
    }

    public void clearToastErrorMessage() {
        toastErrorMessage.setValue(null);
    }

    public CKZiUElektrykClient getClient() {
        return client;
    }

    private boolean settingsChanged = false;
    private boolean timetableNeedsRefresh = true;
    private boolean replacementsNeedsRefresh = true;
    private boolean articlesNeedsRefresh = true;
    private boolean calendarNeedsRefresh = true;

    public boolean isSettingsChanged() {
        return settingsChanged;
    }

    public void setSettingsChanged(boolean settingsChanged) {
        this.settingsChanged = settingsChanged;
    }

    public boolean isTimetableNeedsRefresh() {
        return timetableNeedsRefresh;
    }

    public void setTimetableNeedsRefresh(boolean timetableNeedsRefresh) {
        this.timetableNeedsRefresh = timetableNeedsRefresh;
    }

    public boolean isReplacementsNeedsRefresh() {
        return replacementsNeedsRefresh;
    }

    public void setReplacementsNeedsRefresh(boolean replacementsNeedsRefresh) {
        this.replacementsNeedsRefresh = replacementsNeedsRefresh;
    }

    public boolean isArticlesNeedsRefresh() {
        return articlesNeedsRefresh;
    }

    public void setArticlesNeedsRefresh(boolean articlesNeedsRefresh) {
        this.articlesNeedsRefresh = articlesNeedsRefresh;
    }

    public boolean isCalendarNeedsRefresh() {
        return calendarNeedsRefresh;
    }

    public void setCalendarNeedsRefresh(boolean calendarNeedsRefresh) {
        this.calendarNeedsRefresh = calendarNeedsRefresh;
    }
}
