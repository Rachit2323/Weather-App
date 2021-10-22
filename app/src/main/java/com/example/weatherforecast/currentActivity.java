package com.example.weatherforecast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class currentActivity extends AppCompatActivity {
    EditText editText;
    Button button;
    ImageView imageView;
    TextView longitude,latitude,humidity,sunrise,sunset,pressure,wind,time;
    TextView country,city,temp1,max_temp,min_temp,desc;
    private double lati;
    private double longi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current);
        getSupportActionBar().setTitle("Search Weather for Any city");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editText=findViewById(R.id.editTextTextPersonName);
        button=findViewById(R.id.button);
        country=findViewById(R.id.country);
        city=findViewById(R.id.city);
        temp1=findViewById(R.id.temp);
        imageView=findViewById(R.id.imageView);
        time = findViewById(R.id.textView2);
        max_temp = findViewById(R.id.temp_max);
        min_temp = findViewById(R.id.min_temp);
        desc=findViewById(R.id.desc);



        longitude=findViewById(R.id.Longitude);
        latitude=findViewById(R.id.Latitude);
        humidity=findViewById(R.id.Humidity);
        sunrise=findViewById(R.id.Sunrise);
        sunset=findViewById(R.id.Sunset);
        pressure=findViewById(R.id.Pressure);
        wind=findViewById(R.id.WindSpeed);


        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                findWeather();
                find2();

            }
        });


    }
    @Override
    public void onBackPressed() {

//    finishActivity(0);
//        moveTaskToBack(true);
//        super.onBackPressed();

//        MediaPlayer tick=1;
//        if (tick != null){
//            if(tick.isPlaying())
//                tick.stop();
//
//            tick.release();
//        }
//
//        super.onBackPressed();


    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void findWeather() {
        final String city_Search = editText.getText().toString();
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + city_Search + "&appid=7654cdba35568be6644ed495001c1555\n";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onResponse(String response) {
                //call api
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    //find country
                    JSONObject object1 = jsonObject.getJSONObject("sys");
                    String country_find = object1.getString("country");
                    country.setText(country_find);

                    //find city
                    String city_find = jsonObject.getString("name");
                    city.setText(city_find);

                    //find temperature
                    JSONObject object2 = jsonObject.getJSONObject("main");
                    String temp_find = object2.getString("temp");
                    double d=Double.parseDouble(temp_find);
                    d=d-273.15;
                    temp1.setText(String.format("%.2f",d)+ "℃");

                    //find desc
                    JSONArray jsonArray1 = jsonObject.getJSONArray("weather");
                    JSONObject obj12 = jsonArray1.getJSONObject(0);
                    String des = obj12.getString("description");
                    desc.setText(des);


                    //find weather icon
                    JSONArray jsonArray = jsonObject.getJSONArray("weather");
                    JSONObject obj = jsonArray.getJSONObject(0);
                    String icon = obj.getString("icon");
                    Picasso.get().load("http://openweathermap.org/img/w/"+icon+".png").into(imageView);

                    //find date & time
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat std = new SimpleDateFormat("HH:mm a \nE, MMM dd yyyy");
                    String date = std.format(calendar.getTime());
                    time.setText(date);

                    //find latitude
                    JSONObject obj9 = jsonObject.getJSONObject("coord");
                    double lat_find = obj9.getDouble("lat");
                    lati=lat_find;
                    latitude.setText(String.format("%.2f",lat_find)+ " N");

                    //find longtitude
                    JSONObject obj1 = jsonObject.getJSONObject("coord");
                    double lon_find = obj1.getDouble("lon");
                    longi=lon_find;
                    longitude.setText(String.format("%.2f",lon_find)+ " E");
//                    longitude.setText(lon_find + "  E");

                    //find longtitude
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


//                    //find min temperature
//                    JSONObject object10 = jsonObject.getJSONObject("main");
//                    double mintemp = object10.getDouble("temp_min");
//                    mintemp=mintemp-273.15;
//                    min_temp.setText(String.format("%.2f",mintemp)+ "℃");
//
//                    //find max temperature
//                    JSONObject object12 = jsonObject.getJSONObject("main");
//                    double maxtemp = object12.getDouble("temp_max");
//                    maxtemp=maxtemp-273.15;
//                    max_temp.setText(String.format("%.2f",maxtemp)+ "℃");




                } catch (JSONException e) {
                    e.printStackTrace();
                    Toasty.error(currentActivity.this, "No Internet 2 ", Toast.LENGTH_SHORT, true).show();
//                    Toast.makeText(getApplicationContext(),"Hello Javatpoint",Toast.LENGTH_SHORT).show();
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

                Toasty.warning(currentActivity.this, "Enter the correct city OR give the valid space ", Toast.LENGTH_SHORT, true).show();

//                Toast.makeText(getApplicationContext(),"Internet is unavailable",Toast.LENGTH_SHORT).show();
//                Toast.makeText(currentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(currentActivity.this);
        requestQueue.add(stringRequest);
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public  void find2() {
        final String city_Search = editText.getText().toString();
        String url2="http://api.openweathermap.org/data/2.5/onecall?lat="+ lati + "&lon="+ longi +"&exclude=minutely,hourly,current&appid=7654cdba35568be6644ed495001c1555";
//        Toast.makeText(currentActivity.this, "inside", Toast.LENGTH_SHORT).show();
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


                    //find min temperature
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
                    Toasty.error(currentActivity.this, "No Internet 2 ", Toast.LENGTH_SHORT, true).show();
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

                Toasty.warning(currentActivity.this, "Enter the correct city OR give the valid space ", Toast.LENGTH_SHORT, true).show();

//                Toast.makeText(getApplicationContext(),"Internet is unavailable",Toast.LENGTH_SHORT).show();
//                Toast.makeText(currentActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(currentActivity.this);
        requestQueue.add(stringRequest);
    }
}

