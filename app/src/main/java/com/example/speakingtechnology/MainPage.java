package com.example.speakingtechnology;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Locale;

public class MainPage extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT=1000;
    private TextToSpeech textToSpeech;
    private TextView txtScreen2;
    float x1,x2,y1,y2;
    String ans;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        txtScreen2=findViewById(R.id.txtScreen2);

        textToSpeech= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.7);
                textToSpeech.speak("Opening  Main Page , !!!  Swipe right to listen the features of the app !!!  Swipe left to Say what you want",TextToSpeech.QUEUE_FLUSH,null,null);
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
                    textToSpeech.stop();
                    Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.getDefault());
                    try
                    {
                        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                    }


                    catch (ActivityNotFoundException a)
                    {
                        Toast.makeText(getApplicationContext(),"Sorry ! Your device does not support speech input",Toast.LENGTH_LONG).show();
                        textToSpeech.speak("Sorry ! Your device does not support speech input",TextToSpeech.QUEUE_FLUSH,null,null);
                    }

                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(MainPage.this, features.class);
                    startActivity(i);

                }
                break;
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String>  result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    ans=result.get(0).toLowerCase(Locale.ROOT);
                    txtScreen2.setText(ans);

                    if (ans.contains("calculator")||ans.contains("calculate"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, MainActivity2.class);
                        startActivity(i);
                    }
                    else if (ans.contains("date"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, datetime.class);
                        startActivity(i);
                    }
                    else if (ans.contains("time"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, datetime.class);
                        startActivity(i);
                    }
                    else if (ans.contains("exit"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        moveTaskToBack(true);
                        android.os.Process.killProcess(android.os.Process.myPid());
                        System.exit(1);
                    }
                    else if (ans.contains("battery"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, battery.class);
                        startActivity(i);


                    }
                    else if (ans.contains("message")||ans.contains("messages")||ans.contains("messenger"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, Message12.class);
                        startActivity(i);
                    }
                    else if (ans.contains("location"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, Location.class);
                        startActivity(i);
                    }
                    else if (ans.contains("read"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, OCR.class);
                        startActivity(i);
                    }
                    else if (ans.contains("feature")||ans.contains("features"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, features.class);
                        startActivity(i);
                    }
                    else if (ans.contains("call")||ans.contains("caller"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, Call.class);
                        startActivity(i);
                    }
                    else if (ans.contains("object")||ans.contains("detection"))
                    {
                        textToSpeech.stop();
                        textToSpeech.shutdown();
                        Intent i = new Intent(MainPage.this, Object_Detection.class);
                        startActivity(i);
                    }
                    else
                    {
                        textToSpeech.speak("Sorry ! this feature is not available ",TextToSpeech.QUEUE_FLUSH,null,null);
                    }

                }
                break;
        }
    }
}