package com.connectus.mobile.ui.old.otp;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.ValidateOTPRequest;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.old.resetpassword.ResetPasswordFragment;
import com.connectus.mobile.ui.old.resetpassword.ResetPasswordViewModel;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
// TODO countDownTimer
public class OTPFragment extends Fragment {
    // TODO OTP Count Down
    private static final String TAG = OTPFragment.class.getSimpleName();

    String otpType, otpTitle, msisdn;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;

    FragmentManager fragmentManager;
    ProgressDialog pd;
    TextView textViewOTPTitle, textViewOTPDescription, textViewMsisdn;
    Button buttonRequestOTP;

    private OTPViewModel otpViewModel;
    private ResetPasswordViewModel resetPasswordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        otpViewModel = new ViewModelProvider(this).get(OTPViewModel.class);
        resetPasswordViewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);

        Bundle arguments = getArguments();
        if (arguments != null) {
            otpType = arguments.getString("otpType");
            otpTitle = arguments.getString("otpTitle");
            msisdn = arguments.getString("msisdn");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_o_t_p, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        if (otpType == null || otpTitle == null || msisdn == null) {
            getActivity().onBackPressed();
        }

        pd = new ProgressDialog(getActivity());
        SharedPreferencesManager sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        switch (otpType) {
            case "PHONE_VERIFICATION":
                initiateUserVerification(view, authentication);
                break;
            case "PASSWORD_RESET":
                initiateResetPassword(view, msisdn);
                break;
            default:
                getActivity().onBackPressed();
        }

        textViewOTPTitle = view.findViewById(R.id.text_view_otp_title);
        textViewOTPDescription = view.findViewById(R.id.text_view_o_t_p_description);
        textViewMsisdn = view.findViewById(R.id.text_view_full_name);
        buttonRequestOTP = view.findViewById(R.id.button_send_otp);
        buttonRequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String label = "Resend Code";
                buttonRequestOTP.setText(label);
                String otpDescription = "OTP Code was sent to";
                textViewOTPDescription.setText(otpDescription);
                switch (otpType) {
                    case "PHONE_VERIFICATION":
                        initiateUserVerification(view, authentication);
                        break;
                    case "PASSWORD_RESET":
                        initiateResetPassword(view, msisdn);
                        break;
                    default:
                        getActivity().onBackPressed();
                }
            }
        });

        textViewOTPTitle.setText(otpTitle);
        textViewMsisdn.setText(msisdn);

        otp1 = view.findViewById(R.id.otp1);
        otp2 = view.findViewById(R.id.otp2);
        otp3 = view.findViewById(R.id.otp3);
        otp4 = view.findViewById(R.id.otp4);
        otp5 = view.findViewById(R.id.otp5);
        otp6 = view.findViewById(R.id.otp6);

        otp1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (!isOTPComplete()) {
                        otp2.requestFocus();
                    } else {
                        switch (otpType) {
                            case "PHONE_VERIFICATION":
                                executeProfileVerification(view, authentication, getOTP());
                                break;
                            case "PASSWORD_RESET":
                                executeResetPasswordOTPValidation(view, msisdn, getOTP());
                                break;
                            default:
                                getActivity().onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (!isOTPComplete()) {
                        otp3.requestFocus();
                    } else {
                        switch (otpType) {
                            case "PHONE_VERIFICATION":
                                executeProfileVerification(view, authentication, getOTP());
                                break;
                            case "PASSWORD_RESET":
                                executeResetPasswordOTPValidation(view, msisdn, getOTP());
                                break;
                            default:
                                getActivity().onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (!isOTPComplete()) {
                        otp4.requestFocus();
                    } else {
                        switch (otpType) {
                            case "PHONE_VERIFICATION":
                                executeProfileVerification(view, authentication, getOTP());
                                break;
                            case "PASSWORD_RESET":
                                executeResetPasswordOTPValidation(view, msisdn, getOTP());
                                break;
                            default:
                                getActivity().onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (!isOTPComplete()) {
                        otp5.requestFocus();
                    } else {
                        switch (otpType) {
                            case "PHONE_VERIFICATION":
                                executeProfileVerification(view, authentication, getOTP());
                                break;
                            case "PASSWORD_RESET":
                                executeResetPasswordOTPValidation(view, msisdn, getOTP());
                                break;
                            default:
                                getActivity().onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (!isOTPComplete()) {
                        otp6.requestFocus();
                    } else {
                        switch (otpType) {
                            case "PHONE_VERIFICATION":
                                executeProfileVerification(view, authentication, getOTP());
                                break;
                            case "PASSWORD_RESET":
                                executeResetPasswordOTPValidation(view, msisdn, getOTP());
                                break;
                            default:
                                getActivity().onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count == 1) {
                    if (isOTPComplete()) {
                        switch (otpType) {
                            case "PHONE_VERIFICATION":
                                executeProfileVerification(view, authentication, getOTP());
                                break;
                            case "PASSWORD_RESET":
                                executeResetPasswordOTPValidation(view, msisdn, getOTP());
                                break;
                            default:
                                getActivity().onBackPressed();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private boolean isOTPComplete() {
        return (!otp1.getText().toString().isEmpty() && !otp2.getText().toString().isEmpty() && !otp3.getText().toString().isEmpty() && !otp4.getText().toString().isEmpty() && !otp5.getText().toString().isEmpty() && !otp6.getText().toString().isEmpty());
    }

    private String getOTP() {
        return otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() + otp4.getText().toString() + otp5.getText() + otp6.getText().toString();
    }

    private void initiateUserVerification(View view, String authentication) {
        pd.setMessage("Sending OTP...");
        pd.show();
        otpViewModel.hitUserVerificationApi(authentication).observe(getViewLifecycleOwner(), responseDTO -> {
            pd.dismiss();
            switch (responseDTO.getStatus()) {
                case "success":
                    String label = "Resend Code";
                    buttonRequestOTP.setText(label);
                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    private void executeProfileVerification(View view, String authentication, String otp) {
        pd.setMessage("Please Wait ...");
        pd.show();
        otpViewModel.hitUserVerificationApi(getActivity(), authentication, new ValidateOTPRequest(otp)).observe(getViewLifecycleOwner(), responseDTO -> {
            pd.dismiss();
            switch (responseDTO.getStatus()) {
                case "success":
                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
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
        });
    }


    private void initiateResetPassword(View view, String msisdn) {
        pd.setMessage("Sending OTP...");
        pd.show();
        resetPasswordViewModel.hitResetPasswordApi(getActivity(), msisdn).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        String label = "Resend Code";
                        buttonRequestOTP.setText(label);
                        Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void executeResetPasswordOTPValidation(View view, String msisdn, String otp) {
        pd.setMessage("Validating OTP...");
        pd.show();
        resetPasswordViewModel.hitResetPasswordApi(msisdn, otp).observe(getViewLifecycleOwner(), responseDTO -> {
            pd.dismiss();
            switch (responseDTO.getStatus()) {
                case "success":
                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    Bundle bundle = new Bundle();
                    bundle.putString("msisdn", msisdn);
                    bundle.putString("otp", otp);
                    ResetPasswordFragment resetPasswordFragment = new ResetPasswordFragment();
                    resetPasswordFragment.setArguments(bundle);
                    fragmentManager.popBackStack();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.container, resetPasswordFragment, ResetPasswordFragment.class.getSimpleName());
                    transaction.addToBackStack("resetPasswordFragment");
                    transaction.commit();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }
}