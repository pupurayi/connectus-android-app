package com.connectus.mobile.ui.profile;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.connectus.mobile.api.APIService;
import com.connectus.mobile.api.RestClients;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.UpdateProfileRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileDetailsViewModel extends ViewModel {

    private static final String TAG = ProfileDetailsViewModel.class.getSimpleName();

    private MutableLiveData<ResponseDTO> responseLiveData;
    private final APIService apiService = new RestClients().get();

    public MutableLiveData<ResponseDTO> hitGetProfileApi(final Context context, String authentication) {
        responseLiveData = new MutableLiveData<>();
        Call<ProfileDto> ul = apiService.getProfile(authentication);
        try {
            ul.enqueue(new Callback<ProfileDto>() {
                @Override
                public void onResponse(Call<ProfileDto> call, Response<ProfileDto> response) {
                    if (response.code() == 200) {
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        ProfileDto profileDTO = response.body();
                        sharedPreferencesManager.setProfile(profileDTO);

                        responseLiveData.setValue(new ResponseDTO("success", "Profile Syncing Complete!", null));
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
                public void onFailure(Call<ProfileDto> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO> hitUpdateProfileApi(final Context context, String authentication, UpdateProfileRequest updateProfileRequest) {
        responseLiveData = new MutableLiveData<>();
        Call<ProfileDto> ul = apiService.updateProfile(authentication, updateProfileRequest);
        try {
            ul.enqueue(new Callback<ProfileDto>() {
                @Override
                public void onResponse(Call<ProfileDto> call, Response<ProfileDto> response) {
                    if (response.code() == 200) {
                        ProfileDto profileDTO = response.body();
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setProfile(profileDTO);
                        responseLiveData.setValue(new ResponseDTO("success", "Successfully Updated!", null));
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
                public void onFailure(Call<ProfileDto> call, Throwable t) {
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

    public MutableLiveData<ResponseDTO> hitUploadProfilePictureApi(final Context context, String authorization, MultipartBody.Part requestFile) {
        responseLiveData = new MutableLiveData<>();
        Call<ResponseDTO> ul = apiService.uploadProfilePicture(authorization, requestFile);
        try {
            ul.enqueue(new Callback<ResponseDTO>() {
                @Override
                public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
                    if (response.code() == 200) {
                        ResponseDTO responseDTO = response.body();
                        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(context);
                        sharedPreferencesManager.setAvatarAvailable(true);
                        responseLiveData.setValue(new ResponseDTO("success", responseDTO.getMessage(), null));
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
                public void onFailure(Call<ResponseDTO> call, Throwable t) {
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

