package com.example.speakingtechnology;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.speakingtechnology.ml.SsdMobilenetV11Metadata1;

import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Object_Detection extends AppCompatActivity {

    ImageView imageView;
    Paint p;
    Bitmap bitmap;
    TextureView textureView;
    CameraManager cameraManager;
    SsdMobilenetV11Metadata1 model;
    ImageProcessor imageProcessor;
    List<String> labels;
    float x1,x2,y1,y2;
    private TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_object_detection);
        textToSpeech= new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                textToSpeech.setLanguage(Locale.US);
                textToSpeech.setSpeechRate((float)0.8);
                textToSpeech.speak("Opening Object Detection !!! ",TextToSpeech.QUEUE_FLUSH,null,null);
            }
        });

        check_permissions();
        // Initialize the object detection model using the SsdMobilenetV11Metadata1 class
        try {
            model= SsdMobilenetV11Metadata1.newInstance(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Load labels from a file named "labels.txt"
        try {
            labels = FileUtil.loadLabels(this, "labels.txt");
            Log.d("labels", "labels loaded "+ labels.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Set up an ImageProcessor object with a ResizeOp to resize images to 300x300 pixels using bilinear interpolation
        imageView = findViewById(R.id.imageView);
        p = new Paint();
        imageProcessor = new ImageProcessor.Builder().add(new ResizeOp(300, 300, ResizeOp.ResizeMethod.BILINEAR)).build();

        // Initialize a CameraManager object
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        // Set up a TextureView with a SurfaceTextureListener to open the camera when the surface texture is available
        textureView = findViewById(R.id.textureView);
        textureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
                open_camera();
            }

            @Override
            public void onSurfaceTextureSizeChanged(@NonNull SurfaceTexture surfaceTexture, int i, int i1) {
            }
            @Override
            public boolean onSurfaceTextureDestroyed(@NonNull SurfaceTexture surfaceTexture) {
                return false;
            }
            @Override
            public void onSurfaceTextureUpdated(@NonNull SurfaceTexture surfaceTexture) {
                // Get the current frame from the texture view as a bitmap
                Bitmap bitmap = textureView.getBitmap();
                // Convert the bitmap to a TensorImage for processing
                TensorImage image = TensorImage.fromBitmap(bitmap);
                // Process the image using an image processor
                image = imageProcessor.process(image);

                // Run the processed image through the model and get the outputs
                SsdMobilenetV11Metadata1.Outputs outputs = model.process(image);
                // Extract the output data from the model
                float[] locations = outputs.getLocationsAsTensorBuffer().getFloatArray();
                float[] classes = outputs.getClassesAsTensorBuffer().getFloatArray();
                float[] scores = outputs.getScoresAsTensorBuffer().getFloatArray();
                float[] numberOfDetections = outputs.getNumberOfDetectionsAsTensorBuffer().getFloatArray();
                // Create a mutable copy of the bitmap to draw on
                Bitmap mutable = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(mutable);
                // Get the height and width of the bitmap
                int height = bitmap.getHeight();
                int width = bitmap.getWidth();
                boolean objectDetected = false;
                    for (int i = 0; i < numberOfDetections[0]; i++) {
                        if (scores[i] > 0.55) {
                            Log.d("mssgs", classes[i] + "");
                            try {
                                Paint paint = new Paint();
                                paint.setAntiAlias(true);
                                paint.setTextSize(150.0f);
                                paint.setColor(Color.WHITE);
                                // Draw the label of the detected object on the canvas
                                canvas.drawText(labels.get((int) classes[i]), locations[i * 4 + 1] * width, (locations[i * 4] + 0.1f) * height, paint);
                                Thread.sleep(2000);
                                // Use text-to-speech to speak the label of the detected object
                                textToSpeech.speak(labels.get((int) classes[i]), TextToSpeech.QUEUE_FLUSH, null, null);
                            } catch (NullPointerException | InterruptedException e) {
                            }
                            Paint paint = new Paint();
                            paint.setAntiAlias(true);
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(20.0f);
                            paint.setColor(Color.RED);
                            // Create a rectangle around the detected object
                            RectF rect = new RectF(locations[i * 4 + 1] * width, locations[i * 4] * height, locations[i * 4 + 3] * width, locations[i * 4 + 2] * height);
                            // Draw the rectangle on the canvas
                            canvas.drawRect(rect, paint);
                            objectDetected = true;
                        }
                    }
                if (!objectDetected) {
                    // If no object was detected, use text-to-speech to inform the user
                    textToSpeech.speak("No object detected. Swipe right to go to the main page.", TextToSpeech.QUEUE_FLUSH, null, null);
                    try {
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                // Set the image view to display the updated bitmap
                imageView.setImageBitmap(mutable);

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Close the model when the activity is destroyed
        model.close();
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
                if(x1 > x2)
                {
                    textToSpeech.stop();
                    textToSpeech.shutdown();
                    Intent i = new Intent(Object_Detection.this, MainPage.class);
                    startActivity(i);
                }
                break;
        }
        return false;
    }

    @SuppressLint("MissingPermission")
    //This code defines a method called open_camera that opens the camera and sets up a preview using a TextureView.
    void open_camera(){
        try {
            // Open the camera
            cameraManager.openCamera(cameraManager.getCameraIdList()[0], new CameraDevice.StateCallback() {
                @Override
                public void onOpened(@NonNull CameraDevice cameraDevice) {
                    try {
                        // Create a capture request for the camera preview
                        CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                        // Set the surface for the capture request
                        Surface surface = new Surface(textureView.getSurfaceTexture());
                        builder.addTarget(surface);
                        // Create a capture session for the camera preview
                        cameraDevice.createCaptureSession(Collections.singletonList(surface), new CameraCaptureSession.StateCallback() {
                            @Override
                            public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                                try {
                                    // Set the capture request to repeat
                                    cameraCaptureSession.setRepeatingRequest(builder.build(), null, null);
                                } catch (CameraAccessException e) {
                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onConfigureFailed(@NonNull CameraCaptureSession cameraCaptureSession) {

                            }
                        }, null);
                    } catch (CameraAccessException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onDisconnected(@NonNull CameraDevice cameraDevice) {

                }

                @Override
                public void onError(@NonNull CameraDevice cameraDevice, int i) {

                }
            }, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    void check_permissions(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, 101);
            }
        }
    }
}