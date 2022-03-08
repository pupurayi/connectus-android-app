package com.connectus.mobile.ui.services;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.BankDto;
import com.connectus.mobile.api.dto.airtime.OperatorResponse;
import com.connectus.mobile.api.dto.ResponseDTO;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServicesViewModel extends ViewModel {
    private static final String TAG = ServicesViewModel.class.getSimpleName();

    private final APIService apiService = new RestClients().get();
    MutableLiveData<ResponseDTO> responseLiveData = new MutableLiveData<>();

    public MutableLiveData<ResponseDTO<OperatorResponse>> hitGetMobileOperatorApi(String authorization, String phoneNumber) {
        MutableLiveData<ResponseDTO<OperatorResponse>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<OperatorResponse>> ul = apiService.getMobileOperator(authorization, phoneNumber);
        try {
            ul.enqueue(new Callback<ResponseDTO<OperatorResponse>>() {
                @Override
                public void onResponse(Call<ResponseDTO<OperatorResponse>> call, Response<ResponseDTO<OperatorResponse>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<OperatorResponse> responseDTO = response.body();
                        OperatorResponse operatorResponse = responseDTO.getData();
                        responseLiveData.setValue(new ResponseDTO("success", responseDTO.getMessage(), operatorResponse));
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
                public void onFailure(Call<ResponseDTO<OperatorResponse>> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO> hitGetBanksApi(String authorization, String countryCode) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<List<BankDto>>> ul = apiService.getBanks(authorization, countryCode);
        try {
            ul.enqueue(new Callback<ResponseDTO<List<BankDto>>>() {
                @Override
                public void onResponse(Call<ResponseDTO<List<BankDto>>> call, Response<ResponseDTO<List<BankDto>>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<List<BankDto>> responseDTO = response.body();
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
                public void onFailure(Call<ResponseDTO<List<BankDto>>> call, Throwable t) {
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


