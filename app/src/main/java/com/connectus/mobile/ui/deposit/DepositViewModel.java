package com.connectus.mobile.ui.deposit;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.payfast.CreatePayFastToken;
import com.connectus.mobile.api.dto.payfast.CreateTokenResponseDto;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DepositViewModel extends ViewModel {
    private static final String TAG = DepositViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO<CreateTokenResponseDto>> hitCreateTokenApi(String authentication, CreatePayFastToken createPayFastToken) {
        MutableLiveData<ResponseDTO<CreateTokenResponseDto>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<CreateTokenResponseDto>> ul = apiService.createToken(authentication, createPayFastToken);
        try {
            ul.enqueue(new Callback<ResponseDTO<CreateTokenResponseDto>>() {
                @Override
                public void onResponse(Call<ResponseDTO<CreateTokenResponseDto>> call, Response<ResponseDTO<CreateTokenResponseDto>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<CreateTokenResponseDto> responseDTO = response.body();
                        responseLiveData.setValue(new ResponseDTO<>("success", responseDTO.getMessage(), responseDTO.getData()));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<CreateTokenResponseDto>> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

