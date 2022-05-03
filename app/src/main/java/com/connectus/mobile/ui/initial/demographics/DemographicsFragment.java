package com.connectus.mobile.ui.initial.demographics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.user.UserDetailsViewModel;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.hbb20.CountryCodePicker;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DemographicsFragment extends Fragment {
    private static final String TAG = DemographicsFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextGender, editTextDob, editTextEthnicity;
    Button buttonSave;

    FragmentManager fragmentManager;
    private UserDetailsViewModel userDetailsViewModel;
    private SharedPreferencesManager sharedPreferencesManager;
    private String authentication;
    private boolean pickingGender;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userDetailsViewModel = new ViewModelProvider(this).get(UserDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_demographics, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        this.authentication = sharedPreferencesManager.getAuthenticationToken();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        UserDto userDTO = sharedPreferencesManager.getUser();

        imageViewProfileAvatar = view.findViewById(R.id.circular_image_view_avatar);

        editTextGender = view.findViewById(R.id.edit_text_gender);
        editTextDob = view.findViewById(R.id.edit_text_dob);
        editTextEthnicity = view.findViewById(R.id.edit_text_ethnicity);

        editTextGender.setInputType(InputType.TYPE_NULL);
        editTextGender.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!pickingGender) {
                    pickGender();
                }
                return true;
            }
        });

        buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void pickGender() {

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