package com.connectus.mobile.ui.airtime;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.airtime.OperatorResponse;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.services.ServicesViewModel;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import static com.connectus.mobile.common.Validate.isValidMobileNumber;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MobileTopupFragment extends Fragment {
    private static final String TAG = MobileTopupFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;
    Button buttonGetMobileOperator;
    CountryCodePicker ccp;
    EditText editTextPhoneNumber;

    private SharedPreferencesManager sharedPreferencesManager;
    private ServicesViewModel servicesViewModel;

    String authentication, countryCode;
    ProfileDto profileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        servicesViewModel = new ViewModelProvider(this).get(ServicesViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile_topup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();

        profileDTO = sharedPreferencesManager.getProfile();

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

        ccp = getView().findViewById(R.id.ccp);
        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                countryCode = ccp.getSelectedCountryCode();
                Log.d(TAG, countryCode);
            }
        });
        editTextPhoneNumber = view.findViewById(R.id.edit_text_phone_number);
        buttonGetMobileOperator = view.findViewById(R.id.button_add_get_operator);
        buttonGetMobileOperator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                countryCode = (countryCode == null || countryCode.isEmpty()) ? ccp.getSelectedCountryCode() : countryCode;
                long phone = 0;
                try {
                    phone = Long.parseLong(editTextPhoneNumber.getText().toString().trim());
                } catch (Exception ignored) {

                }
                String phoneNumber = String.format("+%s%s", countryCode, phone);
                boolean isPhoneNumberValid = isValidMobileNumber(phoneNumber);
                if (isPhoneNumberValid) {
                    getMobileOperator(phoneNumber);
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("ConnectUs Alert")
                            .setMessage("Enter valid Phone Number!")
                            .setPositiveButton(android.R.string.yes, null)
                            .show();
                }
            }
        });
    }

    public void getMobileOperator(String phoneNumber) {
        pd.setMessage("Please Wait ...");
        pd.show();
        servicesViewModel.hitGetMobileOperatorApi(authentication, phoneNumber).observe(getViewLifecycleOwner(), new Observer<ResponseDTO<OperatorResponse>>() {
            @Override
            public void onChanged(ResponseDTO<OperatorResponse> responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        OperatorResponse operatorResponse = responseDTO.getData();
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        MobileTopupCheckoutFragment mobileTopupCheckoutFragment = new MobileTopupCheckoutFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("operatorResponse", new Gson().toJson(operatorResponse));
                        bundle.putString("phoneNumber", phoneNumber);
                        mobileTopupCheckoutFragment.setArguments(bundle);
                        transaction.replace(R.id.container, mobileTopupCheckoutFragment, MobileTopupCheckoutFragment.class.getSimpleName());
                        transaction.addToBackStack(TAG);
                        transaction.commit();
                        break;
                    case "failed":
                    case "error":
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("ConnectUs Alert")
                                .setIcon(R.drawable.account_circle)
                                .setMessage(responseDTO.getMessage())
                                .setPositiveButton(android.R.string.yes, null)
                                .show();
                        break;
                }
            }
        });

    }
}