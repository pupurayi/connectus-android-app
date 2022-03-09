package com.connectus.mobile.ui.old.withdraw;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.transaction.ResponseCodes;
import com.connectus.mobile.api.dto.transaction.TransactionDto;
import com.connectus.mobile.api.dto.transaction.TransactionUtil;
import com.connectus.mobile.api.dto.transaction.TransactionViewModel;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.transaction.TransactionStatusFragment;
import com.connectus.mobile.ui.old.transfer.TransferFragment;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.UUID;

import static com.connectus.mobile.utils.LocalStrings.RANDS_CURRENCY_CODE;
import static com.connectus.mobile.utils.LocalStrings.REQUEST;

public class CashWithdrawalFragment extends Fragment {
    private static final String TAG = TransferFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewPaymateAvatar;
    TextView textViewPaymateName, textViewPaymateCode;
    EditText editTextTransactionAmount;
    Button buttonWithdraw;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private TransactionViewModel transactionViewModel;

    private String paymateName, paymateCode;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            paymateCode = arguments.getString("paymateCode");
            paymateName = arguments.getString("paymateName");
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cash_withdrawal, container, false);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.cw_image_view_profile_avatar);
        loadAvatar(profileDTO.isAvatarAvailable(), profileDTO.getId(), imageViewProfileAvatar);

        textViewPaymateName = view.findViewById(R.id.text_view_paymate_name);
        textViewPaymateName.setText(paymateName);

        textViewPaymateCode = view.findViewById(R.id.text_view_paymate_code);
        textViewPaymateCode.setText(paymateCode);

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        editTextTransactionAmount = view.findViewById(R.id.edit_text_amount);
        buttonWithdraw = view.findViewById(R.id.button_withdraw);
        buttonWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                BigDecimal transactionAmount = !editTextTransactionAmount.getText().toString().isEmpty() ? new BigDecimal(editTextTransactionAmount.getText().toString()) : BigDecimal.ZERO;
                if (transactionAmount.compareTo(BigDecimal.ZERO) > 0) {
                    buttonWithdraw.setEnabled(false);
                    pd.setMessage("Processing ...");
                    pd.show();

                    TransactionDto transactionDto = TransactionUtil.prepareTransaction(sharedPreferencesManager, REQUEST, "TRANSFER", "2001");
                    transactionDto.setCurrencyCode(RANDS_CURRENCY_CODE);
                    transactionDto.setAmount(transactionAmount);
                    transactionDto.setPaymateCode(paymateCode);

                    transactionViewModel.hitProcessTransactionApi(getContext(), authentication, transactionDto).observe(getViewLifecycleOwner(), responseTransactionDto -> {
                        Bundle bundle = new Bundle();
                        TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (responseTransactionDto.getResponseCode().equals(ResponseCodes.APPROVED)) {
                            bundle.putBoolean("success", true);
                            bundle.putString("transactionStatusTitle", "Payment Successful");
                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("transactionStatusTitle", "Payment Failed");
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

    public void loadAvatar(boolean avatarAvailable, UUID userId, ImageView imageView) {
        if (avatarAvailable) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + userId + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageView);
        }
    }
}