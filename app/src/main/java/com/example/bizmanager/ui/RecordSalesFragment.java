package com.example.bizmanager.ui;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.InternetConnection;
import com.example.bizmanager.MessageParser;
import com.example.bizmanager.R;
import com.example.bizmanager.models.Commodities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RecordSalesFragment extends Fragment {
    private TextView particularsEdit, quantityEdit, unitPriceEdit;
    Spinner commoditySpinner;
    private RadioButton cashRadio, mpesaRadio, cardRadio, paidRadio, creditRadio;
    String commodity, particulars, quantity, unitPrice, paymentMethod, creditStatus;
    String record_sale_url = "http://josiekarimis.agria.co.ke/biz-manager/recordSale.php";
    String retrieve_commodity_details_url = "http://192.168.0.108/biz-manager/retrieveCommodities.php";
    private static final String TAG = "RecordSalesFragment";
    AlertDialog.Builder alertDialogBuilder;
    ProgressDialog progressDialog;
    Commodities commodities;
    List<Commodities> commoditiesList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_sales, container, false);
        Button saveRecordBtn;
        commoditySpinner = view.findViewById(R.id.commodity);
        particularsEdit = view.findViewById(R.id.particulars);
        quantityEdit = view.findViewById(R.id.quantity);
        unitPriceEdit = view.findViewById(R.id.unit_price);
        cashRadio = view.findViewById(R.id.cash_payment);
        mpesaRadio = view.findViewById(R.id.mpesa_payment);
        cardRadio = view.findViewById(R.id.card_payment);
        paidRadio = view.findViewById(R.id.paidRadio);
        creditRadio = view.findViewById(R.id.creditRadio);
        saveRecordBtn = view.findViewById(R.id.save_record);
        alertDialogBuilder = new AlertDialog.Builder(this.requireContext());
        progressDialog = new ProgressDialog(getContext());

        retrieveCommodities();

        saveRecordBtn.setOnClickListener(v -> {
            if (InternetConnection.checkConnection(requireContext())) {
                getFromEditTexts(particularsEdit, quantityEdit, unitPriceEdit);
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

    private void retrieveCommodities() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, retrieve_commodity_details_url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                Log.i(TAG, "retrieveCommodities: response: " + response);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    String id = object.getString("id");
                    String name = object.getString("Name");
                    Log.i(TAG, "retrieveCommodities: response: " + name);
                    String production_cost = object.getString("Production Cost");
                    String unitPrice = object.getString("Unit Price");
                    commodities = new Commodities(id, name, production_cost, unitPrice);
                    commoditiesList.add(commodities);
                    ListAdapter listAdapter = new ArrayAdapter<Commodities>(getContext(), android.R.layout.simple_spinner_dropdown_item, commoditiesList);
                    commoditySpinner.setAdapter((SpinnerAdapter) listAdapter);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            alertDialogBuilder.setTitle("Error");
            alertDialogBuilder.setMessage(error.getLocalizedMessage() + "\n" + "Check your internet connection and try again");
            alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void getFromEditTexts(TextView particularsEdit, TextView amountEdit, TextView unitPriceEdit) {
        particulars = particularsEdit.getText().toString();
        quantity = amountEdit.getText().toString();
        unitPrice = unitPriceEdit.getText().toString();

        if (cashRadio.isChecked()) {
            paymentMethod = cashRadio.getText().toString();
        } else if (mpesaRadio.isChecked()) {
            paymentMethod = mpesaRadio.getText().toString();
        } else if (cardRadio.isChecked()) {
            paymentMethod = cardRadio.getText().toString();
        }

        if (paidRadio.isChecked()) {
            creditStatus = paidRadio.getText().toString();
        } else if (creditRadio.isChecked()) {
            creditStatus = creditRadio.getText().toString();
        }

        if (TextUtils.isEmpty(particulars)) {
            particularsEdit.setError("Enter particulars details");
            return;
        }
        if (TextUtils.isEmpty(quantity)) {
            amountEdit.setError("Enter Quantity");
            return;
        }

        if (TextUtils.isEmpty(unitPrice)) {
            unitPriceEdit.setError("Enter Unit Price");
            return;
        }

        if (TextUtils.isEmpty(paymentMethod)) {
            Toast.makeText(getContext(), "Select Payment Method!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(creditStatus)) {
            Toast.makeText(getContext(), "Select Credit Status!", Toast.LENGTH_SHORT).show();
            return;
        }

        sendToDatabase(commodity, particulars, quantity, unitPrice, paymentMethod, creditStatus);
    }

    private void sendToDatabase(String commodity, String particulars, String quantity, String unitPrice, String paymentMethod, String creditStatus) {
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
                        quantityEdit.setText("");
                        unitPriceEdit.setText("");
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
                data.put("commodity", commodity);
                data.put("particulars", particulars);
                data.put("Quantity", quantity);
                data.put("UnitPrice", unitPrice);
                data.put("PaymentMethod", paymentMethod);
                data.put("CreditStatus", creditStatus);
                return data;
            }
        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    //todo: get mpesa sms and retrieve crucial details
    private void extractMessage() {
        String message = "inputEdit.getText().toString()";
        MessageParser messageParser = new MessageParser();
        Map<String, String> response = messageParser.parse(message);
        String type = response.get("type");
        String code = Objects.requireNonNull(response.get("code")).toUpperCase();
        String amount = response.get("amount");
        String unbalanced = response.get("balance");
        assert unbalanced != null;
        String[] balanceArray = unbalanced.split("\\.");
        List<String> bal = new ArrayList<>();
        int i = 0;
        while (i < 2) {
            bal.add(balanceArray[i]);
            i++;
        }
        String balance = String.join(".", bal);
        Log.d(TAG, "extractMessage: " + balance);
        String cost = response.get("cost");
        String particulars = response.get("participant");
        String formattedResponse = "Type: " + type + "\nCode: " + code + "\nAmount: " + amount + "\nBalance: " + balance + "\nTransaction Cost: " + cost + "\nParticulars: " + particulars;

        //resultView.setText(formattedResponse);
    }
}