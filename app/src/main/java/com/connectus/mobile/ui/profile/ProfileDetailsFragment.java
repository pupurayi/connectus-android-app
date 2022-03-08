package com.connectus.mobile.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.AddressResponse;
import com.connectus.mobile.api.dto.IdentificationResponse;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProfileDetailsFragment extends Fragment {
    private static final String TAG = ProfileDetailsFragment.class.getSimpleName();

    private static final int IMAGE_PICKER_REQUEST = 100;

    ImageView imageViewBack, imageViewPlus, imageViewProfileAvatar;
    ProgressDialog pd;
    TextView textViewFullName, textViewPhoneNumber, textViewEmail, textViewDob, textViewSex, textViewIdentificationLabel, textViewIdentificationValue, textViewAddressValue;
    Button buttonEditProfile;
    String authentication;
    SharedPreferencesManager sharedPreferencesManager;

    private ProfileDetailsViewModel profileDetailsViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());

        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        ProfileDTO profileDTO = sharedPreferencesManager.getProfile();
        authentication = sharedPreferencesManager.getAuthenticationToken();

        long lastSync = sharedPreferencesManager.getLastSync();
        long now = new Date().getTime();
        // 1 Second = 1000 Milliseconds, 300000 = 5 Minutes
        if (now - lastSync >= 300000) {
            pd.setMessage("Syncing Profile ...");
            pd.show();
            profileDetailsViewModel.hitGetProfileApi(getActivity(), authentication).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                @Override
                public void onChanged(ResponseDTO responseDTO) {
                    pd.dismiss();
                    switch (responseDTO.getStatus()) {
                        case "success":
                            Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            ProfileDTO profileDTO = sharedPreferencesManager.getProfile();
                            populateFields(profileDTO);
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                }
            });
        }

        imageViewProfileAvatar = view.findViewById(R.id.adf_image_view_profile_avatar);
        imageViewPlus = view.findViewById(R.id.adf_image_view_plus);
        imageViewPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
//                showDialogForProfileImageEdit();
            }
        });

        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getUserId());

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewPhoneNumber = view.findViewById(R.id.text_view_phone_value);
        textViewEmail = view.findViewById(R.id.text_view_email_value);
        textViewDob = view.findViewById(R.id.text_view_dob_value);
        textViewSex = view.findViewById(R.id.text_view_sex_value);
        textViewIdentificationLabel = view.findViewById(R.id.text_view_identification_label);
        textViewIdentificationValue = view.findViewById(R.id.text_view_identification_value);
        textViewAddressValue = view.findViewById(R.id.text_view_adrdess_value);

        populateFields(profileDTO);

        buttonEditProfile = view.findViewById(R.id.button_edit_profile);
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditProfileFragment editProfileFragment = new EditProfileFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, editProfileFragment, EditProfileFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);//pano
                transaction.commit();
            }
        });

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        ProfileDTO profileDTO = sharedPreferencesManager.getProfile();
        populateFields(profileDTO);
        Common.loadAvatar(profileDTO.isAvatarAvailable(),imageViewProfileAvatar, profileDTO.getUserId());
    }

    public void populateFields(ProfileDTO profileDTO) {
        String firstName = profileDTO.getFirstName();
        String fullName = firstName + " " + profileDTO.getLastName();
        String username = profileDTO.getUsername();
        String email = profileDTO.getEmail();
        Date dob = profileDTO.getDob();
        String sex = (profileDTO.getSex() != null) ? profileDTO.getSex().getTitle() : null;
        IdentificationResponse identificationResponse = profileDTO.getIdentification();
        String identificationType = (identificationResponse != null && identificationResponse.getType() != null) ? identificationResponse.getType().getLabel() : "Passport or National ID";
        String identificationNumber = (identificationResponse != null && identificationResponse.getNumber() != null) ? identificationResponse.getNumber() : null;
        AddressResponse addressResponse = profileDTO.getAddress();
        String address = null;
        if (addressResponse != null) {
            String addressLine1 = addressResponse.getAddressLine1();
            String city = addressResponse.getCity();
            String province = addressResponse.getProvince();
            String country = addressResponse.getCountryCode();
            address = addressLine1 + ", " + city + ", " + province + ", " + country;
        }

        textViewFullName.setText(fullName);
        textViewPhoneNumber.setText(username);
        textViewEmail.setText(email);
        textViewIdentificationLabel.setText(identificationType);
        textViewIdentificationValue.setText(identificationNumber);
        textViewAddressValue.setText(address);

        @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String dobToDisplay = null;
        if (dob != null) {
            dobToDisplay = simpleDateFormat.format(dob);
        }
        textViewDob.setText(dobToDisplay);
        textViewSex.setText(sex);
    }

    public void pickImage() {
        ImagePicker.with(this)
                .crop()//Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                .start(IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                uploadProfilePicture(sourceUri);
            } else {
                Toast.makeText(getContext(), "Error Occurred!", Toast.LENGTH_SHORT).show();
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void uploadProfilePicture(Uri uri) {
        pd.setMessage("Uploading Profile ...");
        pd.show();
        File file = new File(uri.getPath());

        RequestBody requestBody = RequestBody.create(file, MediaType.parse("image/png"));
        MultipartBody.Part profilePicture = MultipartBody.Part.createFormData("profilePicture", "avatar.png", requestBody);
        profileDetailsViewModel.hitUploadProfilePictureApi(getActivity(), authentication, profilePicture).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        ProfileDTO profileDTO = sharedPreferencesManager.getProfile();
                        invalidateAvatarCache(profileDTO.getUserId());
                        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getUserId());
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    private void invalidateAvatarCache(UUID userId) {
        Picasso.get().invalidate(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + userId + ".png");
    }
}