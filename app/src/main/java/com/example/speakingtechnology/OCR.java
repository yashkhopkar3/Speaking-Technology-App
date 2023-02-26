package com.example.speakingtechnology;

import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.INTERNET;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

import pl.droidsonroids.gif.GifImageView;

public class OCR extends AppCompatActivity {
    private TextView textView;
    private SurfaceView surfaceView;

    private CameraSource cameraSource;
    private TextRecognizer textRecognizer;
    float x1,x2,y1,y2;
    private TextToSpeech textToSpeech;
    private String stringResult = null;
    ScrollView sv;
    GifImageView gifImageView ;


     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        sv= findViewById(R.id.a);
         gifImageView = findViewById(R.id.gif);
        sv.setVisibility(View.GONE);
        ActivityCompat.requestPermissions(this, new String[]{CAMERA,INTERNET}, PackageManager.PERMISSION_GRANTED);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.7);
                textToSpeech.speak("  swipe left to capture image and convert to voice  !!! and swipe right to go to the main page ",TextToSpeech.QUEUE_FLUSH,null,null);

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cameraSource.release();
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
                    setContentView(R.layout.surface);
                    gifImageView.setVisibility(View.GONE);
                    textToSpeech.speak("Tap to Process Image To text !!!! ", TextToSpeech.QUEUE_FLUSH, null, null);
                    textRecognizer();
                }
                else if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(OCR.this, MainPage.class);
                    startActivity(i);

                }
                break;
        }
        return false;
    }

    private void textRecognizer() {
        textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        cameraSource = new CameraSource.Builder(getApplicationContext(),(Detector<?>)textRecognizer)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1280, 1024)
                .setAutoFocusEnabled(true)
                .build();

        surfaceView = findViewById(R.id.surfaceView);
        Context context = this;
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                try {

                    if (ActivityCompat.checkSelfPermission(OCR.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    cameraSource.start(surfaceView.getHolder());

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
            }


            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder)
            {
                cameraSource.stop();
            }
        });
    }

    public void capture(View view) {
        textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
            @Override
            public void release() {
            }
            @Override
            public void receiveDetections(Detector.Detections<TextBlock> detections) {

                SparseArray<TextBlock> sparseArray = detections.getDetectedItems();
                StringBuilder stringBuilder = new StringBuilder();

                for (int i = 0; i < sparseArray.size(); ++i) {
                    TextBlock textBlock = sparseArray.valueAt(i);
                    if (textBlock != null) {
                        textBlock.getValue();
                        stringBuilder.append(Arrays.toString(new String[]{textBlock.getValue()}));
                    }
                }

                final String stringText = stringBuilder.toString();

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        stringResult = stringText;
                        resultObtained();
                    }
                });
            }
        });
    }

    private void resultObtained() {
        setContentView(R.layout.activity_ocr);
        GifImageView gifImageView = findViewById(R.id.gif);
        gifImageView.setVisibility(View.GONE);
        sv.setVisibility(View.VISIBLE);
        textView = findViewById(R.id.textView);
        textView.setText(stringResult);
        textToSpeech.speak(stringResult+"  swipe left to capture image Again !!! and swipe right to go to the main page ", TextToSpeech.QUEUE_FLUSH, null, null);
     }

    public void onPause() {
        if (textToSpeech != null) {
            textToSpeech.stop();

        }
        super.onPause();
    }

}
