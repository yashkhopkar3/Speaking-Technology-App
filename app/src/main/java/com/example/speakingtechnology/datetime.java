package com.example.speakingtechnology;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class datetime extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    private TextView txtScreen;
    float x1,x2,y1,y2;
    String call;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datetime);
        txtScreen=findViewById(R.id.txtScreen);
        calender();
        textToSpeech= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.7);
                textToSpeech.speak(call+" !!!! swipe left to repeat again !! and swipe right to go to the main page ",TextToSpeech.QUEUE_FLUSH,null,null);
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
                    calender();
                    textToSpeech.speak(call+" !!!! swipe left to repeat again !! and swipe right to go to the main page ",TextToSpeech.QUEUE_FLUSH,null,null);
                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(datetime.this, MainPage.class);
                    startActivity(i);

                }
                break;
        }
        return false;
    }

    private void calender()
    {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE , dd-MMM-yyyy hh:mm:ss a");
        String ex = simpleDateFormat.format(calendar.getTime());
        String [] a= ex.split(" ");
        String Year =a[0].toString();
        String y =a[1].toString();
        String Date =a[2].toString();
        String Time =a[3].toString();

        String DateTime = Year+y+" Date: "+Date+"  Time :"+Time;
        txtScreen.setText(DateTime);
        call = txtScreen.getText().toString();
    }
}