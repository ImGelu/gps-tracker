package edu.utcluj.gpstrack.client;

import android.os.Bundle;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText userEmail, userPassword, userPasswordVerification;
    private TextInputLayout userEmailLayout;
    private TextInputLayout userPasswordLayout;
    private TextInputLayout userPasswordVerificationLayout;

    private static final String STATIC_USER = "{" +
            "\"email\":\"%s\"," +
            "\"password\":\"%s\"" +
            "}";

    private final Executor executor = Executors.newFixedThreadPool(1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        userEmail = findViewById(R.id.text_field_email_value);
        userPassword = findViewById(R.id.text_field_password_value);
        userPasswordVerification = findViewById(R.id.text_field_password_verification_value);

        userEmailLayout = findViewById(R.id.text_field_email);
        userPasswordLayout = findViewById(R.id.text_field_password);
        userPasswordVerificationLayout = findViewById(R.id.text_field_password_verification);

        userEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!userEmail.getText().toString().isEmpty()) userEmailLayout.setError(null);
                else userEmailLayout.setError(getString(R.string.error_required));

                if (!TextUtils.isEmpty(s) && !Patterns.EMAIL_ADDRESS.matcher(s).matches())
                    userEmailLayout.setError(getString(R.string.error_email_pattern));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!userPassword.getText().toString().isEmpty()) {
                    if (!userPasswordVerification.getText().toString().isEmpty() && !userPasswordVerification.getText().toString().equals(userPassword.getText().toString()))
                        userPasswordVerificationLayout.setError(getString(R.string.error_password_verification));
                    else userPasswordVerificationLayout.setError(null);

                    userPasswordLayout.setError(null);
                } else userPasswordLayout.setError(getString(R.string.error_required_password));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        userPasswordVerification.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!userPasswordVerification.getText().toString().isEmpty())
                    userPasswordVerificationLayout.setError(null);

                if (!userPasswordVerification.getText().toString().equals(userPassword.getText().toString()))
                    userPasswordVerificationLayout.setError(getString(R.string.error_password_verification));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public void createAccount(View view) {
        String email = userEmail.getText().toString().trim();
        String password = userPassword.getText().toString().trim();
        String passwordVerification = userPasswordVerification.toString().trim();

        if (!email.isEmpty() && !password.isEmpty() && !passwordVerification.isEmpty()) {
            if (!userPasswordVerification.getText().toString().equals(userPassword.getText().toString())) {
                Snackbar.make(view, R.string.error_password_verification, BaseTransientBottomBar.LENGTH_SHORT);
            } else {
                sendRegister(email, password, view);
            }
        } else {
            Snackbar.make(view, R.string.error_unfilled, BaseTransientBottomBar.LENGTH_SHORT).show();
        }
    }

    private boolean tryRegister(String emailInput, String passwordInput, View view) {
        if (emailInput != null && passwordInput != null) {
            HttpURLConnection con = null;

            try {
                String endpointURL = "http://" + GlobalData.getEndpoint() + "/api/auth/signup";
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

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }

                in.close();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    finish();
                    con.disconnect();

                    return true;
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

    public void sendRegister(String emailInput, String passwordInput, View view) {
        executor.execute(() -> {
            tryRegister(emailInput, passwordInput, view);
        });
    }
}