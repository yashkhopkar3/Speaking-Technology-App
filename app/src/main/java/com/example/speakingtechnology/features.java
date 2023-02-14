package com.example.speakingtechnology;

import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

public class features extends AppCompatActivity {
    private TextToSpeech textToSpeech;
    float x1,x2,y1,y2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_features);
        textToSpeech= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.7);
                textToSpeech.speak("Features of Application, ! Say Calculator to perform mathematical calculations,! Say Date and Time to get date and time , ! Say Read it enables the camera to take pictures of printed text and read it, ! Say Battery to get current battery status, ! Say Location to get current Location , ! Say (Call ) to call, ! Say (message ) to message  , !   Say (Object Dectection ) to detect the object  ,  Say Exit to close the application !!!!!! AND swipe right to go to the  main page !!!! and swipe left  repeat features again ",TextToSpeech.QUEUE_FLUSH,null,null);
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
                    textToSpeech.speak("Features of Application,! Say Calculator to perform mathematical calculations, ! Say Date and Time to get date and time ,! Say Read it enables the camera to take pictures of printed text and read it,! Say Battery to get current battery status, ! Say Location to get current Location, ! Say Exit to close the application AND  !!!!! swipe right to go  to the main page !!!! and swipe left  repeat features again ",TextToSpeech.QUEUE_FLUSH,null,null);
                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(features.this, MainPage.class);
                    startActivity(i);

                }
                break;
        }
        return false;
    }
}