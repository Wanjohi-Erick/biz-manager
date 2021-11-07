package com.example.bizmanager.ui.home;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.net.ConnectivityManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.InternetConnection;
import com.example.bizmanager.R;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    private TextView commodityEdit, particularsEdit, amountEdit;
    String commodity, particulars, amount;
    String record_sale_url = "http://josiekarimis.agria.co.ke/biz-manager/recordSale.php";
    private static final String TAG = "HomeFragment";
    AlertDialog.Builder alertDialogBuilder;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button saveRecordBtn;
        commodityEdit = view.findViewById(R.id.commodity);
        particularsEdit = view.findViewById(R.id.particulars);
        amountEdit = view.findViewById(R.id.amount);
        saveRecordBtn = view.findViewById(R.id.save_record);
        alertDialogBuilder = new AlertDialog.Builder(this.requireContext());
        progressDialog = new ProgressDialog(getContext());

        saveRecordBtn.setOnClickListener(v -> {
            if (InternetConnection.checkConnection(requireContext())) {
                getFromEditTexts(commodityEdit, particularsEdit, amountEdit);
            } else {
                alertDialogBuilder.setTitle("Internet Error");
                alertDialogBuilder.setMessage("Please make sure you have an active internet connection");
                alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> dialog.dismiss());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        return view;
    }

    private void getFromEditTexts(TextView commodityEdit, TextView particularsEdit, TextView amountEdit) {
        commodity = commodityEdit.getText().toString();
        particulars = particularsEdit.getText().toString();
        amount = amountEdit.getText().toString();

        if (TextUtils.isEmpty(particulars)) {
            particularsEdit.setError("Enter particulars details");
            return;
        }
        if (TextUtils.isEmpty(amount)) {
            amountEdit.setError("Enter Amount");
            return;
        }

        sendToDatabase(commodity, particulars, amount);
    }

    private void sendToDatabase(String commodity, String particulars, String amount) {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Recording ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, record_sale_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equalsIgnoreCase("success")) {
                    alertDialogBuilder.setTitle("Server Response");
                    alertDialogBuilder.setMessage(response);
                    alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        particularsEdit.setText("");
                        amountEdit.setText("");
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            }
        }, error -> {
            progressDialog.dismiss();
            alertDialogBuilder.setTitle("Server Response");
            alertDialogBuilder.setMessage(error.getLocalizedMessage());
            alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                dialog.dismiss();
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }){
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> data = new HashMap<>();
                data.put("commodity", commodity);
                data.put("particulars", particulars);
                data.put("amount", amount);
                return data;
            }
        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}