package com.example.bookphoria.ui.book

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.bookphoria.BarcodeScannerHandler
import com.google.mlkit.vision.common.InputImage
import android.Manifest
import android.content.pm.PackageManager
import android.os.SystemClock
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.geometry.Offset

@androidx.camera.core.ExperimentalGetImage
@Composable
fun ScanCodeScreen(
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission handling
    val hasPermission = remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission.value = isGranted
    }

    // Request camera permission when screen loads
    LaunchedEffect(Unit) {
        if (!hasPermission.value) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // Only show camera preview if permission is granted
    if (hasPermission.value) {
        CameraPreview(onScanResult, onCancel)
    } else {
        // Permission not granted UI
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Camera permission is required to scan barcodes")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                    Text("Grant Permission")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onCancel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("BATAL", color = Color.White)
                }
            }
        }
    }
}

@androidx.camera.core.ExperimentalGetImage
@Composable
private fun CameraPreview(
    onScanResult: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    val previewView = remember {
        PreviewView(context).apply {
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }

    AndroidView(
        factory = { previewView },
        modifier = Modifier.fillMaxSize()
    )

    val lastScanTime = remember { mutableStateOf(SystemClock.elapsedRealtime()) }

    LaunchedEffect(Unit) {
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                ContextCompat.getMainExecutor(context)
            ) { imageProxy ->
                val currentTime = SystemClock.elapsedRealtime()
                val debounceTime = 4000L

                val mediaImage = imageProxy.image
                if (mediaImage != null && currentTime - lastScanTime.value > debounceTime) {
                    val image = InputImage.fromMediaImage(
                        mediaImage,
                        imageProxy.imageInfo.rotationDegrees
                    )

                    BarcodeScannerHandler.processImage(image) { result ->
                        if (result != null) {
                            lastScanTime.value = currentTime
                            onScanResult(result)
                        }
                        imageProxy.close()
                    }
                } else {
                    imageProxy.close()
                }
            }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                Log.e("CameraPreview", "Use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(modifier = Modifier.fillMaxSize()) {
        val scanBoxHeight = 120.dp
        val scanBoxWidth = 280.dp
        val lineY = remember { Animatable(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                lineY.animateTo(
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1500, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    )
                )
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .width(scanBoxWidth)
                    .height(scanBoxHeight)
                    .border(2.dp, Color.Red, RoundedCornerShape(8.dp))
            ) {
                // Garis merah animasi
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val y = size.height * lineY.value
                    drawLine(
                        color = Color.Red,
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 3f
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onCancel,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("BATAL", color = Color.White)
            }
        }
    }
}
