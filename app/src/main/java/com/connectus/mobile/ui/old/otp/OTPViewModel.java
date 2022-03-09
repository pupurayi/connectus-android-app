package com.connectus.mobile.ui.old.otp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.ValidateOTPRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class OTPViewModel extends ViewModel {

    private static final String TAG = OTPViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitUserVerificationApi(String authentication) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.userVerificationOTP(authentication);
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

    public MutableLiveData<ResponseDTO> hitUserVerificationApi(final Context context, String authentication, ValidateOTPRequest validateOTPRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.userVerification(authentication, validateOTPRequest);
        try {
            ul.enqueue(new Callback<ResponseDTO>() {
                @Override
                public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();

                        SharedPreferences.Editor editor = context.getSharedPreferences("eMalyami", MODE_PRIVATE).edit();
                        editor.putString("userStatus", "ENABLED");
                        editor.apply();

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

