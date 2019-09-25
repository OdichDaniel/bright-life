package com.matchstick.brightlife.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matchstick.brightlife.R;
import com.matchstick.brightlife.activities.LeadDetailActivity;
import com.matchstick.brightlife.models.LeadSale;

import java.util.List;

public class LeadSaleAdapter extends RecyclerView.Adapter<LeadSaleAdapter.ViewHolder>{
    Context context;
    private List<LeadSale> leadSaleList;

    public LeadSaleAdapter(Context context, List<LeadSale> leadSaleList) {
        this.context = context;
        this.leadSaleList = leadSaleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_lead_sale, parent, false);
        return  new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeadSale leadSale = leadSaleList.get(position);
        holder.txtProductName.setText(leadSale.getProductName());
        holder.txtCustomerName.setText(leadSale.getCustomerName());
        holder.txtPhoneNumber.setText(leadSale.getCustomerTelephone());
        holder.leadSaleContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, LeadDetailActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return leadSaleList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPhoneNumber, txtCustomerName, txtProductName;
        RelativeLayout leadSaleContainer;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtPhoneNumber = (TextView) itemView.findViewById(R.id.customer_telephone);
            txtCustomerName = (TextView) itemView.findViewById(R.id.customer_name);
            txtProductName = (TextView) itemView.findViewById(R.id.txt_product_name);
            leadSaleContainer = (RelativeLayout) itemView.findViewById(R.id.lead_sale_container);
        }
    }
}
