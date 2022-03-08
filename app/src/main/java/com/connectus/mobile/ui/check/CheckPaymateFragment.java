package com.connectus.mobile.ui.check;

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
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.CheckPaymateDTO;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.TransactionType;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.payment.PaymentFragment;
import com.connectus.mobile.ui.withdraw.CashWithdrawalFragment;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CheckPaymateFragment extends Fragment {
    private static final String TAG = CheckPaymateFragment.class.getSimpleName();

    private static final int REQUEST_CODE_QR_SCAN = 101, REQUEST_CAMERA_PERMISSION = 2;


    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewScanQRCode;
    TextView textViewTitle;
    EditText editTextPaymateCode;
    Button buttonCheckPaymate;

    private String title, authentication;
    private TransactionType transactionType;
    private SharedPreferencesManager sharedPreferencesManager;
    private CheckViewModel checkViewModel;
    ProfileDTO profileDTO;


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
        return inflater.inflate(R.layout.fragment_check_paymate, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();

        profileDTO = sharedPreferencesManager.getProfile();

        textViewTitle = view.findViewById(R.id.text_view_title);
        textViewTitle.setText(title);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

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

        imageViewProfileAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/core/api/v1/user/profile-picture/" + profileDTO.getUserId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        editTextPaymateCode = view.findViewById(R.id.ca_edit_text_paymate_code);
        buttonCheckPaymate = view.findViewById(R.id.ca_button_check_paymate);
        buttonCheckPaymate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                long paymateCode = editTextPaymateCode.getText() != null && !editTextPaymateCode.getText().toString().isEmpty() ? Long.parseLong(editTextPaymateCode.getText().toString()) : 0;
                if (paymateCode != 0) {
                    checkPaymate(paymateCode);
                } else {
                    Toast.makeText(getContext(), "Enter Paymate Code!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }


    public void checkPaymate(long paymateCode) {
        if (profileDTO.getPaymate() != null && profileDTO.getPaymate().getPaymateCode() == paymateCode) {
            Toast.makeText(getContext(), "Cannot transact with yourself!", Toast.LENGTH_LONG).show();
        } else {
            pd.setMessage("Checking Paymate ...");
            pd.show();
            checkViewModel.hitCheckPaymateApi(authentication, paymateCode).observe(getViewLifecycleOwner(), new Observer<ResponseDTO<CheckPaymateDTO>>() {
                @Override
                public void onChanged(ResponseDTO<CheckPaymateDTO> responseDTO) {
                    pd.dismiss();
                    switch (responseDTO.getStatus()) {
                        case "success":
                            CheckPaymateDTO checkPaymateDTO = responseDTO.getData();
                            Bundle bundle = new Bundle();
                            bundle.putLong("paymateCode", checkPaymateDTO.getPaymateCode());
                            bundle.putString("paymateName", checkPaymateDTO.getPaymateName());
                            bundle.putBoolean("avatarAvailable", checkPaymateDTO.isAvatarAvailable());
                            bundle.putString("userId", checkPaymateDTO.getUserId());
                            bundle.putString("paymateStatus", checkPaymateDTO.getPaymateStatus());

                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

                            switch (transactionType) {
                                case CASH_WITHDRAWAL:
                                    CashWithdrawalFragment cashWithdrawalFragment = new CashWithdrawalFragment();
                                    cashWithdrawalFragment.setArguments(bundle);
                                    transaction.replace(R.id.container, cashWithdrawalFragment, CashWithdrawalFragment.class.getSimpleName());
                                    transaction.addToBackStack(TAG);
                                    transaction.commit();
                                    break;
                                case GOODS_AND_SERVICES:
                                    PaymentFragment paymentFragment = new PaymentFragment();
                                    paymentFragment.setArguments(bundle);
                                    transaction.replace(R.id.container, paymentFragment, PaymentFragment.class.getSimpleName());
                                    transaction.addToBackStack(TAG);
                                    transaction.commit();
                                    break;
                            }
                            break;
                        case "failed":
                        case "error":
                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setTitle("eMalyami Alert")
                                    .setMessage(responseDTO.getMessage())
                                    .setPositiveButton(android.R.string.yes, null)
                                    .show();
                            break;
                    }
                }
            });
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
                String paymateCode = String.valueOf(obj.get("paymateCode"));
                if (paymateCode != null) {
                    checkPaymate(Long.parseLong(paymateCode));
                } else {
                    Toast.makeText(getContext(), "Not a valid paymate QR Code!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "QR Code is not valid!", Toast.LENGTH_SHORT).show();
            }
        }
    }
}