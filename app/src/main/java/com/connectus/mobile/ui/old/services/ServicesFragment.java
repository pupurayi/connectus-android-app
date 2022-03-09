package com.connectus.mobile.ui.old.services;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.TransactionType;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.airtime.MobileTopupFragment;
import com.connectus.mobile.ui.old.check.CheckProfileFragment;
import com.connectus.mobile.ui.old.check.CheckPaymateFragment;
import com.connectus.mobile.ui.old.deposit.WireDepositFragment;
import com.connectus.mobile.ui.old.withdraw.BankWithdrawalFragment;
import com.google.gson.Gson;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ServicesFragment extends Fragment {
    private static final String TAG = ServicesFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack;

    private SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    private ServicesViewModel servicesViewModel;
    String authentication;

    ProfileDto profileDTO;

    ServicesRecyclerAdapter servicesRecyclerAdapter;
    RecyclerView recyclerViewServices;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        servicesViewModel = new ViewModelProvider(this).get(ServicesViewModel.class);
        return inflater.inflate(R.layout.fragment_services, container, false);
    }

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

        List<EmalyamiService> emalyamiServices = new LinkedList<>();
        EmalyamiService depositService = new EmalyamiService("deposit", "Deposit Funds", "Deposit via Paymate or from your Bank", R.drawable.deposit);
        EmalyamiService withdrawService = new EmalyamiService("withdraw", "Withdraw Funds", "Withdraw Cash via eMalyami Paymate", R.drawable.withdraw);
        EmalyamiService mobileTopupService = new EmalyamiService("mobile-topup", "Mobile Topup", "Recharge your phone with Airtime", R.drawable.mobile_recharge);

        emalyamiServices.add(depositService);
        emalyamiServices.add(withdrawService);
        emalyamiServices.add(mobileTopupService);

        servicesRecyclerAdapter = new ServicesRecyclerAdapter(getContext(), emalyamiServices, fragmentManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewServices = getView().findViewById(R.id.recycler_view_services);
        recyclerViewServices.setAdapter(servicesRecyclerAdapter);
        recyclerViewServices.setLayoutManager(linearLayoutManager);
    }

    public void showFragmentById(String id) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (id) {
            case "deposit":
                if (profileDTO.getPaymate() != null) {
                    final CharSequence[] options = {"Cash Deposit", "Deposit from Bank"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Choose Action");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("Cash Deposit")) {
                                if (profileDTO.getPaymate().getPaymateStatus().equals("ACTIVE")) {
                                    Bundle bundle = new Bundle();
                                    bundle.putString("type", String.valueOf(TransactionType.CASH_DEPOSIT));
                                    bundle.putString("title", "Deposit");
                                    CheckProfileFragment checkProfileFragment = new CheckProfileFragment();
                                    checkProfileFragment.setArguments(bundle);
                                    transaction.add(R.id.container, checkProfileFragment, CheckProfileFragment.class.getSimpleName());
                                    transaction.addToBackStack(TAG);
                                    transaction.commit();
                                } else {
                                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                                    alertBuilder.setTitle("ConnectUs Alert")
                                            .setMessage(String.format("Your Paymate profile is  %s, please contact eMalyami Support for Assistance!", profileDTO.getPaymate().getPaymateStatus()))
                                            .show();
                                }
                            } else if (options[item].equals("Deposit from Bank")) {
                                WireDepositFragment wireDepositFragment = new WireDepositFragment();
                                transaction.add(R.id.container, wireDepositFragment, WireDepositFragment.class.getSimpleName());
                                transaction.addToBackStack(TAG);
                                transaction.commit();
                            }
                        }
                    });
                    builder.show();
                } else {
                    WireDepositFragment wireDepositFragment = new WireDepositFragment();
                    transaction.add(R.id.container, wireDepositFragment, WireDepositFragment.class.getSimpleName());
                    transaction.commit();
                    // Deposit from Here
                }
                break;
            case "withdraw":

                final CharSequence[] options = {"Cash Withdrawal", "Wallet to Bank"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Choose Action");
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Cash Withdrawal")) {
                            Bundle bundle = new Bundle();
                            bundle.putString("type", String.valueOf(TransactionType.CASH_WITHDRAWAL));
                            bundle.putString("title", "Withdraw");
                            CheckPaymateFragment checkPaymateFragment = new CheckPaymateFragment();
                            checkPaymateFragment.setArguments(bundle);
                            transaction.add(R.id.container, checkPaymateFragment, CheckPaymateFragment.class.getSimpleName());
                            transaction.addToBackStack(TAG);
                            transaction.commit();
                        } else if (options[item].equals("Wallet to Bank")) {

                            pd.setMessage("Preparing ...");
                            pd.show();
                            servicesViewModel.hitGetBanksApi(authentication, "ZA").observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                                @Override
                                public void onChanged(ResponseDTO responseDTO) {
                                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                                    switch (responseDTO.getStatus()) {
                                        case "success":
                                            BankWithdrawalFragment bankFragment = new BankWithdrawalFragment();
                                            Bundle bundle = new Bundle();
                                            bundle.putString("banks", new Gson().toJson(responseDTO.getData()));
                                            bankFragment.setArguments(bundle);
                                            transaction.add(R.id.container, bankFragment, BankWithdrawalFragment.class.getSimpleName());
                                            transaction.commit();
                                            break;
                                        case "failed":
                                        case "error":
                                            Toast.makeText(getContext(), responseDTO.getMessage(), Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                    pd.dismiss();
                                }
                            });
                        }
                    }
                });
                builder.show();
                break;
            case "mobile-topup":
                MobileTopupFragment mobileTopupFragment = new MobileTopupFragment();
                transaction.replace(R.id.container, mobileTopupFragment, MobileTopupFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
                break;
        }

    }
}