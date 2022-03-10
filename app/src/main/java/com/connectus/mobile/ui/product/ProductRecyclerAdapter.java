package com.connectus.mobile.ui.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<com.connectus.mobile.ui.product.ProductRecyclerAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


    private Context context;
    private final List<ProductDto> notifications;

    public ProductRecyclerAdapter(Context context, List<ProductDto> notifications) {
        this.context = context;
        Collections.reverse(notifications);
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDto productDto = notifications.get(position);
        holder.textViewName.setText(productDto.getName());
        holder.textViewDescription.setText(productDto.getDescription());
        holder.textViewCreated.setText(productDto.getCreated());
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName, textViewDescription, textViewCreated;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_product_name);
            textViewDescription = itemView.findViewById(R.id.text_view_product_description);
            textViewCreated = itemView.findViewById(R.id.text_view_created);
        }
    }
}

