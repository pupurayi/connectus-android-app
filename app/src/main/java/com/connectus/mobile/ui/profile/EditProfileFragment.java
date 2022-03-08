package com.connectus.mobile.ui.profile;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.api.dto.UpdateProfileRequest;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.common.Sex;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class EditProfileFragment extends Fragment {

    private static final String TAG = EditProfileFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextFirstName, editTextLastName, editTextEmail, editTextDOB;
    DatePickerDialog dobPicker;
    RadioButton radioMale, radioFemale;
    Button buttonUpdateProfile;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd-MM-yyyy");
    boolean dobPickerActive = false;

    FragmentManager fragmentManager;
    private ProfileDetailsViewModel profileDetailsViewModel;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        profileDetailsViewModel = new ViewModelProvider(this).get(ProfileDetailsViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
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
        String firstName = profileDTO.getFirstName();
        String lastName = profileDTO.getLastName();
        String email = profileDTO.getEmail();
        Date dob = profileDTO.getDob();
        Sex sex = profileDTO.getSex();

        editTextFirstName = view.findViewById(R.id.edit_text_first_name);
        editTextLastName = view.findViewById(R.id.edit_text_last_name);
        editTextEmail = view.findViewById(R.id.edit_text_email);
        editTextDOB = view.findViewById(R.id.edit_text_dob);
        radioMale = view.findViewById(R.id.radio_male);
        radioFemale = view.findViewById(R.id.radio_female);

        editTextFirstName.setText(firstName);
        editTextLastName.setText(lastName);
        editTextEmail.setText(email);
        if (dob != null) {
            editTextDOB.setText(shortDateFormat.format(dob));
        }
        if (sex != null) {
            if (sex.equals(Sex.M)) {
                radioMale.setChecked(true);
            } else {
                radioFemale.setChecked(true);
            }
        } else {
            radioMale.setChecked(false);
            radioFemale.setChecked(false);
        }

        editTextDOB.setInputType(InputType.TYPE_NULL);
        editTextDOB.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (!dobPickerActive) {
                    dobPickerActive = true;
                    // TODO Age Limit 16
                    Calendar calendar = Calendar.getInstance();
                    int year;
                    if (dob != null) {
                        calendar.setTime(dob);
                        year = calendar.get(Calendar.YEAR);
                    } else {
                        year = calendar.get(Calendar.YEAR) - 16;
                    }
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    int month = calendar.get(Calendar.MONTH);

                    dobPicker = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            @SuppressLint("DefaultLocale") String dob = String.format("%02d-%02d-%d", dayOfMonth, monthOfYear + 1, year);
                            editTextDOB.setText(dob);
                            dobPickerActive = false;
                        }
                    }, year, month, day);
                    dobPicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            dobPickerActive = false;
                        }
                    });
                    dobPicker.show();
                }
                return false;
            }
        });

        imageViewProfileAvatar = view.findViewById(R.id.uaf_image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

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
                String dob = editTextDOB.getText().toString();
                boolean male = radioMale.isChecked();
                boolean female = radioFemale.isChecked();


                String oldDate = null;
                if (profileDTO.getDob() != null) {
                    oldDate = shortDateFormat.format(profileDTO.getDob());
                }

                if (firstName.length() > 1 && lastName.length() > 1 && email.length() > 1 && !dob.isEmpty() && (male || female)) {
                    if (profileDTO != null && firstName.equals(profileDTO.getFirstName()) && lastName.equals(profileDTO.getLastName()) && email.equals(profileDTO.getEmail()) && dob.equals(oldDate)) {
                        nextPage();
                    } else {
                        Sex sex = male ? Sex.M : Sex.F;
                        UpdateProfileRequest updateProfileRequest = new UpdateProfileRequest(email, firstName, lastName, dob, sex);
                        pd.setMessage("Updating ...");
                        pd.show();
                        profileDetailsViewModel.hitUpdateProfileApi(getActivity(), authentication, updateProfileRequest).observe(getViewLifecycleOwner(), new Observer<ResponseDTO>() {
                            @Override
                            public void onChanged(ResponseDTO responseDTO) {
                                pd.dismiss();
                                switch (responseDTO.getStatus()) {
                                    case "success":
                                        nextPage();
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
                    } else if (dob.isEmpty()) {
                        // TODO check if is at least 16
                        Snackbar.make(view, "Enter your Date of Birth!", Snackbar.LENGTH_LONG).show();
                    } else if (!(male || female)) {
                        Snackbar.make(view, "Specify your Sex!", Snackbar.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    public void nextPage() {
        EditProfileIdentificationFragment editProfileIdentificationFragment = new EditProfileIdentificationFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, editProfileIdentificationFragment, EditProfileIdentificationFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }
}