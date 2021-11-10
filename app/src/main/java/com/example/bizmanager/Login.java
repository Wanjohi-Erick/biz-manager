package com.example.bizmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    private EditText loginEdit, usernameEdit, passwordEdit;
    private Button loginBtn;
    private String faculty, username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginEdit = findViewById(R.id.login_category);
        usernameEdit = findViewById(R.id.username);
        passwordEdit = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login);

        loginBtn.setOnClickListener(v -> getFromEditTexts(loginEdit, usernameEdit, passwordEdit));
    }

    private void getFromEditTexts(EditText loginEdit, EditText usernameEdit, EditText passwordEdit) {
        faculty = loginEdit.getText().toString();
        username = usernameEdit.getText().toString();
        password = passwordEdit.getText().toString();

        checkFilled(faculty, username, password);
        verifyLogin(faculty, username, password);
    }

    private void verifyLogin(String faculty, String username, String password) {
        //todo get login details from database
    }

    private void checkFilled(String faculty, String username, String password) {
        if (TextUtils.isEmpty(faculty)) {
            loginEdit.setError("Field cannot be empty");
            loginEdit.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(username)) {
            usernameEdit.setError("Field cannot be empty");
            usernameEdit.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            passwordEdit.setError("Field cannot be empty");
            passwordEdit.requestFocus();
        }
    }
}