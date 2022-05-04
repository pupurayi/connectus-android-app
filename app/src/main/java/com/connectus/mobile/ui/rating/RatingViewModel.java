package com.connectus.mobile.ui.rating;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDto;

public class RatingViewModel extends ViewModel {
    private static final String TAG = RatingViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();
}