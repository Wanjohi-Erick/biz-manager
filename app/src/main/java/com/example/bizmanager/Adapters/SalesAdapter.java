package com.example.bizmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bizmanager.R;
import com.example.bizmanager.models.Sales;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SalesAdapter extends RecyclerView.Adapter<SalesAdapter.MyViewHolder> {
    List<Sales> salesList;
    public SalesAdapter(List<Sales> salesList) {
        this.salesList = salesList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        holder.date.setText(salesList.get(position).getDate());
        holder.particulars.setText(salesList.get(position).getParticulars());
        String amountStr = "Kes " + salesList.get(position).getAmount();
        holder.amount.setText(amountStr);
        holder.commodity.setText(salesList.get(position).getCommodity());
    }

    @Override
    public int getItemCount() {
        return salesList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, commodity, amount, particulars;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_view);
            commodity = itemView.findViewById(R.id.commodity);
            particulars = itemView.findViewById(R.id.particulars);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}
