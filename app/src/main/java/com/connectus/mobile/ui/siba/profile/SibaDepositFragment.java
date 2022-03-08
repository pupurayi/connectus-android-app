package com.connectus.mobile.ui.siba.profile;

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
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.SibaDepositDTO;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.siba.SibaViewModel;
import com.connectus.mobile.ui.transaction.TransactionStatusFragment;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SibaDepositFragment extends Fragment {
    private static final String TAG = SibaDepositFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;
    TextView textViewSibaProfileSubject;
    EditText editTextAmount, editTextContributionType;
    Button buttonDeposit;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private SibaViewModel sibaViewModel;

    String sibaProfileId;
    SibaProfile sibaProfile;
    ProfileDto profileDTO;
    String authentication;
    boolean dialogNotActive = true;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sibaViewModel = new ViewModelProvider(this).get(SibaViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            sibaProfileId = arguments.getString("sibaProfileId");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siba_deposit, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sibaProfileId == null) {
            getActivity().onBackPressed();
        }
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

        fetchSibaProfile();

        textViewSibaProfileSubject = view.findViewById(R.id.text_view_siba_profile_subject);
        textViewSibaProfileSubject.setText(sibaProfile.getSubject());

        editTextAmount = view.findViewById(R.id.edit_text_amount);

        editTextContributionType = view.findViewById(R.id.edit_text_contribution_type);
        editTextContributionType.setInputType(InputType.TYPE_NULL);
        String contributionType = "GENERAL";
        editTextContributionType.setText(contributionType);
        editTextContributionType.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (dialogNotActive) {
                    dialogNotActive = false;
                    CharSequence[] options = new CharSequence[]{"GENERAL"};
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
                            editTextContributionType.setText(option);
                            dialogNotActive = true;
                        }
                    });
                    builder.show();
                }
                return false;
            }
        });

        buttonDeposit = view.findViewById(R.id.button_deposit_to_siba);
        buttonDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                double amount = !editTextAmount.getText().toString().isEmpty() ? Double.parseDouble(editTextAmount.getText().toString()) : 0.00;
                String contributionType = editTextContributionType.getText().toString();
                if (amount > 0) {
                    buttonDeposit.setEnabled(false);
                    pd.setMessage("Processing ...");
                    pd.show();
                    sibaViewModel.hitDepositToSibaApi(getContext(), authentication, profileDTO.getProfileId(), sibaProfile.getId(), new SibaDepositDTO("ZAR", amount, contributionType)).observe(getViewLifecycleOwner(), responseDTO -> {
                        Bundle bundle = new Bundle();
                        TransactionStatusFragment transactionStatusFragment = new TransactionStatusFragment();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        switch (responseDTO.getStatus()) {
                            case "success":
                                bundle.putBoolean("success", true);
                                bundle.putString("transactionStatusTitle", "Deposit Successful");
                                bundle.putString("transactionStatusMessage", responseDTO.getMessage());
                                break;
                            case "failed":
                            case "error":
                                bundle.putBoolean("success", false);
                                bundle.putString("transactionStatusTitle", "Deposit Failed");
                                bundle.putString("transactionStatusMessage", responseDTO.getMessage());
                                break;
                        }
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

    public void fetchSibaProfile() {
        sibaProfile = Common.getSibaProfileById(getContext(), sibaProfileId);
        if (sibaProfile == null) {
            getActivity().onBackPressed();
        }
    }
}