package edu.utcluj.gpstrack.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;

    private static final String STATIC_USER = "{" +
            "\"email\":\"%s\"," +
            "\"password\":\"%s\"" +
            "}";

    private final Executor executor = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Gson gson = new Gson();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        GlobalData.setLoggedInUser(gson.fromJson(preferences.getString(GlobalData.LOGGED_IN_USER, ""), User.class), this);
        GlobalData.setEndpoint(preferences.getString(GlobalData.ENDPOINT, GlobalData.defaultEndpoint).split(":")[0], Integer.parseInt(preferences.getString(GlobalData.ENDPOINT, GlobalData.defaultEndpoint).split(":")[1]), this);

        Intent intent;
        if (GlobalData.getLoggedInUser() != null) {
            intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
            finish();
        }

        email = findViewById(R.id.text_field_email_value);
        password = findViewById(R.id.text_field_password_value);
        emailLayout = findViewById(R.id.text_field_email);
        passwordLayout = findViewById(R.id.text_field_password);

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!email.getText().toString().isEmpty()) emailLayout.setError(null);
                else emailLayout.setError(getString(R.string.error_required));

                if (!TextUtils.isEmpty(s) && !Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    emailLayout.setError(getString(R.string.error_email_pattern));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!password.getText().toString().isEmpty()) passwordLayout.setError(null);
                else passwordLayout.setError(getString(R.string.error_required_password));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void beginLogin(View view) {
        String emailValue = email.getText().toString().trim();
        String passwordValue = password.getText().toString().trim();

        if (!emailValue.isEmpty() && !passwordValue.isEmpty()) {
            sendLogin(emailValue, passwordValue, view);
        } else {
            if (emailValue.isEmpty()) {
                emailLayout.setError(getString(R.string.error_required));
            } else {
                emailLayout.setError(null);
            }

            if (passwordValue.isEmpty()) {
                passwordLayout.setError(getString(R.string.error_required_password));
            } else {
                passwordLayout.setError(null);
            }
        }
    }

    public void beginRegister(View view) {
        Intent intent = new Intent(view.getContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private boolean tryLogin(String emailInput, String passwordInput, View view) {
        if (emailInput != null && passwordInput != null) {
            HttpURLConnection con = null;

            try {
                String endpointURL = "http://" + GlobalData.getEndpoint() + "/api/auth/signin";
                URL obj = new URL(endpointURL);
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(String.format(STATIC_USER, emailInput, passwordInput).getBytes());
                os.flush();
                os.close();

                int responseCode = con.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();

                    Gson gson = new Gson();
                    String token = gson.fromJson(response.toString(), Response.class).getToken();
                    User newUser = new User(emailInput, passwordInput, token);
                    GlobalData.setLoggedInUser(newUser, view.getContext());

                    Intent intent = new Intent(view.getContext(), MapsActivity.class);
                    startActivity(intent);

                    return true;
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Snackbar.make(view, R.string.wrong_email_pass, BaseTransientBottomBar.LENGTH_LONG).show();
                    return false;
                } else {
                    Snackbar.make(view, R.string.connection_failed, BaseTransientBottomBar.LENGTH_LONG).show();
                    return false;
                }
            } catch (Exception e) {
                Snackbar.make(view, R.string.connection_failed, BaseTransientBottomBar.LENGTH_LONG).show();
                return false;
            } finally {
                if (con != null) {
                    con.disconnect();
                }
            }
        } else {
            Snackbar.make(view, R.string.connection_failed, BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
    }

    public void sendLogin(String emailInput, String passwordInput, View view) {
        executor.execute(() -> {
            tryLogin(emailInput, passwordInput, view);
        });
    }
}