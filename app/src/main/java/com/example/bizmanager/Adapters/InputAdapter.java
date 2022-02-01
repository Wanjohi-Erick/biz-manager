package com.example.bizmanager.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.R;
import com.example.bizmanager.models.Input;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.MyViewHolder> {
    List<Input> inputList;
    public InputAdapter(List<Input> inputList) {
        this.inputList = inputList;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_input_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull MyViewHolder holder, int position) {
        String id = inputList.get(position).getId();
        holder.date.setText(inputList.get(position).getDate());
        holder.quantity.setText(inputList.get(position).getQuantity());
        String amountStr = "Kes " + inputList.get(position).getTotalPrice();
        holder.totalPrice.setText(amountStr);
        holder.commodity.setText(inputList.get(position).getCommodity());
        holder.itemView.setOnLongClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(v.getContext());
            alertDialogBuilder.setTitle("Warning");
            alertDialogBuilder.setMessage("Are you  sure you want to delete this item?");
            alertDialogBuilder.setPositiveButton("Yes", (dialog, which) -> {
                String update_url = "http://biz-manager.agria.co.ke/deleteInput.php";
                StringRequest stringRequest = new StringRequest(Request.Method.POST, update_url, response -> {
                    if (response.equalsIgnoreCase("successfully deleted")) {
                        alertDialogBuilder.setTitle("Server Response");
                        alertDialogBuilder.setMessage(response);
                        alertDialogBuilder.setPositiveButton("Ok", (dialog1, which1) -> {
                            dialog1.dismiss();
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    } else {
                        alertDialogBuilder.setTitle("Server Response");
                        alertDialogBuilder.setMessage(response);
                        alertDialogBuilder.setPositiveButton("Ok", (dialog1, which1) -> {
                            dialog1.dismiss();
                        });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();
                    }
                }, error -> {
                    alertDialogBuilder.setTitle("Server Response");
                    alertDialogBuilder.setTitle(error.getMessage());
                    alertDialogBuilder.setPositiveButton("Ok", (dialog1, which1) -> {
                        dialog1.dismiss();
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }){
                    @Nullable
                    @org.jetbrains.annotations.Nullable
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> data = new HashMap<>();
                        data.put("position", id);
                        return data;
                    }
                };
                stringRequest.setShouldCache(false);
                RequestQueue requestQueue = Volley.newRequestQueue(v.getContext());
                requestQueue.add(stringRequest);
                dialog.dismiss();
            });
            alertDialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return inputList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView date, commodity, quantity, totalPrice;
        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date_view);
            commodity = itemView.findViewById(R.id.commodity);
            quantity = itemView.findViewById(R.id.quantity);
            totalPrice = itemView.findViewById(R.id.total_price);
        }
    }
}
