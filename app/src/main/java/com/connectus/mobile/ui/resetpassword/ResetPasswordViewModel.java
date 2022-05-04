package com.connectus.mobile.ui.resetpassword;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDto;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordViewModel extends ViewModel {
    private static final String TAG = ResetPasswordViewModel.class.getSimpleName();
    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> hitResetPasswordApi(String msisdn) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDto> ul = apiService.resetPassword(msisdn);
        try {
            ul.enqueue(new Callback<ResponseDto>() {
                @Override
                public void onResponse(Call<ResponseDto> call, Response<ResponseDto> response) {
                    if (response.code() == 200) {
                        ResponseDto responseDto = response.body();
                        responseLiveData.setValue(new ResponseDto("success", responseDto.getMessage(), null));
                    } else {
                        String errorMsg = null;
                        try {
                            errorMsg = response.errorBody().string();
                        } catch (IOException e) {
                            errorMsg = "Something went wrong";
                        }
                        responseLiveData.setValue(new ResponseDto("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDto> call, Throwable t) {
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
