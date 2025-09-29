package com.example.planlekcji.listener;

import com.example.planlekcji.ckziu_elektryk.client.replacements.Replacement;

import java.util.List;

public interface ReplacementsDownloadCompleteListener {

    void onDownloadComplete(List<List<Replacement>> rawReplacements);

    void onDownloadFailed();
}
