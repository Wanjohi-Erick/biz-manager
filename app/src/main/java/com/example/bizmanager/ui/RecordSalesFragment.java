package com.example.bizmanager.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bizmanager.InternetConnection;
import com.example.bizmanager.MessageParser;
import com.example.bizmanager.R;
import com.example.bizmanager.ReceiveSms;
import com.example.bizmanager.models.Commodities;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

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
    TextView smsView;
    Spinner commoditySpinner;
    private RadioButton cashRadio, mpesaRadio;
    FloatingActionButton addSaleBtn;
    String commodity, particulars, quantity, unitPrice, paymentMethod;
    String transactionCost, phone, firstName, lastName, code, amount;
    String record_sale_url = "http://biz-manager.agria.co.ke/recordSale.php";
    String retrieve_commodity_details_url = "http://biz-manager.agria.co.ke/retrieveCommodities.php";
    private static final String TAG = "RecordSalesFragment";
    AlertDialog.Builder alertDialogBuilder;
    ProgressDialog progressDialog;
    Commodities commodities;
    List<Commodities> commoditiesList = new ArrayList<>();
    List<String> names = new ArrayList<>();

    ReceiveSms receiveSms = new ReceiveSms() {
        @Override
        public void onReceive(Context context, Intent intent) {
            super.onReceive(context, intent);

            if (sender.equalsIgnoreCase("Mpesa")) {
                extractMessage(message_body);
                Log.i(TAG, "onReceive: response: " + message_body);
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(receiveSms, new IntentFilter("android.provider.Telephony.SMS_RECEIVED"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiveSms);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_record_sales, container, false);
        Button saveRecordBtn;
        commoditySpinner = view.findViewById(R.id.commodity);
        particularsEdit = view.findViewById(R.id.particulars);
        quantityEdit = view.findViewById(R.id.quantity);
        unitPriceEdit = view.findViewById(R.id.unit_price);
        smsView = view.findViewById(R.id.sms_content);
        cashRadio = view.findViewById(R.id.cash_payment);
        mpesaRadio = view.findViewById(R.id.mpesa_payment);
        addSaleBtn = view.findViewById(R.id.add_sale);
        addSaleBtn.setOnClickListener(v -> AddSale());
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

    private void AddSale() {

    }

    private void retrieveCommodities() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, retrieve_commodity_details_url, response -> {
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
                    names.add(commodities.getName());
                    ListAdapter listAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_dropdown_item, names);
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

        commoditySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                unitPriceEdit.setText(commoditiesList.get(position).getUnit_price());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                unitPriceEdit.setText(commoditiesList.get(0).getUnit_price());
            }
        });
    }

    private void getFromEditTexts(TextView particularsEdit, TextView amountEdit, TextView unitPriceEdit) {
        particulars = particularsEdit.getText().toString();
        quantity = amountEdit.getText().toString();
        unitPrice = unitPriceEdit.getText().toString();
        commodity = commoditySpinner.getSelectedItem().toString();

        if (cashRadio.isChecked()) {
            paymentMethod = cashRadio.getText().toString();
        } else if (mpesaRadio.isChecked()) {
            paymentMethod = mpesaRadio.getText().toString();
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

        sendToDatabase(commodity, particulars, quantity, unitPrice, paymentMethod);
    }

    private void sendToDatabase(String commodity, String particulars, String quantity, String unitPrice, String paymentMethod) {
        progressDialog.setTitle("Please Wait");
        progressDialog.setMessage("Recording ...");
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, record_sale_url, response -> {
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
                if (paymentMethod.equalsIgnoreCase("M-Pesa")) {
                    data.put("TransactionCost", transactionCost);
                    data.put("TransactionCode", code);
                    data.put("FirstName", firstName);
                    data.put("LastName", lastName);
                    data.put("Phone", phone);
                } else if (paymentMethod.equalsIgnoreCase("cash")) {
                    data.put("TransactionCost", "transactionCost");
                    data.put("TransactionCode", "code");
                    data.put("FirstName", "firstName");
                    data.put("LastName", "lastName");
                    data.put("Phone", "phone");
                }
                return data;
            }
        };
        stringRequest.setShouldCache(false);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    //todo: get mpesa sms and retrieve crucial details
    private void extractMessage(String message) {
        MessageParser messageParser = new MessageParser();
        Map<String, String> response = messageParser.parse(message);
        String type = response.get("type");
        code = Objects.requireNonNull(response.get("code")).toUpperCase();
        amount = response.get("amount");
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
        transactionCost = response.get("cost");
        String particulars = response.get("participant");
        String date = response.get("date");
        String time = response.get("time");

        assert particulars != null;
        String[] arrayList = particulars.split(" ");
        phone = arrayList[0];
        firstName = arrayList[1];
        lastName = arrayList[arrayList.length -1];

        particularsEdit.setText(firstName);

        String formattedResponse = "Type: " + type + "\nCode: " + code + "\nAmount: " + amount + "\nBalance: " + balance + "\nTransaction Cost: " + transactionCost + "\nPhone: " + phone + "\nFirst Name: " + firstName + "\nLast Name: " + lastName + "\nDate: " + date + "\nTime: " + time;
        smsView.setText(formattedResponse);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_stk_push) {

        }
        return super.onOptionsItemSelected(item);
    }
}