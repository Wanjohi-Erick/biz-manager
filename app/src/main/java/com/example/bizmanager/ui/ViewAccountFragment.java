package com.example.bizmanager.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.bizmanager.R;

public class ViewAccountFragment extends Fragment {
    String get_sales_url = "http://josiekarimis.agria.co.ke/biz-manager/retrieveSales.php";
    String get_input_url = "http://josiekarimis.agria.co.ke/biz-manager/retrieveInput.php";
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_account, container, false);
        return view;
    }
}