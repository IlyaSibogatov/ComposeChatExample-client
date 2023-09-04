package com.example.composechatexample.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.util.Locale

object Ext {
    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun setLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getLocale(context: Context): String {
        val resources = context.resources
        return resources.configuration.locale.language
    }

    fun getCompressedFile(uri: Uri, context: Context): ByteArray {
        val inputStream: InputStream = context.contentResolver.openInputStream(uri)!!
        val file = createTempFile()
        inputStream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        inputStream.close()
        val exif = ExifInterface(file)
        val oldOrient = when (exif.getAttribute(ExifInterface.TAG_ORIENTATION)?.toInt()) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90
            ExifInterface.ORIENTATION_ROTATE_180 -> 180
            ExifInterface.ORIENTATION_ROTATE_270 -> 270
            else -> 0
        }
        val matrix = Matrix()
        matrix.postRotate(oldOrient.toFloat())
        val image = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)

        val rotated = Bitmap.createBitmap(
            MediaStore.Images.Media.getBitmap(context.contentResolver, uri),
            0, 0, image.width, image.height, matrix, true
        )
        val baos = ByteArrayOutputStream()
//        rotated.compress(Bitmap.CompressFormat.JPEG, getQuality(file.getMbSize()), baos)
        rotated.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        return baos.toByteArray()
    }

    private fun getQuality(sizeKb: Double, requiredSize: Double = 0.5): Int {
        return if (sizeKb > requiredSize)
            ((requiredSize / sizeKb) * 100).toInt()
        else 100
    }

    private fun File.getMbSize(): Double {
        val size = this.length()
        val kbSize = (size / 1024).toString().toDouble()
        return (kbSize / 1024).toString().toDouble()
    }
}