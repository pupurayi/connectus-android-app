package com.connectus.mobile.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import retrofit2.Response;

public class Utils {
    private static final String TAG = Utils.class.getSimpleName();

    public static void loadAvatar(UserDto userDto, ImageView imageView) {
        if (userDto.getAvatar() != null) {
            byte[] imageAsBytes = Base64.decode(userDto.getAvatar(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }

    public static void alert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.yes, null)
                .show();
    }

    public static String handleHttpException(Response response) {
        try {
            String json = response.errorBody().string();
            JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
            if (jsonObject.get("error") != null) {
                return jsonObject.get("error").getAsString();
            }
        } catch (IOException e) {
            Log.d(TAG, "Exception: ", e);
        }
        return "Something went wrong!";
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {

        }
    }

    public static boolean isSessionValid(SharedPreferencesManager sharedPreferencesManager) {
        return sharedPreferencesManager.getUser() != null;
    }

    public static void clearSessionData(SharedPreferencesManager sharedPreferencesManager, Context context) {
        try {
            sharedPreferencesManager.clearAll();
            DbHandler dbHandler = new DbHandler(context);
            dbHandler.deleteAllProducts();
        } catch (Exception ignore) {
        }
    }

    public static  boolean appInstalled(Activity activity, String url) {
        PackageManager packageManager = activity.getPackageManager();
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (Exception ignored) {
        }
        return false;
    }

    public static boolean isWhatsappInstalled(Activity activity) {
        return Utils.appInstalled(activity, "com.whatsapp");
    }

    public static void sendWhatsappMessage(Activity activity, String msisdn, String message) {
        if (isWhatsappInstalled(activity)) {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + msisdn + "&text=" + message));
            activity.startActivity(intent);
        }else{
            Utils.alert(activity, "Connect Us", "Please install Whatsapp or Whatsapp Web!");
        }
    }

    public static boolean hasLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermission(Activity activity) {
        if (!hasLocationPermission(activity.getApplicationContext())) {
            ActivityCompat.requestPermissions(
                    activity,
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    999);
        }
    }

    public static String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public static void returnToDashboard(FragmentManager fragmentManager) {
        DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
        transaction.commit();
    }
}
