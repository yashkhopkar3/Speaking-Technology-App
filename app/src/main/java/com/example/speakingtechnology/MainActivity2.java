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

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;



public class MainActivity2 extends AppCompatActivity {

    private static final int REQ_CODE_SPEECH_INPUT=1000 ;
    private TextToSpeech textToSpeech;
    private TextView txtScreen;
    private boolean lastNumeric;
    private boolean lastDot;
    private  boolean stateError;
    String change,Answer,ans,ans1;
    ArrayList<String> Result;
    char[] val;
    float x1,x2,y1,y2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        txtScreen=findViewById(R.id.txtScreen);

        textToSpeech= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.7);
                textToSpeech.speak("Opening Calculator ,!!  Now  Swipe left  to Calculate !  and Swipe right to go on main page ",TextToSpeech.QUEUE_FLUSH,null,null);
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
                    speak();
                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(MainActivity2.this, MainPage.class);
                    startActivity(i);

                }
                break;
        }
        return false;
    }



    public void speak() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode==RESULT_OK && null!=data){
                    ArrayList<String> result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                     Result= data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    change= result.get(0).toLowerCase(Locale.ROOT);
                    change = change.replace("x","*");
                    change = change.replace("multiply","*");
                    change = change.replace("add","+");
                    change = change.replace("sub","-");
                    change = change.replace("to","2");
                    change = change.replace("plus","+");
                    change = change.replace("minus","-");
                    change = change.replace("times","*");
                    change = change.replace("into","*");
                    change = change.replace("in2","*");
                    change = change.replace("multiply by","*");
                    change = change.replace("divide by","/");
                    change = change.replace("divide","/");
                    change = change.replace("equal","=");
                    change = change.replace("equals","=");
                    change = change.replace("equals to","=");


                    if (change.contains("="))
                    {
                        change = change.replace("="," ");
                    }
                    txtScreen.setText(change);
                    onEqual();
                }

                break;
        }

    }

    private void onEqual()
    {
            String txt= txtScreen.getText().toString();

            try
            {
                Expression expression= null;
                try
                {
                    expression=new ExpressionBuilder(txt).build();
                    double result= expression.evaluate();
                      ans =Double.toString(result);
                      char[] ch =ans.toCharArray();
                     int  n= ch.length;
                     String xyz= Character.toString(ch[n-1]);
                     switch (xyz){
                         case "0":
                             val= Arrays.copyOfRange(ch, 0, n-2);
                             ans1= new String(val);
                             Answer=Result+"Equals to ! "+ans1+" !!!!! Now  Swipe left  to Calculate again !  and Swipe right to go on the main page";
                             txtScreen.setText(Result+" = "+ans1);
                             break;
                         default:
                             Answer=Result+"Equals to ! "+ans+" !!!!! Now  Swipe left  to Calculate again !  and Swipe right to go on the main page";
                             txtScreen.setText(Result+" = "+ans);
                     }

                    textToSpeech.speak(Answer,TextToSpeech.QUEUE_FLUSH,null,null);

                }
                catch (Exception e)
                {
                    txtScreen.setText("Error");
                    textToSpeech.speak("Error in expression",TextToSpeech.QUEUE_FLUSH,null,null);
                }
            }
            catch (ArithmeticException ex )
            {
                txtScreen.setText("Error");
                textToSpeech.speak("Error in expression",TextToSpeech.QUEUE_FLUSH,null,null);
                stateError=true;
                lastNumeric=false;
            }
    }
}