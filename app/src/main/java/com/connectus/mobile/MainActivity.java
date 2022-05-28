package com.connectus.mobile;

import static com.connectus.mobile.utils.Utils.requestLocationPermission;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.IntentSender;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.initial.demographics.DemographicsFragment;
import com.connectus.mobile.ui.initial.splashscreen.SplashScreenFragment;
import com.connectus.mobile.utils.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int DAYS_FOR_FLEXIBLE_UPDATE = 7;

    private ProgressDialog pd;
    private SharedPreferencesManager sharedPreferencesManager;
    private FusedLocationProviderClient fusedLocationClient;
    private UserDto user;
    public double currentLat = 0, currentLng = 0;

    // https://developer.android.com/guide/playcore/in-app-updates/kotlin-java
    AppUpdateManager appUpdateManager;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

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
        new Timer()
                .schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                Log.d(TAG, "Syncing location...");
                                requestLocationPermission(getParent());
                                getLastLocation();
                            }
                        },
                        0,
                        60 * 1000);

        sharedPreferencesManager = new SharedPreferencesManager(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (Utils.isSessionValid(sharedPreferencesManager)) {
            user = sharedPreferencesManager.getUser();
            if (user.getGender() == null || user.getEthnicity() == null || user.getDob() == null || user.getReligion() == null || user.getTownship() == null || user.getTown() == null) {
                DemographicsFragment demographicsFragment = new DemographicsFragment();
                transaction.add(R.id.container, demographicsFragment, DemographicsFragment.class.getSimpleName());
            } else {
                DashboardFragment dashboardFragment = new DashboardFragment();
                transaction.add(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
            }
        } else {
            SplashScreenFragment splashScreenFragment = new SplashScreenFragment();
            transaction.add(R.id.container, splashScreenFragment, SplashScreenFragment.class.getSimpleName());
        }
        transaction.commit();
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

    @SuppressLint("MissingPermission")
    public void getLastLocation() {
        fusedLocationClient
                .getLastLocation()
                .addOnSuccessListener(
                        location -> {
                            if (location != null) {
                                this.currentLat = location.getLatitude();
                                this.currentLng = location.getLongitude();
                            }
                        });
    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLng() {
        return currentLng;
    }

    public void setCurrentLng(double currentLng) {
        this.currentLng = currentLng;
    }
}