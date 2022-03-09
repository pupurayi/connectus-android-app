package com.connectus.mobile.ui.offering;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProfileDto;
import com.connectus.mobile.api.dto.ResponseDTO;
import com.connectus.mobile.common.Constants;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.ui.initial.check.CheckFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class OfferingsFragment extends Fragment {

    ProgressDialog pd;
    ImageView imageViewBack;

    ImageView imageViewProfileAvatar;
    OfferingsRecyclerAdapter offeringsRecyclerAdapter;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    private OfferingViewModel offeringViewModel;
    List<OfferingDto> offerings = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        offeringViewModel = new ViewModelProvider(this).get(OfferingViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_offering, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        fragmentManager = getActivity().getSupportFragmentManager();
        String authentication = sharedPreferencesManager.getAuthenticationToken();

        fetchOfferings(getContext(), authentication);
        ProfileDto profileDTO = sharedPreferencesManager.getProfile();

        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        imageViewProfileAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        if (profileDTO.isAvatarAvailable()) {
            Picasso.get()
                    .load(Constants.CORE_BASE_URL + "/api/v1/user/profile-picture/" + profileDTO.getId() + ".png")
                    .placeholder(R.drawable.account_circle_gold)
                    .error(R.drawable.account_circle_gold)
                    .into(imageViewProfileAvatar);
        }

        DbHandler dbHandler = new DbHandler(getContext());
        offerings = dbHandler.getOfferings();
        offeringsRecyclerAdapter = new OfferingsRecyclerAdapter(getContext(), offerings);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewOfferings = view.findViewById(R.id.recycler_view_offerings);
        recyclerViewOfferings.setAdapter(offeringsRecyclerAdapter);
        recyclerViewOfferings.setLayoutManager(linearLayoutManager);
    }

    private void fetchOfferings(Context context, String authentication) {
        offeringViewModel.getOfferings(context, authentication).observe(getViewLifecycleOwner(), responseDTO -> {
            switch (responseDTO.getStatus()) {
                case "success":
                    DbHandler dbHandler = new DbHandler(getContext());
                    offerings = dbHandler.getOfferings();
                    offeringsRecyclerAdapter.notifyDataSetChanged();
                    Snackbar.make(getView(), responseDTO.getMessage(), Snackbar.LENGTH_LONG).show();
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