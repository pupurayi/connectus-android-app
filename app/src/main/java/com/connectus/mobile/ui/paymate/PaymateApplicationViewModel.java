package com.connectus.mobile.ui.paymate;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.PaymateApplication;
import com.connectus.mobile.api.dto.PaymateBusinessLocation;
import com.connectus.mobile.api.dto.ResponseDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymateApplicationViewModel extends ViewModel {
    private static final String TAG = PaymateApplicationViewModel.class.getSimpleName();

    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO<PaymateApplication>> hitUpdatePaymateBusinessLocationApi(String authentication, PaymateBusinessLocation paymateBusinessLocation) {
        MutableLiveData<ResponseDTO<PaymateApplication>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<PaymateApplication>> ul = apiService.updatePaymateBusinessLocation(authentication, paymateBusinessLocation);
        try {
            ul.enqueue(new Callback<ResponseDTO<PaymateApplication>>() {
                @Override
                public void onResponse(Call<ResponseDTO<PaymateApplication>> call, Response<ResponseDTO<PaymateApplication>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<PaymateApplication> responseDTO = response.body();
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
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<PaymateApplication>> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO<PaymateApplication>> hitUploadPaymateApplicationDocumentApi(String authentication, String documentType, MultipartBody.Part requestFile) {
        MutableLiveData<ResponseDTO<PaymateApplication>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<PaymateApplication>> ul = apiService.uploadPaymateApplicationDocument(authentication, documentType, requestFile);
        try {
            ul.enqueue(new Callback<ResponseDTO<PaymateApplication>>() {
                @Override
                public void onResponse(Call<ResponseDTO<PaymateApplication>> call, Response<ResponseDTO<PaymateApplication>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<PaymateApplication> responseDTO = response.body();
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
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<PaymateApplication>> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO<PaymateApplication>> hitGetPaymateApplicationApi(String authentication) {
        MutableLiveData<ResponseDTO<PaymateApplication>> responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<PaymateApplication>> ul = apiService.getPaymateApplication(authentication);
        try {
            ul.enqueue(new Callback<ResponseDTO<PaymateApplication>>() {
                @Override
                public void onResponse(Call<ResponseDTO<PaymateApplication>> call, Response<ResponseDTO<PaymateApplication>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<PaymateApplication> responseDTO = response.body();
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
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<PaymateApplication>> call, Throwable t) {
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

