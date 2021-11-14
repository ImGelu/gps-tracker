package edu.utcluj.gpstrack.client;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

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

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;

import edu.utcluj.gpstrack.client.room.User;
import edu.utcluj.gpstrack.client.room.UserDao;
import edu.utcluj.gpstrack.client.room.UserDataBase;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private UserDao userDao;

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

        UserDataBase userDataBase = Room.databaseBuilder(this, UserDataBase.class, GlobalData.USERS_DB_NAME).allowMainThreadQueries().build();
        userDao = userDataBase.getUserDao();

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
            User user = userDao.getUser(emailValue, passwordValue);

            if (user != null) {
                GlobalData.setLoggedInUser(user, view.getContext());

                Intent intent = new Intent(view.getContext(), MapsActivity.class);
                startActivity(intent);
            } else {
                emailLayout.setError(getString(R.string.error_wrong_credentials));
            }
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
}