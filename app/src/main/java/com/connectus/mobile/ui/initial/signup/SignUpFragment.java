package com.connectus.mobile.ui.initial.signup;

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
import com.connectus.mobile.api.dto.SignUpRequest;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.initial.demographics.DemographicsFragment;
import com.connectus.mobile.ui.initial.check.CheckFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SignUpFragment extends Fragment {
    private static final String TAG = SignUpFragment.class.getSimpleName();

    ProgressDialog pd;
    TextView textViewPhoneNumber, textViewChangePhone;
    EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword;
    Button buttonSignUp;

    String phoneNumber;
    boolean passwordShow = false;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    private SignUpViewModel signUpViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        signUpViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sign_up, container, false);
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
        editTextFirstName = view.findViewById(R.id.edit_text_first_name);
        editTextLastName = view.findViewById(R.id.edit_text_last_name);
        editTextEmail = view.findViewById(R.id.edit_text_email);

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

        buttonSignUp = view.findViewById(R.id.button_next);
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString().trim();
                String lastName = editTextLastName.getText().toString().trim();
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                // TODO validations pwd>=8 email
                if (firstName.length() > 2 && lastName.length() > 2 && email.length() != 0 && password.length() >= 8) {
                    pd.setMessage("Please Wait ...");
                    pd.show();
                    signUp(firstName, lastName, email, phoneNumber, password);
                } else {
                    if (firstName.length() == 0) {
                        Snackbar.make(view, "Enter First Name!", Snackbar.LENGTH_LONG).show();
                    } else if (lastName.length() == 0) {
                        Snackbar.make(view, "Enter Last Name!", Snackbar.LENGTH_LONG).show();
                    } else if (email.length() == 0) {
                        Snackbar.make(view, "Enter valid Email!", Snackbar.LENGTH_LONG).show();
                    } else if (password.length() < 8) {
                        Snackbar.make(view, "Password should have at least 8 characters!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });

        textViewChangePhone = view.findViewById(R.id.text_view_change_phone);
        textViewChangePhone.setOnClickListener(v -> {
            Common.clearSessionData(sharedPreferencesManager, getContext());
            fragmentManager.popBackStack();
            Fragment authorizeFragment = fragmentManager.findFragmentByTag(CheckFragment.class.getSimpleName());
            if (authorizeFragment == null) {
                authorizeFragment = new CheckFragment();
            }
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.container, authorizeFragment, CheckFragment.class.getSimpleName());
            transaction.commit();
        });
    }

    public void signUp(String firstName, String lastName, String email, String msisdn, String password) {
        signUpViewModel.hitSignUpApi(getActivity(), new SignUpRequest(firstName, lastName, email, msisdn, password)).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        DemographicsFragment demographicsFragment = new DemographicsFragment();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.container, demographicsFragment, DemographicsFragment.class.getSimpleName());
                        transaction.commit();
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

}