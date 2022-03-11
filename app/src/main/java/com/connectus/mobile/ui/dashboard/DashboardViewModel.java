package com.connectus.mobile.ui.dashboard;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDTO;

public class DashboardViewModel extends ViewModel {
    private static final String TAG = DashboardViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

}

