package com.connectus.mobile.ui.product;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class NavigationFragment extends Fragment {

    private static final String TAG = NavigationFragment.class.getSimpleName();


    ProgressDialog pd;
    ImageView imageViewBack;

    double currentLat, currentLng, destinationLat = 0, destinationLng = 0;

    FragmentManager fragmentManager;
    private SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        if (arguments != null) {
            currentLat = arguments.getDouble("currentLat");
            currentLng = arguments.getDouble("currentLng");
            destinationLat = arguments.getDouble("destinationLat");
            destinationLng = arguments.getDouble("destinationLng");
        } else {
            getActivity().onBackPressed();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_navigation, container, false);
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
        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());
    }
}