package com.connectus.mobile.ui.old.siba.onboarding;

import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.siba.MySibaInvite;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.siba.SibaViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class MySibaInvitesFragment extends Fragment {
    private static final String TAG = MySibaInvitesFragment.class.getSimpleName();

    ImageView imageViewBack, imageViewProfileAvatar;
    ProgressDialog pd;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    SibaViewModel sibaViewModel;
    MySibaInvitesRecyclerAdapter mySibaInvitesRecyclerAdapter;
    List<MySibaInvite> sibaInvites;

    String authentication;
    ProfileDto profileDTO;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sibaViewModel = new ViewModelProvider(this).get(SibaViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_siba_invites, container, false);
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

        imageViewProfileAvatar = view.findViewById(R.id.image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        DbHandler dbHandler = new DbHandler(getContext());
        sibaInvites = dbHandler.getMySibaInvites();

        mySibaInvitesRecyclerAdapter = new MySibaInvitesRecyclerAdapter(getContext(), sibaInvites, fragmentManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewProfiles = view.findViewById(R.id.recycler_view_siba_my_invites);
        recyclerViewProfiles.setAdapter(mySibaInvitesRecyclerAdapter);
        recyclerViewProfiles.setLayoutManager(linearLayoutManager);

        getMySibaInvites();
    }

    @Override
    public void onResume() {
        super.onResume();
        getMySibaInvites();
    }

    public void getMySibaInvites() {
        pd.setMessage("Updating Invites ...");
        pd.show();
        sibaViewModel.hitGetMySibaInvites(getContext(), authentication, profileDTO.getProfileId()).observe(getViewLifecycleOwner(), responseDTO -> {
            switch (responseDTO.getStatus()) {
                case "success":
                    sibaInvites.clear();
                    DbHandler dbHandler = new DbHandler(getContext());
                    sibaInvites.addAll(dbHandler.getMySibaInvites());
                    mySibaInvitesRecyclerAdapter.notifyDataSetChanged();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
            pd.dismiss();
        });
    }

    public void joinProfile(int position) {
        MySibaInvite mySibaInvite = sibaInvites.get(position);
        pd.setMessage("Joining ...");
        pd.show();
        sibaViewModel.hitActionInvite(authentication, profileDTO.getProfileId(), mySibaInvite.getInvite(), "join").observe(getViewLifecycleOwner(), responseDTO -> {
            switch (responseDTO.getStatus()) {
                case "success":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    getMySibaInvites();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
            pd.dismiss();
        });
    }

    public void declineInvite(int position) {
        MySibaInvite mySibaInvite = sibaInvites.get(position);
        pd.setMessage("Please Wait ...");
        pd.show();
        sibaViewModel.hitActionInvite(authentication, profileDTO.getProfileId(), mySibaInvite.getInvite(), "decline").observe(getViewLifecycleOwner(), responseDTO -> {
            switch (responseDTO.getStatus()) {
                case "success":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    getMySibaInvites();
                    break;
                case "failed":
                case "error":
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
                    break;
            }
            pd.dismiss();
        });
    }
}