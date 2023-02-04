package com.example.speakingtechnology;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class battery extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private TextView txtScreen;
    float x1,x2,y1,y2;
    String call;

    private BroadcastReceiver batterylevelReceiver= new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int percentage = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,0);
            txtScreen.setText("Phone Battery percentage is "+ String.valueOf(percentage)+" %");
            call=String.valueOf(percentage);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);
        txtScreen=findViewById(R.id.txtScreen3);
        this.registerReceiver(this.batterylevelReceiver,new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        textToSpeech= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.7);
                textToSpeech.speak("Phone Battery percentage is !!!! "+ call+" % !!!!  swipe left to repeat again  !!! and swipe right to go to the main page ",TextToSpeech.QUEUE_FLUSH,null,null);
            }
        });

    }
    public boolean onTouchEvent(MotionEvent touchEvent){
        switch(touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if(x1 < x2)
                {
                    textToSpeech.speak("Phone Battery percentage is !!!! "+ call+" % !!!!  swipe left to repeat again  !!! and swipe right to go to the main page ",TextToSpeech.QUEUE_FLUSH,null,null);
                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(battery.this, MainPage.class);
                    startActivity(i);

                }
                break;
        }
        return false;
    }
}