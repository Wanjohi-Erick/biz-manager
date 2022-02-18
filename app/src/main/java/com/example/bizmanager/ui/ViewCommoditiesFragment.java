package com.example.bizmanager.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.Adapters.CommoditiesAdapter;
import com.example.bizmanager.InternetConnection;
import com.example.bizmanager.R;
import com.example.bizmanager.models.Commodities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewCommoditiesFragment extends Fragment {
    String get_commodities_url = "http://biz-manager.agria.co.ke/retrieveCommodities.php";
    //String get_commodities_url = "http://192.168.0.111/biz-manager/retrieveCommodities.php";
    String record_commodities_url = "http://biz-manager.agria.co.ke/recordCommodity.php";
    //String record_commodities_url = "http://192.168.0.111/biz-manager/recordCommodity.php";
    private FloatingActionButton addCommodityFab;
    private RecyclerView commoditiesRecycler;
    ProgressDialog progressDialog;
    AlertDialog.Builder builder;
    Commodities commodities;
    CommoditiesAdapter commoditiesAdapter;
    List<Commodities> commoditiesList = new ArrayList<>();
    EditText commodityEdit, pCostEdit, sPriceEdit;
    String commodity, productionCost, sellingPrice;
    Button cancelBtn, saveBtn;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_commodities, container, false);
        addCommodityFab = view.findViewById(R.id.add_commodity_fab);
        commoditiesRecycler = view.findViewById(R.id.commodity_recycler);
        builder = new AlertDialog.Builder(getContext());
        progressDialog = new ProgressDialog(getContext());

        boolean isConnected = InternetConnection.checkConnection(getContext());
        if (isConnected) {
            retrieveCommodities();
            progressDialog.setTitle("Loading");
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        } else {
            builder.setTitle("Error");
            builder.setMessage("No Internet Connection");
            builder.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }

        addCommodityFab.setOnClickListener(v -> recordNewCommodity());

        return view;
    }

    private void recordNewCommodity() {
        Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.record_commodity_layout);
        commodityEdit = dialog.findViewById(R.id.commodityEdit);
        pCostEdit = dialog.findViewById(R.id.production_cost_edit);
        sPriceEdit = dialog.findViewById(R.id.unit_price_edit);
        cancelBtn = dialog.findViewById(R.id.cancel_button);
        saveBtn = dialog.findViewById(R.id.save_button);

        cancelBtn.setOnClickListener(v -> dialog.dismiss());
        saveBtn.setOnClickListener(v -> {
            commodity = commodityEdit.getText().toString();
            productionCost = pCostEdit.getText().toString();
            sellingPrice = sPriceEdit.getText().toString();

            if (validFields(commodity, productionCost, sellingPrice)) {
                sendToDatabase(commodity, productionCost, sellingPrice, dialog);
                progressDialog.setTitle("Loading");
                progressDialog.setMessage("Please wait...");
                progressDialog.show();
            }
        });

        dialog.show();
    }

    private void sendToDatabase(String commodity, String productionCost, String sellingPrice, Dialog dialogDialog) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, record_commodities_url, response -> {
            if (response.equalsIgnoreCase("success")) {
                progressDialog.dismiss();
                builder.setTitle("Server Response");
                builder.setMessage(response);
                builder.setPositiveButton("ok", (dialog, which) -> {
                    dialog.dismiss();
                    dialogDialog.dismiss();
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                progressDialog.dismiss();
                builder.setTitle("Server Response");
                builder.setMessage(response);
                builder.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }, error -> {
            progressDialog.dismiss();
            builder.setTitle("Error");
            builder.setMessage(error.getLocalizedMessage());
            builder.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        }){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Commodity", commodity);
                params.put("ProductionCost", productionCost);
                params.put("UnitPrice", sellingPrice);
                return params;
            }
        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private boolean validFields(String commodity, String productionCost, String sellingPrice) {
        if (TextUtils.isEmpty(commodity)) {
            commodityEdit.setError("Input Required!");
            commodityEdit.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(productionCost)) {
            pCostEdit.setError("Input Required!");
            pCostEdit.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(sellingPrice)) {
            sPriceEdit.setError("Input Required!");
            sPriceEdit.requestFocus();
            return false;
        }

        return true;
    }

    private void retrieveCommodities() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, get_commodities_url, response -> {
            progressDialog.dismiss();
            try {
                JSONArray jsonArray = new JSONArray(response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.getString("ID");
                    String name = object.getString("Name");
                    String production_cost = object.getString("Production Cost");
                    String unitPrice = object.getString("Unit Price");
                    commodities = new Commodities(id, name, production_cost, unitPrice);
                    commoditiesList.add(commodities);

                    commoditiesRecycler.setHasFixedSize(true);
                    commoditiesRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    commoditiesAdapter = new CommoditiesAdapter(commoditiesList);
                    commoditiesRecycler.setAdapter(commoditiesAdapter);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            progressDialog.dismiss();
            builder.setTitle("Error");
            builder.setMessage(error.getLocalizedMessage());
            builder.setPositiveButton("ok", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}