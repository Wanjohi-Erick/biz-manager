package com.example.bizmanager.ui.view_sales;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    List<Sales> salesList = new ArrayList<>();
    Sales sales;
    String retrieve_sales_url = "http://josiekarimis.agria.co.ke/biz-manager/retrieveSales.php";
    private static final String TAG = "ViewSalesFragment";
    public TextView countView, sumView;
    int count = 0, sum = 0;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_sales, container, false);
        salesRecycler = view.findViewById(R.id.view_sales_recycler);
        countView = view.findViewById(R.id.count);
        sumView = view.findViewById(R.id.sum);
        salesRecycler.setHasFixedSize(true);
        salesRecycler.setLayoutManager(new LinearLayoutManager(this.getContext()));

        getFromDatabase();
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
                    String commodity = "Tomatoes";
                    String date = object.getString("date");
                    String amount = object.getString("amount");
                    sales = new Sales(id, date, particulars, commodity, amount);
                    salesList.add(sales);
                    salesAdapter = new SalesAdapter(salesList);
                    salesRecycler.setAdapter(salesAdapter);
                    count++;
                    sum += Integer.parseInt(amount);
                    countView.setText(String.valueOf(count));
                    String amountStr = "Kes " + sum;
                    sumView.setText(amountStr);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            Log.d(TAG, "onResponse: Error: " + error.getMessage());
        });
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
}