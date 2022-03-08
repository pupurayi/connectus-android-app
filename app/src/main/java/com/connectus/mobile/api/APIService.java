package com.connectus.mobile.api;

import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.AuthenticationResponse;
import com.connectus.mobile.api.dto.AuthorizationResponse;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.api.dto.BankDto;
import com.connectus.mobile.api.dto.ChangePasswordRequest;
import com.connectus.mobile.api.dto.CheckProfileDTO;
import com.connectus.mobile.api.dto.CheckPaymateDTO;
import com.connectus.mobile.api.dto.PaymateApplication;
import com.connectus.mobile.api.dto.PaymateBusinessLocation;
import com.connectus.mobile.api.dto.SibaDepositDTO;
import com.connectus.mobile.api.dto.airtime.MobileTopupRequest;
import com.connectus.mobile.api.dto.airtime.OperatorResponse;
import com.connectus.mobile.api.dto.ResetPasswordRequest;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.SignInRequest;
import com.connectus.mobile.api.dto.SignUpRequest;
import com.connectus.mobile.api.dto.Transaction;
import com.connectus.mobile.api.dto.UpdateProfileRequest;
import com.connectus.mobile.api.dto.UpdateAddressRequest;
import com.connectus.mobile.api.dto.UpdateIdentificationRequest;
import com.connectus.mobile.api.dto.ValidateOTPRequest;
import com.connectus.mobile.api.dto.payfast.CreatePayFastToken;
import com.connectus.mobile.api.dto.payfast.CreateTokenResponseDto;
import com.connectus.mobile.api.dto.siba.CreateSibaProfileDTO;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.api.dto.siba.MySibaInvite;
import com.connectus.mobile.api.dto.transaction.TransactionDto;

import java.util.List;
import java.util.UUID;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

public interface APIService {

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/authorize/{phoneNumber}")
    Call<ResponseDTO<AuthorizationResponse>> authorize(@Path("phoneNumber") String phoneNumber);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/sign-in")
    Call<AuthenticationResponse> signIn(@Body SignInRequest signInRequest);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/sign-up")
    Call<AuthenticationResponse> signUp(@Body SignUpRequest signUpRequest);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/verification")
    Call<ResponseDTO> userVerificationOTP(@Header("Authorization") String authentication);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/verification")
    Call<ResponseDTO> userVerification(@Header("Authorization") String authentication, @Body ValidateOTPRequest validateOTO);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/reset-password/{username}")
    Call<ResponseDTO> resetPassword(@Path("username") String username);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/user/reset-password/{username}/{otp}")
    Call<ResponseDTO> resetPassword(@Path("username") String username, @Path("otp") String otp);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/reset-password")
    Call<ResponseDTO> resetPassword(@Body ResetPasswordRequest resetPasswordRequest);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/user/change-password")
    Call<ResponseDTO> changePassword(@Header("Authorization") String authentication, @Body ChangePasswordRequest changePasswordRequest);

    @Headers({
            "Accept: application/json"
    })
    @Multipart
    @POST("/api/v1/user/profile-picture")
    Call<ResponseDTO> uploadProfilePicture(@Header("Authorization") String authentication, @Part MultipartBody.Part profilePicture);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/profile")
    Call<ProfileDTO> getProfile(@Header("Authorization") String authentication);

    @Headers({
            "Accept: application/json"
    })
    @PUT("/api/v1/profile")
    Call<ProfileDTO> updateProfile(@Header("Authorization") String authentication, @Body UpdateProfileRequest updateProfileRequest);


    @Headers({
            "Accept: application/json"
    })
    @PUT("/api/v1/profile/address")
    Call<ResponseDTO<ProfileDTO>> updateAddress(@Header("Authorization") String authentication, @Body UpdateAddressRequest updateAddressRequest);

    @Headers({
            "Accept: application/json"
    })
    @PUT("/api/v1/profile/identification")
    Call<ResponseDTO<ProfileDTO>> updateIdentification(@Header("Authorization") String authentication, @Body UpdateIdentificationRequest updateIdentificationRequest);

    @Headers({
            "Accept: application/json"
    })
    @Multipart
    @POST("/api/v1/profile/upload/{type}")
    Call<ResponseDTO> uploadProfileDocuments(@Header("Authorization") String authentication, @Path("type") String type, @Part MultipartBody.Part document);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/profile/check/{username}")
    Call<ResponseDTO<CheckProfileDTO>> checkProfile(@Header("Authorization") String authentication, @Path("username") String username);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/transaction/process")
    Call<TransactionDto> processTransaction(@Header("Authorization") String authentication, @Body TransactionDto transactionDto);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/transaction")
    Call<ResponseDTO<List<Transaction>>> getTransactionHistory(@Header("Authorization") String authentication);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/transaction/banks/{username}")
    Call<ResponseDTO<List<BankDto>>> getBanks(@Header("Authorization") String authorization, @Path("username") String username);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/paymate/update-business-location")
    Call<ResponseDTO<PaymateApplication>> updatePaymateBusinessLocation(@Header("Authorization") String authentication, @Body PaymateBusinessLocation paymateBusinessLocation);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/paymate/application")
    Call<ResponseDTO<PaymateApplication>> getPaymateApplication(@Header("Authorization") String authentication);

    @Headers({
            "Accept: application/json"
    })
    @Multipart
    @POST("/api/v1/paymate/upload-document/{documentType}")
    Call<ResponseDTO<PaymateApplication>> uploadPaymateApplicationDocument(@Header("Authorization") String authentication, @Path("documentType") String documentType, @Part MultipartBody.Part document);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/paymate/check/{paymateCode}")
    Call<ResponseDTO<CheckPaymateDTO>> checkPaymate(@Header("Authorization") String authentication, @Path("paymateCode") long paymateCode);

    @Headers({
            "Accept: application/json"
    })
    @GET("/api/v1/airtime/operator/{phoneNumber}")
    Call<ResponseDTO<OperatorResponse>> getMobileOperator(@Header("Authorization") String authentication, @Path("phoneNumber") String phoneNumber);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/airtime/topup")
    Call<ResponseDTO<BalanceDTO>> mobileTopup(@Header("Authorization") String authorization, @Body MobileTopupRequest mobileTopupRequest);

    @Headers({
            "Accept: application/json"
    })
    @POST("/api/v1/payfast/create-token")
    Call<ResponseDTO<CreateTokenResponseDto>> createToken(@Header("Authorization") String authorization, @Body CreatePayFastToken createPayFastToken);

    @Headers({
            "Accept: application/json"
    })
    @GET("/siba/api/v1/profile/check-eligibility/{username}")
    Call<ResponseDTO> checkEligibility(@Header("Authorization") String authorization, @Path("username") String username);

    @Headers({
            "Accept: application/json"
    })
    @POST("/siba/api/v1/profile")
    Call<ResponseDTO<SibaProfile>> createSibaProfile(@Header("Authorization") String authorization, @Body CreateSibaProfileDTO createSibaProfileDTO);

    @Headers({
            "Accept: application/json"
    })
    @GET("/siba/api/v1/profile/{profileId}/profiles")
    Call<ResponseDTO<List<SibaProfile>>> getMySibaProfiles(@Header("Authorization") String authorization, @Path("profileId") UUID profileId);

    @Headers({
            "Accept: application/json"
    })
    @GET("/siba/api/v1/profile/{profileId}/invites")
    Call<ResponseDTO<List<MySibaInvite>>> getMySibaInvites(@Header("Authorization") String authorization, @Path("profileId") UUID profileId);

    @Headers({
            "Accept: application/json"
    })
    @GET("/siba/api/v1/profile/{profileId}/invite/{inviteId}/{action}")
    Call<ResponseDTO> actionInvite(@Header("Authorization") String authorization, @Path("profileId") UUID profileId, @Path("inviteId") String inviteId, @Path("action") String action);

    @Headers({
            "Accept: application/json"
    })
    @POST("/siba/api/v1/profile/{profileId}/deposit/{sibaProfileId}")
    Call<ResponseDTO<BalanceDTO>> depositToSiba(@Header("Authorization") String authorization, @Path("profileId") UUID profileId, @Path("sibaProfileId") UUID sibaProfileId, @Body SibaDepositDTO sibaDepositDTO);
}
