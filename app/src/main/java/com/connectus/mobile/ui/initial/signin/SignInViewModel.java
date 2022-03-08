package com.connectus.mobile.ui.initial.signin;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.AuthResponseDto;
import com.connectus.mobile.api.dto.JWT;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.SignInRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInViewModel extends ViewModel {
    private static final String TAG = SignInViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSignInApi(final Context context, SignInRequest signInRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<AuthResponseDto> ul = apiService.signIn(signInRequest);
        try {
            ul.enqueue(new Callback<AuthResponseDto>() {
                @Override
                public void onResponse(Call<AuthResponseDto> call, Response<AuthResponseDto> response) {
                    if (response.code() == 200) {
                        AuthResponseDto authResponseDto = response.body();

                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        JWT jwt = authResponseDto.getJwt();
                        sharedPreferencesManager.setJWT(jwt);

                        ProfileDto profileDTO = authResponseDto.getProfile();
                        sharedPreferencesManager.setProfile(profileDTO);

                        Set<BalanceDTO> balances = profileDTO.getBalances();
                        if (balances != null) {
                            DbHandler dbHandler = new DbHandler(context);
                            for (BalanceDTO balanceDTO : balances) {
                                dbHandler.insertBalance(balanceDTO);
                            }
                        }

                        responseLiveData.setValue(new ResponseDTO("success", null, null));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDTO("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<AuthResponseDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

