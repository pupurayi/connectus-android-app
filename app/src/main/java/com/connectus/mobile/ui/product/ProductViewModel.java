package com.connectus.mobile.ui.product;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.ProductDto;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.ResponseDto;
import com.connectus.mobile.utils.Utils;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductViewModel extends ViewModel {

    private static final String TAG = ProductViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDto> hitSaveProductApi(CreateProductDto createProductDto) {
        responseLiveData = new MutableLiveData<>();
        Call<ProductDto> ul = apiService.createProduct(createProductDto);
        try {
            ul.enqueue(new Callback<ProductDto>() {
                @Override
                public void onResponse(Call<ProductDto> call, Response<ProductDto> response) {
                    if (response.code() == 200) {
                        ProductDto new_productsDto = response.body();
                        responseLiveData.setValue(new ResponseDto<>("success", "Successfully added new_products!", new_productsDto));
                    } else {
                        String errorMsg = Utils.handleHttpException(response);
                        responseLiveData.setValue(new ResponseDto<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ProductDto> call, Throwable t) {
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

    public MutableLiveData<ResponseDto> getProducts(UUID userId, ProductType productType, String category, String name, String sortBy) {
        responseLiveData = new MutableLiveData<>();
        Call<List<ProductDto>> ul = null;
        if (productType.equals(ProductType.USER)) {
            ul = apiService.getUserProducts(userId);
        } else if (productType.equals(ProductType.RECOMMENDED)) {
            ul = apiService.getRecommendedProducts(userId);
        } else if (productType.equals(ProductType.USER_RATING)) {
            ul = apiService.getProductsForUserRating(userId);
        } else if (productType.equals(ProductType.SEARCH_QUERY)) {
            ul = apiService.searchProducts(userId, category, name, sortBy);
        }
        try {
            ul.enqueue(new Callback<List<ProductDto>>() {
                @Override
                public void onResponse(Call<List<ProductDto>> call, Response<List<ProductDto>> response) {
                    if (response.code() == 200) {
                        List<ProductDto> products = response.body();
                        responseLiveData.setValue(new ResponseDto<>("success", "Sync Complete", products));
                    } else {
                        responseLiveData.setValue(new ResponseDto<>("failed", "Failed to sync Products", null));
                    }
                }

                @Override
                public void onFailure(Call<List<ProductDto>> call, Throwable t) {
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

