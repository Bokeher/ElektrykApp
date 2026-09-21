package com.example.planlekcji.replacements;

import android.content.Context;
import android.util.Log;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.database.DatabaseCacheManager;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;
import com.example.planlekcji.preview.PreviewDataStore;
import com.example.planlekcji.utils.RefreshCooldownManager;
import com.example.planlekcji.utils.RefreshDataType;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class ReplacementDataDownloader implements Runnable {
    public static final int REPLACEMENTS_PERIOD_DAYS = 7;

    private final ReplacementsDownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public ReplacementDataDownloader(CKZiUElektrykClient client, ReplacementsDownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        SchoolEntryType timetableType = MainActivity.getTimetableType();
        String token = MainActivity.getToken(timetableType);

        if (Config.getOrCreateConfig().isPreviewMode()) {
            listener.onDownloadComplete(PreviewDataStore.getReplacements(timetableType, token));
            return;
        }

        // Cache first
        Context context = MainActivity.getContext();
        String cacheKey = "replacements_v2_" + timetableType.name() + "_" + token;
        Type listType = new TypeToken<List<DayReplacements>>(){}.getType();
        List<DayReplacements> cached = null;

        if (context != null && !token.isEmpty()) {
            try {
                cached = DatabaseCacheManager.getInstance(context).getObject(cacheKey, listType);
                if (cached != null && !cached.isEmpty()) {
                    listener.onCacheLoaded(cached);
                }
            } catch (Exception e) {
                Log.e("ReplacementsDownloader", "Failed to load cached replacements", e);
            }
        }

        // Complete immediately if cache is fresh within TTL
        RefreshCooldownManager cooldown = (context != null) ? RefreshCooldownManager.getInstance(context) : null;
        if (cooldown != null && cooldown.isFresh(RefreshDataType.REPLACEMENTS, cached)) {
            listener.onDownloadComplete(cached);
            return;
        }

        try {
            ReplacementService replacementService = client.getReplacementService();

            Calendar startCal = Calendar.getInstance();
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            startCal.set(Calendar.MILLISECOND, 0);
            Date startDate = startCal.getTime();

            Calendar endCal = (Calendar) startCal.clone();
            endCal.add(Calendar.DAY_OF_MONTH, Math.max(0, REPLACEMENTS_PERIOD_DAYS - 1));
            Date endDate = endCal.getTime();

            ReplacementType replacementType = (timetableType == SchoolEntryType.CLASSES)
                    ? ReplacementType.CLASSES
                    : ReplacementType.TEACHERS;

            Map<Date, List<Replacement>> rawMap = replacementService.getReplacements(replacementType, startDate, endDate);
            if (rawMap == null) {
                listener.onDownloadFailed();
                return;
            }

            List<DayReplacements> latestReplacements = new ArrayList<>();
            List<Date> sortedDates = new ArrayList<>(rawMap.keySet());
            Collections.sort(sortedDates);

            for (Date date : sortedDates) {
                if (Thread.currentThread().isInterrupted()) return;

                List<Replacement> rawReplacements = rawMap.get(date);
                if (rawReplacements == null) continue;

                List<Replacement> filtered;
                if (timetableType == SchoolEntryType.CLASSES) {
                    filtered = rawReplacements.stream()
                            .filter(Objects::nonNull)
                            .filter(r -> Objects.equals(r.name(), token))
                            .collect(Collectors.toList());
                } else {
                    filtered = rawReplacements.stream()
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                }

                if (!filtered.isEmpty()) {
                    latestReplacements.add(new DayReplacements(date, filtered));
                }
            }

            if (context != null && !token.isEmpty()) {
                DatabaseCacheManager.getInstance(context).saveObject(cacheKey, latestReplacements);
                if (cooldown != null) {
                    cooldown.recordRefresh(RefreshDataType.REPLACEMENTS);
                }
            }
            listener.onDownloadComplete(latestReplacements);
        } catch (Exception e) {
            listener.onDownloadFailed();
        }
    }

    public static Date[] getNext5Dates() {
        Date[] dates = new Date[5];
        Calendar cal = Calendar.getInstance();

        int count = 0;
        while (count < 5) {
            int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            if (dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY) {
                dates[count] = cal.getTime();
                count++;
            }

            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        return dates;
    }
}
