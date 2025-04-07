package com.example.bookphoria

import android.util.Log
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

object BarcodeScannerHandler {
    private val scanner = BarcodeScanning.getClient()

    fun processImage(image: InputImage, onResult: (String?) -> Unit) {
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val barcode = barcodes.firstOrNull()
                onResult(barcode?.rawValue)
            }
            .addOnFailureListener {
                Log.e("BarcodeScanner", "Scan failed: ${it.message}")
                onResult(null)
            }
    }
}