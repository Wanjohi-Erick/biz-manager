package com.example.bizmanager.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.Adapters.SalesAdapter;
import com.example.bizmanager.R;
import com.example.bizmanager.models.Sales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewSalesFragment extends Fragment {
    private RecyclerView salesRecycler;
    SalesAdapter salesAdapter;
    SwipeRefreshLayout swipeRefreshLayout;
    List<Sales> salesList = new ArrayList<>();
    Sales sales;
    AlertDialog.Builder alertDialogBuilder;
    String retrieve_sales_url = "http://biz-manager.agria.co.ke/retrieveSales.php";
    private static final String TAG = "ViewSalesFragment";
    public TextView countView, sumView;
    int count = 0, sum = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_sales, container, false);
        salesRecycler = view.findViewById(R.id.view_sales_recycler);
        countView = view.findViewById(R.id.count);
        sumView = view.findViewById(R.id.sum);
        swipeRefreshLayout = view.findViewById(R.id.refreshLayout);
        alertDialogBuilder = new AlertDialog.Builder(requireContext());
        swipeRefreshLayout.setOnRefreshListener(this::getFromDatabase);
        swipeRefreshLayout.setColorSchemeResources(R.color.design_default_color_primary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);
        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            getFromDatabase();
        });
        salesRecycler.setHasFixedSize(true);
        salesRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));
        return view;
    }

    private void getFromDatabase() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, retrieve_sales_url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.getString("id");
                    String particulars = object.getString("particulars");
                    String commodity = object.getString("Commodity");
                    String date = object.getString("date");
                    String quantity = object.getString("Quantity");
                    String unitPrice = object.getString("Unit Price");
                    String totalPrice = object.getString("Total Price");
                    String paymentMethod = object.getString("Payment Method");
                    String creditStatus = object.getString("Credit Status");
                    sales = new Sales(id, date, particulars, commodity, quantity, unitPrice, totalPrice, paymentMethod, creditStatus);
                    salesList.add(sales);
                    salesAdapter = new SalesAdapter(salesList);
                    salesRecycler.setAdapter(salesAdapter);
                    count++;
                    sum += Integer.parseInt(totalPrice);
                    countView.setText(String.valueOf(count));
                    String amountStr = "Kes " + sum;
                    sumView.setText(amountStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            swipeRefreshLayout.setRefreshing(false);
        }, error -> {
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setMessage(error.getLocalizedMessage() + "\n" + "Check your internet connection and try again");
            alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            swipeRefreshLayout.setRefreshing(false);
        });
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
}