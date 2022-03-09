package com.connectus.mobile.ui.initial.signin;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.CheckResponseDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.SignInRequest;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.initial.check.CheckFragment;
import com.connectus.mobile.ui.old.resetpassword.ForgotPasswordFragment;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SignInFragment extends Fragment {
    private static final String TAG = SignInFragment.class.getSimpleName();

    EditText editTextPassword;
    TextView textViewPhoneNumber, textViewChangePhone, textViewForgotPassword;
    Button buttonSignIn;
    ProgressDialog pd;

    private String phoneNumber;
    boolean passwordShow = false;

    FragmentManager fragmentManager;
    private SignInViewModel signInViewModel;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        signInViewModel = new ViewModelProvider(this).get(SignInViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_in, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        CheckResponseDto checkResponseDto = sharedPreferencesManager.getAuthorization();
        phoneNumber = checkResponseDto.getMsisdn();


        textViewPhoneNumber = view.findViewById(R.id.text_view_phone_number);
        textViewPhoneNumber.setText(phoneNumber);
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


        textViewForgotPassword = getView().findViewById(R.id.text_view_otp_title);
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPasswordFragment forgotPasswordFragment = new ForgotPasswordFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, forgotPasswordFragment, ForgotPasswordFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        buttonSignIn = view.findViewById(R.id.button_sign_in);
        buttonSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = editTextPassword.getText().toString().trim();
                if (password.length() != 0) {
                    pd.setMessage("Authenticating ...");
                    pd.show();

                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            String fcm_token = null;
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                pd.dismiss();
                            } else {
                                fcm_token = task.getResult();
                            }
                            signIn(view, phoneNumber, password, fcm_token);
                        }
                    });
                } else {
                    Snackbar.make(view, "Enter Password!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        textViewChangePhone = getView().findViewById(R.id.text_view_change_phone);
        textViewChangePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.clearSessionData(sharedPreferencesManager, getContext());
                fragmentManager.popBackStack();
                Fragment authorizeFragment = fragmentManager.findFragmentByTag(CheckFragment.class.getSimpleName());
                if (authorizeFragment == null) {
                    authorizeFragment = new CheckFragment();
                }
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.container, authorizeFragment, CheckFragment.class.getSimpleName());
                transaction.commit();
            }
        });
    }

    public void signIn(View view, String msisdn, String password, String fcm_token) {
        signInViewModel.hitSignInApi(getActivity(), new SignInRequest(msisdn, password, fcm_token)).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                switch (responseDTO.getStatus()) {
                    case "success":
                        Common.subscribeToTopic(Constants.GENERAL_TOPIC);
                        DashboardFragment dashboardFragment = new DashboardFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
                        transaction.commit();
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
                pd.dismiss();
            }
        });
    }
}