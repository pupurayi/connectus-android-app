package com.connectus.mobile.ui.old.services;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ServicesRecyclerAdapter extends RecyclerView.Adapter<ServicesRecyclerAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


    private Context context;
    private final List<EmalyamiService> services;
    private final FragmentManager fragmentManager;

    public ServicesRecyclerAdapter(Context context, List<EmalyamiService> services, FragmentManager fragmentManager) {
        this.context = context;
        this.services = services;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_service_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EmalyamiService service = services.get(position);
        holder.imageViewServiceIcon.setImageResource(service.getIconResource());
        holder.textViewServiceName.setText(service.getName());
        holder.textViewServiceDescription.setText(service.getDescription());
        holder.emalyamiService = service;
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageViewServiceIcon;
        private TextView textViewServiceName, textViewServiceDescription;
        private EmalyamiService emalyamiService;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewServiceIcon = (ImageView) itemView.findViewById(R.id.image_view_service_icon);
            textViewServiceName = (TextView) itemView.findViewById(R.id.text_view_service_name);
            textViewServiceDescription = (TextView) itemView.findViewById(R.id.text_view_service_description);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ServicesFragment servicesFragment = (ServicesFragment) fragmentManager.findFragmentByTag(ServicesFragment.class.getSimpleName());
                    servicesFragment.showFragmentById(emalyamiService.getId());
                }
            });
        }
    }
}

