package com.connectus.mobile.ui.settings;

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
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.user.UserViewModel;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

public class SettingsFragment extends Fragment {

    ProgressDialog pd;
    ImageView imageViewBack;
    ImageView imageViewAvatar;
    EditText editTextCurrentPassword, editTextNewPassword;
    Button buttonChangePassword;

    boolean passwordShow = false;
    boolean verifyPasswordShow = false;

    SharedPreferencesManager sharedPreferencesManager;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());


        UserDto userDto = sharedPreferencesManager.getUser();

        imageViewAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        editTextCurrentPassword = view.findViewById(R.id.edit_text_current_password);
        editTextCurrentPassword.setOnTouchListener((v, event) -> {
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
        });

        editTextNewPassword = view.findViewById(R.id.edit_text_new_password);
        editTextNewPassword.setOnTouchListener((v, event) -> {
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
        });

        buttonChangePassword = view.findViewById(R.id.button_change_password);
        buttonChangePassword.setOnClickListener(v -> {

            String mCurrentPassword = editTextCurrentPassword.getText().toString().trim();
            String newPassword = editTextNewPassword.getText().toString().trim();
            if (newPassword.length() >= 8) {
                pd.setMessage("Please Wait...");
                pd.show();

                String currentPassword = userDto.getPassword();
                if (mCurrentPassword.equals(currentPassword)) {
                    userDto.setPassword(newPassword);
                    userViewModel.hitUpdateUser(getContext(), userDto).observe(getViewLifecycleOwner(), responseDTO -> {
                        pd.dismiss();
                        switch (responseDTO.getStatus()) {
                            case "success":
                                editTextCurrentPassword.setText(null);
                                editTextNewPassword.setText(null);
                                Utils.alert(getContext(), "Connect Us", responseDTO.getMessage());
                                break;
                            case "failed":
                            case "error":
                                Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    });
                } else {
                    pd.dismiss();
                    Snackbar.make(view, "Current password is not correct!", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(view, "Password should be longer than 8 characters!", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}