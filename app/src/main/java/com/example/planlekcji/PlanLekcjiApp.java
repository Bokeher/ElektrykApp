package com.example.planlekcji;

import android.app.Application;
import android.content.Context;

public class PlanLekcjiApp extends Application {
    private static PlanLekcjiApp instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Context getAppContext() {
        return instance;
    }
}
