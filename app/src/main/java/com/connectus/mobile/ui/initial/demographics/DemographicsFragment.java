package com.connectus.mobile.ui.initial.demographics;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.rating.RatingFragment;
import com.connectus.mobile.ui.user.UserViewModel;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DemographicsFragment extends Fragment {
    private static final String TAG = DemographicsFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAvatar;

    DatePickerDialog dobPicker;
    EditText editTextGender, editTextDob, editTextEthnicity, editTextReligion, editTextTownship, editTextTown;
    Button buttonSave;

    FragmentManager fragmentManager;
    private UserViewModel userViewModel;
    private SharedPreferencesManager sharedPreferencesManager;
    UserDto userDto = null;
    private boolean pickingGender = false, pickingDob = false, pickingEthnicity = false, pickingReligion = false, pickingTown = false;
    private Date dob = null;

    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat shortDateFormat = new SimpleDateFormat("dd-MM-yyyy");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
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
        this.userDto = sharedPreferencesManager.getUser();
        if (userDto.getDob() != null) {
            try {
                dob = shortDateFormat.parse(userDto.getDob());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        imageViewAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);

        editTextGender = view.findViewById(R.id.edit_text_gender);
        if (userDto.getGender() != null) {
            editTextGender.setText(userDto.getGender());
        }
        editTextDob = view.findViewById(R.id.edit_text_dob);
        if (userDto.getDob() != null) {
            editTextDob.setText(userDto.getDob());
        }
        editTextEthnicity = view.findViewById(R.id.edit_text_ethnicity);
        if (userDto.getEthnicity() != null) {
            editTextDob.setText(userDto.getEthnicity());
        }

        editTextReligion = view.findViewById(R.id.edit_text_religion);
        if (userDto.getReligion() != null) {
            editTextReligion.setText(userDto.getReligion());
        }
        editTextTownship = view.findViewById(R.id.edit_text_township);
        if (userDto.getTownship() != null) {
            editTextTownship.setText(userDto.getTownship());
        }
        editTextTown = view.findViewById(R.id.edit_text_town);
        if (userDto.getTown() != null) {
            editTextTown.setText(userDto.getTown());
        }

        editTextGender.setInputType(InputType.TYPE_NULL);
        editTextGender.setOnTouchListener((v, event) -> {
            if (!pickingGender) {
                pickingGender = true;
                String[] genders = {"Male", "Female"};
                CharSequence[] options = new CharSequence[genders.length];
                for (int i = 0; i < genders.length; i++) {
                    options[i] = genders[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.gender));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    pickingGender = false;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextGender.setText(option);
                    pickingGender = false;
                });
                builder.show();
            }
            return true;
        });

        editTextDob.setInputType(InputType.TYPE_NULL);
        editTextDob.setOnTouchListener((v, event) -> {
            if (!pickingDob) {
                pickingDob = true;
                // TODO Age Limit 9
                Calendar calendar = Calendar.getInstance();
                int year;
                if (dob != null) {
                    calendar.setTime(dob);
                    year = calendar.get(Calendar.YEAR);
                } else {
                    year = calendar.get(Calendar.YEAR) - 9;
                }
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                int month = calendar.get(Calendar.MONTH);

                dobPicker = new DatePickerDialog(getContext(), (view1, y, m, d) -> {
                    @SuppressLint("DefaultLocale") String mDob = String.format("%02d-%02d-%d", d, m + 1, y);
                    editTextDob.setText(mDob);
                    pickingDob = false;
                }, year, month, day);
                dobPicker.setOnCancelListener(dialog -> pickingDob = false);
                dobPicker.show();
            }
            return false;
        });

        editTextEthnicity.setInputType(InputType.TYPE_NULL);
        editTextEthnicity.setOnTouchListener((v, event) -> {
            if (!pickingEthnicity) {
                pickingEthnicity = true;
                String[] ethnic_groups = {"Shona", "Ndebele", "Tonga", "Asian", "Caucasian", "Other"};
                CharSequence[] options = new CharSequence[ethnic_groups.length];
                for (int i = 0; i < ethnic_groups.length; i++) {
                    options[i] = ethnic_groups[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.ethnicity));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    pickingEthnicity = false;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextEthnicity.setText(option);
                    pickingEthnicity = false;
                });
                builder.show();
            }
            return true;
        });

        editTextReligion.setInputType(InputType.TYPE_NULL);
        editTextReligion.setOnTouchListener((v, event) -> {
            if (!pickingReligion) {
                pickingReligion = true;
                String[] religions = {"Christian", "Islam", "Hinduism", "Buddhism", "Atheist", "Other"};
                CharSequence[] options = new CharSequence[religions.length];
                for (int i = 0; i < religions.length; i++) {
                    options[i] = religions[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.religion));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    pickingReligion = false;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextReligion.setText(option);
                    pickingReligion = false;
                });
                builder.show();
            }
            return true;
        });

        editTextTown.setInputType(InputType.TYPE_NULL);
        editTextTown.setOnTouchListener((v, event) -> {
            if (!pickingTown) {
                pickingTown = true;

                String[] towns = {"Harare", "Bulawayo", "Chitungwiza", "Mutare", "Gweru", "Epworth",
                        "Gweru", "Kwekwe", "Kadoma", "Masvingo", "Chinhoyi", "Norton", "Marondera",
                        "Ruwa", "Chegutu", "Zvishavane", "Bindura", "Beitbridge", "Redcliff", "Victoria Falls", "Hwange", "Rusape", "Chiredzi", "Kariba", "Karoi", "Chipinge", "Gokwe", "Shurugwi"};


                CharSequence[] options = new CharSequence[towns.length];
                for (int i = 0; i < towns.length; i++) {
                    options[i] = towns[i];
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.town));
                builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> {
                    pickingTown = false;
                    dialog.dismiss();
                });
                builder.setItems(options, (dialog, item) -> {
                    String option = (String) options[item];
                    editTextTown.setText(option);
                    pickingTown = false;
                });
                builder.show();
            }
            return true;
        });

        buttonSave = view.findViewById(R.id.button_save);
        buttonSave.setOnClickListener(v -> {
            String gender = editTextGender.getText().toString().trim();
            String mDob = editTextDob.getText().toString().trim();
            String ethnicity = editTextEthnicity.getText().toString().trim();
            String religion = editTextReligion.getText().toString().trim();
            String township = editTextTownship.getText().toString().trim();
            String town = editTextTown.getText().toString().trim();

            if (!TextUtils.isEmpty(gender) && !TextUtils.isEmpty(mDob) && !TextUtils.isEmpty(ethnicity) && !TextUtils.isEmpty(religion) && !TextUtils.isEmpty(township) && !TextUtils.isEmpty(town)) {
                userDto.setGender(gender);
                userDto.setDob(mDob);
                userDto.setEthnicity(ethnicity);
                userDto.setReligion(religion);
                userDto.setTownship(township);
                userDto.setTown(town);

                userViewModel.hitUpdateUser(getActivity(), userDto).observe(getViewLifecycleOwner(), responseDto -> {
                    pd.dismiss();
                    switch (responseDto.getStatus()) {
                        case "success":
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            RatingFragment ratingFragment = new RatingFragment();
                            transaction.replace(R.id.container, ratingFragment, RatingFragment.class.getSimpleName());
                            transaction.commit();
                            break;
                        case "failed":
                        case "error":
                            Snackbar.make(getView(), responseDto.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });

            } else {
                if (TextUtils.isEmpty(gender)) {
                    Snackbar.make(view, "Enter your gender!", Snackbar.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(mDob)) {
                    Snackbar.make(view, "Enter your Date of Birth!", Snackbar.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(ethnicity)) {
                    Snackbar.make(view, "Enter your ethnic background!", Snackbar.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(religion)) {
                    Snackbar.make(view, "Enter your religion!", Snackbar.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(township)) {
                    Snackbar.make(view, "Enter your township!", Snackbar.LENGTH_LONG).show();
                } else if (TextUtils.isEmpty(town)) {
                    Snackbar.make(view, "Enter your town!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}