package com.e.homelessindicator;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static  final int REQUEST_LOCATION=1;

    Button getlocationBtn;
    TextView showLocationTxt;
    AddLocationToDb location = new AddLocationToDb();
    LocationManager locationManager;
    String latitude,longitude;
    String locationBD;
    Button buttonAddItem;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAddItem = findViewById(R.id.btn_add_item);
        buttonAddItem.setOnClickListener(this);

        //Add permission

        ActivityCompat.requestPermissions(this,new String[]
                {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        showLocationTxt=findViewById(R.id.show_location);
        getlocationBtn=findViewById(R.id.getLocation);
        getlocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGet();

            }
        });
    }

    public void getGet(){
        locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //Check gps is enable or not
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            //Write Function To enable gps
            OnGPS();
        }
        else
        {
            //GPS is already On then
            getLocation();
        }
    }

    private void getLocation() {
        //Check Permissions again
        if (ActivityCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this,

                Manifest.permission.ACCESS_COARSE_LOCATION) !=PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,new String[]
                    {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        }
        else
        {
            Location LocationGps= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            Location LocationNetwork=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            Location LocationPassive=locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            if (LocationGps !=null)
            {
                double lat=LocationGps.getLatitude();
                double longi=LocationGps.getLongitude();
                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
                locationBD = latitude + " , " + longitude;
            }
            else if (LocationNetwork !=null)
            {
                double lat=LocationNetwork.getLatitude();
                double longi=LocationNetwork.getLongitude();
                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
                locationBD = latitude + " , " + longitude;
            }
            else if (LocationPassive !=null)
            {
                double lat=LocationPassive.getLatitude();
                double longi=LocationPassive.getLongitude();
                latitude=String.valueOf(lat);
                longitude=String.valueOf(longi);
                showLocationTxt.setText("Your Location:"+"\n"+"Latitude= "+latitude+"\n"+"Longitude= "+longitude);
                latitude  = latitude + " , " + longitude;
            }
            else
            {
                Toast.makeText(this, "Can't Get Your Location", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void OnGPS() {

        final AlertDialog.Builder builder= new AlertDialog.Builder(this);

        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();
            }
        });
        final AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

    public void buttonTest(View v){
        getGet();
        final ProgressDialog loading = ProgressDialog.show(this,"Adiciondo","Por favor espere");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxNpU_ov0O-xPAMg-X8870Y1I-SsRyo22_n8gOty2y7FvF5yoo/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        loading.dismiss();
                        Toast.makeText(MainActivity.this, response, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action", "addItem");
                parmas.put("latitude", latitude);
                parmas.put("longitude", longitude);

                return parmas;
            }
        };
        int socketTimeOut = 50000;// u can change this .. here it is 50 seconds

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);
        Toast.makeText(MainActivity.this, "Teste", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {
        if(view == buttonAddItem){
            Intent intent = new Intent(getApplicationContext(),AddItem.class);
            startActivity(intent);
        }
    }
}
