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

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.gson.Gson;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    private static final String TAG = ProductFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAvatar;

    ImageView imageViewProduct;
    TextView textViewProductName, textViewProductDescription, textViewProductPrice;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    ProductDto product;

    Button buttonNavigate;

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
        return inflater.inflate(R.layout.fragment_product, container, false);
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