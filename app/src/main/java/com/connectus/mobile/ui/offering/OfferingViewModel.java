package com.connectus.mobile.ui.offering;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.NewOfferingDto;
import com.connectus.mobile.api.dto.ResponseDTO;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferingViewModel extends ViewModel {

    private static final String TAG = OfferingViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSaveOfferingApi(final Context context, String authentication, NewOfferingDto newOfferingDto) {
        responseLiveData = new MutableLiveData<>();
        Call<OfferingDto> ul = apiService.addNewOffering(authentication, newOfferingDto);
        try {
            ul.enqueue(new Callback<OfferingDto>() {
                @Override
                public void onResponse(Call<OfferingDto> call, Response<OfferingDto> response) {
                    if (response.code() == 200) {
                        OfferingDto offeringDto = response.body();
                        responseLiveData.setValue(new ResponseDTO<>("success", "Successfully added offering!", offeringDto));
                    } else {
                        responseLiveData.setValue(new ResponseDTO<>("failed", "Failed to add offering", null));
                    }
                }

                @Override
                public void onFailure(Call<OfferingDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Successfully added offering!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

