package com.example.planlekcji.replacements;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReplacementDataDownloader implements Runnable {
    private final ReplacementsDownloadCompleteListener listener;
    private final CKZiUElektrykClient client;

    public ReplacementDataDownloader(CKZiUElektrykClient client, ReplacementsDownloadCompleteListener listener) {
        this.listener = listener;
        this.client = client;
    }

    @Override
    public void run() {
        ReplacementService replacementService = client.getReplacementService();

        Date[] next5Dates = getNext5Dates();
        SchoolEntryType timetableType = MainActivity.getTimetableType();

        List<List<Replacement>> latestReplacements = new ArrayList<>();

        ReplacementType replacementType;
        if (timetableType == SchoolEntryType.CLASSES) {
            String token = MainActivity.getToken(MainActivity.getTimetableType());

            replacementType = ReplacementType.CLASSES;
            for (Date date : next5Dates) {
                List<Replacement> newReplacements = replacementService.getReplacements(replacementType, date)
                        .stream()
                        .filter(r -> r.name().equals(token))
                        .collect(Collectors.toList());

                latestReplacements.add(newReplacements);
            }
        } else {
            replacementType = ReplacementType.TEACHERS;
            for (Date date : next5Dates) {
                List<Replacement> newReplacements = replacementService.getReplacements(replacementType, date);
                latestReplacements.add(newReplacements);
            }
        }

        listener.onDownloadComplete(latestReplacements);
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
