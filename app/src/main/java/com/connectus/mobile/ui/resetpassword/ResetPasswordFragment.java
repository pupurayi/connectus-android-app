package com.connectus.mobile.ui.resetpassword;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;

import static com.connectus.mobile.common.Validate.isValidMobileNumber;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ResetPasswordFragment extends Fragment {

    private static final String TAG = ResetPasswordFragment.class.getSimpleName();

    ProgressDialog pd;
    CountryCodePicker ccp;
    EditText editTextPhoneNumber;
    Button buttonResetPassword;
    TextView textViewSignIn;

    String countryCode;
    FragmentManager fragmentManager;
    ResetPasswordViewModel resetPasswordViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        resetPasswordViewModel = new ViewModelProvider(this).get(ResetPasswordViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reset_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());

        ccp = view.findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(() -> {
            countryCode = ccp.getSelectedCountryCode();
            Log.d(TAG, countryCode);
        });
        editTextPhoneNumber = view.findViewById(R.id.edit_text_phone_number);

        buttonResetPassword = view.findViewById(R.id.button_reset_password);
        buttonResetPassword.setOnClickListener(v -> {
            countryCode = (countryCode == null || countryCode.isEmpty()) ? ccp.getSelectedCountryCode() : countryCode;
            long phoneNumber = 0;
            try {
                phoneNumber = Long.parseLong(editTextPhoneNumber.getText().toString().trim());
            } catch (Exception ignored) {

            }
            String msisdn = String.format("+%s%s", countryCode, phoneNumber);
            boolean isPhoneNumberValid = isValidMobileNumber(msisdn);
            if (isPhoneNumberValid) {
                pd.setMessage("Resetting password...");
                pd.show();
                resetPasswordViewModel.hitResetPasswordApi(msisdn).observe(getViewLifecycleOwner(), responseDto -> {
                    Utils.hideSoftKeyboard(getActivity());
                    pd.dismiss();
                    switch (responseDto.getStatus()) {
                        case "success":
                        case "failed":
                        case "error":
                            Utils.alert(getContext(), "Connect Us", responseDto.getMessage());
                            break;
                    }
                });
            } else {
                Snackbar.make(view, "Enter valid Phone Number!", Snackbar.LENGTH_LONG).show();
            }
        });

        textViewSignIn = getView().findViewById(R.id.text_view_sign_up_title);
        textViewSignIn.setOnClickListener(v -> fragmentManager.popBackStack());
    }
}