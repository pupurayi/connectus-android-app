package com.connectus.mobile.ui.profile;

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
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
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
    TextView textViewFullName, textViewPhoneNumber, textViewEmail;
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
        ProfileDto profileDTO = sharedPreferencesManager.getProfile();
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
                            ProfileDto profileDTO = sharedPreferencesManager.getProfile();
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
            }
        });

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewPhoneNumber = view.findViewById(R.id.text_view_phone_value);
        textViewEmail = view.findViewById(R.id.text_view_email_value);

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
        ProfileDto profileDTO = sharedPreferencesManager.getProfile();
        populateFields(profileDTO);
    }

    public void populateFields(ProfileDto profileDTO) {
        String fullName = profileDTO.getFirstName() + " " + profileDTO.getLastName();
        String msisdn = profileDTO.getMsisdn();
        String email = profileDTO.getEmail();
        textViewFullName.setText(fullName);
        textViewPhoneNumber.setText(msisdn);
        textViewEmail.setText(email);
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
                        ProfileDto profileDTO = sharedPreferencesManager.getProfile();
                        invalidateAvatarCache(profileDTO.getId());
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