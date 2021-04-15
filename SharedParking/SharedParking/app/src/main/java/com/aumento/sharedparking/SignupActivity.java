package com.aumento.sharedparking;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.sharedparking.utils.GlobalPreference;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";

    EditText userFirstNameET, userPhoneNumberET, userEmailET, userPasswordET, userVehicleTypeET, userVehicleNoET;
    TextView submitButtonTV;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        GlobalPreference globalPreference = new GlobalPreference(getApplicationContext());
        ip = globalPreference.RetriveIP();

        init();

        submitButtonTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

    }

    private void signUp() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/shared_parking/API/signup.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                Toast.makeText(SignupActivity.this, ""+response, Toast.LENGTH_SHORT).show();

                if(response.contains("Failed")){
                    Toast.makeText(SignupActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                }
                else {

                    Intent intent = new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
                Toast.makeText(SignupActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("fname",userFirstNameET.getText().toString());
                params.put("phone",userPhoneNumberET.getText().toString());
                params.put("email",userEmailET.getText().toString());
                params.put("vno",userVehicleNoET.getText().toString());
                params.put("vtype",userVehicleTypeET.getText().toString());
                params.put("password",userPasswordET.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(SignupActivity.this);
        requestQueue.add(stringRequest);

    }

    private void init() {

        userFirstNameET = (EditText) findViewById(R.id.userNameEditText);
        userPhoneNumberET = (EditText) findViewById(R.id.userPhoneNumberEditText);
        userEmailET = (EditText) findViewById(R.id.userEmailEditText);
        userPasswordET = (EditText) findViewById(R.id.userPasswordEditText);
        userVehicleTypeET = (EditText) findViewById(R.id.userVehicleTypeEditText);
        userVehicleNoET = (EditText) findViewById(R.id.userVehicleNoEditText);
        submitButtonTV = (TextView) findViewById(R.id.submitButtonTextView);

    }
}