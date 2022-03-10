package com.connectus.mobile.ui.product;

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
import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PickProductImagesFragment extends Fragment {

    private static final String TAG = PickProductImagesFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    String name, description;
    double price;
    String imageFirst, imageSecond;
    ImageView imageViewFirst, imageViewSecond;
    Button buttonNext;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            name = arguments.getString("name");
            description = arguments.getString("description");
            price = arguments.getDouble("price");
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_pick_images, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/avatar/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        imageViewFirst = view.findViewById(R.id.image_view_first);
        imageViewSecond = view.findViewById(R.id.image_view_second);


        buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> {

            if (imageFirst != null && !imageFirst.isEmpty()
                    && imageSecond != null && !imageSecond.isEmpty()
            ) {
                Bundle bundle = new Bundle();
                bundle.putString("name", name);
                bundle.putString("description", description);
                bundle.putDouble("price", price);
                bundle.putString("imageFirst", imageFirst);
                bundle.putString("imageSecond", imageSecond);
                PickProductImagesFragment pickProductImagesFragment = new PickProductImagesFragment();
                pickProductImagesFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, pickProductImagesFragment, PickProductImagesFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();

            } else {
                if (imageFirst == null || imageFirst.isEmpty()) {
                    Snackbar.make(view, "Please pick first image!", Snackbar.LENGTH_LONG).show();
                } else if (imageSecond == null || imageSecond.isEmpty()) {
                    Snackbar.make(view, "Please pick second image!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}