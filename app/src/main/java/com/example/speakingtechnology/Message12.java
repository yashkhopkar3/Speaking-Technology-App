package com.example.speakingtechnology;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

public class Message12 extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 10000;
    private TextToSpeech textToSpeech;
    float x1, x2, y1, y2;
    String phoneno,number ;
    String n,name12,MessegeAB;
    int ind,abxc;
    private TextView txtScreen2;
    static int PERMISSION_CODE= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        txtScreen2 = findViewById(R.id.txtScreen);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float) 0.8);
                textToSpeech.speak("Opening messenger , !!!  Swipe left and speak a number or name to send message ", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        ActivityCompat.requestPermissions(Message12.this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.SEND_SMS},PackageManager.PERMISSION_GRANTED);

    }

    public boolean onTouchEvent(MotionEvent touchEvent) {
        switch (touchEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();
                y1 = touchEvent.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                y2 = touchEvent.getY();
                if (x1 < x2) {
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        Thread.sleep(3000);
                        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);

                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Sorry ! Your device does not support speech input", Toast.LENGTH_LONG).show();
                        textToSpeech.speak("Sorry ! Your device does not support speech input", TextToSpeech.QUEUE_FLUSH, null, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else if (x1 > x2) {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(Message12.this, MainPage.class);
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
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    phoneno = result.get(0).toLowerCase(Locale.ROOT);
                    txtScreen2.setText(phoneno);
                    textToSpeech.speak("Please Speak the Message  ", TextToSpeech.QUEUE_FLUSH, null, null);
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        Thread.sleep(4000);
                        startActivityForResult(intent, 100);

                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Sorry ! Your device does not support speech input", Toast.LENGTH_LONG).show();
                        textToSpeech.speak("Sorry ! Your device does not support speech input", TextToSpeech.QUEUE_FLUSH, null, null);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case 100:
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    MessegeAB = result.get(0);
                    if(phoneno.contains("0")||phoneno.contains("1")||phoneno.contains("2")||phoneno.contains("3")||phoneno.contains("4")||phoneno.contains("5")
                            ||phoneno.contains("6")||phoneno.contains("91")||phoneno.contains("7")||phoneno.contains("8"))
                    {

                        String abc=phoneno.trim();
                        txtScreen2.setText(abc);
                        SmsManager smsManager= SmsManager.getDefault();
                        smsManager.sendTextMessage(abc,null,MessegeAB,null,null);
                        Toast.makeText(getApplicationContext(), abc+"   SMS Send Successfully", Toast.LENGTH_LONG).show();
                        textToSpeech.speak(abc+ "  SMS Send Successfully", TextToSpeech.QUEUE_FLUSH, null, null);

                    }
                    else {
                        try {
                            Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                    null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            ArrayList<String> a = new ArrayList<String>();
                            while (cursor.moveToNext()) {
                                String Name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                                String Num = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                a.add(Name.toLowerCase(Locale.ROOT)+"@"+Num);
                            }
                            ind=a.size();
                            cursor.close();
                            for (int i = 0;i < ind-1; i++)
                            {
                                if (a.get(i).toString().startsWith(phoneno))
                                {
                                    abxc=i;
                                    break;
                                }
                            }
                            n = a.get(abxc).toString();
                            String []d=n.split("@");
                            number=d[1];
                            name12=d[0];
                            SmsManager smsManager= SmsManager.getDefault();
                            smsManager.sendTextMessage(number,null,MessegeAB,null,null);
                            Toast.makeText(getApplicationContext(), name12+"   SMS Send Successfully", Toast.LENGTH_LONG).show();
                        }
                        catch (Exception e) {
                            txtScreen2.setText("Error");

                        }
                    }
                    textToSpeech.speak("  Message send Successfully  !!!  Swipe left and speak a number or name to send message it  !!! and swipe right to go to the main page",TextToSpeech.QUEUE_FLUSH,null,null);
                }
                break;
        }


    }
}