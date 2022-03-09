package com.connectus.mobile.ui.offering;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.api.dto.NewOfferingDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.database.DbHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OfferingViewModel extends ViewModel {

    private static final String TAG = OfferingViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSaveOfferingApi(final Context context, String authentication, NewOfferingDto newOfferingDto) {
        responseLiveData = new MutableLiveData<>();
        Call<OfferingDto> ul = apiService.createOffering(authentication, newOfferingDto);
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

    public MutableLiveData<ResponseDTO> getOfferings(Context context, String authentication) {
        responseLiveData = new MutableLiveData<>();
        Call<List<OfferingDto>> ul = apiService.getOfferings(authentication);
        try {
            ul.enqueue(new Callback<List<OfferingDto>>() {
                @Override
                public void onResponse(Call<List<OfferingDto>> call, Response<List<OfferingDto>> response) {
                    if (response.code() == 200) {
                        List<OfferingDto> offerings = response.body();
                        if (offerings != null) {
                            DbHandler dbHandler = new DbHandler(context);
                            for (OfferingDto offering : offerings) {
                                dbHandler.insertOffering(offering);
                            }
                        }
                        responseLiveData.setValue(new ResponseDTO<>("success", "Fetched new Offerings", offerings));
                    } else {
                        responseLiveData.setValue(new ResponseDTO<>("failed", "Failed to add offering", null));
                    }
                }

                @Override
                public void onFailure(Call<List<OfferingDto>> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Connectivity Error!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

