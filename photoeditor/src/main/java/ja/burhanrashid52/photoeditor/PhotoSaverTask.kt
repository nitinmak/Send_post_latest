package ja.burhanrashid52.photoeditor

import android.annotation.SuppressLint
import android.graphics.*
import android.os.AsyncTask
import android.text.TextUtils
import android.util.Log
import android.view.View
import ja.burhanrashid52.photoeditor.BitmapUtil.removeTransparency
import ja.burhanrashid52.photoeditor.PhotoSaverTask.SaveResult
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by Burhanuddin Rashid on 18/05/21.
 *
 * @author <https:></https:>//github.com/burhanrashid52>
 */
class PhotoSaverTask(photoEditorView: PhotoEditorView) :

    AsyncTask<String?, String?, SaveResult>() {
    private var mSaveSettings: SaveSettings
    private var mOnSaveBitmap: OnSaveBitmap? = null
    private val mPhotoEditorView: PhotoEditorView?

    fun setOnSaveBitmap(onSaveBitmap: OnSaveBitmap?) {
        mOnSaveBitmap = onSaveBitmap
    }

    fun setSaveSettings(saveSettings: SaveSettings) {
        mSaveSettings = saveSettings
    }

    override fun onPreExecute() {
        super.onPreExecute()
    }

    @SuppressLint("MissingPermission")
    override fun doInBackground(vararg inputs: String?): SaveResult {
        // Create a media file name
        return if (inputs.isEmpty()) {
            saveImageAsBitmap()
        } else {
            saveImageInFile(inputs.first().toString())
        }
    }

    private fun saveImageAsBitmap(): SaveResult {
        Log.d(TAG, "saveImageInFile -> bitmap")
        return SaveResult(null, null, buildBitmap())
    }

    private fun saveImageInFile(mImagePath: String): SaveResult {
        val file = File(mImagePath)
        Log.d(TAG, "saveImageInFile -> "+mImagePath)
        return try {
            val out = FileOutputStream(file)
            if (mPhotoEditorView != null) {
                val capturedBitmap = buildBitmap()
                capturedBitmap?.compress(mSaveSettings.compressFormat, mSaveSettings.compressQuality, out)
            }
            out.flush()
            out.close()
            Log.d(TAG, "Filed Saved Successfully")
            SaveResult(null, mImagePath, null)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e(TAG, "Failed to save File "+e.message)
            SaveResult(e, mImagePath, null)
        }
    }

    private fun buildBitmap(): Bitmap? {
        return if (mSaveSettings.isTransparencyEnabled) removeTransparency(
            captureView(mPhotoEditorView)
        ) else captureView(mPhotoEditorView)
    }

    override fun onPostExecute(saveResult: SaveResult) {
        super.onPostExecute(saveResult)
        if (TextUtils.isEmpty(saveResult.mImagePath)) {
            handleBitmapCallback(saveResult)
        } else {
            handleFileCallback(saveResult)
        }
    }

    private fun handleFileCallback(saveResult: SaveResult) {
        val exception = saveResult.mException
        val imagePath = saveResult.mImagePath
        if (exception == null) {
            //Clear all views if its enabled in save settings
            assert(imagePath != null)
        } else {
        }
    }

    private fun handleBitmapCallback(saveResult: SaveResult) {
        val bitmap = saveResult.mBitmap
        if (bitmap != null) {
            mOnSaveBitmap?.onBitmapReady(bitmap)

        } else {
            mOnSaveBitmap?.onFailure(Exception("Failed to load the bitmap"))
        }
    }

    private fun captureView(view: View?): Bitmap? {
        if (view == null) {
            return null
        }
        val bitmap = Bitmap.createBitmap(
            view.width,
            view.height,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.TRANSPARENT)
        view.draw(canvas)
        return bitmap
    }

    fun saveBitmap() {
        execute()
    }

    class SaveResult(
        val mException: Exception?,
        val mImagePath: String?,
        val mBitmap: Bitmap?
    )

    companion object {
        const val TAG = "PhotoSaverTask"
    }

    init {
        mPhotoEditorView = photoEditorView
        mSaveSettings = SaveSettings.Builder().build()
    }
}