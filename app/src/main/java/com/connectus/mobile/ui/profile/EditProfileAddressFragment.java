package com.connectus.mobile.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
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
import com.connectus.mobile.api.dto.AddressResponse;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.UpdateAddressRequest;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditProfileAddressFragment extends Fragment {
    private static final String TAG = EditProfileAddressFragment.class.getSimpleName();

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
        return inflater.inflate(R.layout.fragment_edit_profile_address, container, false);
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
        AddressResponse addressResponse = profileDTO.getAddress();

        String addressLine1 = (addressResponse != null) ? addressResponse.getAddressLine1() : null;
        String city = (addressResponse != null) ? addressResponse.getCity() : null;
        String province = (addressResponse != null) ? addressResponse.getProvince() : null;
        String proofOfResidence = (addressResponse != null) ? addressResponse.getFileName() : null;
        String countryCode = (addressResponse != null) ? addressResponse.getCountryCode() : null;

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imageViewProfileAvatar);
        }
        editTextAddress = view.findViewById(R.id.edit_text_address);
        editTextCity = view.findViewById(R.id.edit_text_city);
        editTextProvince = view.findViewById(R.id.edit_text_province);
        editTextProofOfResidence = view.findViewById(R.id.edit_text_proof_of_residence);
        ccpCountryCode = view.findViewById(R.id.ccp_country_code);

        editTextAddress.setText(addressLine1);
        editTextCity.setText(city);
        editTextProvince.setText(province);
        if (proofOfResidence != null) {
            editTextProofOfResidence.setText(getText(R.string.proof_of_residence));
            editTextProofOfResidence.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.file_download_done), null);
        } else {
            editTextProofOfResidence.setText(null);
            editTextProofOfResidence.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.attachment), null);
        }

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

        if (countryCode != null) {
            ccpCountryCode.setCountryForNameCode(countryCode);
        } else {
            ccpCountryCode.setAutoDetectedCountry(true);
        }

        buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String addressLine1 = editTextAddress.getText().toString();
                String city = editTextCity.getText().toString();
                String province = editTextProvince.getText().toString();
                String proofOfResidence = editTextProofOfResidence.getText().toString();
                String countryCode = ccpCountryCode.getSelectedCountryNameCode();

                if (addressLine1.length() > 1 && city.length() > 1 && province.length() > 1 && proofOfResidence.equals(getString(R.string.proof_of_residence))) {
                    if (addressResponse != null && addressLine1.equals(addressResponse.getAddressLine1()) && city.equals(addressResponse.getCity()) && province.equals(addressResponse.getProvince()) && countryCode.equals(addressResponse.getCountryCode())) {
                        close();
                    } else {
                        UpdateAddressRequest updateAddressRequest = new UpdateAddressRequest(addressLine1, city, province, countryCode);
                        pd.setMessage("Updating ...");
                        pd.show();
                        profileDetailsViewModel.hitUpdateProfileAddressApi(getActivity(), authentication, updateAddressRequest).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                            @Override
                            public void onChanged(ResponseDTO responseDTO) {
                                pd.dismiss();
                                switch (responseDTO.getStatus()) {
                                    case "success":
                                        close();
                                        break;
                                    case "failed":
                                    case "error":
                                        Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        });
                    }
                } else {
                    if (addressLine1.length() == 0) {
                        Snackbar.make(view, "Enter valid address!", Snackbar.LENGTH_LONG).show();
                    } else if (city.length() == 0) {
                        Snackbar.make(view, "Enter valid city!", Snackbar.LENGTH_LONG).show();
                    } else if (province.length() == 0) {
                        Snackbar.make(view, "Enter valid province!", Snackbar.LENGTH_LONG).show();
                    } else if (!proofOfResidence.equals(getString(R.string.proof_of_residence))) {
                        Snackbar.make(view, "Upload Proof of Residence!", Snackbar.LENGTH_LONG).show();
                    }
                }
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
                uploadDocument(sourceUri);
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

    public void uploadDocument(Uri uri) {
        pd.setMessage("Uploading...");
        pd.show();
        File file = new File(uri.getPath());
        String mimeType = Common.getMimeType(file.getPath());

        RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeType));
        MultipartBody.Part document = MultipartBody.Part.createFormData("document", file.getName(), requestBody);
        profileDetailsViewModel.hitUploadProfileDocumentApi(

                getActivity(), this.authentication, DOCUMENT_TYPE, document).

                observe(getViewLifecycleOwner(), responseDTO -> {
                    pd.dismiss();
                    switch (responseDTO.getStatus()) {
                        case "success":
                            editTextProofOfResidence.setText(getText(R.string.proof_of_residence));
                            editTextProofOfResidence.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.file_download_done), null);
                            Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });

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