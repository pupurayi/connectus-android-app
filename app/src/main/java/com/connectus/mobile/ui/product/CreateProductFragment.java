package com.connectus.mobile.ui.product;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.common.IdType;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.math.BigDecimal;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CreateProductFragment extends Fragment {

    private static final String TAG = CreateProductFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextProductCategory, editTextProductName, editTextProductDescription, editTextProductPrice;
    Button buttonNext;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    boolean dialogActive = false;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_create, container, false);
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

        editTextProductCategory = view.findViewById(R.id.edit_text_product_category);
        editTextProductName = view.findViewById(R.id.edit_text_product_name);
        editTextProductDescription = view.findViewById(R.id.edit_text_product_description);
        editTextProductPrice = view.findViewById(R.id.edit_text_product_price);

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/avatar/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imageViewProfileAvatar);
        }

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        editTextProductCategory.setInputType(InputType.TYPE_NULL);
        editTextProductCategory.setOnTouchListener((v, event) -> {
            if (!dialogActive) {
                dialogActive = true;
                String[] categories = ProductConstants.categories.toArray(new String[0]);
                CharSequence[] options = new CharSequence[categories.length];
                for (int i = 0; i < categories.length; i++) {
                    options[i] = categories[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.categories));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    dialogActive = true;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextProductCategory.setText(option);
                    dialogActive = false;
                });
                builder.show();
            }
            return false;
        });


        buttonNext = view.findViewById(R.id.button_next);
        buttonNext.setOnClickListener(v -> {
            String category = editTextProductCategory.getText().toString();
            String name = editTextProductName.getText().toString();
            String description = editTextProductDescription.getText().toString();
            double price = 0;
            if (editTextProductPrice.getText() != null && !editTextProductPrice.getText().toString().isEmpty()) {
                price = Double.parseDouble(editTextProductPrice.getText().toString());
            }

            if (!category.isEmpty() && !name.isEmpty() && !description.isEmpty() && price > 0) {
                Bundle bundle = new Bundle();
                bundle.putString("category", category);
                bundle.putString("name", name);
                bundle.putString("description", description);
                bundle.putDouble("price", price);
                PickProductImagesFragment pickProductImagesFragment = new PickProductImagesFragment();
                pickProductImagesFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, pickProductImagesFragment, PickProductImagesFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();

            } else {
                if (category.isEmpty()) {
                    Snackbar.make(view, "Product Category cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (name.isEmpty()) {
                    Snackbar.make(view, "Product Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (description.isEmpty()) {
                    Snackbar.make(view, "Product Description cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (price <= 0) {
                    Snackbar.make(view, "Product Price must be greater than zero!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}