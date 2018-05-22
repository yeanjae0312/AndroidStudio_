package org.androidtown.mymap;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity {
    //맵 연결
    SupportMapFragment mapFragment;
    GoogleMap map;
    Button button;

    MarkerOptions myLocMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 위험 권한 부여 요청
        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치서비스 사용 권한 있음.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "위치서비스 사용 권한 없음.", Toast.LENGTH_LONG).show();
            // 허용하는 것을 받겠다.
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "위치서비스 사용 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }



        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED); {
                    map.setMyLocationEnabled(true); // 버전23미만일때 사용
                }

            }
        });

        try {
            MapsInitializer.initialize(this); // 맵 뜰때 처음 나오는 화면
        } catch(Exception e) {
            e.printStackTrace();
        }

        button = (Button) findViewById(R.id.btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestMyLocation();
            }
        });
    }

    private void requestMyLocation() {
        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // 위치가 바뀔때마다 showCurrentLocation()을 부르겠다.
        try {
            long minTime = 10000;
            float minDistance = 0;
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, minTime, minDistance, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    showCurrentLocation(location); //현재위치를 받고 받은 위치로 바꿔라
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                }
            });

            Location lastLocation = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastLocation != null) {
                showCurrentLocation(lastLocation); // 마지막에 봤던 위치를 보여줘라
            }

            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, minTime, minDistance, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    showCurrentLocation(location);
                }
                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {
                }
                @Override
                public void onProviderEnabled(String provider) {
                }
                @Override
                public void onProviderDisabled(String provider) {
                }
            });

        } catch(SecurityException e) {
            e.printStackTrace();
        }
    }
    private void showCurrentLocation(Location location) {
        LatLng curPoint = new LatLng(location.getLatitude(), location.getLongitude());

        // 가장 중요한 함수이다.
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(curPoint, 15));//현재위치를 보여주는 효과들을 역할한다.(ex 줌)

        String msg = "Latitude : "+ location.getLatitude() + "\nLongitude:"+ location.getLongitude();
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

        showMyMarker(location);
    }

    private void showMyMarker(Location location) {
        if(myLocMarker == null) {
            myLocMarker = new MarkerOptions();
            myLocMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
            myLocMarker.title("GSP 위치");
            myLocMarker.snippet("우리 학교~~");
            map.addMarker(myLocMarker);
        }
        else {
            myLocMarker.position(new LatLng(location.getLatitude(), location.getLongitude()));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 1: {
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "위치서비스 사용을 사용자가 승인함.", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(this, "위치서비스 사용을 사용자가 거부함.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(map!=null && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true); // 버전23미만일때 사용
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(map!=null && ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true); // 버전23미만일때 사용
        }
    }
}