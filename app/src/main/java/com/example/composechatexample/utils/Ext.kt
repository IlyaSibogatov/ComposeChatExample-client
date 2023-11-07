package com.example.composechatexample.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Matrix
import android.media.ExifInterface
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.example.composechatexample.data.model.VideoSource
import java.io.ByteArrayOutputStream
import java.io.File
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

    fun getVideoStream(context: Context, uri: Uri): VideoSource {
        val inputStream = context.contentResolver.openInputStream(uri)
        val baos = ByteArrayOutputStream()
        val file = createTempFile()

        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        inputStream?.close()

        val mMMR = MediaMetadataRetriever()
        mMMR.setDataSource(context, uri)
        mMMR.frameAtTime?.compress(Bitmap.CompressFormat.JPEG, 25, baos)

        val videoItem = VideoSource(
            name = "",
            description = "",
            video = file.readBytes(),
            image = baos.toByteArray()
        )

        file.delete()
        mMMR.release()
        baos.reset()
        return videoItem
    }

    fun getCompressedImage(uri: Uri, context: Context): ByteArray {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = createTempFile()
        inputStream.use { input ->
            file.outputStream().use { output ->
                input?.copyTo(output)
            }
        }
        inputStream?.close()
        val baos = ByteArrayOutputStream()
        val image = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val exif = ExifInterface(file)
            val oldOrient = when (exif.getAttribute(ExifInterface.TAG_ORIENTATION)?.toInt()) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
            val matrix = Matrix()
            matrix.postRotate(oldOrient.toFloat())
            val rotated = Bitmap.createBitmap(
                MediaStore.Images.Media.getBitmap(context.contentResolver, uri),
                0, 0, image.width, image.height, matrix, true
            )
            rotated.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        } else {
            image.compress(Bitmap.CompressFormat.JPEG, 25, baos)
        }
        file.delete()
//        rotated.compress(Bitmap.CompressFormat.JPEG, getQuality(file.getMbSize()), baos)
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

    fun shareProfile(uuid: String, context: Context) {

        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "http://open_profile/uuid=$uuid")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        context.startActivity(shareIntent)
    }
}