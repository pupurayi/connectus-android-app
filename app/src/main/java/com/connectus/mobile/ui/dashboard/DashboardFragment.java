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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.MainActivity;
import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProductDto;
import com.connectus.mobile.ui.product.RecommendationsMapFragment;
import com.connectus.mobile.ui.product.SearchProductsFragment;
import com.connectus.mobile.ui.product.ViewProductFragment;
import com.connectus.mobile.ui.product.ProductRecyclerAdapter;
import com.connectus.mobile.ui.product.ProductViewModel;
import com.connectus.mobile.ui.product.ProductsFragment;
import com.connectus.mobile.ui.rating.RatingFragment;
import com.connectus.mobile.ui.serviceprovider.ServiceProvidersFragment;
import com.connectus.mobile.ui.user.UserDetailsFragment;
import com.connectus.mobile.ui.initial.check.CheckFragment;
import com.connectus.mobile.ui.settings.SettingsFragment;
import com.connectus.mobile.ui.user.UserViewModel;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DashboardFragment extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = DashboardFragment.class.getSimpleName();

    DrawerLayout drawerLayout;

    TextView textViewFullName, textViewNavHeaderFullName, textViewMsisdn, textViewNavHeaderMsisdn;
    ImageView imageViewAvatar, imageViewNavHeaderAvatar, imageViewMenu, imageViewRecommendationsMap, imageViewRefreshRecommendedProducts;
    RecyclerView recyclerViewProducts;
    ProgressDialog pd;

    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;
    UserDto userDto;
    private UserViewModel userViewModel;
    private ProductViewModel productViewModel;

    ProductRecyclerAdapter productRecyclerAdapter;
    List<ProductDto> recommendedProducts;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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
        userDto = sharedPreferencesManager.getUser();

        drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = getView().findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View navHeaderView = navigationView.getHeaderView(0);

        imageViewNavHeaderAvatar = navHeaderView.findViewById(R.id.image_view_nav_header_avatar);
        textViewNavHeaderFullName = navHeaderView.findViewById(R.id.text_view_nav_header_full_name);
        textViewNavHeaderMsisdn = navHeaderView.findViewById(R.id.text_view_nav_header_msisdn);

        imageViewAvatar = view.findViewById(R.id.image_view_profile_avatar);
        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewMsisdn = view.findViewById(R.id.text_view_msisdn);

        long lastSync = sharedPreferencesManager.getLastSync();
        long now = new Date().getTime();
        if (now - lastSync >= 300000) {
            syncProfileAndDisplay();
        }

        DbHandler dbHandler = new DbHandler(getContext());
        recommendedProducts = dbHandler.getProducts();
        recommendedProducts = sortRecommendedProducts(recommendedProducts);
        productRecyclerAdapter = new ProductRecyclerAdapter(getContext(), recommendedProducts, fragmentManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerViewProducts = getView().findViewById(R.id.recycler_view_recommended_products);
        recyclerViewProducts.setAdapter(productRecyclerAdapter);
        recyclerViewProducts.setLayoutManager(linearLayoutManager);

        imageViewMenu = view.findViewById(R.id.image_view_menu);
        imageViewMenu.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        imageViewAvatar.setOnClickListener(v -> showProfileDetailsFragment());
        imageViewRecommendationsMap = view.findViewById(R.id.image_view_recommendations_map);
        imageViewRecommendationsMap.setOnClickListener(view1 -> {
            RecommendationsMapFragment recommendationsMapFragment = new RecommendationsMapFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, recommendationsMapFragment, RecommendationsMapFragment.class.getSimpleName());
            transaction.addToBackStack(TAG);
            transaction.commit();
        });
        imageViewRefreshRecommendedProducts = view.findViewById(R.id.image_view_refresh_recommended_products);
        imageViewRefreshRecommendedProducts.setOnClickListener(view1 -> {
            fetchRecommendedProducts();
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        syncDisplay();
        fetchRecommendedProducts();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        userDto = sharedPreferencesManager.getUser();
        syncDisplay();
    }

    public void showProfileDetailsFragment() {
        UserDetailsFragment userDetailsFragment = new UserDetailsFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, userDetailsFragment, UserDetailsFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }

    public void navigateToProvider(ProductDto product) {

        Bundle bundle = new Bundle();
        bundle.putString("product", new Gson().toJson(product));
        ViewProductFragment viewProductFragment = new ViewProductFragment();
        viewProductFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, viewProductFragment, FragmentTransaction.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }


    public void getUserDetails() {
        userViewModel.hitGetUserApi(getContext(), userDto.getId()).observe(getViewLifecycleOwner(), responseDto -> {
            switch (responseDto.getStatus()) {
                case "success":
                    userDto = (UserDto) responseDto.getData();
                    break;
                case "failed":
                case "error":
                    break;
            }
            pd.dismiss();
        });
    }

    public void fetchRecommendedProducts() {
        pd.setMessage("Please Wait ...");
        pd.show();

        MainActivity mainActivity = ((MainActivity) getActivity());
        productViewModel.getProducts(userDto.getId(), ProductType.RECOMMENDED, null, null, mainActivity.getCurrentLat(), mainActivity.getCurrentLng(), null).observe(getViewLifecycleOwner(), responseDto -> {
            pd.dismiss();
            switch (responseDto.getStatus()) {
                case "success":
                    List<ProductDto> products = (List<ProductDto>) responseDto.getData();
                    if (products != null) {
                        DbHandler dbHandler = new DbHandler(getContext());
                        dbHandler.deleteAllProducts();
                        for (ProductDto new_products : products) {
                            dbHandler.insertProduct(new_products);
                        }
                    }
                    loadRecommendedProductsRecyclerView();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
            pd.dismiss();
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
                Bundle bundle = new Bundle();
                bundle.putString("userId", userDto.getId().toString());
                bundle.putString("title", "My Products");
                bundle.putString("productType", ProductType.USER.toString());
                bundle.putBoolean("promptCreateProduct", true);
                ProductsFragment productsFragment = new ProductsFragment();
                productsFragment.setArguments(bundle);
                transaction.add(R.id.container, productsFragment, ProductsFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_service_providers:
                ServiceProvidersFragment serviceProvidersFragment = new ServiceProvidersFragment();
                transaction.add(R.id.container, serviceProvidersFragment, ServiceProvidersFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_search_products:
                SearchProductsFragment searchProductsFragment = new SearchProductsFragment();
                transaction.add(R.id.container, searchProductsFragment, SearchProductsFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                break;
            case R.id.nav_review:
                RatingFragment ratingFragment = new RatingFragment();
                transaction.add(R.id.container, ratingFragment, RatingFragment.class.getSimpleName());
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

        userViewModel.hitGetUserApi(getActivity(), userDto.getId()).observe(getViewLifecycleOwner(), responseDto -> {
            pd.dismiss();
            switch (responseDto.getStatus()) {
                case "success":
                    Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                    userDto = sharedPreferencesManager.getUser();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    public void logout() {
        pd.setMessage("Signing Out ...");
        pd.show();
        Utils.clearSessionData(sharedPreferencesManager, getContext());
        CheckFragment checkFragment = new CheckFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, checkFragment, CheckFragment.class.getSimpleName());
        transaction.commit();
        pd.dismiss();
    }

    public void syncDisplay() {
        getUserDetails();
        String fullName = userDto.getFirstName() + " " + userDto.getLastName();
        String msisdn = userDto.getMsisdn();
        textViewFullName.setText(fullName);
        textViewNavHeaderFullName.setText(fullName);
        textViewMsisdn.setText(msisdn);
        textViewNavHeaderMsisdn.setText(msisdn);
        Utils.loadAvatar(userDto, imageViewAvatar);
        Utils.loadAvatar(userDto, imageViewNavHeaderAvatar);
    }
}
    