package com.example.bizmanager.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.InternetConnection;
import com.example.bizmanager.R;

import java.util.HashMap;
import java.util.Map;

public class RecordInputFragment extends Fragment {
    private TextView commodityEdit, QuantityEdit, UnitPriceEdit;
    String commodity, quantity, unitPrice, paymentMethod;
    RadioButton cashPaymentRadio, mpesaPaymentRadio, cardPaymentRadio;
    String record_input_url = "http://biz-manager.agria.co.ke/recordInput.php";
    private static final String TAG = "RecordInputFragment";
    AlertDialog.Builder alertDialogBuilder;
    ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_input, container, false);
        Button saveRecordBtn;
        commodityEdit = view.findViewById(R.id.commodity);
        QuantityEdit = view.findViewById(R.id.quantity);
        UnitPriceEdit = view.findViewById(R.id.unit_price);
        saveRecordBtn = view.findViewById(R.id.save_record);
        cashPaymentRadio = view.findViewById(R.id.cash_payment);
        mpesaPaymentRadio = view.findViewById(R.id.mpesa_payment);
        cardPaymentRadio = view.findViewById(R.id.card_payment);
        alertDialogBuilder = new AlertDialog.Builder(this.requireContext());
        progressDialog = new ProgressDialog(getContext());

        saveRecordBtn.setOnClickListener(v -> {
            if (InternetConnection.checkConnection(requireContext())) {
                getFromEditTexts(commodityEdit, QuantityEdit, UnitPriceEdit, cashPaymentRadio, mpesaPaymentRadio, cardPaymentRadio);
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

    private void getFromEditTexts(TextView commodityEdit, TextView quantityEdit, TextView unitPriceEdit, RadioButton cashPaymentRadio, RadioButton mpesaPaymentRadio, RadioButton cardPaymentRadio) {
        commodity = commodityEdit.getText().toString();
        quantity = quantityEdit.getText().toString();
        unitPrice = unitPriceEdit.getText().toString();

        if (cashPaymentRadio.isChecked()) {
            paymentMethod = cashPaymentRadio.getText().toString();
        } else if (mpesaPaymentRadio.isChecked()) {
            paymentMethod = mpesaPaymentRadio.getText().toString();
        } else if (cardPaymentRadio.isChecked()) {
            paymentMethod = cardPaymentRadio.getText().toString();
        }

        if (TextUtils.isEmpty(quantity)) {
            quantityEdit.setError("Enter Quantity");
            return;
        }
        if (TextUtils.isEmpty(unitPrice)) {
            unitPriceEdit.setError("Enter Amount");
            return;
        }
        if (TextUtils.isEmpty(paymentMethod)) {
            Toast.makeText(getContext(), "Select a Payment Method", Toast.LENGTH_SHORT).show();
            return;
        }
        sendToDatabase(commodity, quantity, unitPrice, paymentMethod);
    }

    private void sendToDatabase(String commodity, String quantity, String unitPrice, String paymentMethod) {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Recording ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, record_input_url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                if (response.equalsIgnoreCase("success")) {
                    alertDialogBuilder.setTitle("Server Response");
                    alertDialogBuilder.setMessage(response);
                    alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
                        commodityEdit.setText("");
                        QuantityEdit.setText("");
                        UnitPriceEdit.setText("");
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                } else {
                    alertDialogBuilder.setTitle("Error");
                    alertDialogBuilder.setMessage(response);
                    alertDialogBuilder.setPositiveButton("Ok", (dialog, which) -> {
                        dialog.dismiss();
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
                data.put("Commodity", commodity);
                data.put("Quantity", quantity);
                data.put("UnitPrice", unitPrice);
                data.put("PaymentMethod", paymentMethod);
                return data;
            }
        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }
}