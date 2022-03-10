package com.connectus.mobile.ui.goods_and_services;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.NewGoodsAndServicesDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.database.DbHandler;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GoodsAndServicesViewModel extends ViewModel {

    private static final String TAG = com.connectus.mobile.ui.goods_and_services.GoodsAndServicesViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSaveOfferingApi(final Context context, String authentication, NewGoodsAndServicesDto newGoodsAndServicesDto) {
        responseLiveData = new MutableLiveData<>();
        Call<GoodsAndServicesDto> ul = apiService.createOffering(authentication, newGoodsAndServicesDto);
        try {
            ul.enqueue(new Callback<GoodsAndServicesDto>() {
                @Override
                public void onResponse(Call<GoodsAndServicesDto> call, Response<GoodsAndServicesDto> response) {
                    if (response.code() == 200) {
                        GoodsAndServicesDto new_goods_and_servicesDto = response.body();
                        responseLiveData.setValue(new ResponseDTO<>("success", "Successfully added new_goods_and_services!", new_goods_and_servicesDto));
                    } else {
                        responseLiveData.setValue(new ResponseDTO<>("failed", "Failed to add new_goods_and_services", null));
                    }
                }

                @Override
                public void onFailure(Call<GoodsAndServicesDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Successfully added new_goods_and_services!", null));
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
        Call<List<GoodsAndServicesDto>> ul = apiService.getOfferings(authentication);
        try {
            ul.enqueue(new Callback<List<GoodsAndServicesDto>>() {
                @Override
                public void onResponse(Call<List<GoodsAndServicesDto>> call, Response<List<GoodsAndServicesDto>> response) {
                    if (response.code() == 200) {
                        List<GoodsAndServicesDto> new_goods_and_servicess = response.body();
                        if (new_goods_and_servicess != null) {
                            DbHandler dbHandler = new DbHandler(context);
                            for (GoodsAndServicesDto new_goods_and_services : new_goods_and_servicess) {
                                dbHandler.insertOffering(new_goods_and_services);
                            }
                        }
                        responseLiveData.setValue(new ResponseDTO<>("success", "Fetched new Offerings", new_goods_and_servicess));
                    } else {
                        responseLiveData.setValue(new ResponseDTO<>("failed", "Failed to add new_goods_and_services", null));
                    }
                }

                @Override
                public void onFailure(Call<List<GoodsAndServicesDto>> call, Throwable t) {
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

