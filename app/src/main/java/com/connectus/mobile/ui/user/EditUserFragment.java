package com.connectus.mobile.ui.user;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.UpdateProfileRequest;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.UserDto;
import com.google.android.material.snackbar.Snackbar;

import java.text.SimpleDateFormat;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditUserFragment extends Fragment {

    private static final String TAG = EditUserFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextFirstName, editTextLastName, editTextEmail;
    Button buttonUpdateProfile;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    boolean dobPickerActive = false;

    FragmentManager fragmentManager;
    private UserDetailsViewModel userDetailsViewModel;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userDetailsViewModel = new ViewModelProvider(this).get(UserDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_user, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        UserDto userDTO = sharedPreferencesManager.getUser();
        String firstName = userDTO.getFirstName();
        String lastName = userDTO.getLastName();
        String email = userDTO.getEmail();

        editTextFirstName = view.findViewById(R.id.edit_text_first_name);
        editTextLastName = view.findViewById(R.id.edit_text_last_name);
        editTextEmail = view.findViewById(R.id.edit_text_email);

        editTextFirstName.setText(firstName);
        editTextLastName.setText(lastName);
        editTextEmail.setText(email);

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);
        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        buttonUpdateProfile = view.findViewById(R.id.button_next);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = editTextFirstName.getText().toString();
                String lastName = editTextLastName.getText().toString();
                String email = editTextEmail.getText().toString();


                if (firstName.length() > 1 && lastName.length() > 1 && email.length() > 1) {
                    if (userDTO != null && firstName.equals(userDTO.getFirstName()) && lastName.equals(userDTO.getLastName())) {
                    } else {
                        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(email, firstName, lastName);
                        pd.setMessage("Updating ...");
                        pd.show();
                        userDetailsViewModel.hitUpdateProfileApi(getActivity(), authentication, updateProfileRequest).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                            @Override
                            public void onChanged(ResponseDTO responseDTO) {
                                pd.dismiss();
                                switch (responseDTO.getStatus()) {
                                    case "success":
                                        break;
                                    case "failed":
                                    case "error":
                                        Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                                        break;
                                }
                            }
                        });
                    }
                } else {
                    if (firstName.length() < 2) {
                        Snackbar.make(view, "Enter valid First Name!", Snackbar.LENGTH_LONG).show();
                    } else if (lastName.length() < 2) {
                        Snackbar.make(view, "Enter valid Last Name!", Snackbar.LENGTH_LONG).show();
                    } else if (email.length() < 5) {
                        Snackbar.make(view, "Enter valid Email!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }
}