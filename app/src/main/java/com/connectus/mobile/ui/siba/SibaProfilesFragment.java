package com.connectus.mobile.ui.siba;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDTO;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.siba.profile.SibaProfileFragment;
import com.connectus.mobile.ui.siba.onboarding.CreateSibaProfileFragment;
import com.connectus.mobile.ui.siba.onboarding.MySibaInvitesFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SibaProfilesFragment extends Fragment {
    private static final String TAG = SibaProfilesFragment.class.getSimpleName();

    ImageView imageViewBack;
    ProgressDialog pd;
    FloatingActionButton fabCreateSibaGroup;


    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    SibaViewModel sibaViewModel;
    SibaProfilesRecyclerAdapter sibaProfilesRecyclerAdapter;
    List<SibaProfile> sibaProfiles;
    Button buttonSibaInvites;

    String authentication;
    ProfileDTO profileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sibaViewModel = new ViewModelProvider(this).get(SibaViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siba_profiles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();
        profileDTO = sharedPreferencesManager.getProfile();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        DbHandler dbHandler = new DbHandler(getContext());
        sibaProfiles = dbHandler.getSibaProfiles();
        sibaProfilesRecyclerAdapter = new SibaProfilesRecyclerAdapter(getContext(), sibaProfiles, fragmentManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewProfiles = view.findViewById(R.id.recycler_view_siba_profiles);
        recyclerViewProfiles.setAdapter(sibaProfilesRecyclerAdapter);
        recyclerViewProfiles.setLayoutManager(linearLayoutManager);

        getSibaProfiles();

        buttonSibaInvites = view.findViewById(R.id.button_siba_invites);
        buttonSibaInvites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySibaInvitesFragment mySibaInvitesFragment = new MySibaInvitesFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, mySibaInvitesFragment, MySibaInvitesFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        fabCreateSibaGroup = view.findViewById(R.id.fab_create_siba_group);
        fabCreateSibaGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateSibaProfileFragment createSibaProfileFragment = new CreateSibaProfileFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, createSibaProfileFragment, CreateSibaProfileFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getSibaProfiles();
    }

    public void getSibaProfiles() {
        sibaViewModel.hitGetMySibaProfiles(getContext(), authentication, profileDTO.getProfileId()).observe(getViewLifecycleOwner(), responseDTO -> {
            switch (responseDTO.getStatus()) {
                case "success":
                    sibaProfiles.clear();
                    DbHandler dbHandler = new DbHandler(getContext());
                    sibaProfiles.addAll(dbHandler.getSibaProfiles());
                    sibaProfilesRecyclerAdapter.notifyDataSetChanged();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
        });
    }

    public void openProfileFragment(int position) {
        SibaProfile sibaProfile = sibaProfiles.get(position);
        Bundle bundle = new Bundle();
        bundle.putString("sibaProfileId", sibaProfile.getId().toString());
        SibaProfileFragment sibaProfileFragment = new SibaProfileFragment();
        sibaProfileFragment.setArguments(bundle);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.container, sibaProfileFragment, SibaProfileFragment.class.getSimpleName());
        transaction.addToBackStack(TAG);
        transaction.commit();
    }
}