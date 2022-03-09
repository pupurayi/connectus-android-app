package com.connectus.mobile.ui.old.check;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import android.widget.TextView;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.CheckProfileDTO;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.TransactionType;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.deposit.CashDepositFragment;
import com.connectus.mobile.ui.old.transfer.TransferFragment;
import com.hbb20.CountryCodePicker;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import static com.connectus.mobile.common.Validate.isValidMobileNumber;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CheckProfileFragment extends Fragment {
    private static final String TAG = CheckProfileFragment.class.getSimpleName();

    private static final int REQUEST_CODE_QR_SCAN = 101, REQUEST_CAMERA_PERMISSION = 2;

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewScanQRCode;
    TextView textViewTitle;
    CountryCodePicker ccp;
    EditText editTextPhoneNumber;
    Button buttonCheckProfile;

    private String title, countryCode;
    private TransactionType transactionType;
    private SharedPreferencesManager sharedPreferencesManager;
    private CheckViewModel checkViewModel;

    String authentication;
    ProfileDto profileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        checkViewModel = new ViewModelProvider(this).get(CheckViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            title = arguments.getString("title");
            transactionType = TransactionType.valueOf(arguments.getString("type"));
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_check_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();

        profileDTO = sharedPreferencesManager.getProfile();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        imageViewProfileAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        textViewTitle = view.findViewById(R.id.text_view_title);
        textViewTitle.setText(title);

        imageViewScanQRCode = view.findViewById(R.id.image_view_scan_q_r_code);
        imageViewScanQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    Intent i = new Intent(getContext(), QrCodeActivity.class);
                    startActivityForResult(i, REQUEST_CODE_QR_SCAN);
                }
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
                    Common.showCameraPermissionRationale(getActivity(), 101);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                }
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
        buttonCheckProfile = view.findViewById(R.id.button_proceed);
        buttonCheckProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                countryCode = (countryCode == null || countryCode.isEmpty()) ? ccp.getSelectedCountryCode() : countryCode;
                long phoneNumber = 0;
                try {
                    phoneNumber = Long.parseLong(editTextPhoneNumber.getText().toString().trim());
                } catch (Exception ignored) {

                }
                String msisdn = String.format("+%s%s", countryCode, phoneNumber);
                boolean isPhoneNumberValid = isValidMobileNumber(msisdn);
                if (isPhoneNumberValid) {
                    checkProfile(msisdn);
                } else {
                    Toast.makeText(getContext(), "Enter a valid phone number!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void checkProfile(String msisdn) {
        if (!profileDTO.getMsisdn().equals(msisdn)) {
            pd.setMessage("Checking Profile ...");
            pd.show();
            checkViewModel.hitCheckProfileApi(authentication, msisdn).observe(getViewLifecycleOwner(), new Observer<ResponseDTO<CheckProfileDTO>>() {
                @Override
                public void onChanged(ResponseDTO<CheckProfileDTO> responseDTO) {
                    pd.dismiss();
                    switch (responseDTO.getStatus()) {
                        case "success":
                            CheckProfileDTO checkProfileDTO = responseDTO.getData();
                            Bundle bundle = new Bundle();
                            bundle.putString("userId", checkProfileDTO.getId().toString());
                            bundle.putString("msisdn", checkProfileDTO.getMsisdn());
                            bundle.putString("userStatus", checkProfileDTO.getUserStatus());
                            bundle.putBoolean("avatarAvailable", checkProfileDTO.isAvatarAvailable());
                            bundle.putString("profileId", checkProfileDTO.getProfileId().toString());
                            bundle.putString("firstName", checkProfileDTO.getFirstName());
                            bundle.putString("lastName", checkProfileDTO.getLastName());
                            bundle.putString("profileStatus", checkProfileDTO.getUserStatus());
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            switch (transactionType) {
                                case INTERNAL_TRANSFER:
                                    TransferFragment transferFragment = new TransferFragment();
                                    transferFragment.setArguments(bundle);
                                    transaction.replace(R.id.container, transferFragment, TransferFragment.class.getSimpleName());
                                    transaction.addToBackStack(TAG);
                                    transaction.commit();
                                    break;
                                case CASH_DEPOSIT:
                                    CashDepositFragment cashDepositFragment = new CashDepositFragment();
                                    cashDepositFragment.setArguments(bundle);
                                    transaction.replace(R.id.container, cashDepositFragment, CashDepositFragment.class.getSimpleName());
                                    transaction.addToBackStack(TAG);
                                    transaction.commit();
                                    break;
                            }
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
        } else {
            Toast.makeText(getContext(), "Cannot transact with yourself!", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;

        }
        if (requestCode == REQUEST_CODE_QR_SCAN && resultCode == Activity.RESULT_OK) {
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            JSONObject obj = null;
            try {
                obj = new JSONObject(result);
                String msisdn = String.valueOf(obj.get("msisdn"));
                if (msisdn != null) {
                    checkProfile(msisdn);
                } else {
                    Toast.makeText(getContext(), "QR Code is not valid!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "QR Code is not valid!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}