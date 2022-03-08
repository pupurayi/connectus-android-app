package com.connectus.mobile;

import android.app.ProgressDialog;
import android.content.IntentSender;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.splashscreen.SplashScreenFragment;
import com.freshchat.consumer.sdk.Freshchat;
import com.freshchat.consumer.sdk.FreshchatConfig;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DAYS_FOR_FLEXIBLE_UPDATE = 7;

    ProgressDialog pd;
    SharedPreferencesManager sharedPreferencesManager;
    ProfileDTO profileDTO;

    // https://developer.android.com/guide/playcore/in-app-updates/kotlin-java
    AppUpdateManager appUpdateManager;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                if ((appUpdateInfo.updatePriority() >= 4 || (appUpdateInfo.clientVersionStalenessDays() != null && appUpdateInfo.clientVersionStalenessDays() >= DAYS_FOR_FLEXIBLE_UPDATE) && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE))) {
                    updateApp(appUpdateInfo, AppUpdateType.IMMEDIATE);
                } else if (appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    updateApp(appUpdateInfo, AppUpdateType.FLEXIBLE);
                }
            }
            try {
                appUpdateManager.startUpdateFlowForResult(appUpdateInfo,
                        AppUpdateType.IMMEDIATE, this, 100);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        });

        fragmentManager = getSupportFragmentManager();
        pd = new ProgressDialog(this);

        FreshchatConfig config = new FreshchatConfig(Constants.FRESH_CHAT_APP_ID, Constants.FRESH_CHAT_APP_KEY);
        config.setDomain(Constants.FRESH_CHAT_DOMAIN);
        config.setCameraCaptureEnabled(true);
        config.setGallerySelectionEnabled(true);
        config.setResponseExpectationEnabled(true);
        Freshchat.getInstance(getApplicationContext()).init(config);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (Common.isSessionValid(sharedPreferencesManager)) {
            profileDTO = sharedPreferencesManager.getProfile();
//            if (profileDTO.getUserStatus().equals("PENDING_VERIFICATION")) {
//                Bundle bundle = new Bundle();
//                bundle.putString("otpType", "PHONE_VERIFICATION");
//                bundle.putString("otpTitle", "Phone Verification");
//                bundle.putString("username", profileDTO.getUsername());
//                OTPFragment otpFragment = new OTPFragment();
//                otpFragment.setArguments(bundle);
//                transaction.add(R.id.container, otpFragment, OTPFragment.class.getSimpleName());
//            } else {
            DashboardFragment dashboardFragment = new DashboardFragment();
            transaction.add(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
//            }
        } else {
            SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
            transaction.add(R.id.container, splashScreenFragment, SplashScreenFragment.class.getSimpleName());
        }
        transaction.commit();

//        fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
//            @Override
//            public void onBackStackChanged() {
//                Toast.makeText(getApplicationContext(), "" + fragmentManager.getBackStackEntryCount(), Toast.LENGTH_LONG).show();
//            }
//        });
    }

    public void updateApp(AppUpdateInfo appUpdateInfo, int appUpdateType) {
        try {
            appUpdateManager.startUpdateFlowForResult(appUpdateInfo, appUpdateType,
                    this, 100);
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }
}