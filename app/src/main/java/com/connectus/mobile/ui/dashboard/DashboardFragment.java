package com.connectus.mobile.ui.dashboard;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.Transaction;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.profile.ProfileDetailsFragment;
import com.connectus.mobile.ui.profile.ProfileDetailsViewModel;
import com.connectus.mobile.ui.initial.check.CheckFragment;
import com.connectus.mobile.ui.qrcode.QRCodeFragment;
import com.connectus.mobile.ui.old.settings.SettingsFragment;
import com.connectus.mobile.ui.old.transaction.TransactionRecyclerAdapter;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = DashboardFragment.class.getSimpleName();

    DrawerLayout drawerLayout;

    TextView textViewFullName, textViewNavHeaderFullName, textViewMsisdn, textViewNavHeaderMsisdn, textViewProfileBalance;
    ImageView imageViewProfileAvatar, imageViewNavHeaderAvatar, imageViewMenu, imageViewRefresh, imageViewQRCode;
    ImageView imageViewInternalTransfer, imageViewCashWithdraw, imageViewPay;
    Button buttonMoreServices;
    RecyclerView recyclerViewTransactions;
    ProgressDialog pd;

    //Freshchat.showFAQs(getApplicationContext());


    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;
    ProfileDto profileDTO;
    String authentication;
    private DashboardViewModel dashboardViewModel;
    private ProfileDetailsViewModel profileDetailsViewModel;
    TransactionRecyclerAdapter transactionRecyclerAdapter;
    List<Transaction> transactions;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();
        profileDTO = sharedPreferencesManager.getProfile();

        drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = getView().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);

        Menu navMenu = navigationView.getMenu();
        MenuItem becomeAnPaymateItem = navMenu.findItem(R.id.nav_new_goods_and_services);

        if (profileDTO.getPaymate() == null) {
            becomeAnPaymateItem.setVisible(true);
        } else {
            becomeAnPaymateItem.setVisible(false);
        }

        imageViewNavHeaderAvatar = navHeaderView.findViewById(R.id.image_view_nav_header_avatar);
        textViewNavHeaderFullName = navHeaderView.findViewById(R.id.text_view_nav_header_full_name);
        textViewNavHeaderMsisdn = navHeaderView.findViewById(R.id.text_view_nav_header_msisdn);

        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewMsisdn = view.findViewById(R.id.text_view_msisdn);
//        textViewProfileBalance = view.findViewById(R.id.text_view_profile_balance);

        long lastSync = sharedPreferencesManager.getLastSync();
        long now = new Date().getTime();
        // 1 Second = 1000 Milliseconds, 300000 = 5 Minutes
        if (now - lastSync >= 300000) {
            syncProfileAndDisplay();
        }

        DbHandler dbHandler = new DbHandler(getContext());
        transactions = dbHandler.getTransactions();
        transactions = sortTransactions(transactions);
        transactionRecyclerAdapter = new TransactionRecyclerAdapter(getContext(), transactions);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewTransactions = getView().findViewById(R.id.recycler_view_transactions);
        recyclerViewTransactions.setAdapter(transactionRecyclerAdapter);
        recyclerViewTransactions.setLayoutManager(linearLayoutManager);

        fetchTransactions();

        imageViewMenu = view.findViewById(R.id.image_view_menu);
        imageViewMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        imageViewProfileAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileDetailsFragment();
            }
        });

//        imageViewPay = view.findViewById(R.id.image_view_pay);
//        imageViewPay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("type", String.valueOf(TransactionType.GOODS_AND_SERVICES));
//                bundle.putString("title", "Payment");
//                CheckPaymateFragment checkPaymateFragment = new CheckPaymateFragment();
//                checkPaymateFragment.setArguments(bundle);
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.container, checkPaymateFragment, CheckPaymateFragment.class.getSimpleName());
//                transaction.addToBackStack(TAG);
//                transaction.commit();
//            }
//        });
//
//        imageViewCashWithdraw = view.findViewById(R.id.image_view_cash_withdraw);
//        imageViewCashWithdraw.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("type", String.valueOf(TransactionType.CASH_WITHDRAWAL));
//                bundle.putString("title", "Withdraw");
//                CheckPaymateFragment checkPaymateFragment = new CheckPaymateFragment();
//                checkPaymateFragment.setArguments(bundle);
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.container, checkPaymateFragment, CheckPaymateFragment.class.getSimpleName());
//                transaction.addToBackStack(TAG);
//                transaction.commit();
//            }
//        });
//
//        imageViewInternalTransfer = view.findViewById(R.id.image_view_internal_transfer);
//        imageViewInternalTransfer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Bundle bundle = new Bundle();
//                bundle.putString("type", String.valueOf(TransactionType.INTERNAL_TRANSFER));
//                bundle.putString("title", "Transfer");
//                CheckProfileFragment checkProfileFragment = new CheckProfileFragment();
//                checkProfileFragment.setArguments(bundle);
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.container, checkProfileFragment, CheckProfileFragment.class.getSimpleName());
//                transaction.addToBackStack(TAG);
//                transaction.commit();
//            }
//        });
//
//        buttonMoreServices = view.findViewById(R.id.button_more_services);
//        buttonMoreServices.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ServicesFragment servicesFragment = new ServicesFragment();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.container, servicesFragment, ServicesFragment.class.getSimpleName());
//                transaction.addToBackStack(TAG);
//                transaction.commit();
//            }
//        });


        imageViewQRCode = view.findViewById(R.id.image_view_qr_code_options);
        imageViewQRCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QRCodeFragment qrCodeFragment = new QRCodeFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, qrCodeFragment, QRCodeFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();

            }
        });


        imageViewRefresh = view.findViewById(R.id.image_view_refresh);
        imageViewRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                syncProfileAndDisplay();
                fetchTransactions();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        profileDTO = sharedPreferencesManager.getProfile();
        syncDisplay(profileDTO);
    }

    public void showProfileDetailsFragment() {
        ProfileDetailsFragment profileDetailsFragment = new ProfileDetailsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, profileDetailsFragment, ProfileDetailsFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }

    public void fetchTransactions() {
        dashboardViewModel.hitTransactionHistoryApi(getContext(), authentication).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                switch (responseDTO.getStatus()) {
                    case "success":
                        loadTransactionRecyclerView();
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
                pd.dismiss();
            }
        });
    }

    public List<Transaction> sortTransactions(List<Transaction> transactions) {
        Collections.sort(transactions, (t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));
        return transactions;
    }

    public void loadTransactionRecyclerView() {
        // TODO reload recycler
        DbHandler dbHandler = new DbHandler(getContext());
        List<Transaction> updatedTransactions = dbHandler.getTransactions();
        List<Transaction> sortedTransactions = sortTransactions(updatedTransactions);
        transactions.clear();
        transactions.addAll(sortedTransactions);
        transactionRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        switch (item.getItemId()) {
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
            case R.id.nav_profile:
                showProfileDetailsFragment();
                break;
            case R.id.nav_new_goods_and_services:
                com.connectus.mobile.ui.goods_and_services.GoodsAndServicesFragment new_goods_and_servicessFragment = new com.connectus.mobile.ui.goods_and_services.GoodsAndServicesFragment();
                transaction.add(R.id.container, new_goods_and_servicessFragment, com.connectus.mobile.ui.goods_and_services.GoodsAndServicesFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_new_new_goods_and_services:
                com.connectus.mobile.ui.goods_and_services.AddGoodsAndServicesFragment addGoodsAndServicesFragment = new com.connectus.mobile.ui.goods_and_services.AddGoodsAndServicesFragment();
                transaction.add(R.id.container, addGoodsAndServicesFragment, com.connectus.mobile.ui.goods_and_services.AddGoodsAndServicesFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                transaction.add(R.id.container, settingsFragment, SettingsFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_logout:
                logout();
                break;
            default:
                break;
        }
        transaction.commit();
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    public void syncProfileAndDisplay() {
        pd.setMessage("Please Wait ...");
        pd.show();

        profileDetailsViewModel.hitGetProfileApi(getActivity(), authentication).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                pd.dismiss();
                switch (responseDTO.getStatus()) {
                    case "success":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        profileDTO = sharedPreferencesManager.getProfile();
                        syncDisplay(profileDTO);
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            }
        });
    }

    public void syncDisplay(ProfileDto profileDTO) {
        String firstName = profileDTO.getFirstName();
        String fullName = firstName + " " + profileDTO.getLastName();
        String msisdn = (profileDTO.getPaymate() != null && profileDTO.getPaymate().getPaymateStatus().equals("ACTIVE")) ? "Paymate Code: " + profileDTO.getPaymate().getPaymateCode() : profileDTO.getMsisdn();
        DbHandler dbHandler = new DbHandler(getContext());
        Set<BalanceDTO> balances = dbHandler.getBalances();
        String profileBalance = null;
        for (BalanceDTO balance : balances) {
            if (balance.getCurrency().equals("ZAR")) {
                profileBalance = String.format("%s %.2f", balance.getCurrency(), balance.getAmount());
            }
        }
        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewProfileAvatar, profileDTO.getId());
        Common.loadAvatar(profileDTO.isAvatarAvailable(), imageViewNavHeaderAvatar, profileDTO.getId());
        textViewFullName.setText(fullName);
        textViewNavHeaderFullName.setText(fullName);
        textViewMsisdn.setText(msisdn);
        textViewNavHeaderMsisdn.setText(msisdn);
    }

    public void logout() {
        pd.setMessage("Signing Out ...");
        pd.show();
        Common.clearSessionData(sharedPreferencesManager, getContext());
        CheckFragment checkFragment = new CheckFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, checkFragment, CheckFragment.class.getSimpleName());
        transaction.commit();
        pd.dismiss();
    }
}
    