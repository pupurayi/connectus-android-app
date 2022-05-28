package com.connectus.mobile.ui.serviceprovider;

import android.app.ProgressDialog;
import android.content.Context;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.ProductType;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.database.DbHandler;
import com.connectus.mobile.database.SharedPreferencesManager;
import com.connectus.mobile.utils.Utils;
import com.google.android.material.snackbar.Snackbar;

import java.util.LinkedList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class ServiceProvidersFragment extends Fragment {

    ProgressDialog pd;
    ImageView imageViewBack;

    ImageView imageViewAvatar;
    ServiceProvidersRecyclerAdapter serviceProvidersRecyclerAdapter;

    SharedPreferencesManager sharedPreferencesManager;
    FragmentManager fragmentManager;
    private ServiceProvidersViewModel serviceProvidersViewModel;
    UserDto userDto = null;
    List<UserDto> serviceProviders = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        serviceProvidersViewModel = new ViewModelProvider(this).get(ServiceProvidersViewModel.class);
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_service_providers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        pd = new ProgressDialog(getActivity());
        sharedPreferencesManager = new SharedPreferencesManager(getContext());
        userDto = sharedPreferencesManager.getUser();
        fragmentManager = getActivity().getSupportFragmentManager();


        imageViewBack = view.findViewById(R.id.image_view_back);
        imageViewBack.setOnClickListener(v -> getActivity().onBackPressed());

        imageViewAvatar = view.findViewById(R.id.ctf_image_view_profile_avatar);
        Utils.loadAvatar(userDto, imageViewAvatar);

        getServiceProviders();
        serviceProvidersRecyclerAdapter = new ServiceProvidersRecyclerAdapter(getContext(), serviceProviders, fragmentManager);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        RecyclerView recyclerViewProducts = view.findViewById(R.id.recycler_view_products);
        recyclerViewProducts.setAdapter(serviceProvidersRecyclerAdapter);
        recyclerViewProducts.setLayoutManager(linearLayoutManager);
    }

    private void getServiceProviders() {
        pd.setMessage("Fetching ...");
        pd.show();
        serviceProvidersViewModel.getServiceProviders(userDto.getId()).observe(getViewLifecycleOwner(), responseDto -> {
            switch (responseDto.getStatus()) {
                case "success":
                    pd.dismiss();
                    serviceProviders.clear();
                    List<UserDto> mProducts = (List<UserDto>) responseDto.getData();
                    serviceProviders.addAll(mProducts);
                    serviceProvidersRecyclerAdapter.notifyDataSetChanged();
                    break;
                case "failed":
                case "error":
                    pd.dismiss();
                    Utils.alert(getContext(), "Connect Us", responseDto.getMessage());
                    break;
            }
            pd.dismiss();
        });
    }
}