package com.connectus.mobile.ui.serviceprovider;

import android.content.pm.PackageManager;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDto;
import com.connectus.mobile.api.dto.UserDto;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ServiceProvidersViewModel extends ViewModel {

    private static final String TAG = ServiceProvidersViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> getServiceProviders(UUID userId) {
        responseLiveData = new MutableLiveData<>();
        Call<List<UserDto>> ul = apiService.getServiceProviders(userId);
        try {
            ul.enqueue(new Callback<List<UserDto>>() {
                @Override
                public void onResponse(Call<List<UserDto>> call, Response<List<UserDto>> response) {
                    if (response.code() == 200) {
                        List<UserDto> serviceProviders = response.body();
                        responseLiveData.setValue(new ResponseDto<>("success", "Sync Complete", serviceProviders));
                    } else {
                        responseLiveData.setValue(new ResponseDto<>("failed", "Failed to sync Service Providers", null));
                    }
                }

                @Override
                public void onFailure(Call<List<UserDto>> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDto<>("error", "Connectivity Error!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}