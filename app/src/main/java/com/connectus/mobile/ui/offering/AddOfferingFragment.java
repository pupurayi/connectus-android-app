package com.connectus.mobile.ui.offering;

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
import com.connectus.mobile.api.dto.NewOfferingDto;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AddOfferingFragment extends Fragment {

    private static final String TAG = AddOfferingFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    EditText editTextOfferingName, editTextOfferingDescription;
    Button buttonSaveOffering;

    FragmentManager fragmentManager;
    private OfferingViewModel offeringViewModel;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        offeringViewModel = new ViewModelProvider(this).get(OfferingViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_offering, container, false);
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

        editTextOfferingName = view.findViewById(R.id.edit_text_offering_name);
        editTextOfferingDescription = view.findViewById(R.id.edit_text_offering_description);

        imageViewProfileAvatar = view.findViewById(R.id.uaf_image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/avatar/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());


        buttonSaveOffering = view.findViewById(R.id.button_save_offering);
        buttonSaveOffering.setOnClickListener(v -> {
            String name = editTextOfferingName.getText().toString();
            String description = editTextOfferingDescription.getText().toString();

            if (!name.isEmpty() && !description.isEmpty()) {

                NewOfferingDto newOfferingDto = new NewOfferingDto(name, description);
                pd.setMessage("Updating ...");
                pd.show();
                offeringViewModel.hitSaveOfferingApi(getActivity(), authentication, newOfferingDto).observe(getViewLifecycleOwner(), responseDTO -> {
                    pd.dismiss();
                    switch (responseDTO.getStatus()) {
                        case "success":
                        case "failed":
                        case "error":
                            Snackbar.make(view, responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                            break;
                    }
                });
            } else {
                if (name.isEmpty()) {
                    Snackbar.make(view, "Offering Name cannot be null!", Snackbar.LENGTH_LONG).show();
                } else if (description.isEmpty()) {
                    Snackbar.make(view, "Offering Description cannot be null!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }
}