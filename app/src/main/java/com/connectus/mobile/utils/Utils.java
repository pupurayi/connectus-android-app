package com.connectus.mobile.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.connectus.mobile.api.dto.UserDto;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

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
}
