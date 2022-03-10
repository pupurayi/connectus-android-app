package com.connectus.mobile.ui.profile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import com.connectus.mobile.api.dto.IdentificationResponse;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.UpdateIdentificationRequest;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.common.IdType;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
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
public class EditProfileIdentificationFragment extends Fragment {
    private static final String TAG = EditProfileIdentificationFragment.class.getSimpleName();

    private static final int IMAGE_PICKER_REQUEST = 100;
    private static final String DOCUMENT_TYPE = "identification";

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    boolean dialogNotActive = true;

    EditText editTextType, editTextNumber, editTextIdentificationDocument;
    CountryCodePicker ccpCountryCode;
    Button buttonUpdateIdentification;

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
        return inflater.inflate(R.layout.fragment_edit_profile_identification, container, false);
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
        IdentificationResponse identificationResponse = profileDTO.getIdentification();
        IdType type = (identificationResponse != null) ? identificationResponse.getType() : null;
        String number = (identificationResponse != null) ? identificationResponse.getNumber() : null;
        String identificationDocument = (identificationResponse != null) ? identificationResponse.getFileName() : null;
        String countryCode = (identificationResponse != null) ? identificationResponse.getCountryCode() : null;

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }
        editTextType = view.findViewById(R.id.edit_text_type);
        editTextNumber = view.findViewById(R.id.edit_text_number);
        ccpCountryCode = view.findViewById(R.id.ccp_country_code);
        editTextIdentificationDocument = view.findViewById(R.id.edit_text_identification_document);

        if (type != null) {
            editTextType.setText(type.name());
        } else {
            editTextType.setText(IdType.NATIONAL_ID.name());
        }

        editTextType.setInputType(InputType.TYPE_NULL);
        editTextType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (dialogNotActive) {
                    dialogNotActive = false;
                    IdType[] idTypes = IdType.values();
                    CharSequence[] options = new CharSequence[idTypes.length];
                    for (int i = 0; i < idTypes.length; i++) {
                        options[i] = idTypes[i].name();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.id_type));
                    builder.setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialogNotActive = true;
                            dialog.dismiss();
                        }
                    });
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            String option = (String) options[item];
                            editTextType.setText(option);
                            dialogNotActive = true;
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });


        editTextNumber.setText(number);
        if (countryCode != null) {
            ccpCountryCode.setCountryForNameCode(countryCode);
        } else {
            ccpCountryCode.setAutoDetectedCountry(true);
        }
        if (identificationDocument != null) {
            editTextIdentificationDocument.setText(getText(R.string.identification_document));
            editTextIdentificationDocument.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.file_download_done), null);
        } else {
            editTextIdentificationDocument.setText(null);
            editTextIdentificationDocument.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.attachment), null);
        }

        editTextIdentificationDocument.setInputType(InputType.TYPE_NULL);
        editTextIdentificationDocument.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!pickingImage) {
                    pickImage();
                }
                return true;
            }
        });

        buttonUpdateIdentification = view.findViewById(R.id.button_update_identification);
        buttonUpdateIdentification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdType type = IdType.valueOf(editTextType.getText().toString());
                String number = editTextNumber.getText().toString();
                String identificationDocument = editTextIdentificationDocument.getText().toString();
                String countryCode = ccpCountryCode.getSelectedCountryNameCode();
                if (number.length() > 3 && identificationDocument.equals(getString(R.string.identification_document))) {
                    if (identificationResponse != null && type.equals(identificationResponse.getType()) && number.equals(identificationResponse.getNumber()) && countryCode.equals(identificationResponse.getCountryCode())) {
                        nextPage();
                    } else {
                        UpdateIdentificationRequest updateIdentificationRequest = new UpdateIdentificationRequest(type, number, countryCode);
                        pd.setMessage("Updating ...");
                        pd.show();
                        profileDetailsViewModel.hitUpdateProfileIdentificationApi(getActivity(), authentication, updateIdentificationRequest).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                            @Override
                            public void onChanged(ResponseDTO responseDTO) {
                                pd.dismiss();
                                switch (responseDTO.getStatus()) {
                                    case "success":
                                        nextPage();
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
                    if (number.length() <= 3) {
                        Snackbar.make(view, "ID Number is too short!", Snackbar.LENGTH_LONG).show();
                    } else if (!identificationDocument.equals(getString(R.string.identification_document))) {
                        Snackbar.make(view, "Upload Identification Document!", Snackbar.LENGTH_LONG).show();
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
                            editTextIdentificationDocument.setText(getText(R.string.identification_document));
                            editTextIdentificationDocument.setCompoundDrawablesWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getContext(), R.drawable.file_download_done), null);
                            Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });

    }

    public void nextPage() {
        EditProfileAddressFragment editProfileAddressFragment = new EditProfileAddressFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, editProfileAddressFragment, EditProfileAddressFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }
}