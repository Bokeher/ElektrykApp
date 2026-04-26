package com.example.planlekcji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.ckziu_elektryk.client.Config;
import com.example.planlekcji.fragments.model.ViewPagerAdapter;
import com.example.planlekcji.utils.ToastUtils;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MainActivity extends AppCompatActivity {
    private static Context appContext;
    private MainViewModel mainViewModel;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the application context for other functions.
        appContext = this;

        // Obtain the MainViewModel instance to update data on settings changes
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Check for internet connection; exit the app if not connected.
        if (!Config.getOrCreateConfig().isPreviewMode() && !isOnline()) {
            String errorMessage = getString(R.string.toastErrorMessage_noInternetConnection);
            ToastUtils.showToast(this, errorMessage, true);
        }

        // Lock the orientation of the screen to portrait mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Force night mode for the entire application.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Set the content view for the main activity.
        setContentView(R.layout.activity_main);

        // Progress bar
        ProgressBar progressBar = findViewById(R.id.progressBar);

        mainViewModel.getIsLoadingReplacements().observe(this, isLoading ->
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE)
        );

        mainViewModel.getIsLoadingTimetable().observe(this, isLoading ->
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE)
        );

        // Set adapter
        ViewPager2 viewPager2_appContent = findViewById(R.id.viewPager2_appContent);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2_appContent.setAdapter(adapter);
        viewPager2_appContent.setOffscreenPageLimit(3);

        // Disable user input to allow input of timetable ViewPager
        viewPager2_appContent.setUserInputEnabled(false);

        // Connect the TabLayout (navigation) with the ViewPager2 (app content)
        TabLayout tabLayout_navigate = findViewById(R.id.tabLayout_navigate);
        new TabLayoutMediator(tabLayout_navigate, viewPager2_appContent, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.navigate_timetable);
                    tab.setIcon(R.drawable.timetable_icon);
                    break;
                case 1:
                    tab.setText(R.string.navigate_replacements);
                    tab.setIcon(R.drawable.replacement_icon);
                    break;
                case 2:
                    tab.setText(R.string.navigate_settings);
                    tab.setIcon(R.drawable.settings_icon);
                    break;
            }
        }).attach();

        // Add listener to refresh data upon exiting settings
        tabLayout_navigate.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {}

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Update data upon exiting settings
                if(tab.getPosition() == ViewPagerAdapter.SETTINGS_TAB_ID) {
                    mainViewModel.fetchData();
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    public static Context getContext() {
        return appContext;
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static SchoolEntryType getTimetableType() {
        Context context = MainActivity.getContext();
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

        // 0 - classes, 1 - teachers, 2 - classrooms
        int typeOfTimetable = sharedPreferences.getInt("selectedTypeOfTimetable", 0);

        return SchoolEntryType.values()[typeOfTimetable];
    }

    public static String getToken(SchoolEntryType timetableType) {
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