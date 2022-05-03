package com.connectus.mobile.utils;

import android.graphics.BitmapFactory;
import android.util.Base64;
import android.widget.ImageView;

import com.connectus.mobile.api.dto.UserDto;

public class Utils {

    public static void loadAvatar(UserDto userDto, ImageView imageView) {
        if (userDto.getAvatar() != null) {
            byte[] imageAsBytes = Base64.decode(userDto.getAvatar(), Base64.DEFAULT);
            imageView.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
        }
    }
}
