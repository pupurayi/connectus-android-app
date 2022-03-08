package com.connectus.mobile.ui.paymate;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.PaymateApplication;
import com.connectus.mobile.api.dto.PaymateBusinessLocation;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PaymateApplicationFragment extends Fragment {
    private static final String TAG = PaymateApplicationFragment.class.getSimpleName();


    private static final int IMAGE_PICKER_REQUEST = 100,
            MAP_BUTTON_REQUEST_CODE = 200, ADDRESS_PICKER_REQUEST = 300;

    ImageView imageViewBack;
    ProgressDialog pd;
    CardView cardViewSelfie, cardViewCertifiedId, cardViewBusinessLicense, cardViewTaxClearance, cardViewAcademicCertificate, cardViewRecommendationLetter, cardViewLocation;
    String authentication, documentType;
    SharedPreferencesManager sharedPreferencesManager;
    PaymateApplicationViewModel paymateApplicationViewModel;

    FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        paymateApplicationViewModel = new ViewModelProvider(this).get(PaymateApplicationViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_paymate_application, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());

        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        ProfileDto profileDTO = sharedPreferencesManager.getProfile();
        authentication = sharedPreferencesManager.getAuthenticationToken();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        cardViewSelfie = view.findViewById(R.id.card_view_selfie);
        cardViewCertifiedId = view.findViewById(R.id.card_view_certified_id);
        cardViewBusinessLicense = view.findViewById(R.id.card_view_business_license);
        cardViewTaxClearance = view.findViewById(R.id.card_view_tax_clearance);
        cardViewAcademicCertificate = view.findViewById(R.id.card_view_academic_certificate);
        cardViewRecommendationLetter = view.findViewById(R.id.card_view_recommendation_letter);
        cardViewLocation = view.findViewById(R.id.card_view_location);

        paymateApplicationViewModel.hitGetPaymateApplicationApi(authentication).observe(getViewLifecycleOwner(), new Observer<ResponseDTO<PaymateApplication>>() {
            @Override
            public void onChanged(ResponseDTO<PaymateApplication> responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        syncPaymateApplication(responseDTO.getData());
                        break;
                    case "failed":
                    case "error":
                        Log.d(TAG, "onChanged: " + responseDTO.getMessage());
                        break;
                }
            }
        });

        cardViewSelfie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage("SELFIE");
            }
        });
        cardViewCertifiedId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage("CERTIFIED_ID");
            }
        });
        cardViewBusinessLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage("BUSINESS_LICENSE");
            }
        });
        cardViewTaxClearance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage("TAX_CLEARANCE");
            }
        });
        cardViewAcademicCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage("ACADEMIC_CERTIFICATE");
            }
        });
        cardViewRecommendationLetter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage("RECOMMENDATION_LETTER");
            }
        });
        cardViewLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), LocationPickerActivity.class);
                startActivityForResult(i, ADDRESS_PICKER_REQUEST);
            }
        });
    }

    public void pickImage(String documentType) {
        this.documentType = documentType;
        ImagePicker.with(this)
                .crop()//Crop image(Optional), Check Customization for more option
                .compress(1024)            //Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)
                .start(IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                uploadDocument(sourceUri);
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
        if (requestCode == ADDRESS_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    // String address = data.getStringExtra(MapUtility.ADDRESS);
                    double currentLatitude = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    double currentLongitude = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    Bundle completeAddress = data.getBundleExtra("fullAddress");
                    /* data in completeAddress bundle
                    fulladdress, city, state, postalcode, country, addressline1, addressline2
                     */
                    pd.setMessage("Updating Location ...");
                    pd.show();
                    paymateApplicationViewModel.hitUpdatePaymateBusinessLocationApi(authentication, new PaymateBusinessLocation(currentLatitude, currentLongitude, completeAddress.getString("fulladdress"))).observe(getViewLifecycleOwner(), new Observer<ResponseDTO<PaymateApplication>>() {
                        @Override
                        public void onChanged(ResponseDTO<PaymateApplication> responseDTO) {
                            pd.dismiss();
                            switch (responseDTO.getStatus()) {
                                case "success":
                                    syncPaymateApplication(responseDTO.getData());
                                case "failed":
                                case "error":
                                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    });
                    pd.dismiss();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    public void uploadDocument(Uri uri) {
        pd.setMessage("Uploading...");
        pd.show();

        File file = new File(uri.getPath());
        String mimeType = Common.getMimeType(file.getPath());

        RequestBody requestBody = RequestBody.create(file, MediaType.parse(mimeType));
        MultipartBody.Part document = MultipartBody.Part.createFormData("document", file.getName(), requestBody);
        paymateApplicationViewModel.hitUploadPaymateApplicationDocumentApi(authentication, this.documentType, document).observe(getViewLifecycleOwner(), new Observer<ResponseDTO<PaymateApplication>>() {
            @Override
            public void onChanged(ResponseDTO<PaymateApplication> responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        syncPaymateApplication(responseDTO.getData());
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    public void syncPaymateApplication(PaymateApplication paymateApplication) {
        if (paymateApplication != null) {
            if (paymateApplication.getSelfie() != null) {
                ImageView imageViewSelfie = getView().findViewById(R.id.image_view_selfie_icon);
                imageViewSelfie.setColorFilter(Color.GREEN);
            }
            if (paymateApplication.getCertifiedId() != null) {
                ImageView imageViewCertifiedId = getView().findViewById(R.id.image_view_certified_id_icon);
                imageViewCertifiedId.setColorFilter(Color.GREEN);
            }
            if (paymateApplication.getBusinessLicense() != null) {
                ImageView imageViewBusinessLicence = getView().findViewById(R.id.image_view_business_licence_icon);
                imageViewBusinessLicence.setColorFilter(Color.GREEN);
            }
            if (paymateApplication.getTaxClearance() != null) {
                ImageView imageViewTaxClearance = getView().findViewById(R.id.image_view_tax_clearance_icon);
                imageViewTaxClearance.setColorFilter(Color.GREEN);
            }
            if (paymateApplication.getAcademicCertificate() != null) {
                ImageView imageViewAcademicCertificate = getView().findViewById(R.id.image_view_academic_certificate_icon);
                imageViewAcademicCertificate.setColorFilter(Color.GREEN);
            }
            if (paymateApplication.getRecommendationLetter() != null) {
                ImageView imageViewRecommendationLetter = getView().findViewById(R.id.image_view_recommendation_letter_icon);
                imageViewRecommendationLetter.setColorFilter(Color.GREEN);
            }
            if (paymateApplication.getLat() != 0 && paymateApplication.getLng() != 0) {
                ImageView imageViewLocation = getView().findViewById(R.id.image_view_location_icon);
                imageViewLocation.setColorFilter(Color.GREEN);
            }
        }
    }

}