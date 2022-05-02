package com.connectus.mobile.ui.settings;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ChangePasswordRequest;
import com.connectus.mobile.api.dto.ResponseDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsViewModel extends ViewModel {
    private static final String TAG = SettingsViewModel.class.getSimpleName();
    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitChangePasswordApi(String authentication, ChangePasswordRequest changePasswordRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.changePassword(authentication, changePasswordRequest);
        try {
            ul.enqueue(new Callback<ResponseDTO>() {
                @Override
                public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();
                        responseLiveData.setValue(new ResponseDTO("success", responseDTO.getMessage(), null));
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
                public void onFailure(Call<ResponseDTO> call, Throwable t) {
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
