package com.example.time_to_code.camerax;
import android.content.Context;
import android.util.Log;

import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.time_to_code.face_detection.FaceContourDetectionProcessor;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CameraManager {

    private Context context;
    private PreviewView finderView;
    private LifecycleOwner lifecycleOwner;
    private GraphicOverlay graphicOverlay;

    private Preview preview;
    private Camera camera;
    private ExecutorService cameraExecutor;
    private int cameraSelectorOption = CameraSelector.LENS_FACING_FRONT;
    private ProcessCameraProvider cameraProvider;
    private ImageAnalysis imageAnalyzer;

    private static final String TAG = "CameraXBasic";

    public CameraManager(
            Context context,
            PreviewView finderView,
            LifecycleOwner lifecycleOwner,
            GraphicOverlay graphicOverlay) {
        this.context = context;
        this.finderView = finderView;
        this.lifecycleOwner = lifecycleOwner;
        this.graphicOverlay = graphicOverlay;
        createNewExecutor();
    }

    private void createNewExecutor() {
        cameraExecutor = Executors.newSingleThreadExecutor();
    }

    public void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                preview = new Preview.Builder().build();

                imageAnalyzer = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalyzer.setAnalyzer(cameraExecutor, selectAnalyzer());

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraSelectorOption)
                        .build();

                setCameraConfig(cameraProvider, cameraSelector);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    private void setCameraConfig(ProcessCameraProvider cameraProvider, CameraSelector cameraSelector) {
        try {
            cameraProvider.unbindAll();
            camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalyzer
            );
            preview.setSurfaceProvider(
                    finderView.createSurfaceProvider()
            );
        } catch (Exception e) {
            Log.e(TAG, "Use case binding failed", e);
        }
    }

    private ImageAnalysis.Analyzer selectAnalyzer() {
        return new FaceContourDetectionProcessor(graphicOverlay);
    }

    public void changeCameraSelector() {
        cameraProvider.unbindAll();
        cameraSelectorOption = (cameraSelectorOption == CameraSelector.LENS_FACING_BACK) ?
                CameraSelector.LENS_FACING_FRONT : CameraSelector.LENS_FACING_BACK;
        graphicOverlay.toggleSelector();
        startCamera();
    }
}