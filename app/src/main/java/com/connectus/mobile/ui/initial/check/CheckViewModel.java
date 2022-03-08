package com.connectus.mobile.ui.initial.check;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.CheckResponseDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ResponseDTO;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckViewModel extends ViewModel {
    private static final String TAG = CheckViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitCheckApi(final Context context, String phoneNumber) {
        responseLiveData = new MutableLiveData<>();
        Call<CheckResponseDto> ul = apiService.check(phoneNumber);
        try {
            ul.enqueue(new Callback<CheckResponseDto>() {
                @Override
                public void onResponse(Call<CheckResponseDto> call, Response<CheckResponseDto> response) {
                    if (response.code() == 200) {
                        CheckResponseDto checkResponseDto = response.body();

                        // TODO save
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setAuthorization(checkResponseDto);

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
                public void onFailure(Call<CheckResponseDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!"+t.toString(), null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}
