package com.connectus.mobile.ui.dashboard;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.Transaction;
import com.connectus.mobile.database.DbHandler;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardViewModel extends ViewModel {
    private static final String TAG = DashboardViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitTransactionHistoryApi(Context context, String authentication) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<List<Transaction>>> ul = apiService.getTransactionHistory(authentication);
        try {
            ul.enqueue(new Callback<ResponseDTO<List<Transaction>>>() {
                @Override
                public void onResponse(Call<ResponseDTO<List<Transaction>>> call, Response<ResponseDTO<List<Transaction>>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<List<Transaction>> responseDTO = response.body();
                        DbHandler dbHandler = new DbHandler(context);
                        for (Transaction transaction : responseDTO.getData()) {
                            dbHandler.insertTransaction(transaction);
                        }
                        responseLiveData.setValue(new ResponseDTO<>("success", null, null));
                    } else {
                        String errorMsg;
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        }
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<List<Transaction>>> call, Throwable t) {
                    Log.d("error", t.toString());
                    responseLiveData.setValue(new ResponseDTO<>("error", "Connectivity Issues!", null));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}

