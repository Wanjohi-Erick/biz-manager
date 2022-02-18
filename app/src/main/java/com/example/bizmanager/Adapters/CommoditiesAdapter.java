package com.example.bizmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bizmanager.R;
import com.example.bizmanager.models.Commodities;

import java.util.List;

public class CommoditiesAdapter extends RecyclerView.Adapter<CommoditiesAdapter.MyViewHolder> {
    List<Commodities> commoditiesList;
    public CommoditiesAdapter(List<Commodities> commoditiesList) {
        this.commoditiesList = commoditiesList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_commodities_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String id, commodity, pCost, uPrice;
        id = commoditiesList.get(position).getID();
        commodity = commoditiesList.get(position).getName();
        pCost = commoditiesList.get(position).getProduction_cost();
        uPrice = commoditiesList.get(position).getUnit_price();

        holder.IDView.setText(id);
        holder.nameView.setText(commodity);
        holder.productionCostView.setText(String.format("PC: %s", pCost));
        holder.unitPriceView.setText(String.format("SP: %s", uPrice));
    }

    @Override
    public int getItemCount() {
        return commoditiesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView IDView, nameView, productionCostView, unitPriceView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            IDView = itemView.findViewById(R.id.id_view);
            nameView = itemView.findViewById(R.id.commodity);
            productionCostView = itemView.findViewById(R.id.production_cost);
            unitPriceView = itemView.findViewById(R.id.unit_price);
        }
    }
}
