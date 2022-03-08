package com.connectus.mobile.common;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.auth0.android.jwt.JWT;
import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.zxing.BarcodeFormat;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Common {
    private static final String TAG = Common.class.getSimpleName();

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ignored) {

        }
    }

    public static void subscribeToTopic(String topic) {
        FirebaseMessaging.getInstance().subscribeToTopic(topic)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Log.d(TAG, String.format("failed to subscribe to %s topic", topic));
                        }
                        Log.d(TAG, String.format("successfully subscribed to %s topic", topic));
                    }
                });
    }

    public static void unsubscribeTopic(String topic) {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(topic);
    }

    public static Bitmap generateQRCode(String contents) {
        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            return barcodeEncoder.encodeBitmap(contents, BarcodeFormat.QR_CODE, 400, 400);
        } catch (Exception e) {
            return null;
        }
    }

    public static void loadAvatar(boolean isAvatarAvailable, ImageView imageView, UUID userId) {
        if (isAvatarAvailable) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/core/api/v1/user/profile-picture/" + userId + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageView);
        }
    }

    public void loadAvatar(boolean avatarAvailable, UUID userId, ImageView imageView) {
        if (avatarAvailable) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/core/api/v1/user/profile-picture/" + userId + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageView);
        }
    }

    public static void showCameraPermissionRationale(Activity activity, int requestCode) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Camera Permission Explanation")
                .setMessage("eMalyami requires the camera permission to allow you to capture Profile Pictures, KYC Documents and scan QR Codes.")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CAMERA}, requestCode
                        );
                    }
                })
                .show();
    }

    public static JWT tokenToJWT(String token) {
        return new JWT(token.replace("Bearer ", ""));
    }

    public static boolean isSessionValid(SharedPreferencesManager sharedPreferencesManager) {
        String authenticationToken = sharedPreferencesManager.getAuthenticationToken();
        JWT jwt = null;
        if (authenticationToken != null) {
            Log.d(TAG, authenticationToken);
            jwt = tokenToJWT(authenticationToken);
            return jwt != null && !jwt.isExpired(0);
        }
        return false;
    }


    public static void clearSessionData(SharedPreferencesManager sharedPreferencesManager, Context context) {
        try {
            sharedPreferencesManager.clearAll();
            DbHandler dbHandler = new DbHandler(context);
            dbHandler.deleteAllNotifications();
            dbHandler.deleteAllBalances();
            dbHandler.deleteAllTransactions();
            dbHandler.deleteAllSibaProfiles();
            dbHandler.deleteAllMySibaInvites();
            dbHandler.deleteAllChatMessages();
            dbHandler.deleteAllSupportMessages();
        } catch (Exception ignore) {
        }
    }

    public static File saveBitmap(Context context, Bitmap bitmap, String filename) {
        ContextWrapper cw = new ContextWrapper(context);
        File directory = cw.getFilesDir();
        File file = new File(directory, filename + ".png");

        OutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
            outputStream.close();
            return file;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static String getFormattedTime(String pattern, Long timestamp) {
        @SuppressLint("SimpleDateFormat") DateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(timestamp);
        return dateFormat.format(date);
    }


    public static SibaProfile getSibaProfileById(Context context, String sibaProfileId) {
        DbHandler dbHandler = new DbHandler(context);
        List<SibaProfile> sibaProfiles = dbHandler.getSibaProfiles();
        for (SibaProfile sibaProfile : sibaProfiles) {
            if (sibaProfile.getId().equals(sibaProfileId)) {
                return sibaProfile;
            }
        }
        return null;
    }

    public static String[] splitCountryCodeFromPhone(String phoneNumber) throws NumberParseException {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(phoneNumber, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
        return new String[]{"+" + phone.getCountryCode(), String.valueOf(phone.getNationalNumber())};
    }
}
