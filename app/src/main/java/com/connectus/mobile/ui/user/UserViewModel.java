package com.connectus.mobile.ui.user;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.api.dto.ResponseDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserViewModel extends ViewModel {

    private static final String TAG = UserViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> hitGetUserApi(final Context context, UUID userId) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.getUser(userId);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        UserDto userDto = response.body();
                        sharedPreferencesManager.setUser(userDto);

                        responseLiveData.setValue(new ResponseDto("success", "Profile Syncing Complete!", null));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDto("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }

    public MutableLiveData<ResponseDto> hitUpdateUser(final Context context, UserDto userDto) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.updateUser(userDto);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        UserDto userDto = response.body();
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setUser(userDto);
                        responseLiveData.setValue(new ResponseDto("success", "Successfully Updated!", null));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDto("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<UserDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDto("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

