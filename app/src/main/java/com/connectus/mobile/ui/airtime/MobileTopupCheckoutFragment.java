package com.connectus.mobile.ui.airtime;

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

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.airtime.DenominationType;
import com.connectus.mobile.api.dto.airtime.OperatorResponse;
import com.connectus.mobile.api.dto.transaction.AdditionalDataTags;
import com.connectus.mobile.api.dto.transaction.ResponseCodes;
import com.connectus.mobile.api.dto.transaction.TransactionDto;
import com.connectus.mobile.api.dto.transaction.TransactionUtil;
import com.connectus.mobile.api.dto.transaction.TransactionViewModel;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.transaction.TransactionStatusFragment;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Locale;

import static com.connectus.mobile.utils.LocalStrings.RANDS_CURRENCY_CODE;
import static com.connectus.mobile.utils.LocalStrings.REQUEST;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MobileTopupCheckoutFragment extends Fragment {
    private static final String TAG = MobileTopupCheckoutFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar, imageViewMobileProvider;
    TextView textViewMobileProvider, textViewPhoneNumber, textViewReceiverGets;
    EditText editTextTransactionAmount;
    Button buttonRecharge;


    private SharedPreferencesManager sharedPreferencesManager;
    private TransactionViewModel transactionViewModel;
    boolean dialogNotActive = true;

    String authentication, phoneNumber;
    ProfileDTO profileDTO;
    OperatorResponse operatorResponse;
    FragmentManager fragmentManager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        transactionViewModel = new ViewModelProvider(this).get(TransactionViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            String json = arguments.getString("operatorResponse");
            phoneNumber = arguments.getString("phoneNumber");
            operatorResponse = new Gson().fromJson(json, OperatorResponse.class);
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mobile_topup_checkout, container, false);
    }

    @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
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

        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getUserId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        imageViewMobileProvider = view.findViewById(R.id.image_view_mobile_provider);
        if (operatorResponse.getLogoUrls().size() > 0) {
            Picasso.get()
                    .load(operatorResponse.getLogoUrls().get(0))
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewMobileProvider);
        }


        textViewMobileProvider = view.findViewById(R.id.text_view_mobile_provider);
        textViewMobileProvider.setText(operatorResponse.getName());
        textViewPhoneNumber = view.findViewById(R.id.text_view_mobile_number);
        textViewPhoneNumber.setText(phoneNumber);
        textViewReceiverGets = view.findViewById(R.id.text_view_receiver_gets);
        editTextTransactionAmount = view.findViewById(R.id.edit_text_transaction_amount);
        BigDecimal transactionAmount = operatorResponse.getMostPopularAmount();
        @SuppressLint("DefaultLocale") String formattedAmount = String.format(Locale.ENGLISH, "%.2f", transactionAmount);
        editTextTransactionAmount.setText(formattedAmount);
        convertAndDisplay(transactionAmount);

        editTextTransactionAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                BigDecimal amount = s.length() != 0 ? new BigDecimal(String.valueOf(s)) : BigDecimal.ZERO;
                convertAndDisplay(amount);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        if (operatorResponse.getDenominationType().equals(DenominationType.FIXED)) {
            editTextTransactionAmount.setInputType(InputType.TYPE_NULL);
            editTextTransactionAmount.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (dialogNotActive) {
                        dialogNotActive = false;

                        ArrayList<String> fixedAmounts = operatorResponse.getFixedAmounts();

                        CharSequence[] options = new CharSequence[fixedAmounts.size()];
                        for (int i = 0; i < fixedAmounts.size(); i++) {
                            options[i] = operatorResponse.getSenderCurrencySymbol() + fixedAmounts.get(i);
                        }
                        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                        builder.setTitle("Choose Amount");
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
                                editTextTransactionAmount.setText(option.replace(operatorResponse.getSenderCurrencySymbol(), ""));
                                dialogNotActive = true;
                            }
                        });
                        builder.show();
                    }
                    return false;
                }
            });
        } else {
            editTextTransactionAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        buttonRecharge = view.findViewById(R.id.button_recharge);
        buttonRecharge.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                BigDecimal transactionAmount = !editTextTransactionAmount.getText().toString().isEmpty() ? new BigDecimal(editTextTransactionAmount.getText().toString()) : BigDecimal.ZERO;

                boolean valid = false;
                if (operatorResponse.getDenominationType().equals(DenominationType.RANGE)) {
                    if (transactionAmount.compareTo(operatorResponse.getMinAmount()) >= 0 && operatorResponse.getMinAmount().compareTo(operatorResponse.getMaxAmount()) <= 0) {
                        valid = true;
                    }
                } else {
                    valid = true;
                }
                if (valid) {
                    buttonRecharge.setEnabled(false);
                    pd.setMessage("Processing ...");
                    pd.show();

                    TransactionDto transactionDto = TransactionUtil.prepareTransaction(sharedPreferencesManager, REQUEST, "BILL", "1003");
                    transactionDto.setCurrencyCode(RANDS_CURRENCY_CODE);
                    transactionDto.setAmount(transactionAmount);
                    transactionDto.setPayee("EMALYAMI");
                    transactionDto.getAdditionalData().put(AdditionalDataTags.PAYEE_PRODUCT, "AIRTIME");
                    transactionDto.getAdditionalData().put(AdditionalDataTags.BILL_ACCOUNT, phoneNumber);

                    transactionViewModel.hitProcessTransactionApi(getContext(), authentication, transactionDto).observe(getViewLifecycleOwner(), responseTransactionDto -> {
                        Bundle bundle = new Bundle();
                        TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        if (responseTransactionDto.getResponseCode().equals(ResponseCodes.APPROVED)) {
                            bundle.putBoolean("success", true);
                            bundle.putString("transactionStatusTitle", "Mobile Topup  Successful");
                        } else {
                            bundle.putBoolean("success", false);
                            bundle.putString("transactionStatusTitle", "Mobile Topup  Failed");
                        }
                        bundle.putString("transactionStatusMessage", transactionDto.getResponseDescription());
                        pd.dismiss();
                        transactionStatusFragment.setArguments(bundle);
                        transaction.replace(R.id.container, transactionStatusFragment, TransactionStatusFragment.class.getSimpleName());
                        transaction.commit();
                    });
                } else {
                    Toast.makeText(getContext(), String.format("Amount should be between %s %.2f and %s %.2f", operatorResponse.getSenderCurrencySymbol(), operatorResponse.getMinAmount(), operatorResponse.getSenderCurrencySymbol(), operatorResponse.getMaxAmount()), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void convertAndDisplay(BigDecimal amount) {
        BigDecimal convertedAmount = amount.multiply(operatorResponse.getFx().getRate());
        @SuppressLint("DefaultLocale") String receiverGets = String.format("Receiver gets %s %.2f", operatorResponse.getFx().getCurrencyCode(), convertedAmount);
        textViewReceiverGets.setText(receiverGets);
    }
}