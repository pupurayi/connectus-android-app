package com.connectus.mobile.api;

import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.api.dto.AuthResponseDto;
import com.connectus.mobile.api.dto.CheckResponseDto;
import com.connectus.mobile.api.dto.ChangePasswordRequest;
import com.connectus.mobile.api.dto.ResetPasswordRequest;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.SignInRequest;
import com.connectus.mobile.api.dto.SignUpRequest;
import com.connectus.mobile.ui.product.ProductDto;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

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
    Call<AuthResponseDto> signIn(@Body SignInRequest signInRequest);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/sign-up")
    Call<AuthResponseDto> signUp(@Body SignUpRequest signUpRequest);


    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/product")
    Call<ProductDto> createProduct(@Header("Authorization") String authentication, @Body CreateProductDto createProductDto);


    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/product")
    Call<List<ProductDto>> getProducts(@Header("Authorization") String authentication);


    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/change-password")
    Call<ResponseDTO> changePassword(@Header("Authorization") String authentication, @Body ChangePasswordRequest changePasswordRequest);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/reset-password/{msisdn}")
    Call<ResponseDTO> resetPassword(@Path("msisdn") String msisdn);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/reset-password/{msisdn}/{otp}")
    Call<ResponseDTO> resetPassword(@Path("msisdn") String msisdn, @Path("otp") String otp);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/reset-password")
    Call<ResponseDTO> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user")
    Call<UserDto> getUser(@Header("Authorization") String authentication);

    @Headers({
            "Accept: application/json"
    })
    @PUT("/api/v1/user")
    Call<UserDto> updateProfile(@Header("Authorization") String authentication, @Body UserDto userDto);
}
