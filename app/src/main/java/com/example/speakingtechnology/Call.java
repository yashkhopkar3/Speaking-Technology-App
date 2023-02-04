package com.example.speakingtechnology;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Locale;

public class Call extends AppCompatActivity {
    private static final int REQ_CODE_SPEECH_INPUT = 10000;
    private TextToSpeech textToSpeech;
    float x1, x2, y1, y2;
    String phoneno,number ;
    String n;
    int ind,abxc;
    private TextView txtScreen2;
    static int PERMISSION_CODE= 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);
        txtScreen2 = findViewById(R.id.txtScreen);
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float) 0.7);
                textToSpeech.speak("Opening Caller , !!!  Swipe left and speak a number or name to call !!! and swipe right to go to the main page", TextToSpeech.QUEUE_FLUSH, null, null);
            }
        });

        ActivityCompat.requestPermissions(Call.this,new String[]{Manifest.permission.READ_CONTACTS,Manifest.permission.WRITE_CONTACTS,Manifest.permission.CALL_PHONE},PackageManager.PERMISSION_GRANTED);

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
                    textToSpeech.stop();
                    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
                    try {
                        startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
                    } catch (ActivityNotFoundException a) {
                        Toast.makeText(getApplicationContext(), "Sorry ! Your device does not support speech input", Toast.LENGTH_LONG).show();
                        textToSpeech.speak("Sorry ! Your device does not support speech input", TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                } else if (x1 > x2) {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(Call.this, MainPage.class);
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
                    textToSpeech.speak(" Number is !!!! "+phoneno,TextToSpeech.QUEUE_FLUSH,null,null);
                    textToSpeech.shutdown();
                }
                break;
        }

        if(phoneno.contains("0")||phoneno.contains("1")||phoneno.contains("2")||phoneno.contains("3")||phoneno.contains("4")||phoneno.contains("5")
        ||phoneno.contains("6")||phoneno.contains("91")||phoneno.contains("7")||phoneno.contains("8"))
        {

            String abc=phoneno.trim();
            txtScreen2.setText(abc);
            Intent i = new Intent(Intent.ACTION_CALL);
            i.setData(Uri.parse("tel:" + abc));
            startActivity(i);

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
                Intent i = new Intent(Intent.ACTION_CALL);
                i.setData(Uri.parse("tel:" + number));
                startActivity(i);

            }
            catch (Exception e) {
                txtScreen2.setText("Error");

            }
        }
    }
}