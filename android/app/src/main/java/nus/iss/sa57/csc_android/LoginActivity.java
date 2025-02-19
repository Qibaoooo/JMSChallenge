package nus.iss.sa57.csc_android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import nus.iss.sa57.csc_android.payload.LoginResponse;
import nus.iss.sa57.csc_android.utils.HttpHelper;
import nus.iss.sa57.csc_android.utils.MessageHelper;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private String username;
    private String password;
    private boolean isRemember;
    private static String HOST;
    private SharedPreferences userInfoPref;
    private SharedPreferences loginPref;
    private SharedPreferences listPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        HOST = HttpHelper.getLocalHost(this);
        userInfoPref = getSharedPreferences("user_info", MODE_PRIVATE);
        loginPref = getSharedPreferences("login_info", MODE_PRIVATE);
        listPref = getSharedPreferences("list_info", MODE_PRIVATE);

        //If redirected by other activities, show toast
        Intent intent = getIntent();
        if (intent.getBooleanExtra("notLoggedin", false)) {
            Toast.makeText(this, "Please Login!", Toast.LENGTH_SHORT).show();
        }

        loggedinUser();

        Button login_btn = findViewById(R.id.login);
        login_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        EditText usernameView = findViewById(R.id.username);
        username = usernameView.getText().toString();
        EditText passwordView = findViewById(R.id.password);
        password = passwordView.getText().toString();
        InputMethodManager immName = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immName.hideSoftInputFromWindow(usernameView.getWindowToken(), 0);
        usernameView.clearFocus();
        InputMethodManager immPwd = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        immPwd.hideSoftInputFromWindow(passwordView.getWindowToken(), 0);
        passwordView.clearFocus();
        CheckBox rememberBox = findViewById(R.id.remember);
        isRemember = rememberBox.isChecked();
        if (v.getId() == R.id.login) {
            if (!username.isEmpty()) {
                if (!password.isEmpty()) {
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("username", username);
                        jsonObject.put("password", password);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // login() method will take care of starting new intent
                    login(jsonObject.toString());
                } else {
                    Toast.makeText(this, "Password is required!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Username is required!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void login(final String data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String urlString = HOST + "/api/auth/login";
                HttpURLConnection urlConnection = null;
                try {
                    URL url = new URL(urlString);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestMethod("POST");
                    urlConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    urlConnection.setRequestProperty("Accept", "application/json");
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    DataOutputStream outputStream = new DataOutputStream(urlConnection.getOutputStream());
                    outputStream.writeBytes(data);
                    outputStream.flush();
                    outputStream.close();
                    int responseCode = urlConnection.getResponseCode();
                    Log.d("login", String.valueOf(responseCode));

                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        // parse login response and save to SharedPreferences
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(urlConnection.getInputStream()));
                        Gson gson = new Gson();
                        LoginResponse lr = gson.fromJson(in, LoginResponse.class);
                        userInfoPref.edit()
                                .putString("jwt", lr.getJwt())
                                .putInt("id", lr.getId())
                                .putString("username", lr.getUsername())
                                .putString("role", lr.getRole())
                                .putLong("expirationTime", lr.getExpirationTime())
                                .commit();

                        if (isRemember) {
                            loginPref.edit()
                                    .putBoolean("isRemember", true)
                                    .putString("username", username)
                                    .putString("password", password)
                                    .commit();
                        } else {
                            loginPref.edit().clear().commit();
                        }

                        listPref.edit()
                                .putBoolean("isFetched", false)
                                .commit();

                        runOnUiThread(() -> {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            finish();
                            startActivity(intent);
                        });
                    } else {
                        Log.e("LoginActivity", String.valueOf(responseCode));
                        runOnUiThread(() -> MessageHelper.showErrMessage(getApplicationContext()));
                    }
                } catch (IOException e) {
                    Log.e("LoginActivity", "Error fetching data from server: " + e.getMessage());
                    runOnUiThread(() -> MessageHelper.showErrMessage(getApplicationContext()));
                }
            }
        }).start();
    }

    private void loggedinUser() {
        if (loginPref.getBoolean("isRemember", false)) {
            username = loginPref.getString("username", null);
            password = loginPref.getString("password", null);
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", username);
                jsonObject.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            login(jsonObject.toString());
        }
    }
}