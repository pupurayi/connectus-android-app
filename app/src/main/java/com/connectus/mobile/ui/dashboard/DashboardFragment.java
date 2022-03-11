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
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.BalanceDTO;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.product.CreateProductFragment;
import com.connectus.mobile.ui.product.ProductDto;
import com.connectus.mobile.ui.product.ProductRecyclerAdapter;
import com.connectus.mobile.ui.product.ProductViewModel;
import com.connectus.mobile.ui.product.ProductsFragment;
import com.connectus.mobile.ui.profile.ProfileDetailsFragment;
import com.connectus.mobile.ui.profile.ProfileDetailsViewModel;
import com.connectus.mobile.ui.initial.check.CheckFragment;
import com.connectus.mobile.ui.old.settings.SettingsFragment;
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
    RecyclerView recyclerViewProducts;
    ProgressDialog pd;

    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;
    ProfileDto profileDTO;
    String authentication;
    private DashboardViewModel dashboardViewModel;
    private ProductViewModel productViewModel;

    ProductRecyclerAdapter productRecyclerAdapter;
    List<ProductDto> recommendedProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
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
        MenuItem becomeAnPaymateItem = navMenu.findItem(R.id.nav_new_products);

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
        if (now - lastSync >= 300000) {
            syncProfileAndDisplay();
        }

        DbHandler dbHandler = new DbHandler(getContext());
        recommendedProducts = dbHandler.getProducts();
        recommendedProducts = sortRecommendedProducts(recommendedProducts);
        productRecyclerAdapter = new ProductRecyclerAdapter(getContext(), recommendedProducts);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewProducts = getView().findViewById(R.id.recycler_view_products);
        recyclerViewProducts.setAdapter(productRecyclerAdapter);
        recyclerViewProducts.setLayoutManager(linearLayoutManager);

        fetchRecommendedProducts();

        imageViewMenu = view.findViewById(R.id.image_view_menu);
        imageViewMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        imageViewProfileAvatar.setOnClickListener(v -> showProfileDetailsFragment());
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

    public void fetchRecommendedProducts() {
        dashboardViewModel.hitTransactionHistoryApi(getContext(), authentication).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
            @Override
            public void onChanged(ResponseDTO responseDTO) {
                switch (responseDTO.getStatus()) {
                    case "success":
                        loadRecommendedProductsRecyclerView();
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

    public List<ProductDto> sortRecommendedProducts(List<ProductDto> recommendedProducts) {
        Collections.sort(recommendedProducts, (t1, t2) -> t2.getCreated().compareTo(t1.getCreated()));
        return recommendedProducts;
    }

    public void loadRecommendedProductsRecyclerView() {
        DbHandler dbHandler = new DbHandler(getContext());
        List<ProductDto> products = dbHandler.getProducts();
        List<ProductDto> sortedRecommendedProducts = sortRecommendedProducts(products);
        recommendedProducts.clear();
        recommendedProducts.addAll(sortedRecommendedProducts);
        productRecyclerAdapter.notifyDataSetChanged();
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
            case R.id.nav_new_products:
                ProductsFragment productsFragment = new com.connectus.mobile.ui.product.ProductsFragment();
                transaction.add(R.id.container, productsFragment, com.connectus.mobile.ui.product.ProductsFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_create_product:
                CreateProductFragment createProductFragment = new CreateProductFragment();
                transaction.add(R.id.container, createProductFragment, CreateProductFragment.class.getSimpleName());
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

        productViewModel.getProducts(getActivity(), authentication).observe(getViewLifecycleOwner(), responseDTO -> {
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
        });
    }

    public void syncDisplay(ProfileDto profileDTO) {
        String firstName = profileDTO.getFirstName();
        String fullName = firstName + " " + profileDTO.getLastName();
        String msisdn = (profileDTO.getPaymate() != null && profileDTO.getPaymate().getPaymateStatus().equals("ACTIVE")) ? "Paymate Code: " + profileDTO.getPaymate().getPaymateCode() : profileDTO.getMsisdn();
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
    