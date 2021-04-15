package com.aumento.sharedparking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aumento.sharedparking.utils.GlobalPreference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private LinearLayout loginSignUpTextLL;
    private EditText loginUsernameET;
    private EditText loginPasswordET;
    private Button loginButton;
    private GlobalPreference globalPreference;
    private String ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        init();

        globalPreference = new GlobalPreference(LoginActivity.this);
        ip = globalPreference.RetriveIP();

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        loginSignUpTextLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });
    }

    private void login() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "http://"+ip+"/shared_parking/API/login.php", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d(TAG, "onResponse: "+response);

                if(response.contains("Failed")){
                    Toast.makeText(LoginActivity.this, ""+response, Toast.LENGTH_SHORT).show();
                }
                else {

                    try{

                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("data");

                        for(int i=0; i< jsonArray.length(); i++)
                        {
                            JSONObject object = jsonArray.getJSONObject(i);
                            globalPreference.addUID(object.getString("id"));
                            Log.d(TAG, "user id: "+object.getString("id"));
                        }

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                    Intent intent = new Intent(LoginActivity.this,HomeMapsActivity.class);
                    startActivity(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "onErrorResponse: "+error);
                Toast.makeText(LoginActivity.this, ""+error, Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> params = new HashMap<>();
                params.put("phone",loginUsernameET.getText().toString());
                params.put("pwd",loginPasswordET.getText().toString());
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
        requestQueue.add(stringRequest);
    }

    private void init() {

        loginUsernameET = (EditText) findViewById(R.id.loginUsernameEditText);
        loginPasswordET = (EditText) findViewById(R.id.loginPasswordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginSignUpTextLL = (LinearLayout) findViewById(R.id.loginSignUpTextLL);

    }
}