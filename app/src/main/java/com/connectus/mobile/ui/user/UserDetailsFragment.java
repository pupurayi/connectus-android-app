package com.connectus.mobile.ui.user;

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
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.utils.Utils;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.snackbar.Snackbar;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class UserDetailsFragment extends Fragment {
    private static final String TAG = UserDetailsFragment.class.getSimpleName();

    private static final int IMAGE_PICKER_REQUEST = 100;

    ImageView imageViewBack, imageViewPlus, imageViewAvatar;
    ProgressDialog pd;
    TextView textViewFullName, textViewPhoneNumber, textViewEmail;
    Button buttonEditUser;
    SharedPreferencesManager sharedPreferencesManager;

    private UserViewModel userViewModel;
    private UserDto userDto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());

        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        this.userDto = sharedPreferencesManager.getUser();


        long lastSync = sharedPreferencesManager.getLastSync();
        long now = new Date().getTime();
        if (now - lastSync >= 300000) {
            pd.setMessage("Syncing Profile ...");
            pd.show();
            userViewModel.hitGetUserApi(getActivity(), userDto.getId()).observe(getViewLifecycleOwner(), responseDto -> {
                pd.dismiss();
                switch (responseDto.getStatus()) {
                    case "success":
                        Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                        UserDto userDto1 = sharedPreferencesManager.getUser();
                        populateFields(userDto1);
                        break;
                    case "failed":
                    case "error":
                        Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                        break;
                }
            });
        }

        imageViewAvatar = view.findViewById(R.id.adf_image_view_profile_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);

        imageViewPlus = view.findViewById(R.id.adf_image_view_plus);
        imageViewPlus.setOnClickListener(v -> pickImage());

        textViewFullName = view.findViewById(R.id.text_view_full_name);
        textViewPhoneNumber = view.findViewById(R.id.text_view_phone_value);
        textViewEmail = view.findViewById(R.id.text_view_email_value);

        populateFields(userDto);

        buttonEditUser = view.findViewById(R.id.button_edit_user);
        buttonEditUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditUserFragment editUserFragment = new EditUserFragment();
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.container, editUserFragment, EditUserFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

    }

    @Override
    public void onResume() {
        super.onResume();
        this.userDto = sharedPreferencesManager.getUser();
        populateFields(userDto);
    }

    public void populateFields(UserDto userDto) {
        String fullName = userDto.getFirstName() + " " + userDto.getLastName();
        String msisdn = userDto.getMsisdn();
        String email = userDto.getEmail();
        textViewFullName.setText(fullName);
        textViewPhoneNumber.setText(msisdn);
        textViewEmail.setText(email);
    }

    public void pickImage() {
        ImagePicker.with(this)
                .crop()
                .compress(1024)
                .maxResultSize(1080, 1080)
                .start(IMAGE_PICKER_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICKER_REQUEST && resultCode == Activity.RESULT_OK) {
            Uri sourceUri = data.getData();
            if (sourceUri != null) {
                try {
                    final InputStream imageStream = getContext().getContentResolver().openInputStream(sourceUri);
                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                    userDto.setAvatar(encodeImage(selectedImage));
                    uploadAvatar(userDto);
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

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] b = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void uploadAvatar(UserDto userDto) {
        pd.setMessage("Uploading Avatar ...");
        pd.show();

        userViewModel.hitUpdateUser(getActivity(), userDto).observe(getViewLifecycleOwner(), responseDto -> {
            pd.dismiss();
            switch (responseDto.getStatus()) {
                case "success":
                    this.userDto = sharedPreferencesManager.getUser();
                    Utils.loadAvatar(userDto, imageViewAvatar);
                    Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }
}