package com.connectus.mobile.ui.siba.onboarding;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.siba.EligibilityResponse;
import com.connectus.mobile.ui.siba.SibaViewModel;
import com.google.gson.internal.LinkedTreeMap;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.connectus.mobile.common.Validate.isValidMobileNumber;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class InviteSibaMemberFragment extends Fragment {
    private static final String TAG = InviteSibaMemberFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;
    Button buttonInviteMember;
    CountryCodePicker ccp;
    EditText editTextPhoneNumber;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private SibaViewModel sibaViewModel;


    String authentication, countryCode;
    ProfileDTO profileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sibaViewModel = new ViewModelProvider(this).get(SibaViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_invite_siba_member, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();

        profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getUserId() + ".png")
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
        buttonInviteMember = view.findViewById(R.id.button_invite_member);
        buttonInviteMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                countryCode = (countryCode == null || countryCode.isEmpty()) ? ccp.getSelectedCountryCode() : countryCode;
                long phoneNumber = 0;
                try {
                    phoneNumber = Long.parseLong(editTextPhoneNumber.getText().toString().trim());
                } catch (Exception ignored) {

                }
                String username = String.format("+%s%s", countryCode, phoneNumber);
                boolean isPhoneNumberValid = isValidMobileNumber(username);
                if (isPhoneNumberValid && !profileDTO.getUsername().equals(username)) {
                    pd.setMessage("Checking Eligibility ...");
                    pd.show();
                    sibaViewModel.hitCheckEligibilityByUsernameApi(authentication, username).observe(getViewLifecycleOwner(), responseDTO -> {
                        switch (responseDTO.getStatus()) {
                            case "success":
                                LinkedTreeMap data = (LinkedTreeMap) responseDTO.getData();
                                Long profileId = (new Double(data.get("profileId").toString())).longValue();
                                EligibilityResponse eligibilityResponse = new EligibilityResponse(profileId, (String) data.get("username"), (String) data.get("firstName"), (String) data.get("lastName"));
                                CreateSibaProfileFragment createSibaProfileFragment = (CreateSibaProfileFragment) fragmentManager.findFragmentByTag(CreateSibaProfileFragment.class.getSimpleName());
                                createSibaProfileFragment.addInviteToView(eligibilityResponse);
                                getActivity().onBackPressed();
                                break;
                            case "failed":
                            case "error":
                                try {
                                    List<String> reasons = (List<String>) responseDTO.getData();
                                    String allReasons = TextUtils.join(", ", reasons);
                                    AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                    alertDialog.setTitle("Eligibility Error");
                                    alertDialog.setMessage(allReasons);
                                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                    alertDialog.show();
                                } catch (Exception ignore) {
                                    Toast.makeText(getContext(), responseDTO.getMessage(), Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                        pd.dismiss();
                    });
                } else {
                    if (!isPhoneNumberValid) {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("eMalyami Alert");
                        alertDialog.setMessage("Enter a valid phone number!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("eMalyami Alert");
                        alertDialog.setMessage("You cannot invite yourself!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }
        });
    }
}