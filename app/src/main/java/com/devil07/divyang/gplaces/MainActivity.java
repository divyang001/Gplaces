package com.devil07.divyang.gplaces;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private GoogleApiClient mGoogleApiClient;
    Button bt1, bt2;
    int PLACE_PICKER_REQUEST = 1;
    private static final int GOOGLE_API_CLIENT_ID = 0;
    TextView tv1;
    private static final String TAG = "MainActivity";
    String rom = " ";
    private static final int PERMISSION_REQUEST_CODE = 100;
    PendingResult<PlaceLikelihoodBuffer> result;
    private boolean permissionGranted = false;
    ArrayList<MapDetails> mapDetails = new ArrayList<MapDetails>();
    double lat;
    double lng;
    private GoogleMap googleMap;
    MarkerOptions marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bt1 = (Button) findViewById(R.id.bt1);
        bt2 = (Button) findViewById(R.id.bt2);
        tv1 = (TextView) findViewById(R.id.tv1);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .build();


        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    PlacePicker.IntentBuilder intentBuilder = new PlacePicker.IntentBuilder();
                    startActivityForResult(intentBuilder.build(MainActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    requestPermissions(
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSION_REQUEST_CODE);
                } else {
                    permissionGranted = true;
                }
                callapi();






















/*                if(mGoogleApiClient.isConnected()) {


                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                        Toast.makeText(MainActivity.this, "in if", Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                PERMISSION_REQUEST_CODE);



                    } else {

                        Toast.makeText(MainActivity.this, "in else", Toast.LENGTH_SHORT).show();
                        callapi();
                        tv1.setText(rom);

                    }


                }*/


            }
        });


    }


    private void callapi() throws SecurityException {

        PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                .getCurrentPlace(mGoogleApiClient, null);
        result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
            @Override
            public void onResult(@NonNull PlaceLikelihoodBuffer likelyPlaces) {
                Toast.makeText(MainActivity.this, "out for" + likelyPlaces.getCount(), Toast.LENGTH_SHORT).show();

                for (int i = 0; i < likelyPlaces.getCount(); i++) {
                    //     Toast.makeText(MainActivity.this,"in for",Toast.LENGTH_SHORT).show();
                    Place pla = likelyPlaces.get(i).getPlace();

                    lat = pla.getLatLng().latitude;
                    lng = pla.getLatLng().longitude;
                    rom = pla.getName().toString();
                    //   Toast.makeText(MainActivity.this,"Place-"+rom+lat,Toast.LENGTH_SHORT).show();
                    MapDetails obj = new MapDetails(lat, lng, rom);
                    mapDetails.add(obj);

                }

                likelyPlaces.release();
                Toast.makeText(MainActivity.this, "in initial" + mapDetails.size(), Toast.LENGTH_SHORT).show();


                try {
                    // Loading map
                    initilizeMap();

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        });


    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {


        Log.e(TAG, "Google Places API connection failed with error code: "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Google Places API connection failed with error code:" +
                        connectionResult.getErrorCode(),
                Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Toast.makeText(MainActivity.this, "in request" + requestCode, Toast.LENGTH_SHORT).show();
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    permissionGranted = true;

                    Toast.makeText(MainActivity.this, "Granted", Toast.LENGTH_SHORT).show();
                    callapi();
                } else {

                    Toast.makeText(MainActivity.this, "not Granted", Toast.LENGTH_SHORT).show();

                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mGoogleApiClient.connect();

    }

    @Override
    protected void onStop() {
        super.onStop();

        mGoogleApiClient.disconnect();
    }

    public void requestPermission() {

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSION_REQUEST_CODE);
            } else {
                permissionGranted = true;
            }


            return;
        }

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (permissionGranted) {
            if (mGoogleApiClient.isConnected()) {
                callapi();
            }
        }

    }

    private void initilizeMap() {


        Intent intent = new Intent(MainActivity.this, Displaymaps.class);
        intent.putExtra("FILES_TO_SEND", mapDetails);
        startActivity(intent);
    }


}