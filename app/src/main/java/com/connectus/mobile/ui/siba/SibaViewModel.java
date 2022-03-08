package com.connectus.mobile.ui.siba;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.SibaDepositDTO;
import com.connectus.mobile.api.dto.siba.CreateSibaProfileDTO;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.api.dto.siba.MySibaInvite;
import com.connectus.mobile.database.DbHandler;


import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SibaViewModel extends ViewModel {
    private static final String TAG = SibaViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();


    public MutableLiveData<ResponseDTO> hitCheckEligibilityByMsisdnApi(String authentication, String msisdn) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.checkEligibility(authentication, msisdn);
        try {
            ul.enqueue(new Callback<ResponseDTO>() {
                @Override
                public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();
                        responseLiveData.setValue(new ResponseDTO<>(responseDTO.getStatus(), responseDTO.getMessage(), responseDTO.getData()));
                    } else {
                        String errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO> hitCreateSibaProfileApi(String authentication, CreateSibaProfileDTO createSibaProfileDTO) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<SibaProfile>> ul = apiService.createSibaProfile(authentication, createSibaProfileDTO);
        try {
            ul.enqueue(new Callback<ResponseDTO<SibaProfile>>() {
                @Override
                public void onResponse(Call<ResponseDTO<SibaProfile>> call, Response<ResponseDTO<SibaProfile>> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();
                        responseLiveData.setValue(new ResponseDTO<>(responseDTO.getStatus(), responseDTO.getMessage(), responseDTO.getData()));
                    } else {
                        String errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<SibaProfile>> call, Throwable t) {
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


    public MutableLiveData<ResponseDTO> hitGetMySibaProfiles(Context context, String authentication, UUID mProfileId) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<List<SibaProfile>>> ul = apiService.getMySibaProfiles(authentication, mProfileId);
        try {
            ul.enqueue(new Callback<ResponseDTO<List<SibaProfile>>>() {
                @Override
                public void onResponse(Call<ResponseDTO<List<SibaProfile>>> call, Response<ResponseDTO<List<SibaProfile>>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<List<SibaProfile>> responseDTO = response.body();
                        if (responseDTO.getStatus().equals("success")) {
                            DbHandler dbHandler = new DbHandler(context);
                            dbHandler.deleteAllSibaProfiles();
                            for (SibaProfile sibaProfile : responseDTO.getData()) {
                                dbHandler.insertSibaProfile(sibaProfile);
                            }
                        }
                        responseLiveData.setValue(new ResponseDTO<>(responseDTO.getStatus(), responseDTO.getMessage(), null));
                    } else {
                        String errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<List<SibaProfile>>> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO> hitGetMySibaInvites(Context context, String authentication, UUID mProfileId) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<List<MySibaInvite>>> ul = apiService.getMySibaInvites(authentication, mProfileId);
        try {
            ul.enqueue(new Callback<ResponseDTO<List<MySibaInvite>>>() {
                @Override
                public void onResponse(Call<ResponseDTO<List<MySibaInvite>>> call, Response<ResponseDTO<List<MySibaInvite>>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<List<MySibaInvite>> responseDTO = response.body();
                        if (responseDTO.getStatus().equals("success")) {
                            DbHandler dbHandler = new DbHandler(context);
                            dbHandler.deleteAllMySibaInvites();
                            for (MySibaInvite mySibaInvite : responseDTO.getData()) {
                                dbHandler.insertMySibaInvite(mySibaInvite);
                            }
                        }
                        responseLiveData.setValue(new ResponseDTO<>(responseDTO.getStatus(), responseDTO.getMessage(), null));
                    } else {
                        String errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<List<MySibaInvite>>> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO> hitActionInvite(String authentication, UUID mProfileId, String inviteId, String action) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.actionInvite(authentication, mProfileId, inviteId, action);
        try {
            ul.enqueue(new Callback<ResponseDTO>() {
                @Override
                public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();
                        responseLiveData.setValue(new ResponseDTO<>(responseDTO.getStatus(), responseDTO.getMessage(), null));
                    } else {
                        String errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO> call, Throwable t) {
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


    public MutableLiveData<ResponseDTO> hitDepositToSibaApi(Context context, String authentication, UUID mProfileId, UUID sibaProfileId, SibaDepositDTO sibaDepositDTO) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO<BalanceDTO>> ul = apiService.depositToSiba(authentication, mProfileId, sibaProfileId, sibaDepositDTO);
        try {
            ul.enqueue(new Callback<ResponseDTO<BalanceDTO>>() {
                @Override
                public void onResponse(Call<ResponseDTO<BalanceDTO>> call, Response<ResponseDTO<BalanceDTO>> response) {
                    if (response.code() == 200) {
                        ResponseDTO<BalanceDTO> responseDTO = response.body();
                        if (responseDTO.getStatus().equals("success")) {
                            DbHandler dbHandler = new DbHandler(context);
                            dbHandler.insertBalance(responseDTO.getData());
                        }
                        responseLiveData.setValue(new ResponseDTO<>(responseDTO.getStatus(), responseDTO.getMessage(), null));
                    } else {
                        String errorMsg = response.code() == 403 ? "Authentication Failed!" : "Error Occurred!";
                        responseLiveData.setValue(new ResponseDTO<>("failed", errorMsg, null));
                    }
                }

                @Override
                public void onFailure(Call<ResponseDTO<BalanceDTO>> call, Throwable t) {
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

