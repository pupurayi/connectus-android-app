package com.connectus.mobile.ui.product;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class PickProductImagesFragment extends Fragment {

    private static final String TAG = PickProductImagesFragment.class.getSimpleName();

    private static final int FIRST_IMAGE_PICKER_REQUEST = 100,
            SECOND_IMAGE_PICKER_REQUEST = 200, LOCATION_PICKER_REQUEST = 300;

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    String category, name, description;
    double price, lat = 0, lng = 0;
    String imageFirst, imageSecond;
    ImageView imageViewFirst, imageViewSecond;
    Button buttonNext;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private ProductViewModel productViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            category = arguments.getString("category");
            name = arguments.getString("name");
            description = arguments.getString("description");
            price = arguments.getDouble("price");
        } else {
            getActivity().onBackPressed();
        }
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
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

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        imageViewFirst = view.findViewById(R.id.image_view_first);
        imageViewSecond = view.findViewById(R.id.image_view_second);

        imageViewFirst.setOnClickListener(view12 -> pickImage(1));

        imageViewSecond.setOnClickListener(view1 -> pickImage(2));


        buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> {

            if (imageFirst != null && !imageFirst.isEmpty()
                    && imageSecond != null && !imageSecond.isEmpty()
            ) {
                if (category != null && name != null && description != null && price > 0 && imageFirst != null && imageSecond != null && lat != 0 && lng != 0) {
                    CreateProductDto createProductDto = new CreateProductDto(profileDTO.getId(), category, name, description, price, imageFirst, imageSecond, lat, lng);
                    pd.setMessage("Creating ...");
                    pd.show();
                    productViewModel.hitSaveProductApi(authentication, createProductDto).observe(getViewLifecycleOwner(), responseDTO -> {
                        pd.dismiss();
                        switch (responseDTO.getStatus()) {
                            case "success":
                                close();
                                break;
                            case "failed":
                            case "error":
                                Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                break;
                        }
                    });
                } else if (lat == 0 || lng == 0) {
                    Intent i = new Intent(getContext(), LocationPickerActivity.class);
                    startActivityForResult(i, LOCATION_PICKER_REQUEST);
                } else {
                    Snackbar.make(view, "Missing product data!", Snackbar.LENGTH_LONG).show();
                }
            } else {
                if (imageFirst == null || imageFirst.isEmpty()) {
                    Snackbar.make(view, "Please pick first image!", Snackbar.LENGTH_LONG).show();
                } else if (imageSecond == null || imageSecond.isEmpty()) {
                }
            }
        });
    }

    public void pickImage(int count) {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(count == 1 ? FIRST_IMAGE_PICKER_REQUEST : SECOND_IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FIRST_IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                try {
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(sourceUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageFirst = encodeImage(selectedImage);
                    imageViewFirst.setImageURI(sourceUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (requestCode == SECOND_IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                try {
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(sourceUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    imageSecond = encodeImage(selectedImage);
                    imageViewSecond.setImageURI(sourceUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else if (requestCode == LOCATION_PICKER_REQUEST) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    lat = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    lng = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    Bundle completeAddress = data.getBundleExtra("fullAddress");
                    buttonNext.setText(R.string.create_product);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    private void close() {
        DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
        }
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
        transaction.commit();
    }
}