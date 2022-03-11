package com.connectus.mobile.ui.old.siba.onboarding;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.siba.CreateSibaProfileDTO;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.old.siba.EligibilityResponse;
import com.connectus.mobile.ui.old.siba.SibaViewModel;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class CreateSibaProfileFragment extends Fragment {
    private static final String TAG = CreateSibaProfileFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAddMember, imageViewProfileAvatar;
    EditText editTextProfileSubject;
    Button buttonCreateSibaProfile;


    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    SibaOnBoardingInvitesRecyclerAdapter sibaOnBoardingInvitesRecyclerAdapter;
    private SibaViewModel sibaViewModel;


    String authentication;
    ProfileDto profileDTO;
    public List<EligibilityResponse> onBoardingInvites;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        sibaViewModel = new ViewModelProvider(this).get(SibaViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_siba_profile, container, false);
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
                    .placeholder(R.drawable.avatar)
                    .error(R.drawable.avatar)
                    .into(imageViewProfileAvatar);
        }


        onBoardingInvites = new LinkedList<>();
        sibaOnBoardingInvitesRecyclerAdapter = new SibaOnBoardingInvitesRecyclerAdapter(getContext(), onBoardingInvites, fragmentManager);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewNotifications = view.findViewById(R.id.recycler_view_invited_members);
        recyclerViewNotifications.setAdapter(sibaOnBoardingInvitesRecyclerAdapter);
        recyclerViewNotifications.setLayoutManager(linearLayoutManager);

        editTextProfileSubject = view.findViewById(R.id.edit_text_profile_subject);
        buttonCreateSibaProfile = view.findViewById(R.id.button_create_siba_profile);
        buttonCreateSibaProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = editTextProfileSubject.getText().toString();
                if (subject.length() > 0 && onBoardingInvites.size() >= 3) {
                    pd.setMessage("Creating Profile ...");
                    pd.show();
                    List<Long> invites = new LinkedList<>();
                    for (EligibilityResponse eligibilityResponse : onBoardingInvites) {
                        invites.add(eligibilityResponse.getProfileId());
                    }
                    sibaViewModel.hitCreateSibaProfileApi(authentication, new CreateSibaProfileDTO(subject, "ZAR", invites)).observe(getViewLifecycleOwner(), responseDTO -> {
                        switch (responseDTO.getStatus()) {
                            case "success":
                                getActivity().onBackPressed();
                                break;
                            case "failed":
                            case "error":
                                AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                                alertDialog.setTitle("ConnectUs Alert");
                                alertDialog.setMessage(responseDTO.getMessage());
                                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        });
                                alertDialog.show();
                                break;
                        }
                        pd.dismiss();
                    });
                } else {
                    if (subject.length() == 0) {
                        Toast.makeText(getContext(), "Please enter Profile Subject!", Toast.LENGTH_LONG).show();
                    } else {
                        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
                        alertDialog.setTitle("ConnectUs Alert");
                        alertDialog.setMessage("Please add at lease 3 invites!");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
            }
        });

        imageViewAddMember = view.findViewById(R.id.image_view_invite_member);
        imageViewAddMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InviteSibaMemberFragment inviteSibaMemberFragment = new InviteSibaMemberFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, inviteSibaMemberFragment, InviteSibaMemberFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
    }

    public void addInviteToView(EligibilityResponse eligibilityResponse) {
        onBoardingInvites.add(eligibilityResponse);
        sibaOnBoardingInvitesRecyclerAdapter.notifyDataSetChanged();
    }

    public void remoteInviteToView(int position) {
        onBoardingInvites.remove(position);
        sibaOnBoardingInvitesRecyclerAdapter.notifyDataSetChanged();
    }
}