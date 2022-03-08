package com.connectus.mobile.api.dto.transaction;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionViewModel extends ViewModel {
    private static final String TAG = TransactionViewModel.class.getSimpleName();

    private MutableLiveData<TransactionDto> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<TransactionDto> hitProcessTransactionApi(Context context, String authentication, TransactionDto requestTransactionDto) {
        responseLiveData = new MutableLiveData<>();
        Call<TransactionDto> ul = apiService.processTransaction(authentication, requestTransactionDto);
        try {
            ul.enqueue(new Callback<TransactionDto>() {
                @Override
                public void onResponse(Call<TransactionDto> call, Response<TransactionDto> response) {
                    if (response.code() == 200) {
                        TransactionDto responseTransactionDto = response.body();
                        responseLiveData.setValue(responseTransactionDto);
                    } else {
                        TransactionDto transactionDto = new TransactionDto();
                        String errorMsg;
                        transactionDto.setResponseCode(ResponseCodes.ERROR);
                        try {
                            JSONObject jObjError = new JSONObject(response.errorBody().string());
                            errorMsg = jObjError.getString("message");
                            transactionDto.setResponseDescription(errorMsg);
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                            if (response.code() == 401 || response.code() == 403) {
                                transactionDto.setResponseCode(ResponseCodes.SECURITY_VIOLATION);
                                transactionDto.setResponseDescription("Security Violation!");
                            } else {
                                transactionDto.setResponseDescription(ResponseCodes.ERROR);
                            }
                        }
                        responseLiveData.setValue(transactionDto);
                    }
                }

                @Override
                public void onFailure(Call<TransactionDto> call, Throwable t) {
                    Log.d("error", t.toString());
                    TransactionDto transactionDto = new TransactionDto();
                    transactionDto.setResponseCode(ResponseCodes.ERROR);
                    transactionDto.setResponseDescription("Connectivity Issues! Check your Internet Connection!");
                    responseLiveData.setValue(transactionDto);
                }
            });
        } catch (Exception e) {
            TransactionDto transactionDto = new TransactionDto();
            transactionDto.setResponseCode(ResponseCodes.ERROR);
            transactionDto.setResponseDescription(e.getMessage());
            e.printStackTrace();
        } finally {
            return responseLiveData;
        }
    }
}


