package com.example.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();

        Thread thread=new Thread()
        {
            public void run(){
                try{
                    sleep(4000);
                }catch (Exception e)
                {
                    e.printStackTrace();


                }finally{
                    Intent i=new Intent(splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };thread.start();

    }
}