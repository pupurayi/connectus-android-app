package com.connectus.mobile.ui.product;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SearchProductsFragment extends Fragment {

    private static final String TAG = SearchProductsFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAvatar;

    EditText editTextProductCategory, editTextProductName, editTextSortBy;
    Button buttonSearch;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    boolean productDialogActive = false, sortByDialog = false;


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
        editTextSortBy = view.findViewById(R.id.edit_text_sort_by);

        imageViewAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);


        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        editTextProductCategory.setInputType(InputType.TYPE_NULL);
        editTextProductCategory.setOnTouchListener((v, event) -> {
            if (!productDialogActive) {
                productDialogActive = true;
                String[] categories = ProductConstants.categories.toArray(new String[0]);
                CharSequence[] options = new CharSequence[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    options[i] = categories[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.categories));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    productDialogActive = true;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextProductCategory.setText(option);
                    productDialogActive = false;
                });
                builder.show();
            }
            return false;
        });

        editTextSortBy.setInputType(InputType.TYPE_NULL);
        editTextSortBy.setOnTouchListener((v, event) -> {
            if (!sortByDialog) {
                sortByDialog = true;
                String[] strategies = {"Price", "Proximity", "Rating", "Name", "Created"};
                CharSequence[] options = new CharSequence[strategies.length];
                for (int i = 0; i < strategies.length; i++) {
                    options[i] = strategies[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.sort_by));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    sortByDialog = true;
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


        buttonSearch = view.findViewById(R.id.button_search);
        buttonSearch.setOnClickListener(v -> {
            String category = editTextProductCategory.getText().toString();
            String name = editTextProductName.getText().toString();
            String sortBy = editTextSortBy.getText().toString();

            if (!name.isEmpty()) {
                Bundle bundle = new Bundle();
                bundle.putString("userId", userDto.getId().toString());
                bundle.putString("title", "Search Results");
                bundle.putString("productType", ProductType.SEARCH_QUERY.toString());
                bundle.putString("category", category);
                bundle.putString("name", name);
                bundle.putString("sortBy", sortBy);
                bundle.putBoolean("promptCreateProduct", false);

                ProductsFragment productsFragment = new ProductsFragment();
                productsFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, productsFragment, FragmentTransaction.class.getSimpleName());
                transaction.addToBackStack(ProductsFragment.class.getSimpleName());
                transaction.commit();

            } else {
                Snackbar.make(view, "Product Name cannot be null!", Snackbar.LENGTH_LONG).show();
            }
        });
    }
}