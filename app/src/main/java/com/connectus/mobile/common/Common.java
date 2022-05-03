package com.connectus.mobile.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;

import com.auth0.android.jwt.JWT;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

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

    public static JWT tokenToJWT(String token) {
        return new JWT(token.replace("Bearer ", ""));
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

    public static String[] splitCountryCodeFromPhone(String phoneNumber) throws NumberParseException {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        Phonenumber.PhoneNumber phone = phoneNumberUtil.parse(phoneNumber, Phonenumber.PhoneNumber.CountryCodeSource.UNSPECIFIED.name());
        return new String[]{"+" + phone.getCountryCode(), String.valueOf(phone.getNationalNumber())};
    }
}
