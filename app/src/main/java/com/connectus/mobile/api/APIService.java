package com.connectus.mobile.api;

import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.api.dto.CheckResponseDto;
import com.connectus.mobile.api.dto.ResponseDto;
import com.connectus.mobile.api.dto.SignInRequest;
import com.connectus.mobile.api.dto.ProductDto;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/check/{msisdn}")
    Call<CheckResponseDto> check(@Path("msisdn") String msisdn);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/sign-in")
    Call<UserDto> signIn(@Body SignInRequest signInRequest);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user")
    Call<UserDto> createUser(@Body UserDto userDto);


    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/product")
    Call<ProductDto> createProduct(@Body CreateProductDto createProductDto);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/product/user/{userId}")
    Call<List<ProductDto>> getUserProducts(@Path("userId") UUID userId);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/product/recommended/user/{userId}")
    Call<List<ProductDto>> getRecommendedProducts(@Path("userId") UUID userId);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/product/search/{userId}")
    Call<List<ProductDto>> searchProducts(@Path("userId") UUID userId, @Query("category") String category, @Query("name") String name, @Query("lat") double lat, @Query("lng") double lng, @Query("sortBy") String sortBy);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/product/rating/user/{userId}")
    Call<List<ProductDto>> getProductsForUserRating(@Path("userId") UUID userId);


    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/reset-password/{msisdn}")
    Call<ResponseDto> resetPassword(@Path("msisdn") String msisdn);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/{userId}")
    Call<UserDto> getUser(@Path("userId") UUID userId);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/record-product-order/{userId}")
    Call<UserDto> hitRecordProductOrderApi(@Path("userId") UUID userId, @Query("userId") UUID productId);


    @Headers({
            "Accept: application/json"
    })
    @PUT("/api/v1/user")
    Call<UserDto> updateUser(@Body UserDto userDto);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/products/rate/{userId}")
    Call<String> rateProducts(@Path("userId") UUID userId, @Body Map<UUID, Boolean> ratings);


    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/service-providers/{userId}")
    Call<List<UserDto>> getServiceProviders(@Path("userId") UUID userId);
}
