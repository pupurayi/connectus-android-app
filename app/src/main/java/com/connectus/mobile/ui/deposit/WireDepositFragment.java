package com.connectus.mobile.ui.deposit;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.payfast.CreatePayFastToken;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.webview.WebViewFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class WireDepositFragment extends Fragment {
    private static final String TAG = WireDepositFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;
    EditText editTextAmount;
    Button buttonDeposit;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private String authorization;
    private DepositViewModel depositViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        depositViewModel = new ViewModelProvider(this).get(DepositViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wire_deposit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authorization = sharedPreferencesManager.getAuthenticationToken();

        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getId());

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        editTextAmount = view.findViewById(R.id.edit_text_amount);
        buttonDeposit = view.findViewById(R.id.button_deposit);
        buttonDeposit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                Common.hideSoftKeyboard(getActivity());
                double amount = !editTextAmount.getText().toString().isEmpty() ? Double.parseDouble(editTextAmount.getText().toString()) : 0.00;
                if (amount > 0) {
                    buttonDeposit.setEnabled(false);
                    pd.setMessage("Please Wait ...");
                    pd.show();
                    depositViewModel.hitCreateTokenApi(authorization, new CreatePayFastToken("ZAR", amount, "eMalyami Deposit", String.format("Deposit of $%.2f to eMalyami Profile", amount))).observe(getViewLifecycleOwner(), responseDTO -> {
                        pd.dismiss();
                        switch (responseDTO.getStatus()) {
                            case "success":
                                Bundle bundle = new Bundle();
                                bundle.putString("parentFragment", TAG);
                                bundle.putString("link", responseDTO.getData().getRedirectUrl());
                                Map<String, String> headers = new HashMap<>();
                                headers.put("Authorization", authorization);
                                bundle.putString("headers", new Gson().toJson(headers));
                                WebViewFragment webViewFragment = new WebViewFragment();
                                webViewFragment.setArguments(bundle);

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.add(R.id.container, webViewFragment, WebViewFragment.class.getSimpleName());
                                transaction.addToBackStack(TAG);
                                transaction.commit();
                                editTextAmount.setText(null);
                                buttonDeposit.setEnabled(true);
                                break;
                            case "failed":
                            case "error":
                                buttonDeposit.setEnabled(true);
                                Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    });
                } else {
                    Snackbar.make(getView(), "Amount should be greater than zero!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}