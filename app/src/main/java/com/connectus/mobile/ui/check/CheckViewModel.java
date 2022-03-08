package com.connectus.mobile.ui.check;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.CheckProfileDTO;
import com.connectus.mobile.api.dto.CheckPaymateDTO;
import com.connectus.mobile.api.dto.ResponseDTO;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckViewModel extends ViewModel {
    private static final String TAG = CheckViewModel.class.getSimpleName();

    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO<CheckProfileDTO>> hitCheckProfileApi(String authentication, String msisdn) {
        MutableLiveData<ResponseDTO<CheckProfileDTO>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<CheckProfileDTO>> ul = apiService.checkProfile(authentication, msisdn);
        try {
            ul.enqueue(new Callback<ResponseDTO<CheckProfileDTO>>() {
                @Override
                public void onResponse(Call<ResponseDTO<CheckProfileDTO>> call, Response<ResponseDTO<CheckProfileDTO>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<CheckProfileDTO> responseDTO = response.body();
                        CheckProfileDTO checkProfileDTO = responseDTO.getData();
                        responseLiveData.setValue(new ResponseDTO("success", responseDTO.getMessage(), checkProfileDTO));
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
                public void onFailure(Call<ResponseDTO<CheckProfileDTO>> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO<CheckPaymateDTO>> hitCheckPaymateApi(String authorization, long paymateCode) {
        MutableLiveData<ResponseDTO<CheckPaymateDTO>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<CheckPaymateDTO>> ul = apiService.checkPaymate(authorization, paymateCode);
        try {
            ul.enqueue(new Callback<ResponseDTO<CheckPaymateDTO>>() {
                @Override
                public void onResponse(Call<ResponseDTO<CheckPaymateDTO>> call, Response<ResponseDTO<CheckPaymateDTO>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<CheckPaymateDTO> responseDTO = response.body();
                        CheckPaymateDTO checkPaymateDTO = responseDTO.getData();
                        responseLiveData.setValue(new ResponseDTO("success", responseDTO.getMessage(), checkPaymateDTO));
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
                public void onFailure(Call<ResponseDTO<CheckPaymateDTO>> call, Throwable t) {
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

