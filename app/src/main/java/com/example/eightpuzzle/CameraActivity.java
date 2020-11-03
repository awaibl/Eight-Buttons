package com.example.eightpuzzle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity {

    protected int PERMISSIONS_CAMERA = 5;
    protected static final String TAG = "CAMERA_APP";

    protected PreviewView previewView;
    protected ImageCapture imageCapture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        previewView = findViewById(R.id.preview);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[] {
                            Manifest.permission.CAMERA
                    },
                    PERMISSIONS_CAMERA);
        } else {
            startPreview();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_CAMERA &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startPreview();
        }
    }

    public void startPreview() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                bindPreview(future.get());
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, e.getMessage());
            }
        }, ContextCompat.getMainExecutor(this));
    }

    public void bindPreview(ProcessCameraProvider provider) {
        Preview preview = new Preview.Builder().build();

        imageCapture = new ImageCapture.Builder()
                .setTargetRotation(previewView.getDisplay().getRotation())
                .build();

        CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        provider.unbindAll();
        provider.bindToLifecycle(this, cameraSelector,  preview, imageCapture);
    }

    public void takePicture(View view) throws IOException {
        File picture = new File(getFilesDir(), "my_picture.jpg");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(picture).build();

        imageCapture.takePicture(
                outputFileOptions,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {
                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                        Intent intent = new Intent(CameraActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    @Override
                    public void onError(@NonNull ImageCaptureException exception) {
                        Log.e(TAG, exception.getMessage());
                    }
                });
    }
}