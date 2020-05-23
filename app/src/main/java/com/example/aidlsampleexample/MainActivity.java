package com.example.aidlsampleexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.SyncStateContract;
import android.telephony.CellIdentityLte;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrength;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private Button btnCheck;
    private Button btnCheckNetworkType;
    private TextView tvShowType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheck = findViewById(R.id.btnCheck);
        btnCheckNetworkType = findViewById(R.id.btnCheckNetworkType);

        tvShowType = findViewById(R.id.tvShowType);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        checkPermission();

        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnected()) {
                    Toast.makeText(getApplicationContext(), "Internet Connected", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCheckNetworkType.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (isInternetAvailable()) {
                    ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                    String s = null;
                    NetworkInfo nInfo = cm.getActiveNetworkInfo();
                    if (nInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                        boolean isWiFi = nInfo.isConnected();
                        s = "WiFi";

                        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        // Level of current connection
                        int rssi = wifiManager.getConnectionInfo().getRssi();
                        //    int level = WifiManager.calculateSignalLevel(rssi, 5);
                        if (rssi <= 0 && rssi >= -50) {
                            //Best signal
                            Toast.makeText(MainActivity.this, "Best signal" + rssi, Toast.LENGTH_SHORT).show();
                        } else if (rssi < -50 && rssi >= -70) {
                            //Good signal

                            Toast.makeText(MainActivity.this, "Good signal" + rssi, Toast.LENGTH_SHORT).show();
                        } else if (rssi < -70 && rssi >= -80) {
                            //Low signal

                            Toast.makeText(MainActivity.this, "Low signal" + rssi, Toast.LENGTH_SHORT).show();
                        } else if (rssi < -80 && rssi >= -100) {
                            //Very weak signal
                            Toast.makeText(MainActivity.this, "Very Weak" + rssi, Toast.LENGTH_SHORT).show();
                        } else {
                            // no signals
                            Toast.makeText(MainActivity.this, "No Signal " + rssi, Toast.LENGTH_SHORT).show();
                        }

                    } else if (nInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        boolean isMobile = nInfo.isConnected();
                        s = "Mobile Data";
                        ConnectivityManager connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
                        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            return;
                        }

                        SignalStrength i  = telephonyManager.getSignalStrength();
                          int j = i.getLevel();
                        if (j == CellSignalStrength.SIGNAL_STRENGTH_GOOD) {
                            Toast.makeText(MainActivity.this, "GOOD " + j, Toast.LENGTH_SHORT).show();
                        }else if (j == CellSignalStrength.SIGNAL_STRENGTH_GREAT) {
                            Toast.makeText(MainActivity.this, "GREAT" + j, Toast.LENGTH_SHORT).show();
                        }else if (j == CellSignalStrength.SIGNAL_STRENGTH_MODERATE) {
                            Toast.makeText(MainActivity.this, "MODERATE" + j, Toast.LENGTH_SHORT).show();
                        }else if (j == CellSignalStrength.SIGNAL_STRENGTH_POOR) {
                            Toast.makeText(MainActivity.this, "POOR" + j, Toast.LENGTH_SHORT).show();
                        }else if (j == CellSignalStrength.SIGNAL_STRENGTH_NONE_OR_UNKNOWN) {
                            Toast.makeText(MainActivity.this, "" + j, Toast.LENGTH_SHORT).show();
                        }

                    }

                    tvShowType.setText(s);
                } else {
                    Toast.makeText(MainActivity.this, "Please enable the internet!", Toast.LENGTH_SHORT).show();
                }


            }


        });
    }

    public boolean isConnected() {
        boolean connected = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo nInfo = cm.getActiveNetworkInfo();
            connected = nInfo != null && nInfo.isAvailable() && nInfo.isConnected();
            return connected;
        } catch (Exception e) {
            Log.e("Connectivity Exception Found", e.getMessage());
        }
        return connected;
    }


    public boolean isInternetAvailable() {
        try {
            InetAddress address = InetAddress.getByName("www.google.com");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public boolean checkPermission() {

        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {

            return true;
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
            return false;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 12: {
                if (grantResults.length > 0) {
                    boolean coarseLocationaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (coarseLocationaccepted) {


                    } else {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                                showMessageOKCancel("You need to allow access to both the permissions",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 12);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }

                } else {


                }
                return;
            }
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("Ok", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }

}
