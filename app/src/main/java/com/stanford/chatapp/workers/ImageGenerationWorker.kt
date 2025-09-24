package com.stanford.chatapp.workers

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ImageGenerationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    companion object {
        const val KEY_PROMPT = "prompt"
        const val KEY_IMAGE_URI = "image_uri"
    }

    override suspend fun doWork(): Result {
        val prompt = inputData.getString(KEY_PROMPT) ?: return Result.failure()

        // In a real app, you would call an image generation API here.
        // For this example, we'll just create a placeholder image.
        val bitmap = createPlaceholderBitmap(prompt)

        val uri = saveBitmapToFile(bitmap) ?: return Result.failure()

        val outputData = workDataOf(KEY_IMAGE_URI to uri.toString())
        return Result.success(outputData)
    }

    private fun createPlaceholderBitmap(prompt: String): Bitmap {
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val paint = Paint().apply {
            color = android.graphics.Color.LTGRAY
            textSize = 40f
            textAlign = Paint.Align.CENTER
        }
        canvas.drawText(prompt, 256f, 256f, paint)
        return bitmap
    }

    private fun saveBitmapToFile(bitmap: Bitmap): Uri? {
        return try {
            val imagesFolder = File(applicationContext.cacheDir, "images")
            imagesFolder.mkdirs()
            val file = File(imagesFolder, "${UUID.randomUUID()}.png")
            val stream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            stream.flush()
            stream.close()
            FileProvider.getUriForFile(
                applicationContext,
                "${applicationContext.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            null
        }
    }
}
