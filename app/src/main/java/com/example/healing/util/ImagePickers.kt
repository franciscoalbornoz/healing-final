// app/src/main/java/com/example/healing/util/ImagePickers.kt
package com.example.healing.util

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.TakePicture
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Crea un Uri temporal para que la cámara guarde la foto.
 * Requiere el <provider> en AndroidManifest y res/xml/file_paths.xml (ya lo tienes).
 */
fun createCameraImageUri(context: Context): Uri? {
    return try {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val file = File.createTempFile("IMG_${time}_", ".jpg", context.cacheDir)
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } catch (_: Throwable) {
        null
    }
}

/** Launcher para abrir la galería con el nuevo Photo Picker (Android 13+) y fallback. */
@Composable
fun rememberGalleryPicker(onPicked: (Uri?) -> Unit) =
    rememberLauncherForActivityResult(PickVisualMedia()) { uri -> onPicked(uri) }

/** Launcher para tomar foto con la cámara, usando un Uri de destino. */
@Composable
fun rememberCameraTaker(onResult: (Boolean) -> Unit) =
    rememberLauncherForActivityResult(TakePicture()) { ok -> onResult(ok) }

/** Request estándar para solo-imágenes (evita error de tipos). */
fun imageOnlyRequest() = PickVisualMediaRequest(PickVisualMedia.ImageOnly)
