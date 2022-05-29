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
import androidx.lifecycle.ViewModelProvider;

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
import com.connectus.mobile.api.dto.CreateProductDto;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.shivtechs.maplocationpicker.MapUtility;

import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ProductFragment extends Fragment {

    private static final String TAG = ProductFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack;
    CircularImageView circularImageViewProduct, circularImageViewUpload;

    EditText editTextProductCategory, editTextProductName, editTextProductDescription, editTextProductPrice;
    Button buttonCreateProduct;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;
    private ProductViewModel productViewModel;

    boolean dialogActive = false;

    private static final int FIRST_IMAGE_PICKER_REQUEST = 100;
    private String imageFirst;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        productViewModel = new ViewModelProvider(this).get(ProductViewModel.class);
        return inflater.inflate(R.layout.fragment_product, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        UserDto userDto = sharedPreferencesManager.getUser();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        circularImageViewProduct = view.findViewById(R.id.circular_image_view_product_image);
        circularImageViewUpload = view.findViewById(R.id.circular_image_view_pick_image);
        circularImageViewUpload.setOnClickListener(view1 -> {
            pickImage();
        });

        editTextProductCategory = view.findViewById(R.id.edit_text_product_category);
        editTextProductName = view.findViewById(R.id.edit_text_product_name);
        editTextProductDescription = view.findViewById(R.id.edit_text_product_description);
        editTextProductPrice = view.findViewById(R.id.edit_text_product_price);

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
                    dialogActive = false;
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


        buttonCreateProduct = view.findViewById(R.id.button_create_product);
        buttonCreateProduct.setOnClickListener(v -> {
            String category = editTextProductCategory.getText().toString();
            String name = editTextProductName.getText().toString();
            String description = editTextProductDescription.getText().toString();
            double price = 0;
            if (editTextProductPrice.getText() != null && !editTextProductPrice.getText().toString().isEmpty()) {
                price = Double.parseDouble(editTextProductPrice.getText().toString());
            }

            if (!category.isEmpty() && !name.isEmpty() && !description.isEmpty() && price > 0 && imageFirst != null) {
                MainActivity mainActivity = ((MainActivity) getActivity());
                CreateProductDto createProductDto = new CreateProductDto(userDto.getId(), category, name, description, price, imageFirst, mainActivity.getCurrentLat(), mainActivity.getCurrentLng());
                pd.setMessage("Creating ...");
                pd.show();
                productViewModel.hitSaveProductApi(createProductDto).observe(getViewLifecycleOwner(), responseDto -> {
                    pd.dismiss();
                    switch (responseDto.getStatus()) {
                        case "success":
                            Snackbar.make(getView(), "Product Successfully added!", Snackbar.LENGTH_LONG).show();
                            Utils.returnToDashboard(fragmentManager);
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });

            } else {
                if (category.isEmpty()) {
                    Snackbar.make(view, "Product Category cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (name.isEmpty()) {
                    Snackbar.make(view, "Product Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (description.isEmpty()) {
                    Snackbar.make(view, "Product Description cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (price <= 0) {
                    Snackbar.make(view, "Product Price must be greater than zero!", Snackbar.LENGTH_LONG).show();
                } else if (imageFirst == null) {
                    Snackbar.make(view, "Product should have an image!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
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
                    imageFirst = Utils.encodeImage(selectedImage);
                    circularImageViewProduct.setImageURI(sourceUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        } else if (resultCode == ImagePicker.RESULT_ERROR) {
            Toast.makeText(getContext(), ImagePicker.getError(data), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Task Cancelled", Toast.LENGTH_SHORT).show();
        }
    }

    public void pickImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(FIRST_IMAGE_PICKER_REQUEST);
    }
}