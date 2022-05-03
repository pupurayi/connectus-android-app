package com.connectus.mobile.ui.initial.check;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.CheckResponseDto;
import com.connectus.mobile.api.dto.ResponseDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.initial.signin.SignInFragment;
import com.connectus.mobile.ui.initial.signup.SignUpFragment;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;

import static com.connectus.mobile.common.Validate.isValidMobileNumber;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CheckFragment extends Fragment {
    private static final String TAG = CheckFragment.class.getSimpleName();


    CountryCodePicker ccp;
    EditText editTextPhoneNumber;
    Button buttonProceed;
    ProgressDialog pd;

    String countryCode;
    FragmentManager fragmentManager;
    private CheckViewModel checkViewModel;
    SharedPreferencesManager sharedPreferencesManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        checkViewModel = new ViewModelProvider(this).get(CheckViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_authorize, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        ccp = getView().findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = ccp.getSelectedCountryCode();
                Log.d(TAG, countryCode);
            }
        });

        editTextPhoneNumber = view.findViewById(R.id.edit_text_phone_number);

        buttonProceed = view.findViewById(R.id.button_proceed);
        buttonProceed.setOnClickListener(v -> {
            countryCode = (countryCode == null || countryCode.isEmpty()) ? ccp.getSelectedCountryCode() : countryCode;
            long phoneNumberSuffix = 0;
            try {
                phoneNumberSuffix = Long.parseLong(editTextPhoneNumber.getText().toString().trim());
            } catch (Exception ignored) {

            }
            String phoneNumber = String.format("+%s%s", countryCode, phoneNumberSuffix);
            boolean isPhoneNumberValid = isValidMobileNumber(phoneNumber);

            if (isPhoneNumberValid) {
                pd.setMessage("Authorizing ...");
                pd.show();

                checkViewModel.hitCheckApi(getActivity(), phoneNumber).observe(getViewLifecycleOwner(), new Observer<ResponseDto>() {
                    @Override
                    public void onChanged(ResponseDto responseDTO) {
                        switch (responseDTO.getStatus()) {
                            case "success":
                                CheckResponseDto checkResponseDto = sharedPreferencesManager.getAuthorization();
                                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                                if (checkResponseDto.isSuccess()) {
                                    SignInFragment signInFragment = new SignInFragment();
                                    transaction.replace(R.id.container, signInFragment, SignInFragment.class.getSimpleName());
                                } else {
                                    SignUpFragment signUpFragment = new SignUpFragment();
                                    transaction.replace(R.id.container, signUpFragment, SignUpFragment.class.getSimpleName());
                                }
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

            } else {
                Snackbar.make(view, "Enter valid Phone Number!", Snackbar.LENGTH_LONG).show();
            }
        });

    }
}