package com.connectus.mobile.ui.product;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductDto;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.user.UserViewModel;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ViewProductFragment extends Fragment {

    private static final String TAG = ViewProductFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAvatar;

    ImageView imageViewProduct;
    TextView textViewProductName, textViewProductDescription, textViewProductPrice;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    ProductDto product;
    private ProductViewModel productViewModel;

    Button buttonOrderOrDelete, buttonNavigate, buttonDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            String json = arguments.getString("product");
            product = new Gson().fromJson(json, ProductDto.class);
        }
        // Inflate the layout for this fragment
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        return inflater.inflate(R.layout.fragment_view_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        fragmentManager = getActivity().getSupportFragmentManager();
        UserDto userDto = sharedPreferencesManager.getUser();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        imageViewAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);

        imageViewProduct = view.findViewById(R.id.image_view_product);
        byte[] decodedString = Base64.decode(product.getImageFirst(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViewProduct.setImageBitmap(decodedByte);

        textViewProductName = view.findViewById(R.id.text_view_product_name);
        textViewProductDescription = view.findViewById(R.id.text_view_product_description);
        textViewProductPrice = view.findViewById(R.id.text_view_product_price);

        textViewProductName.setText(product.getName());
        textViewProductDescription.setText(product.getDescription());
        textViewProductPrice.setText(new StringBuilder().append("$").append(product.getPrice()).toString());

        int permissionCheck = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            // ask permissions here using below code
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    100);
        }

        buttonOrderOrDelete = view.findViewById(R.id.button_order_or_delete);
        if (userDto.getId().equals(product.getUserId())) {
            buttonOrderOrDelete.setText(R.string.delete_product);
        } else {
            buttonOrderOrDelete.setText(R.string.order_product);

        }

        buttonOrderOrDelete.setOnClickListener(view12 -> {
            pd.setMessage("Please wait...");
            pd.show();
            if (buttonOrderOrDelete.getText().equals(R.string.order_product)) {
                productViewModel.hitRecordProductOrderApi(product.getUserId(), product.getId()).observe(getViewLifecycleOwner(), responseDto -> {
                    pd.dismiss();
                    switch (responseDto.getStatus()) {
                        case "success":
                            UserDto serviceProvider = (UserDto) responseDto.getData();
                            Utils.sendWhatsappMessage(getActivity(), serviceProvider.getMsisdn(), "Hello " + serviceProvider.getFirstName() + ", I would like to order *" + product.getName() + "* product/service priced at $" + product.getPrice() + " kindly share with me more details.");
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });
            } else {
                productViewModel.hitDeleteProductApi(product.getId()).observe(getViewLifecycleOwner(), responseDto -> {
                    pd.dismiss();
                    switch (responseDto.getStatus()) {
                        case "success":
                            Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                            Utils.returnToDashboard(fragmentManager);
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });
            }
        });
        buttonNavigate = view.findViewById(R.id.button_navigate);
        buttonNavigate.setOnClickListener(view1 -> {
            pd.setMessage("Fetching Location....");
            pd.show();
            Bundle bundle = new Bundle();
            bundle.putDouble("destinationLat", product.getLat());
            bundle.putDouble("destinationLng", product.getLng());
            NavigationFragment navigationFragment = new NavigationFragment();
            navigationFragment.setArguments(bundle);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.container, navigationFragment, NavigationFragment.class.getSimpleName());
            transaction.addToBackStack(TAG);
            transaction.commit();
            pd.dismiss();
        });

    }
}