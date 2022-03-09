package com.connectus.mobile.ui.old.siba.profile;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.api.dto.siba.SibaInvite;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.siba.SibaViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SibaProfileInvitesFragment extends Fragment {
    private static final String TAG = SibaProfileInvitesFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewProfileAvatar;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    SibaViewModel sibaViewModel;

    String authentication, sibaProfileId;
    ProfileDto profileDTO;
    SibaProfile sibaProfile;
    List<SibaInvite> sibaInvites;
    SibaProfileInvitesRecyclerAdapter sibaProfileInvitesRecyclerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sibaViewModel = new ViewModelProvider(this).get(SibaViewModel.class);
        Bundle arguments = getArguments();
        if (arguments != null) {
            sibaProfileId = arguments.getString("sibaProfileId");
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_siba_profile_invites, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (sibaProfileId == null) {
            getActivity().onBackPressed();
        }
        fragmentManager = getActivity().getSupportFragmentManager();
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        authentication = sharedPreferencesManager.getAuthenticationToken();
        profileDTO = sharedPreferencesManager.getProfile();


        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        fetchSibaProfile();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        sibaInvites = sibaProfile.getInvites();

        sibaProfileInvitesRecyclerAdapter = new SibaProfileInvitesRecyclerAdapter(getContext(), sibaInvites, fragmentManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewProfiles = view.findViewById(R.id.recycler_view_siba_profile_invites);
        recyclerViewProfiles.setAdapter(sibaProfileInvitesRecyclerAdapter);
        recyclerViewProfiles.setLayoutManager(linearLayoutManager);
    }

    public void fetchSibaProfile() {
        Log.d(TAG, sibaProfileId);
        sibaProfile = Common.getSibaProfileById(getContext(), sibaProfileId);
        if (sibaProfile == null) {
            getActivity().onBackPressed();
        }
    }
}