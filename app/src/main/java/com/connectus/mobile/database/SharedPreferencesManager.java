package com.connectus.mobile.database;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.AuthorizationResponse;
import com.connectus.mobile.api.dto.PaymateDTO;
import com.connectus.mobile.api.dto.JWT;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
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

    public void setAuthorization(AuthorizationResponse authorizationResponse) {
        editor.putString("authorization", new Gson().toJson(authorizationResponse));
        editor.apply();
    }

    public AuthorizationResponse getAuthorization() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("authorization") != null ? new Gson().fromJson(sharedPreferences.get("authorization").toString(), AuthorizationResponse.class) : new AuthorizationResponse();
    }

    public void setJWT(JWT jwt) {
        editor.putString("authentication", new Gson().toJson(jwt));
        editor.apply();
    }

    public JWT getJWT() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("authentication") != null ? new Gson().fromJson(sharedPreferences.get("authentication").toString(), JWT.class) : null;
    }

    public String getAuthenticationToken() {
        this.sharedPreferences = getSharedPreferences();
        JWT jwt = getJWT();
        if (jwt != null) {
            return String.format("%s %s", jwt.getToken_type(), jwt.getAccess_token());
        }
        return null;
    }

    public void setAvatarAvailable(boolean avatarAvailable) {
        editor.putBoolean("avatarAvailable", avatarAvailable);
        editor.apply();
    }

    public void setProfile(ProfileDTO profileDTO) {
        profileDTO.setBalances(null);
        editor.putString("profile", new Gson().toJson(profileDTO));
        editor.putLong("lastSync", new Date().getTime());
        editor.apply();
    }

    public ProfileDTO getProfile() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("profile") != null ? new Gson().fromJson(sharedPreferences.get("profile").toString(), ProfileDTO.class) : null;
    }

    public void syncPaymateTopicSubscription(ProfileDTO newProfileDTO) {
        PaymateDTO oldPaymate = getProfile().getPaymate();
        PaymateDTO newPaymate = newProfileDTO.getPaymate();

        if (newPaymate != null) {
            if (oldPaymate == null && newPaymate.getPaymateStatus().equals("ACTIVE")) {
                Common.subscribeToTopic(Constants.AGENT_TOPIC);
            }
            if (oldPaymate != null && !oldPaymate.getPaymateStatus().equals("ACTIVE")) {
                Common.subscribeToTopic(Constants.AGENT_TOPIC);
            }
            if (oldPaymate != null && oldPaymate.getPaymateStatus().equals("ACTIVE") && !newPaymate.getPaymateStatus().equals("ACTIVE")) {
                Common.subscribeToTopic(Constants.AGENT_TOPIC);
            }
        } else if (oldPaymate != null && oldPaymate.getPaymateStatus().equals("ACTIVE")) {
            Common.subscribeToTopic(Constants.AGENT_TOPIC);
        }
    }

    public long getLastSync() {
        this.sharedPreferences = getSharedPreferences();
        return sharedPreferences.get("lastSync") != null ? Long.parseLong(sharedPreferences.get("lastSync").toString()) : 0;
    }

    public void clearAll() {
        this.editor.clear().apply();
    }
}
