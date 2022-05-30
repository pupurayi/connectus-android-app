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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.MainActivity;
import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.shivtechs.maplocationpicker.LocationPickerActivity;
import com.shivtechs.maplocationpicker.MapUtility;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SearchProductsFragment extends Fragment {

    private static final String TAG = SearchProductsFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAvatar;

    EditText editTextProductCategory, editTextProductName, editTextLocation, editTextSortBy;
    Button buttonSearch, buttonClear;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    boolean categoryDialog = false, sortByDialog = false, productLocationDialog = false;
    private double lat = 0, lng = 0;

    private static final int ADDRESS_PICKER_REQUEST = 450;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_products, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        UserDto userDto = sharedPreferencesManager.getUser();

        editTextProductCategory = view.findViewById(R.id.edit_text_product_category);
        editTextProductName = view.findViewById(R.id.edit_text_product_name);
        editTextLocation = view.findViewById(R.id.edit_text_location);
        editTextSortBy = view.findViewById(R.id.edit_text_sort_by);

        imageViewAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);


        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        editTextProductCategory.setInputType(InputType.TYPE_NULL);
        editTextProductCategory.setOnTouchListener((v, event) -> {
            if (!categoryDialog) {
                categoryDialog = true;
                String[] categories = ProductConstants.categories.toArray(new String[0]);
                CharSequence[] options = new CharSequence[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    options[i] = categories[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.categories));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    categoryDialog = false;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextProductCategory.setText(option);
                    categoryDialog = false;
                });
                builder.show();
            }
            return false;
        });

        editTextSortBy.setInputType(InputType.TYPE_NULL);
        editTextSortBy.setOnTouchListener((v, event) -> {
            if (!sortByDialog) {
                sortByDialog = true;
                String[] strategies = {"Price", "Rating", "Name", "Created"};
                CharSequence[] options = new CharSequence[strategies.length];
                for (int i = 0; i < strategies.length; i++) {
                    options[i] = strategies[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.sort_by));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    sortByDialog = false;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextSortBy.setText(option);
                    sortByDialog = false;
                });
                builder.show();
            }
            return false;
        });

        editTextLocation.setInputType(InputType.TYPE_NULL);
        editTextLocation.setOnTouchListener((v, event) -> {
            if (!productLocationDialog) {
                productLocationDialog = true;
                Intent intent = new Intent(getContext(), LocationPickerActivity.class);
                MainActivity mainActivity = ((MainActivity) getActivity());
                intent.putExtra(MapUtility.LATITUDE, mainActivity.getCurrentLat());
                intent.putExtra(MapUtility.LONGITUDE, mainActivity.getCurrentLng());
                startActivityForResult(intent, ADDRESS_PICKER_REQUEST);
            }
            return false;
        });


        buttonSearch = view.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            String category = editTextProductCategory.getText().toString().trim();
            String name = editTextProductName.getText().toString().trim();
            String sortBy = editTextSortBy.getText().toString().trim();
            String location = editTextSortBy.getText().toString().trim();

            if (!category.isEmpty() || !name.isEmpty() || !location.isEmpty() || !sortBy.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", userDto.getId().toString());
                bundle.putString("title", "Search Results");
                bundle.putString("productType", ProductType.SEARCH_QUERY.toString());
                bundle.putString("category", category);
                bundle.putString("name", name);
                if (editTextLocation.getText() == null || editTextLocation.getText().toString().isEmpty() || lat == 0 || lng == 0) {
                    MainActivity mainActivity = ((MainActivity) getActivity());
                    lat = mainActivity.getCurrentLat();
                    lng = mainActivity.getCurrentLng();
                }
                bundle.putDouble("lat", lat);
                bundle.putDouble("lng", lng);
                bundle.putString("sortBy", sortBy);
                bundle.putBoolean("promptCreateProduct", false);

                ProductsFragment productsFragment = new ProductsFragment();
                productsFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, productsFragment, FragmentTransaction.class.getSimpleName());
                transaction.addToBackStack(ProductsFragment.class.getSimpleName());
                transaction.commit();

            } else {
                Snackbar.make(view, "Please enter category, name, location, sortBy!", Snackbar.LENGTH_LONG).show();
            }
        });

        buttonClear = view.findViewById(R.id.button_clear);
        buttonClear.setOnClickListener(v -> {
            editTextProductCategory.getText().clear();
            editTextProductName.getText().clear();
            editTextLocation.getText().clear();
            editTextSortBy.getText().clear();
            lat = 0;
            lng = 0;
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDRESS_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            try {
                if (data != null && data.getStringExtra(MapUtility.ADDRESS) != null) {
                    lat = data.getDoubleExtra(MapUtility.LATITUDE, 0.0);
                    lng = data.getDoubleExtra(MapUtility.LONGITUDE, 0.0);
                    editTextLocation.setText(new StringBuilder().append(lat).append(",").append(lng).toString());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            productLocationDialog = false;
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }
}