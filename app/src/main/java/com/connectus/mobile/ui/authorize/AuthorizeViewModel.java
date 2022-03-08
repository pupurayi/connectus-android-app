package com.connectus.mobile.ui.authorize;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.AuthorizationResponse;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ResponseDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthorizeViewModel extends ViewModel {
    private static final String TAG = AuthorizeViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitAuthorizeApi(final Context context, String phoneNumber) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<AuthorizationResponse>> ul = apiService.authorize(phoneNumber);
        try {
            ul.enqueue(new Callback<ResponseDTO<AuthorizationResponse>>() {
                @Override
                public void onResponse(Call<ResponseDTO<AuthorizationResponse>> call, Response<ResponseDTO<AuthorizationResponse>> response) {
                    if (response.code() == 200) {
                        AuthorizationResponse authorizationResponse = response.body().getData();

                        // TODO save
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setAuthorization(authorizationResponse);

                        responseLiveData.setValue(new ResponseDTO("success", null, null));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<AuthorizationResponse>> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

