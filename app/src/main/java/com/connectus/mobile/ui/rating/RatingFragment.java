package com.connectus.mobile.ui.rating;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.initial.signin.SignInFragment;
import com.connectus.mobile.utils.Utils;

public class RatingFragment extends Fragment {
    private static final String TAG = RatingFragment.class.getSimpleName();


    private RatingViewModel ratingViewModel;

    ProgressDialog pd;
    ImageView imageViewAvatar;

    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;

    private UserDto userDto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ratingViewModel = new ViewModelProvider(this).get(RatingViewModel.class);
        return inflater.inflate(R.layout.fragment_rating, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        userDto = sharedPreferencesManager.getUser();

        imageViewAvatar = view.findViewById(R.id.circular_image_view_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);
    }

    private void moveToDashboard() {
        DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
        if (dashboardFragment == null) {
            dashboardFragment = new DashboardFragment();
        }
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, dashboardFragment, DashboardFragment.class.getSimpleName());
        transaction.commit();
    }
}