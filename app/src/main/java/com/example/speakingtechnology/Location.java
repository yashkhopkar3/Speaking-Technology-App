package com.example.speakingtechnology;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Location extends AppCompatActivity {
    TextView txt;
    private TextToSpeech textToSpeech;
    float x1, x2, y1, y2;
    List<Address> addresses;
    String call;
    String [] answer;
    String main;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        txt = findViewById(R.id.txtScreen4);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float) 0.7);
            }
        });
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        speak3();

        ActivityCompat.requestPermissions(Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
    }

    public boolean onTouchEvent(MotionEvent touchEvent)
    {
        switch(touchEvent.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2)
                {
                    speak3();
                    textToSpeech.speak(" Your Location is !!!! "+ main+" !!!!  swipe left to repeat again  !!! and swipe right to go to the main page ",TextToSpeech.QUEUE_FLUSH,null,null);
                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(Location.this, MainPage.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }

    private void speak3() {
        if (ActivityCompat.checkSelfPermission(Location.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<android.location.Location>() {
                @Override
                public void onComplete(@NonNull Task<android.location.Location> task) {

                     android.location.Location location = task.getResult();

                        try {
                            Geocoder geocoder = new Geocoder(Location.this, Locale.getDefault());
                            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            call = addresses.get(0).getAddressLine(0);
                            answer = call.split(",",2);
                            main= answer[1];
                            txt.setText("Your Location is "+ main);
                            textToSpeech.speak("Your Location is !!!! " + main + " !!!!  swipe left to repeat again  !!! and swipe right to go to the main page ", TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        catch (IOException e)
                        {
                            e.printStackTrace();
                        }

                }
            });
        }
        else
        {
            ActivityCompat.requestPermissions(Location.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
            speak3();
        }
    }
}