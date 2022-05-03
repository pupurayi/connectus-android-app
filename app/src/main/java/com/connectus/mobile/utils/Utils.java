package com.connectus.mobile.utils;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;

import com.connectus.mobile.api.dto.UserDto;

public class Utils {

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
}
