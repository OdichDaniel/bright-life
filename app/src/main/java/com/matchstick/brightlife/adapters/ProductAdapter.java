package com.matchstick.brightlife.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.matchstick.brightlife.R;
import com.matchstick.brightlife.models.Product;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {
    Context context;
    public ProductAdapter(Context context, List<Product> productArrayList){
        super(context, 0, productArrayList);
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if (convertView == null){
            convertView = LayoutInflater.from((getContext())).inflate(R.layout.customer_product_spinner, parent, false);
        }

        ImageView productImageView = convertView.findViewById(R.id.product_image);
        TextView productName = convertView.findViewById(R.id.txt_product_name);
        Product currentProduct = getItem(position);
        if (currentProduct != null) {
            RequestOptions requestOptions = new RequestOptions();
            requestOptions.override(612, 612);
            requestOptions.diskCacheStrategy(DiskCacheStrategy.DATA);
            Glide.with(context)
                    .load(currentProduct.getImage())
                    .apply(requestOptions)
                    .into(productImageView);
            productName.setText(currentProduct.getProductName());
        }
        return convertView;
    }
}
