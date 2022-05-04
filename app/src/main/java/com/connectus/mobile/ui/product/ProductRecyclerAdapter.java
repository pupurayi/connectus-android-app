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
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.connectus.mobile.R;
import com.connectus.mobile.ui.dashboard.DashboardFragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {

    private Context context;
    private final List<ProductDto> products;
    private FragmentManager fragmentManager;

    public ProductRecyclerAdapter(Context context, List<ProductDto> products, FragmentManager fragmentManager) {
        this.context = context;
        Collections.reverse(products);
        this.products = products;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommended_product_list, parent, false);
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
        holder.productDto = products.get(position);
        holder.ratingBar.setRating(productDto.getRating());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewName, textViewDescription, textViewCreated;
        private final ImageView imageViewProduct;
        private final RatingBar ratingBar;
        private ProductDto productDto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.text_view_product_name);
            textViewDescription = itemView.findViewById(R.id.text_view_product_description);
            textViewCreated = itemView.findViewById(R.id.text_view_created);
            imageViewProduct = itemView.findViewById(R.id.image_view_product);
            ratingBar = itemView.findViewById(R.id.rating_bar);

            itemView.setOnClickListener(view -> {
                DashboardFragment dashboardFragment = (DashboardFragment) fragmentManager.findFragmentByTag(DashboardFragment.class.getSimpleName());
                dashboardFragment.navigateToProvider(productDto);
            });
        }
    }
}

