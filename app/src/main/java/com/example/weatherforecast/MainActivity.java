package com.example.weatherforecast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.IntentCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.q42.android.scrollingimageview.ScrollingImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Locale;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity implements LocationListener {

//    final String APP_ID = "7654cdba35568be6644ed495001c1555";
//    final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather";

    FusedLocationProviderClient mFusedLocationClient;

    // Initializing other items
    // from layout file
    TextView latitudeTextView, longitTextView;
    final long MIN_TIME = 5000;
    final long MIN_DISTANCE = 1000;
    int f=1;
    int PERMISSION_ID = 44;
    String LOCATION_PROVIDER = LocationManager.GPS_PROVIDER;

    TextView editText;
    Button button;
    double longi, lati;
    ImageView imageView;
    TextView longitude;
    TextView latitude;
    TextView humidity;
    TextView sunrise;
    TextView sunset;
    TextView pressure;
    TextView wind;
    TextView country, city, temp1,time, max_temp, min_temp, desc;
    private Geocoder geoCoder;

    LocationManager mLocationManager;
    LocationListener mLocationListner;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setTitle("Weather App");

        editText = findViewById(R.id.myCity);
        button=findViewById(R.id.nextCity);
        country = findViewById(R.id.country);
        city = findViewById(R.id.city);
        temp1 = findViewById(R.id.temp);
        imageView = findViewById(R.id.imageView);
        desc=findViewById(R.id.desc);
        time = findViewById(R.id.textView2);
        max_temp = findViewById(R.id.temp_max);
        min_temp = findViewById(R.id.min_temp);


        longitude = findViewById(R.id.Longitude);
        latitude = findViewById(R.id.Latitude);
        humidity = findViewById(R.id.Humidity);
        sunrise = findViewById(R.id.Sunrise);
        sunset = findViewById(R.id.Sunset);
        pressure = findViewById(R.id.Pressure);
        wind = findViewById(R.id.WindSpeed);

//        ScrollingImageView scrollingBackground = (ScrollingImageView)findViewById(R.id.scrolling_background);
//        scrollingBackground.stop();
//        scrollingBackground.start();



//      fusedLocationProviderClient=LocationServices.getFusedLocationProviderClient(this);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this,currentActivity.class);
                startActivity(i);

                setContentView(R.layout.activity_current);

            }
        });
        // method to get the location

        getLastLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {

                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();


//if(f==1) {
//    Intent intent = getIntent();
//    finish();
//    startActivity(intent);
//    finish();
//    f=0;
//}


                        if (location == null) {
                            requestNewLocationData();
                        }

                            else {

                            lati = location.getLatitude();
                            longi = location.getLongitude();
//
                            latitude.setText(String.format("%.2f",lati)+ " N");
                            longitude.setText(String.format("%.2f",longi)+ " E");
//

                            findWeather();
                            find2();

                        }
                    }
                });
            } else {
                Toasty.warning(MainActivity.this, "Please turn on your location", Toast.LENGTH_SHORT, true).show();
//                Toast.makeText(this, "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
//                finish();
//                startActivity(getIntent());

            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
            super.onBackPressed();

        }


    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
            lati = mLastLocation.getLatitude();
            longi = mLastLocation.getLongitude();
//            latitudeTextView.setText(lati + "  N");
//            latitudeTextView.setText("Latitude: " + mLastLocation.getLatitude() + "");
//            longitTextView.setText("Longitude: " + mLastLocation.getLongitude() + "");
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (checkPermissions()) {
//            getLastLocation();
//            recreate();
//            if(f==1) {
//                finish();
//                f=0;
//            }
        }
    }


    @Override
    public void onLocationChanged(@NonNull Location location) {

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void findWeather() {

//          String url="http://api.openweathermap.org/data/2.5/onecall?lat="+ lati + "&lon="+ longi +"&exclude=minutely,hourly,current&appid=7654cdba35568be6644ed495001c1555";
        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + lati + "&lon=" + longi + "&appid=7654cdba35568be6644ed495001c1555\n";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                //call api
                try {
                    JSONObject jsonObject = new JSONObject(response);

//                    JSONObject city=jsonObject.getJSONObject("timezone");
//                          String city1= jsonObject.getString("timezone");
//              city.setText(city1);

                    //find country
                    JSONObject object1 = jsonObject.getJSONObject("sys");
                    String country_find = object1.getString("country");
                    country.setText(country_find);

                    //find city
                    String city_find = jsonObject.getString("name");
                    city.setText(city_find);
                    editText.setText(city_find);


                    //find desc
                    JSONArray jsonArray1 = jsonObject.getJSONArray("weather");
                    JSONObject obj12 = jsonArray1.getJSONObject(0);
                    String des = obj12.getString("description");
                    desc.setText(des);

                    //find temperature
                    JSONObject object2 = jsonObject.getJSONObject("main");
                    String temp_find = object2.getString("temp");
                    double d=Double.parseDouble(temp_find);
                    d=d-273.15;

                    temp1.setText(String.format("%.2f",d)+ "℃");

                    //find weather icon
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject obj9 = jsonArray.getJSONObject(0);
                    String icon = obj9.getString("icon");


                    Glide.with(MainActivity.this).load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(imageView);
//                    Picasso.get().load("http://openweathermap.org/img/wn/"+icon+"@2x.png").into(imageView);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat std = new SimpleDateFormat("HH:mm a \nE, MMM dd yyyy");
                    String date = std.format(calendar.getTime());
                    time.setText(date);


//                    //find latitude
//                    JSONObject obj = jsonObject.getJSONObject("coord");
//                    double lat_find = obj.getDouble("lat");
//                  latitude.setText(String.format("%.2f",lati)+ "N");


//                    //find longtitude
//                    JSONObject obj1 = jsonObject.getJSONObject("coord");
//                    double lon_find = obj1.getDouble("lon");
//                    longitude.setText(longi + "  E");

//                    find longtitude
                    JSONObject obj2 = jsonObject.getJSONObject("main");
                    int humidity_find = obj2.getInt("humidity");
                    humidity.setText(humidity_find + "  %");


                    //find Sunrise
                    JSONObject obj4 = jsonObject.getJSONObject("sys");
                    long sunrise_find = obj4.getLong("sunrise");
//                    long s = sunrise_find % 60;
//                    long m = (sunrise_find / 60) % 60;
//                    long h = (sunrise_find/ (60 * 60)) % 24;
                    sunrise.setText(formatTime(Instant.ofEpochSecond(sunrise_find)));

                    //find Sunset
                    JSONObject obj3 = jsonObject.getJSONObject("sys");
                    long sunset_find = obj3.getLong("sunset");
                    sunset.setText(formatTime(Instant.ofEpochSecond(sunset_find)));

                    //find pressure
                    JSONObject obj5 = jsonObject.getJSONObject("main");
                    String pressure_find = obj5.getString("pressure");
                    pressure.setText(pressure_find + " hPa");

                    //find windspeed
                    JSONObject obj6 = jsonObject.getJSONObject("wind");
                    String wind_find = obj6.getString("speed");
                    wind.setText(wind_find + "  Km/h");


//                    find min temperature
//                    JSONObject object10 = jsonObject.getJSONObject("main");
//                    double mintemp = object10.getDouble("temp_min");
//                    mintemp=mintemp-273.15;
////                    JSONArray jsonArray1 = jsonObject.getJSONArray("daily");
////                    JSONObject obj19 = jsonArray1.getJSONObject(0);
////                    JSONObject obj20 = obj19.getJSONObject("temp");
////                    double mintemp=obj20.getDouble("min");
////                    mintemp=mintemp-273.15;
//                    min_temp.setText(String.format("%.2f",mintemp)+ "℃");

//                    //find max temperature
//                    JSONObject object12 = jsonObject.getJSONObject("main");
//                    double maxtemp = object12.getDouble("temp_max");
//                    maxtemp=maxtemp-273.15;
//                    max_temp.setText(String.format("%.2f",maxtemp)+ "℃");
//                    JSONArray jsonArray2 = jsonObject.getJSONArray("daily");
//                    JSONObject obj21 = jsonArray2.getJSONObject(0);
//                    JSONObject obj22 = obj21.getJSONObject("temp");
//                    double maxtemp=obj22.getDouble("max");
//                    maxtemp=maxtemp-273.15;
//                    max_temp.setText(String.format("%.2f",maxtemp)+ "℃");




                } catch (JSONException e) {
                    e.printStackTrace();

                }

            }

            //from stack overflow
            final DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("h:mm a", Locale.ENGLISH)
                    .withZone(ZoneId.of("Asia/Kathmandu"));

            String formatTime(Instant time) {
                return formatter.format(time);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toasty.error(MainActivity.this, "No Internet", Toast.LENGTH_SHORT, true).show();
//                Toast.makeText(MainActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(),"Internet is unavailable",Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void find2() {
        final String city_Search = editText.getText().toString();
        String url2="http://api.openweathermap.org/data/2.5/onecall?lat="+ lati + "&lon="+ longi +"&exclude=minutely,hourly,current&appid=7654cdba35568be6644ed495001c1555";
//        Toast.makeText(MainActivity.this, "inside", Toast.LENGTH_SHORT).show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
            private String tag;


            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                //call api
                try {

                    JSONObject jsonObject1 = new JSONObject(response);

//                    //find country
//                    JSONObject object1 = jsonObject1.getJSONObject("sys");
//                    String country_find = object1.getString("country");
//                    country.setText(country_find);
//
//                    //find city
//                    String city_find = jsonObject1.getString("name");
//                    city.setText(city_find);
//
//                    //find temperature
//                    JSONObject object2 = jsonObject1.getJSONObject("main");
//                    String temp_find = object2.getString("temp");
//                    double d=Double.parseDouble(temp_find);
//                    d=d-273.15;
//                    temp1.setText(String.format("%.2f",d)+ "℃");
//
//                    //find desc
//                    JSONArray jsonArray1 = jsonObject1.getJSONArray("weather");
//                    JSONObject obj12 = jsonArray1.getJSONObject(0);
//                    String des = obj12.getString("description");
//                    desc.setText(des);
//
//
//                    //find weather icon
//                    JSONArray jsonArray = jsonObject1.getJSONArray("weather");
//                    JSONObject obj = jsonArray.getJSONObject(0);
//                    String icon = obj.getString("icon");
//                    Picasso.get().load("http://openweathermap.org/img/w/"+icon+".png").into(imageView);
//
//                    //find date & time
//                    Calendar calendar = Calendar.getInstance();
//                    SimpleDateFormat std = new SimpleDateFormat("HH:mm a \nE, MMM dd yyyy");
//                    String date = std.format(calendar.getTime());
//                    time.setText(date);
//
//                    //find latitude
//                    JSONObject obj9 = jsonObject1.getJSONObject("coord");
//                    double lat_find = obj9.getDouble("lat");
//                    latitude.setText(String.format("%.2f",lat_find)+ " N");
//
//                    //find longtitude
//                    JSONObject obj1 = jsonObject1.getJSONObject("coord");
//                    double lon_find = obj1.getDouble("lon");
//                    longitude.setText(String.format("%.2f",lon_find)+ " E");
////                    longitude.setText(lon_find + "  E");
//
//                    //find longtitude
//                    JSONObject obj2 = jsonObject1.getJSONObject("main");
//                    int humidity_find = obj2.getInt("humidity");
//                    humidity.setText(humidity_find + "  %");
//
//
//                    //find Sunrise
//                    JSONObject obj4 = jsonObject1.getJSONObject("sys");
//                    long sunrise_find = obj4.getLong("sunrise");
////                    long s = sunrise_find % 60;
////                    long m = (sunrise_find / 60) % 60;
////                    long h = (sunrise_find/ (60 * 60)) % 24;
//                    sunrise.setText(formatTime(Instant.ofEpochSecond(sunrise_find)));
//
//                    //find Sunset
//                    JSONObject obj3 = jsonObject1.getJSONObject("sys");
//                    long sunset_find = obj3.getLong("sunset");
//                    sunset.setText(formatTime(Instant.ofEpochSecond(sunset_find)));
//
//                    //find pressure
//                    JSONObject obj5 = jsonObject1.getJSONObject("main");
//                    String pressure_find = obj5.getString("pressure");
//                    pressure.setText(pressure_find + " hPa");
//
//                    //find windspeed
//                    JSONObject obj6 = jsonObject1.getJSONObject("wind");
//                    String wind_find = obj6.getString("speed");
//                    wind.setText(wind_find + "  Km/h");


                    JSONArray jsonArray2 = jsonObject1.getJSONArray("daily");
                    JSONObject obj19 = jsonArray2.getJSONObject(0);
                    JSONObject obj20 = obj19.getJSONObject("temp");
                    double mintemp=obj20.getDouble("min");
                    mintemp=mintemp-273.15;
//                    Log.d(tag, String.valueOf(mintemp));
                    min_temp.setText(String.format("%.2f",mintemp)+ "℃");

                    //find max temperature
                    JSONArray jsonArray21 = jsonObject1.getJSONArray("daily");
                    JSONObject obj21 = jsonArray21.getJSONObject(0);
                    JSONObject obj22 = obj21.getJSONObject("temp");
                    double maxtemp=obj22.getDouble("max");
                    maxtemp=maxtemp-273.15;
                    max_temp.setText(String.format("%.2f",maxtemp)+ "℃");





                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(MainActivity.this, "No Internet 2 ", Toast.LENGTH_SHORT, true).show();
//                    Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
                }

            }

            //from stack overflow
            final DateTimeFormatter formatter = DateTimeFormatter
                    .ofPattern("h:mm a", Locale.ENGLISH)
                    .withZone(ZoneId.of("Asia/Kathmandu"));

            @RequiresApi(api = Build.VERSION_CODES.O)
            String formatTime(Instant time) {
                return formatter.format(time);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toasty.warning(MainActivity.this, "Enter the correct city OR give the valid space ", Toast.LENGTH_SHORT, true).show();

//                Toast.makeText(getApplicationContext(),"Internet is unavailable",Toast.LENGTH_SHORT).show();
//                Toast.makeText(currentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }
}


