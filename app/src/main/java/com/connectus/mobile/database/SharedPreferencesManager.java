package com.connectus.mobile.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.api.dto.CheckResponseDto;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferencesManager {
    private static final String TAG = SharedPreferencesManager.class.getSimpleName();

    private Context context;
    private final SharedPreferences.Editor editor;
    private Map<String, ?> sharedPreferences;
    @SuppressLint("SimpleDateFormat")

    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @SuppressLint("CommitPrefEdits")
    public SharedPreferencesManager(Context context) {
        this.context = context;
        this.editor = context.getSharedPreferences("connectus", MODE_PRIVATE).edit();
    }

    public Map<String, ?> getSharedPreferences() {
        return context.getSharedPreferences("connectus", Context.MODE_PRIVATE).getAll();
    }

    public void setAuthorization(CheckResponseDto checkResponseDto) {
        editor.putString("authorization", new Gson().toJson(checkResponseDto));
        editor.apply();
    }

    public CheckResponseDto getAuthorization() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("authorization") != null ? new Gson().fromJson(sharedPreferences.get("authorization").toString(), CheckResponseDto.class) : new CheckResponseDto();
    }

    public void setAvatarAvailable(boolean avatarAvailable) {
        editor.putBoolean("avatarAvailable", avatarAvailable);
        editor.apply();
    }

    public void setUser(UserDto userDto) {
        editor.putString("user", new Gson().toJson(userDto));
        editor.putLong("lastSync", new Date().getTime());
        editor.apply();
    }

    public UserDto getUser() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("user") != null ? new Gson().fromJson(sharedPreferences.get("user").toString(), UserDto.class) : null;
    }

    public long getLastSync() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("lastSync") != null ? Long.parseLong(sharedPreferences.get("lastSync").toString()) : 0;
    }

    public void clearAll() {
        this.editor.clear().apply();
    }
}
