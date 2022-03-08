package com.connectus.mobile.ui.splashscreen;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.AuthorizationResponse;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.authorize.AuthorizeFragment;
import com.connectus.mobile.ui.signin.SignInFragment;
import com.connectus.mobile.ui.signup.SignUpFragment;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SplashScreenFragment extends Fragment {
    private static final String TAG = SplashScreenFragment.class.getSimpleName();

    FragmentManager fragmentManager;
    SharedPreferencesManager sharedPreferencesManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_splash_screen, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        sharedPreferencesManager = new SharedPreferencesManager(getContext());

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AuthorizationResponse authorizationResponse = sharedPreferencesManager.getAuthorization();
                Fragment fragment;
                String tag;
                if (authorizationResponse.isSuccess() && authorizationResponse.getUsername() != null) {
                    fragment = new SignInFragment();
                    tag = SignInFragment.class.getSimpleName();
                } else if (!authorizationResponse.isSuccess() && authorizationResponse.getUsername() != null) {
                    fragment = new SignUpFragment();
                    tag = SignUpFragment.class.getSimpleName();
                } else {
                    fragment = new AuthorizeFragment();
                    tag = AuthorizeFragment.class.getSimpleName();
                }
                transaction.add(R.id.container, fragment, tag);
                transaction.commit();
            }
        }.start();
    }
}