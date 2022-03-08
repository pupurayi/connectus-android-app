package com.connectus.mobile.ui.transfer;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.CheckProfileDTO;
import com.connectus.mobile.api.dto.transaction.AdditionalDataTags;
import com.connectus.mobile.api.dto.transaction.ResponseCodes;
import com.connectus.mobile.api.dto.transaction.TransactionDto;
import com.connectus.mobile.api.dto.transaction.TransactionDtoAccount;
import com.connectus.mobile.api.dto.transaction.TransactionUtil;
import com.connectus.mobile.api.dto.transaction.TransactionViewModel;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.transaction.TransactionStatusFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.math.BigDecimal;
import java.util.UUID;

import static com.connectus.mobile.utils.LocalStrings.RANDS_CURRENCY_CODE;
import static com.connectus.mobile.utils.LocalStrings.REQUEST;
import static com.connectus.mobile.utils.LocalStrings.WALLET;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TransferFragment extends Fragment {
    private static final String TAG = TransferFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewRecipientAvatar;
    TextView textViewDebitAccountName, textViewDebitAccountNumber;
    EditText editTextTransactionAmount, editTextCustomerReference;
    Button buttonTransfer;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private TransactionViewModel transactionViewModel;
    CheckProfileDTO checkProfileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            UUID userId = UUID.fromString(arguments.getString("userId"));
            String msisdn = arguments.getString("msisdn");
            String userStatus = arguments.getString("userStatus");
            boolean avatarAvailable = arguments.getBoolean("avatarAvailable");
            UUID profileId = UUID.fromString(arguments.getString("profileId"));
            String firstName = arguments.getString("firstName");
            String lastName = arguments.getString("lastName");
            String profileStatus = arguments.getString("profileStatus");
            checkProfileDTO = new CheckProfileDTO(userId, msisdn, userStatus, avatarAvailable, profileId, firstName, lastName, profileStatus);
            Log.d(TAG, new Gson().toJson(checkProfileDTO));
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.itf_image_view_profile_avatar);
        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getId());

        imageViewRecipientAvatar = view.findViewById(R.id.itf_image_view_recipient_avatar);
        Common.loadAvatar(checkProfileDTO.isAvatarAvailable(), imageViewRecipientAvatar, checkProfileDTO.getId());

        textViewDebitAccountName = view.findViewById(R.id.text_view_debit_account_name);
        String debitAccountHolder = checkProfileDTO.getFirstName() + " " + checkProfileDTO.getLastName();
        textViewDebitAccountName.setText(debitAccountHolder);

        textViewDebitAccountNumber = view.findViewById(R.id.text_view_debit_account_number);
        textViewDebitAccountNumber.setText(checkProfileDTO.getMsisdn());

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        editTextTransactionAmount = view.findViewById(R.id.edit_text_transaction_amount);
        editTextCustomerReference = view.findViewById(R.id.edit_text_customer_reference);
        buttonTransfer = view.findViewById(R.id.button_transfer);
        buttonTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                String debitAccountNumber = textViewDebitAccountNumber.getText().toString();
                BigDecimal transactionAmount = !editTextTransactionAmount.getText().toString().isEmpty() ?
                        new BigDecimal(editTextTransactionAmount.getText().toString()) : BigDecimal.ZERO;
                String customerReference = editTextCustomerReference.getText().toString();
                if (transactionAmount.compareTo(BigDecimal.ZERO) > 0) {
                    buttonTransfer.setEnabled(false);
                    pd.setMessage("Processing ...");
                    pd.show();

                    TransactionDto transactionDto = TransactionUtil.prepareTransaction(sharedPreferencesManager, REQUEST, "TRANSFER", "1001");
                    transactionDto.setCurrencyCode(RANDS_CURRENCY_CODE);
                    transactionDto.setAmount(transactionAmount);
                    transactionDto.setDebitAccount(new TransactionDtoAccount(WALLET, debitAccountNumber));
                    transactionDto.getAdditionalData().put(AdditionalDataTags.CUSTOMER_REFERENCE, customerReference);

                    transactionViewModel.hitProcessTransactionApi(getContext(), authentication, transactionDto).observe(getViewLifecycleOwner(), responseTransactionDto -> {
                        Bundle bundle = new Bundle();
                        TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (responseTransactionDto.getResponseCode().equals(ResponseCodes.APPROVED)) {
                            bundle.putBoolean("success", true);
                            bundle.putString("transactionStatusTitle", "Transfer Successful");
                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("transactionStatusTitle", "Transfer Failed");
                        }
                        bundle.putString("transactionStatusMessage", transactionDto.getResponseDescription());
                        pd.dismiss();
                        transactionStatusFragment.setArguments(bundle);
                        transaction.replace(R.id.container, transactionStatusFragment, TransactionStatusFragment.class.getSimpleName());
                        transaction.commit();
                    });
                } else {
                    Snackbar.make(getView(), "Amount should be greater than zero!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}