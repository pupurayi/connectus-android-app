package com.connectus.mobile.ui.old.withdraw;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.BankDto;
import com.connectus.mobile.api.dto.transaction.AdditionalDataTags;
import com.connectus.mobile.api.dto.transaction.ResponseCodes;
import com.connectus.mobile.api.dto.transaction.TransactionDto;
import com.connectus.mobile.api.dto.transaction.TransactionUtil;
import com.connectus.mobile.api.dto.transaction.TransactionViewModel;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.transaction.TransactionStatusFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

import static com.connectus.mobile.utils.LocalStrings.RANDS_CURRENCY_CODE;
import static com.connectus.mobile.utils.LocalStrings.REQUEST;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BankWithdrawalFragment extends Fragment {
    private static final String TAG = BankWithdrawalFragment.class.getSimpleName();

    boolean dialogNotActive = true;

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;
    EditText editTextBankName, editTextBankAccountNumber, editTextTransactionAmount;
    Button buttonWithdraw;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private TransactionViewModel transactionViewModel;
    private List<BankDto> banks;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String temp = arguments.getString("banks");
            Type listType = new TypeToken<LinkedList<BankDto>>() {
            }.getType();
            banks = new Gson().fromJson(temp, listType);
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bank_withdrawal, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.cw_image_view_profile_avatar);
        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getId());

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        editTextBankName = view.findViewById(R.id.edit_text_bank_name);
        editTextBankName.setInputType(InputType.TYPE_NULL);
        editTextBankName.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (dialogNotActive) {
                    dialogNotActive = false;
                    CharSequence[] options = new CharSequence[banks.size()];
                    for (int i = 0; i < banks.size(); i++) {
                        options[i] = banks.get(i).getName();
                    }
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Choose Bank");
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
                            editTextBankName.setText(option);
                            dialogNotActive = true;
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });

        editTextBankAccountNumber = view.findViewById(R.id.edit_text_bank_account_number);
        editTextTransactionAmount = view.findViewById(R.id.edit_text_transaction_amount);
        buttonWithdraw = view.findViewById(R.id.button_withdraw);
        buttonWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                String bankName = editTextBankName.getText().toString();
                String bankAccountNumber = editTextBankAccountNumber.getText().toString();
                BigDecimal transactionAmount = !editTextTransactionAmount.getText().toString().isEmpty() ? new BigDecimal(editTextTransactionAmount.getText().toString()) : BigDecimal.ZERO;
                String bankCode = null;
                if (!bankName.isEmpty() && validateBankAccountNumber(bankAccountNumber) && transactionAmount.compareTo(BigDecimal.ZERO) > 0) {
                    for (BankDto bankDto : banks) {
                        if (bankDto.getName().equals(bankName)) {
                            bankCode = bankDto.getCode();
                        }
                    }
                    buttonWithdraw.setEnabled(false);
                    pd.setMessage("Processing ...");
                    pd.show();

                    TransactionDto transactionDto = TransactionUtil.prepareTransaction(sharedPreferencesManager, REQUEST, "TRANSFER", "2002");
                    transactionDto.setCurrencyCode(RANDS_CURRENCY_CODE);
                    transactionDto.setAmount(transactionAmount);
                    transactionDto.getAdditionalData().put(AdditionalDataTags.BANK_CODE, bankCode);
                    transactionDto.getAdditionalData().put(AdditionalDataTags.BANK_ACCOUNT_NUMBER, bankAccountNumber);

                    transactionViewModel.hitProcessTransactionApi(getContext(), authentication, transactionDto).observe(getViewLifecycleOwner(), responseTransactionDto -> {
                        Bundle bundle = new Bundle();
                        TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (responseTransactionDto.getResponseCode().equals(ResponseCodes.APPROVED)) {
                            bundle.putBoolean("success", true);
                            bundle.putString("transactionStatusTitle", "Bank Withdrawal  Successful");
                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("transactionStatusTitle", "Bank Withdrawal  Failed");
                        }
                        bundle.putString("transactionStatusMessage", transactionDto.getResponseDescription());
                        pd.dismiss();
                        transactionStatusFragment.setArguments(bundle);
                        transaction.replace(R.id.container, transactionStatusFragment, TransactionStatusFragment.class.getSimpleName());
                        transaction.commit();
                    });
                } else {
                    String message;
                    if (bankCode.isEmpty()) {
                        message = "Please choose bank!";
                    } else if (!validateBankAccountNumber(bankAccountNumber)) {
                        message = "Please enter valid bank account number!";
                    } else {
                        message = "Amount should be greater than zero!";
                    }
                    Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean validateBankAccountNumber(String bankAccountNumber) {
        return !bankAccountNumber.isEmpty() && bankAccountNumber.matches("\\d+");
    }
}