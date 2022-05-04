package com.connectus.mobile.ui.rating;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDto;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.utils.Utils;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingViewModel extends ViewModel {
    private static final String TAG = RatingViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> hitSubmitProductsRating(final Context context, UUID userId, Map<UUID, Boolean> ratings) {
        responseLiveData = new MutableLiveData<>();
        Call<String> ul = apiService.rateProducts(userId, ratings);
        try {
            ul.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.code() == 200) {
                        String message = response.body();
                        responseLiveData.setValue(new ResponseDto("success", message, null));
                    } else {
                        String errorMsg =  Utils.handleHttpException(response);
                        responseLiveData.setValue(new ResponseDto("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
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