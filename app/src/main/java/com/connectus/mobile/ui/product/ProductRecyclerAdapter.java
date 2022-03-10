package com.connectus.mobile.ui.product;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {

    @SuppressLint("SimpleDateFormat")
    DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm:ss");


    private Context context;
    private final List<ProductDto> products;

    public ProductRecyclerAdapter(Context context, List<ProductDto> products) {
        this.context = context;
        Collections.reverse(products);
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ProductDto productDto = products.get(position);
        holder.textViewName.setText(productDto.getName());
        holder.textViewDescription.setText(productDto.getDescription());
        holder.textViewCreated.setText(productDto.getCreated());
        byte[] decodedString = Base64.decode(productDto.getImageFirst(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        holder.imageViewProduct.setImageBitmap(decodedByte);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName, textViewDescription, textViewCreated;
        private final ImageView imageViewProduct;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_product_name);
            textViewDescription = itemView.findViewById(R.id.text_view_product_description);
            textViewCreated = itemView.findViewById(R.id.text_view_created);
            imageViewProduct = itemView.findViewById(R.id.image_view_product);
        }
    }
}

