package com.connectus.mobile.ui.serviceprovider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.api.dto.UserDto;
import com.connectus.mobile.ui.dashboard.DashboardFragment;
import com.connectus.mobile.ui.product.ProductDto;

import java.util.Collections;
import java.util.List;

public class ServiceProvidersRecyclerAdapter extends RecyclerView.Adapter<ServiceProvidersRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<UserDto> serviceProviders;
    private FragmentManager fragmentManager;

    public ServiceProvidersRecyclerAdapter(Context context, List<UserDto> serviceProviders, FragmentManager fragmentManager) {
        this.context = context;
        Collections.reverse(serviceProviders);
        this.serviceProviders = serviceProviders;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_provider_list, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserDto userDto = serviceProviders.get(position);
        holder.userDto = serviceProviders.get(position);
        if (userDto.getAvatar() != null) {
            byte[] decodedString = Base64.decode(userDto.getAvatar(), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            holder.imageViewServiceProviderAvatar.setImageBitmap(decodedByte);
        }
        holder.textViewFullName.setText(userDto.getFirstName() + " " + userDto.getLastName());
        holder.textViewEmail.setText(userDto.getEmail());
        holder.textViewMsisdn.setText(userDto.getMsisdn());
        holder.textViewLocation.setText(userDto.getTownship() + ", " + userDto.getTown());
        holder.textViewJoined.setText(userDto.getCreated());
    }

    @Override
    public int getItemCount() {
        return serviceProviders.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageViewServiceProviderAvatar;
        private final TextView textViewFullName, textViewEmail, textViewMsisdn, textViewLocation, textViewJoined;
        private UserDto userDto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewServiceProviderAvatar = itemView.findViewById(R.id.circular_image_view_service_provider_avatar);
            textViewFullName = itemView.findViewById(R.id.text_view_service_provider_name);
            textViewEmail = itemView.findViewById(R.id.text_view_service_provider_email_value);
            textViewMsisdn = itemView.findViewById(R.id.text_view_service_provider_msisdn_value);
            textViewLocation = itemView.findViewById(R.id.text_view_service_provider_location_value);
            textViewJoined = itemView.findViewById(R.id.text_view_service_provider_joined_value);

            itemView.setOnClickListener(view -> {
                DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
//                dashboardFragment.navigateToProvider(userDto);
            });
        }
    }
}

