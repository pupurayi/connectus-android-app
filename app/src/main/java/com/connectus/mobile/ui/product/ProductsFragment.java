package com.connectus.mobile.ui.product;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProductsFragment extends Fragment {

    ProgressDialog pd;
    ImageView imageViewBack;

    TextView productsPageTitle;
    ImageView imageViewAvatar;
    ProductRecyclerAdapter productRecyclerAdapter;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    private ProductViewModel productsViewModel;
    UserDto userDto = null;
    List<ProductDto> products = new LinkedList<>();
    private UUID userId;
    private String title;
    private ProductType productType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            userId = UUID.fromString(arguments.getString("userId"));
            title = arguments.getString("title");
            productType = ProductType.valueOf(arguments.getString("productType"));
        } else {
            getActivity().onBackPressed();
        }
        productsViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        userDto = sharedPreferencesManager.getUser();
        fragmentManager = getActivity().getSupportFragmentManager();


        productsPageTitle = view.findViewById(R.id.text_view_products_page_title);
        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        productsPageTitle.setText(title);
        imageViewAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);

        getProducts();
        productRecyclerAdapter = new ProductRecyclerAdapter(getContext(), products, fragmentManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recycler_view_products);
        recyclerViewProducts.setAdapter(productRecyclerAdapter);
        recyclerViewProducts.setLayoutManager(linearLayoutManager);
    }

    private void getProducts() {
        pd.setMessage("Fetching ...");
        pd.show();
        productsViewModel.getProducts(userId, productType).observe(getViewLifecycleOwner(), responseDto -> {
            switch (responseDto.getStatus()) {
                case "success":
                    pd.dismiss();
                    products.clear();
                    List<ProductDto> mProducts = (List<ProductDto>) responseDto.getData();
                    products.addAll(mProducts);
                    productRecyclerAdapter.notifyDataSetChanged();
                    break;
                case "failed":
                case "error":
                    pd.dismiss();
                    Utils.alert(getContext(), "Connect Us", responseDto.getMessage());
                    break;
            }
            pd.dismiss();
        });
    }
}