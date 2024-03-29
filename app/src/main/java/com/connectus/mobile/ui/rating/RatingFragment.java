package com.connectus.mobile.ui.rating;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.api.dto.ProductDto;
import com.connectus.mobile.ui.product.ProductViewModel;
import com.connectus.mobile.utils.Utils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class RatingFragment extends Fragment {
    private static final String TAG = RatingFragment.class.getSimpleName();


    private RatingViewModel ratingViewModel;
    private ProductViewModel productViewModel;

    ProgressDialog pd;
    ImageView imageViewAvatar, imageViewProductAvatar;
    TextView textViewProductName;
    ImageButton imageButtonDislike, imageButtonLike;
    Button buttonSkip;

    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;

    private UserDto userDto;
    private List<ProductDto> ratingProducts = new LinkedList<>();
    private int index = 0;
    private Map<UUID, Boolean> ratings = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ratingViewModel = new ViewModelProvider(this).get(RatingViewModel.class);
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        userDto = sharedPreferencesManager.getUser();

        imageViewAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);


        imageViewProductAvatar = view.findViewById(R.id.image_view_product);
        textViewProductName = view.findViewById(R.id.text_view_product_name);
        imageButtonDislike = view.findViewById(R.id.image_button_dislike);
        imageButtonLike = view.findViewById(R.id.image_button_like);
        buttonSkip = view.findViewById(R.id.button_skip);

        imageButtonDislike.setOnClickListener(view1 -> rate(false));
        imageButtonLike.setOnClickListener(view1 -> rate(true));
        buttonSkip.setOnClickListener(v -> {
            proceedToDashboard();
        });

        getRatingProducts();
    }

    private void rate(boolean like) {
        ProductDto productDto = ratingProducts.get(index);
        ratings.put(productDto.getId(), like);
        if (index == ratingProducts.size() - 1) {
            ratingViewModel.hitSubmitProductsRating(getContext(), userDto.getId(), ratings).observe(getViewLifecycleOwner(), responseDto -> {
                switch (responseDto.getStatus()) {
                    case "success":
                    case "failed":
                    case "error":
                        proceedToDashboard();
                        break;
                }
            });
            proceedToDashboard();
        } else {
            index++;
            show();
        }
    }

    private void show() {
        ProductDto productDto = ratingProducts.get(index);
        byte[] decodedString = Base64.decode(productDto.getImageFirst(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        imageViewProductAvatar.setImageBitmap(decodedByte);
        textViewProductName.setText(productDto.getName());
    }

    private void proceedToDashboard() {
        DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
        transaction.commit();
    }

    private void getRatingProducts() {
        pd.setMessage("Loading...");
        pd.show();
        productViewModel.getProducts(userDto.getId(), ProductType.USER_RATING, null, null, 0, 0, null).observe(getViewLifecycleOwner(), responseDto -> {
            switch (responseDto.getStatus()) {
                case "success":
                    pd.dismiss();
                    ratingProducts = (List<ProductDto>) responseDto.getData();
                    if (ratingProducts.size() != 0) {
                        show();
                    } else {
                        proceedToDashboard();
                    }
                    break;
                case "failed":
                case "error":
                    pd.dismiss();
                    ratingProducts = new LinkedList<>();
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Connect Us")
                            .setMessage(responseDto.getMessage())
                            .setPositiveButton("Retry", (dialogInterface, i) -> getRatingProducts())
                            .setNegativeButton("Skip", (dialogInterface, i) -> {
                                proceedToDashboard();
                            })
                            .show();
                    break;
            }
        });
    }
}