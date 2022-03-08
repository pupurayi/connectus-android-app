package com.connectus.mobile.ui.resetpassword;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ResetPasswordRequest;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {
    private static final String TAG = ResetPasswordFragment.class.getSimpleName();

    ProgressDialog pd;
    EditText editTextPassword, editTextVerifyPassword;
    Button buttonResetPassword;
    TextView textViewSignIn;

    boolean passwordShow = false;
    boolean verifyPasswordShow = false;

    String msisdn, otp;

    FragmentManager fragmentManager;
    ResetPasswordViewModel resetPasswordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resetPasswordViewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            msisdn = arguments.getString("msisdn");
            otp = arguments.getString("otp");
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());

        editTextPassword = view.findViewById(R.id.edit_text_password);
        editTextPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextPassword.getRight() - editTextPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!passwordShow) {
                            editTextPassword.setTransformationMethod(null);
                            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                            passwordShow = true;
                        } else {
                            editTextPassword.setTransformationMethod(new PasswordTransformationMethod());
                            editTextPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            passwordShow = false;
                        }
                        return passwordShow;
                    }
                }
                return false;
            }
        });

        editTextVerifyPassword = view.findViewById(R.id.edit_text_verify_password);
        editTextVerifyPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (editTextVerifyPassword.getRight() - editTextVerifyPassword.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        if (!verifyPasswordShow) {
                            editTextVerifyPassword.setTransformationMethod(null);
                            editTextVerifyPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility_off, 0);
                            verifyPasswordShow = true;
                        } else {
                            editTextVerifyPassword.setTransformationMethod(new PasswordTransformationMethod());
                            editTextVerifyPassword.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.visibility, 0);
                            verifyPasswordShow = false;
                        }
                        return verifyPasswordShow;
                    }
                }
                return false;
            }
        });

        buttonResetPassword = view.findViewById(R.id.button_reset_password);
        buttonResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString();
                String verifyPassword = editTextVerifyPassword.getText().toString();
                if (password.length() >= 8 && password.equals(verifyPassword)) {
                    pd.setMessage("Please Wait...");
                    pd.show();
                    resetPasswordViewModel.hitResetPasswordApi(new ResetPasswordRequest(msisdn, otp, password)).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                        @Override
                        public void onChanged(ResponseDTO responseDTO) {
                            pd.dismiss();
                            switch (responseDTO.getStatus()) {
                                case "success":
                                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                    fragmentManager.popBackStack();
                                    fragmentManager.popBackStack();
                                    break;
                                case "failed":
                                case "error":
                                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                } else {
                    if (password.length() < 8) {
                        Snackbar.make(view, "Password should be longer than 8 characters!", Snackbar.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(view, "Password Mismatch!", Snackbar.LENGTH_LONG).show();
                    }
                }

            }
        });

        textViewSignIn = getView().findViewById(R.id.text_view_sign_up_title);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragmentManager.popBackStack();
                fragmentManager.popBackStack();
            }
        });
    }
}