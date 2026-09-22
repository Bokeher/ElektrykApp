package com.example.planlekcji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.planlekcji.ckziu_elektryk.client.timetable.SchoolEntryType;
import com.example.planlekcji.fragments.ViewPagerAdapter;
import com.example.planlekcji.notifications.FcmTopicManager;
import com.example.planlekcji.notifications.LiveUpdateRelay;
import com.example.planlekcji.notifications.NotificationHelper;
import com.example.planlekcji.utils.NetworkMonitor;
import com.example.planlekcji.utils.RefreshCooldownManager;
import com.example.planlekcji.utils.RefreshDataType;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity implements LiveUpdateRelay.LiveUpdateListener {
    public static final String EXTRA_TARGET_TAB = "extra_target_tab";

    private static Context appContext;
    private MainViewModel mainViewModel;
    private NetworkMonitor networkMonitor;
    private ViewPager2 viewPager2_appContent;

    private View layoutInAppNotificationBanner;
    private ImageView ivInAppNotificationIcon;
    private TextView tvInAppNotificationCategory;
    private TextView tvInAppNotificationMessage;
    private View btnCloseInAppNotification;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable hideBannerRunnable;
    private int currentBannerTargetTab = -1;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize the application context for other functions.
        appContext = getApplicationContext();

        // Initialize notification channels and sync FCM topic subscriptions
        NotificationHelper.createNotificationChannels(this);
        FcmTopicManager.syncSubscriptions(this);

        // Obtain the MainViewModel instance to update data on settings changes
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Lock the orientation of the screen to portrait mode.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Force night mode for the entire application.
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        // Set the content view for the main activity.
        setContentView(R.layout.activity_main);

        // Apply window insets to avoid drawing behind system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout), (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });

        // Set adapter
        viewPager2_appContent = findViewById(R.id.viewPager2_appContent);
        SwipeRefreshLayout swipeRefresh = findViewById(R.id.swipeRefresh_main);

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2_appContent.setAdapter(adapter);
        viewPager2_appContent.setOffscreenPageLimit(4);
        viewPager2_appContent.setUserInputEnabled(true);

        // Network monitoring and offline banner
        networkMonitor = new NetworkMonitor(this);
        View layoutOfflineBanner = findViewById(R.id.layout_offlineBanner);

        networkMonitor.getIsOnlineLiveData().observe(this, isOnline -> {
            if (layoutOfflineBanner != null) {
                layoutOfflineBanner.setVisibility(Boolean.TRUE.equals(isOnline) ? View.GONE : View.VISIBLE);
            }
            updateSwipeRefreshState(swipeRefresh, viewPager2_appContent.getCurrentItem());
        });

        // Swipe to Refresh
        if (swipeRefresh != null) {
            swipeRefresh.setProgressBackgroundColorSchemeColor(Color.parseColor("#2C2C2C"));
            swipeRefresh.setColorSchemeColors(Color.parseColor("#FFC107"));
            updateSwipeRefreshState(swipeRefresh, viewPager2_appContent.getCurrentItem());
            swipeRefresh.setOnRefreshListener(() -> {
                int currentTab = viewPager2_appContent.getCurrentItem();
                switch (currentTab) {
                    case ViewPagerAdapter.TIMETABLE_TAB_ID -> handleManualRefresh(swipeRefresh, RefreshDataType.TIMETABLE, () -> mainViewModel.fetchTimetable());
                    case ViewPagerAdapter.REPLACEMENTS_TAB_ID -> handleManualRefresh(swipeRefresh, RefreshDataType.REPLACEMENTS, () -> mainViewModel.fetchReplacements());
                    case ViewPagerAdapter.ARTICLES_TAB_ID -> handleManualRefresh(swipeRefresh, RefreshDataType.ARTICLES, () -> mainViewModel.forceFetchArticles());
                    case ViewPagerAdapter.CALENDAR_TAB_ID -> handleManualRefresh(swipeRefresh, RefreshDataType.CALENDAR, () -> mainViewModel.forceFetchCalendar());
                    default -> swipeRefresh.setRefreshing(false);
                }
            });
        }

        // Synchronize SwipeRefreshLayout state with ViewPager2 page changes
        viewPager2_appContent.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateSwipeRefreshState(swipeRefresh, position);
                cancelNotificationForTab(position);
            }
        });

        // In-App Notification Banner
        layoutInAppNotificationBanner = findViewById(R.id.layout_inAppNotificationBanner);
        ivInAppNotificationIcon = findViewById(R.id.iv_inAppNotificationIcon);
        tvInAppNotificationCategory = findViewById(R.id.tv_inAppNotificationCategory);
        tvInAppNotificationMessage = findViewById(R.id.tv_inAppNotificationMessage);
        btnCloseInAppNotification = findViewById(R.id.btn_closeInAppNotification);

        if (btnCloseInAppNotification != null) {
            btnCloseInAppNotification.setOnClickListener(v -> hideInAppNotificationBanner(true));
        }

        // Progress indicator
        LinearProgressIndicator progressBar = findViewById(R.id.linearProgressBar);

        androidx.lifecycle.Observer<Boolean> loadingObserver = unused -> {
            boolean isLoading = Boolean.TRUE.equals(mainViewModel.getIsLoadingReplacements().getValue())
                    || Boolean.TRUE.equals(mainViewModel.getIsLoadingTimetable().getValue())
                    || Boolean.TRUE.equals(mainViewModel.getIsLoadingArticles().getValue())
                    || Boolean.TRUE.equals(mainViewModel.getIsLoadingCalendar().getValue());

            boolean isSwipeRefreshing = swipeRefresh != null && swipeRefresh.isRefreshing();

            if (isLoading) {
                if (!isSwipeRefreshing) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            } else {
                progressBar.setVisibility(View.GONE);
                if (swipeRefresh != null) {
                    swipeRefresh.setRefreshing(false);
                }
            }
        };

        mainViewModel.getIsLoadingReplacements().observe(this, loadingObserver);
        mainViewModel.getIsLoadingTimetable().observe(this, loadingObserver);
        mainViewModel.getIsLoadingArticles().observe(this, loadingObserver);
        mainViewModel.getIsLoadingCalendar().observe(this, loadingObserver);

        // Error message Toast observer
        mainViewModel.getToastErrorMessage().observe(this, resId -> {
            if (resId != null) {
                Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
                mainViewModel.clearToastErrorMessage();
            }
        });

        // Connect the TabLayout (navigation) with the ViewPager2 (app content)
        TabLayout tabLayout_navigate = findViewById(R.id.tabLayout_navigate);
        new TabLayoutMediator(tabLayout_navigate, viewPager2_appContent, (tab, position) -> {
            switch (position) {
                case ViewPagerAdapter.TIMETABLE_TAB_ID:
                    tab.setText(R.string.navigate_timetable);
                    tab.setIcon(R.drawable.timetable_icon);
                    break;
                case ViewPagerAdapter.REPLACEMENTS_TAB_ID:
                    tab.setText(R.string.navigate_replacements);
                    tab.setIcon(R.drawable.replacement_icon);
                    break;
                case ViewPagerAdapter.ARTICLES_TAB_ID:
                    tab.setText(R.string.navigate_articles);
                    tab.setIcon(R.drawable.articles_icon);
                    break;
                case ViewPagerAdapter.CALENDAR_TAB_ID:
                    tab.setText(R.string.navigate_calendar);
                    tab.setIcon(R.drawable.calendar_icon);
                    break;
                case ViewPagerAdapter.SETTINGS_TAB_ID:
                    tab.setText(R.string.navigate_settings);
                    tab.setIcon(R.drawable.settings_icon);
                    break;
            }
        }).attach();

        tabLayout_navigate.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateSwipeRefreshState(swipeRefresh, tab.getPosition());
                triggerTabFetch(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // When exiting settings after changes, mark dependent tabs as needing refresh
                if (tab.getPosition() == ViewPagerAdapter.SETTINGS_TAB_ID) {
                    if (mainViewModel.isSettingsChanged()) {
                        mainViewModel.setSettingsChanged(false);
                        mainViewModel.setTimetableNeedsRefresh(true);
                        mainViewModel.setReplacementsNeedsRefresh(true);

                        RefreshCooldownManager cooldown = RefreshCooldownManager.getInstance(MainActivity.this);
                        if (cooldown != null) {
                            cooldown.invalidate(RefreshDataType.TIMETABLE);
                            cooldown.invalidate(RefreshDataType.REPLACEMENTS);
                        }
                    }
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Trigger initial data load for the default visible tab (both online and offline)
        triggerCurrentTabFetch(viewPager2_appContent);

        // Handle navigation if started from a notification click
        handleNotificationIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleNotificationIntent(intent);
    }

    private void handleNotificationIntent(Intent intent) {
        if (intent == null || !intent.hasExtra(EXTRA_TARGET_TAB) || viewPager2_appContent == null) {
            return;
        }

        int targetTab = intent.getIntExtra(EXTRA_TARGET_TAB, -1);
        if (targetTab < 0) {
            return;
        }

        viewPager2_appContent.setCurrentItem(targetTab, false);
        cancelNotificationForTab(targetTab);
        switch (targetTab) {
            case ViewPagerAdapter.TIMETABLE_TAB_ID -> {
                mainViewModel.setTimetableNeedsRefresh(true);
                mainViewModel.fetchTimetable();
            }
            case ViewPagerAdapter.REPLACEMENTS_TAB_ID -> {
                mainViewModel.setReplacementsNeedsRefresh(true);
                mainViewModel.fetchReplacements();
            }
            case ViewPagerAdapter.ARTICLES_TAB_ID -> {
                mainViewModel.setArticlesNeedsRefresh(true);
                mainViewModel.fetchArticles();
            }
        }
    }

    private void cancelNotificationForTab(int tabPosition) {
        switch (tabPosition) {
            case ViewPagerAdapter.TIMETABLE_TAB_ID ->
                    NotificationHelper.cancelNotification(this, NotificationHelper.NOTIFICATION_ID_TIMETABLE);
            case ViewPagerAdapter.REPLACEMENTS_TAB_ID ->
                    NotificationHelper.cancelNotification(this, NotificationHelper.NOTIFICATION_ID_REPLACEMENTS);
            case ViewPagerAdapter.ARTICLES_TAB_ID ->
                    NotificationHelper.cancelNotification(this, NotificationHelper.NOTIFICATION_ID_ARTICLES);
        }
        if (currentBannerTargetTab == tabPosition) {
            hideInAppNotificationBanner(true);
        }
    }

    private void updateSwipeRefreshState(SwipeRefreshLayout swipeRefresh, int currentPosition) {
        if (swipeRefresh != null) {
            boolean canRefresh = isOnline() && currentPosition != ViewPagerAdapter.SETTINGS_TAB_ID;
            swipeRefresh.setEnabled(canRefresh);
        }
    }

    private void handleManualRefresh(SwipeRefreshLayout swipeRefresh, RefreshDataType type, Runnable refreshAction) {
        RefreshCooldownManager cooldown = RefreshCooldownManager.getInstance(this);
        if (cooldown != null && !cooldown.canManualRefresh(type)) {
            swipeRefresh.setRefreshing(false);
            Toast.makeText(this, R.string.refresh_up_to_date, Toast.LENGTH_SHORT).show();
            return;
        }
        refreshAction.run();
    }

    private void triggerCurrentTabFetch(ViewPager2 viewPager) {
        triggerTabFetch(viewPager.getCurrentItem());
    }

    private void triggerTabFetch(int position) {
        switch (position) {
            case ViewPagerAdapter.TIMETABLE_TAB_ID -> {
                if (mainViewModel.isTimetableNeedsRefresh()) {
                    mainViewModel.setTimetableNeedsRefresh(false);
                    mainViewModel.fetchTimetable();
                }
            }
            case ViewPagerAdapter.REPLACEMENTS_TAB_ID -> {
                if (mainViewModel.isReplacementsNeedsRefresh()) {
                    mainViewModel.setReplacementsNeedsRefresh(false);
                    mainViewModel.fetchReplacements();
                }
            }
            case ViewPagerAdapter.ARTICLES_TAB_ID -> {
                if (mainViewModel.isArticlesNeedsRefresh()) {
                    mainViewModel.setArticlesNeedsRefresh(false);
                    mainViewModel.fetchArticles();
                }
            }
            case ViewPagerAdapter.CALENDAR_TAB_ID -> {
                if (mainViewModel.isCalendarNeedsRefresh()) {
                    mainViewModel.setCalendarNeedsRefresh(false);
                    mainViewModel.fetchCalendar();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        LiveUpdateRelay.register(this);
        if (viewPager2_appContent != null) {
            cancelNotificationForTab(viewPager2_appContent.getCurrentItem());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LiveUpdateRelay.unregister(this);
        hideInAppNotificationBanner(false);
    }

    @Override
    public void onLiveUpdateReceived(LiveUpdateRelay.LiveUpdateEvent event) {
        if (event == null) return;
        onLiveUpdateReceived(event.type());
        if (event.title() != null || event.message() != null) {
            runOnUiThread(() -> showInAppNotificationBanner(event));
        }
    }

    @Override
    public void onLiveUpdateReceived(LiveUpdateRelay.UpdateType type) {
        if (type == null || mainViewModel == null) return;
        runOnUiThread(() -> {
            RefreshCooldownManager cooldown = RefreshCooldownManager.getInstance(this);
            switch (type) {
                case REPLACEMENTS -> {
                    if (cooldown != null) {
                        cooldown.invalidate(RefreshDataType.REPLACEMENTS);
                    }
                    mainViewModel.setReplacementsNeedsRefresh(false);
                    mainViewModel.fetchReplacements();
                }
                case TIMETABLE -> {
                    if (cooldown != null) {
                        cooldown.invalidate(RefreshDataType.TIMETABLE);
                    }
                    mainViewModel.setTimetableNeedsRefresh(false);
                    mainViewModel.fetchTimetable();
                }
                case ARTICLES -> {
                    if (cooldown != null) {
                        cooldown.invalidate(RefreshDataType.ARTICLES);
                    }
                    mainViewModel.setArticlesNeedsRefresh(false);
                    mainViewModel.forceFetchArticles();
                }
            }
        });
    }

    private void showInAppNotificationBanner(LiveUpdateRelay.LiveUpdateEvent event) {
        if (layoutInAppNotificationBanner == null || isFinishing() || isDestroyed()) {
            return;
        }

        if (hideBannerRunnable != null) {
            bannerHandler.removeCallbacks(hideBannerRunnable);
        }

        currentBannerTargetTab = event.targetTab();

        if (tvInAppNotificationCategory != null) {
            tvInAppNotificationCategory.setText(event.title() != null ? event.title() : "");
        }
        if (tvInAppNotificationMessage != null) {
            tvInAppNotificationMessage.setText(event.message() != null ? event.message() : "");
        }

        if (ivInAppNotificationIcon != null) {
            if (event.type() == LiveUpdateRelay.UpdateType.ARTICLES) {
                ivInAppNotificationIcon.setImageResource(R.drawable.articles_icon);
                ivInAppNotificationIcon.setContentDescription(getString(R.string.notification_channel_articles));
            } else {
                ivInAppNotificationIcon.setImageResource(R.drawable.replacement_icon);
                ivInAppNotificationIcon.setContentDescription(getString(R.string.notification_channel_replacements));
            }
        }

        layoutInAppNotificationBanner.setOnClickListener(v -> {
            if (event.targetTab() >= 0 && viewPager2_appContent != null) {
                viewPager2_appContent.setCurrentItem(event.targetTab(), true);
            }
            hideInAppNotificationBanner(true);
        });

        if (layoutInAppNotificationBanner.getVisibility() != View.VISIBLE) {
            layoutInAppNotificationBanner.setVisibility(View.VISIBLE);
            layoutInAppNotificationBanner.setAlpha(0f);
            layoutInAppNotificationBanner.setTranslationY(-80f);
            layoutInAppNotificationBanner.animate()
                    .translationY(0f)
                    .alpha(1f)
                    .setDuration(300)
                    .start();
        } else {
            layoutInAppNotificationBanner.animate().cancel();
            layoutInAppNotificationBanner.setAlpha(1f);
            layoutInAppNotificationBanner.setTranslationY(0f);
        }

        hideBannerRunnable = () -> hideInAppNotificationBanner(true);
        bannerHandler.postDelayed(hideBannerRunnable, 6000);
    }

    private void hideInAppNotificationBanner(boolean animate) {
        if (layoutInAppNotificationBanner == null) return;
        if (hideBannerRunnable != null) {
            bannerHandler.removeCallbacks(hideBannerRunnable);
            hideBannerRunnable = null;
        }
        currentBannerTargetTab = -1;
        if (layoutInAppNotificationBanner.getVisibility() != View.VISIBLE) {
            return;
        }
        if (animate) {
            layoutInAppNotificationBanner.animate()
                    .translationY(-80f)
                    .alpha(0f)
                    .setDuration(250)
                    .withEndAction(() -> {
                        layoutInAppNotificationBanner.setVisibility(View.GONE);
                        layoutInAppNotificationBanner.setTranslationY(0f);
                        layoutInAppNotificationBanner.setAlpha(1f);
                    })
                    .start();
        } else {
            layoutInAppNotificationBanner.animate().cancel();
            layoutInAppNotificationBanner.setVisibility(View.GONE);
            layoutInAppNotificationBanner.setTranslationY(0f);
            layoutInAppNotificationBanner.setAlpha(1f);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkMonitor != null) {
            networkMonitor.unregisterCallback();
        }
    }

    public static Context getContext() {
        if (appContext != null) {
            return appContext;
        }
        return PlanLekcjiApp.getAppContext();
    }

    public NetworkMonitor getNetworkMonitor() {
        return networkMonitor;
    }

    public boolean isOnline() {
        if (networkMonitor != null) {
            return networkMonitor.isCurrentlyOnline();
        }
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        Network network = cm.getActiveNetwork();
        if (network == null) return false;

        NetworkCapabilities capabilities = cm.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public static SchoolEntryType getTimetableType() {
        return getTimetableType(getContext());
    }

    public static SchoolEntryType getTimetableType(Context context) {
        if (context == null) {
            context = getContext();
        }
        if (context == null) {
            return SchoolEntryType.CLASSES;
        }
        SharedPreferences sharedPreferences = context.getSharedPreferences("sharedPrefs", 0);

        // 0 - classes, 1 - teachers, 2 - classrooms
        int typeOfTimetable = sharedPreferences.getInt("selectedTypeOfTimetable", 0);
        SchoolEntryType[] values = SchoolEntryType.values();
        if (typeOfTimetable < 0 || typeOfTimetable >= values.length) {
            return SchoolEntryType.CLASSES;
        }

        return values[typeOfTimetable];
    }

    public static String getToken(SchoolEntryType timetableType) {
        return getToken(getContext(), timetableType);
    }

    public static String getToken(Context context, SchoolEntryType timetableType) {
        if (context == null) {
            context = getContext();
        }
        if (context == null) {
            return "";
        }
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