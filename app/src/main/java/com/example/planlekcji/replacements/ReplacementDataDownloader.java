package com.example.planlekcji.replacements;

import com.example.planlekcji.MainActivity;
import com.example.planlekcji.ckziu_elektryk.client.CKZiUElektrykClient;
import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementService;
import com.example.planlekcji.ckziu_elektryk.client.replacements.ReplacementType;
import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.listener.ReplacementsDownloadCompleteListener;

import java.util.Collections;
import java.util.List;

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

        List<Replacement> latestReplacements;

        SchoolEntryType timetableType = MainActivity.getTimetableType();
        if (timetableType == SchoolEntryType.CLASSES) {
            latestReplacements = replacementService.getLatestReplacements(ReplacementType.CLASSES);
        } else {
            latestReplacements = replacementService.getLatestReplacements();
        }

        if (latestReplacements == null) {
            listener.onDownloadComplete(Collections.emptyList());
            return;
        }

        listener.onDownloadComplete(latestReplacements);
    }
}
