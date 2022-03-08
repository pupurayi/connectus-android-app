package com.connectus.mobile.ui.initial.signup;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.AuthenticationResponse;
import com.connectus.mobile.api.dto.JWT;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.SignUpRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpViewModel extends ViewModel {
    private static final String TAG = SignUpViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitSignUpApi(final Context context, SignUpRequest signUpRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<AuthenticationResponse> ul = apiService.signUp(signUpRequest);
        try {
            ul.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    if (response.code() == 200) {
                        AuthenticationResponse authenticationResponse = response.body();

                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        JWT jwt = authenticationResponse.getJWT();
                        sharedPreferencesManager.setJWT(jwt);

                        ProfileDTO profileDTO = authenticationResponse.getProfile();
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
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
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

