package com.connectus.mobile.ui.old.settings;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.ChangePasswordRequest;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

public class SettingsFragment extends Fragment {

    ProgressDialog pd;
    ImageView imageViewBack;
    ImageView imageViewProfileAvatar;
    EditText editTextCurrentPassword, editTextNewPassword;
    Button buttonChangePassword;

    boolean passwordShow = false;
    boolean verifyPasswordShow = false;

    SharedPreferencesManager sharedPreferencesManager;
    private SettingsViewModel settingsViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        settingsViewModel = new ViewModelProvider(this).get(SettingsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        editTextCurrentPassword = view.findViewById(R.id.edit_text_current_password);
        editTextCurrentPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextCurrentPassword.getRight() - editTextCurrentPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!passwordShow) {
                            editTextCurrentPassword.setTransformationMethod(null);
                            editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                            passwordShow = true;
                        } else {
                            editTextCurrentPassword.setTransformationMethod(new PasswordTransformationMethod());
                            editTextCurrentPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            passwordShow = false;
                        }
                        return passwordShow;
                    }
                }
                return false;
            }
        });

        editTextNewPassword = view.findViewById(R.id.edit_text_new_password);
        editTextNewPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextNewPassword.getRight() - editTextNewPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!verifyPasswordShow) {
                            editTextNewPassword.setTransformationMethod(null);
                            editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                            verifyPasswordShow = true;
                        } else {
                            editTextNewPassword.setTransformationMethod(new PasswordTransformationMethod());
                            editTextNewPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            verifyPasswordShow = false;
                        }
                        return verifyPasswordShow;
                    }
                }
                return false;
            }
        });

        buttonChangePassword = view.findViewById(R.id.button_change_password);
        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String currentPassword = editTextCurrentPassword.getText().toString();
                String newPassword = editTextNewPassword.getText().toString();
                if (newPassword.length() >= 8) {
                    pd.setMessage("Please Wait...");
                    pd.show();

                    settingsViewModel.hitChangePasswordApi(authentication, new ChangePasswordRequest(currentPassword, newPassword)).observe(getViewLifecycleOwner(), responseDTO -> {
                        pd.dismiss();
                        switch (responseDTO.getStatus()) {
                            case "success":
                                editTextCurrentPassword.setText(null);
                                editTextNewPassword.setText(null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("ConnectUs Alert")
                                        .setMessage(responseDTO.getMessage())
                                        .setPositiveButton(android.R.string.yes, null)
                                        .show();
                                break;
                            case "failed":
                            case "error":
                                Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    });
                } else {
                    Snackbar.make(view, "Password should be longer than 8 characters!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}