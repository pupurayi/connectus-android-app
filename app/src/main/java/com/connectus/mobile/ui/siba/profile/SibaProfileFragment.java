package com.connectus.mobile.ui.siba.profile;

import android.annotation.SuppressLint;
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

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.siba.SibaProfile;
import com.connectus.mobile.common.Common;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.siba.SibaViewModel;
import com.connectus.mobile.ui.siba.chat.SibaChatFragment;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SibaProfileFragment extends Fragment {
    private static final String TAG = SibaProfileFragment.class.getSimpleName();

    ProgressDialog pd;
    ImageView imageViewBack, imageViewAdminSettings, imageViewProfileMembers, imageViewChat;
    TextView textViewProfileSubject, textViewProfileBalance, textViewProfileMembersCount, textViewProfileInvitesCount;
    Button buttonDeposit;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    SibaViewModel sibaViewModel;

    String authentication, sibaProfileId;
    ProfileDto profileDTO;
    SibaProfile sibaProfile;

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
        return inflater.inflate(R.layout.fragment_siba_profile, container, false);
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

        fetchSibaProfile();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        imageViewAdminSettings = view.findViewById(R.id.image_view_admin_settings);
        if (profileDTO.getProfileId().equals(sibaProfile.getAdminProfileId())) {
            imageViewAdminSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final CharSequence[] options = new CharSequence[]{"Invites", "Delete Profile"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Choose Option");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            SibaProfileMembersFragment sibaProfileMembersFragment = (SibaProfileMembersFragment) fragmentManager.findFragmentByTag(SibaProfileMembersFragment.class.getSimpleName());
                            if (options[item].equals("Invites")) {
//                                sibaProfileMembersFragment.joinProfile(position);
                            } else if (options[item].equals("Delete Profile")) {
//                                sibaProfileMembersFragment.declineInvite(position);
                            }
                        }
                    });
                    builder.show();
                }
            });
        } else {
            imageViewAdminSettings.setVisibility(View.GONE);
        }


        imageViewProfileMembers = view.findViewById(R.id.image_view_members);
        imageViewProfileMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("sibaProfileId", sibaProfileId);
                SibaProfileMembersFragment sibaProfileMembersFragment = new SibaProfileMembersFragment();
                sibaProfileMembersFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, sibaProfileMembersFragment, SibaProfileMembersFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        imageViewChat = view.findViewById(R.id.image_view_chat);
        imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("sibaProfileId", sibaProfileId);
                SibaChatFragment sibaChatFragment = new SibaChatFragment();
                sibaChatFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, sibaChatFragment, SibaChatFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        buttonDeposit = view.findViewById(R.id.button_deposit_to_siba);
        buttonDeposit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("sibaProfileId", sibaProfileId);
                SibaDepositFragment sibaDepositFragment = new SibaDepositFragment();
                sibaDepositFragment.setArguments(bundle);
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.add(R.id.container, sibaDepositFragment, SibaDepositFragment.class.getSimpleName());
                transaction.addToBackStack(TAG);
                transaction.commit();
            }
        });

        textViewProfileSubject = view.findViewById(R.id.text_view_siba_profile_subject);
        textViewProfileSubject.setText(sibaProfile.getSubject());
        textViewProfileBalance = view.findViewById(R.id.text_view_siba_profile_balance_value);
        @SuppressLint("DefaultLocale") String balance = String.format("%s %.2f", sibaProfile.getCurrency(), sibaProfile.getBalance());
        textViewProfileBalance.setText(balance);

        textViewProfileMembersCount = view.findViewById(R.id.text_view_siba_profile_members_value);
        String membersCount = String.valueOf(sibaProfile.getMembers().size());
        textViewProfileMembersCount.setText(membersCount);

        textViewProfileInvitesCount = view.findViewById(R.id.text_view_siba_profile_invites_value);
        String invitesCount = String.valueOf(sibaProfile.getInvites().size());
        textViewProfileInvitesCount.setText(invitesCount);
    }

    @Override
    public void onResume() {
        super.onResume();
        fetchSibaProfile();
    }

    public void fetchSibaProfile() {
        Log.d(TAG, sibaProfileId);
        sibaProfile = Common.getSibaProfileById(getContext(), sibaProfileId);
        if (sibaProfile == null) {
            getActivity().onBackPressed();
        }
    }
}