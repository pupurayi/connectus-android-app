package com.connectus.mobile.ui.initial.signin;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.api.dto.ResponseDto;
import com.connectus.mobile.api.dto.SignInRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {
    private static final String TAG = SignInViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> hitSignInApi(final Context context, SignInRequest signInRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<UserDto> ul = apiService.signIn(signInRequest);
        try {
            ul.enqueue(new Callback<UserDto>() {
                @Override
                public void onResponse(Call<UserDto> call, Response<UserDto> response) {
                    if (response.code() == 200) {
                        UserDto userDto = response.body();

                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setUser(userDto);

                        responseLiveData.setValue(new ResponseDto("success", null, null));
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

