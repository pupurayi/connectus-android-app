package com.connectus.mobile.ui.initial.demographics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.profile.ProfileDetailsViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DemographicsFragment extends Fragment {
    private static final String TAG = DemographicsFragment.class.getSimpleName();

    private static final int IMAGE_PICKER_REQUEST = 100;
    private static final String DOCUMENT_TYPE = "proof-of-residence";

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextAddress, editTextCity, editTextProvince, editTextProofOfResidence;
    CountryCodePicker ccpCountryCode;
    Button buttonSave;

    FragmentManager fragmentManager;
    private ProfileDetailsViewModel profileDetailsViewModel;
    private SharedPreferencesManager sharedPreferencesManager;
    private String authentication;
    private boolean pickingImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demographics, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        this.authentication = sharedPreferencesManager.getAuthenticationToken();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);

        editTextAddress = view.findViewById(R.id.edit_text_address);
        editTextCity = view.findViewById(R.id.edit_text_city);
        editTextProvince = view.findViewById(R.id.edit_text_province);
        editTextProofOfResidence = view.findViewById(R.id.edit_text_proof_of_residence);
        ccpCountryCode = view.findViewById(R.id.ccp_country_code);

        editTextProofOfResidence.setInputType(InputType.TYPE_NULL);
        editTextProofOfResidence.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!pickingImage) {
                    pickImage();
                }
                return true;
            }
        });

        buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressLine1 = editTextAddress.getText().toString();
                String city = editTextCity.getText().toString();
                String province = editTextProvince.getText().toString();
                String proofOfResidence = editTextProofOfResidence.getText().toString();
                String countryCode = ccpCountryCode.getSelectedCountryNameCode();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        pickingImage = false;
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
            } else {
                Toast.makeText(getContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickImage() {
        ImagePicker.with(this)
                .crop()//Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                .start(IMAGE_PICKER_REQUEST);
        pickingImage = true;
    }

    private void close() {
        DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
        transaction.commit();
    }
}