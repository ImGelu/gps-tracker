package edu.utcluj.gpstrack.client;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class MapsActivity extends AppCompatActivity {

    private int k = 0;
    private static final String STATIC_LOCATION = "{" +
            "\"terminalId\":\"%s\"," +
            "\"latitude\":\"%s\"," +
            "\"longitude\":\"%s\"" +
            "}";

    private final Executor executor = Executors.newFixedThreadPool(1);

    private LocationManager locationManager;
    private String latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        getSupportActionBar().setTitle(GlobalData.getLoggedInUser().getEmail());

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MapsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 1, location -> {
            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";
        });

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        latitude = location.getLatitude() + "";
        longitude = location.getLongitude() + "";
    }

    @Override
    public void onBackPressed() {
        ++k;
        if (k == 1) {
            Toast.makeText(this, "Press back one more time to exit", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> --k, 1000);
        } else {
            finishAffinity();
        }
    }

    public void changeEndpoint(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle(R.string.change_endpoint_url_title);

        View viewInflated = LayoutInflater.from(view.getContext()).inflate(R.layout.alert_dialog, (ViewGroup) view.getRootView(), false);
        builder.setView(viewInflated);

        final EditText inputIp = (EditText) viewInflated.findViewById(R.id.ip);
        final EditText inputPort = (EditText) viewInflated.findViewById(R.id.port);

        inputIp.setText(GlobalData.getEndpoint().split(":")[0]);
        inputPort.setText(GlobalData.getEndpoint().split(":")[1]);

        builder.setPositiveButton(R.string.change_url, (di, i) -> {
            String ip = inputIp.getText().toString();
            int port = Integer.parseInt(inputPort.getText().toString());

            GlobalData.setEndpoint(ip, port, this);
            Snackbar.make(view, "The Endpoint has been set to " + ip + ":" + port, BaseTransientBottomBar.LENGTH_LONG).show();
        });

        builder.setNeutralButton(R.string.cancel, (di, i) -> {
        });

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
        dialog.getButton(DialogInterface.BUTTON_NEUTRAL).setTextColor(getResources().getColor(R.color.black));
    }

    public void logout(View view) {
        GlobalData.setLoggedInUser(null, MapsActivity.this);

        Intent intent = new Intent(MapsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private boolean sendCoordinatesToEndpoint(String terminalId, String lat, String lng, View view) {
        if (lat != null && lng != null) {
            HttpURLConnection con = null;

            try {
                String endpointURL = "http://" + GlobalData.getEndpoint() + "/positions";
                URL obj = new URL(endpointURL);
                con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("User-Agent", "Mozilla/5.0");
                con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                con.setDoOutput(true);
                OutputStream os = con.getOutputStream();
                os.write(String.format(STATIC_LOCATION, terminalId, lat, lng).getBytes());
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

                    Snackbar.make(view, R.string.coordinates_success, BaseTransientBottomBar.LENGTH_LONG).show();

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
            Snackbar.make(view, R.string.coordinates_error, BaseTransientBottomBar.LENGTH_LONG).show();
            return false;
        }
    }

    public void sendCoordinates(View view) {
        executor.execute(() -> {
            sendCoordinatesToEndpoint(GlobalData.getLoggedInUser().getEmail(), latitude, longitude, view);
        });
    }
}