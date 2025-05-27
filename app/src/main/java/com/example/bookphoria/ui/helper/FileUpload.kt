package com.example.bookphoria.ui.helper

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File.createTempFile("profile", ".jpg", context.cacheDir)
    val outputStream = FileOutputStream(file)
    inputStream?.copyTo(outputStream)
    inputStream?.close()
    outputStream.close()
    return file
}