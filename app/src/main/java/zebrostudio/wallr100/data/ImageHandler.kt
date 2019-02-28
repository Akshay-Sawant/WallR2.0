package zebrostudio.wallr100.data

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import zebrostudio.wallr100.data.exception.ImageDownloadException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

interface ImageHandler {
  fun isImageCached(link: String): Boolean
  fun fetchImage(link: String): Observable<Long>
  fun cancelFetchingImage()
  fun getImageBitmap(): Bitmap
  fun clearImageCache(): Completable
  fun getImageUri(): Uri
  fun convertUriToBitmap(uri: Uri): Single<Bitmap>
  fun convertImageToLowpoly(): Single<Bitmap>
}

class ImageHandlerImpl(
  private val context: Context,
  private val fileHandler: FileHandler
) : ImageHandler {

  internal var shouldContinueFetchingImage: Boolean = true
  private val byteArraySize = 2048
  private val downloadProgressCompletedValue: Long = 100
  private val readMode = "r"
  private val bitmapCompressQuality = 100
  private val gradientThresholdLowpoly = 40
  private var imageCacheTracker: Pair<Boolean, String> = Pair(false, "")

  override fun isImageCached(link: String): Boolean {
    return (imageCacheTracker.first && (imageCacheTracker.second == link))
  }

  override fun fetchImage(link: String): Observable<Long> {
    return Observable.create {
      var connection: HttpURLConnection? = null
      var inputStream: InputStream? = null
      var outputStream: OutputStream? = null
      shouldContinueFetchingImage = true
      imageCacheTracker = Pair(false, "")
      try {
        connection = URL(link).openConnection() as HttpURLConnection
        connection.connect()
        val length = connection.contentLength
        if (length <= 0) {
          it.onError(ImageDownloadException())
        }
        inputStream = connection.inputStream
        outputStream = FileOutputStream(fileHandler.getCacheFile())
        val data = ByteArray(byteArraySize)
        var count: Int = inputStream.read(data)
        var read: Long = 0
        while (count != -1) {
          read += count.toLong()
          outputStream.write(data, 0, count)
          if (shouldContinueFetchingImage) {
            val progress = (read * 100 / length)
            if (progress == downloadProgressCompletedValue) {
              imageCacheTracker = Pair(true, link)
            }
            it.onNext(progress)
            count = inputStream.read(data)
          } else {
            connection.disconnect()
            outputStream.flush()
            outputStream.close()
            inputStream?.close()
            break
          }
        }
      } catch (e: IOException) {
        it.onError(e)
      } finally {
        connection?.disconnect()
        outputStream?.flush()
        outputStream?.close()
        inputStream?.close()
        it.onComplete()
      }
    }
  }

  override fun cancelFetchingImage() {
    shouldContinueFetchingImage = false
  }

  override fun getImageBitmap(): Bitmap {
    return BitmapFactory.Options().let {
      it.inPreferredConfig = Bitmap.Config.ARGB_8888
      BitmapFactory.decodeFile(fileHandler.getCacheFile().path, it)
    }
  }

  override fun clearImageCache(): Completable {
    return Completable.create {
      fileHandler.deleteCacheFiles()
      imageCacheTracker = Pair(false, "")
      it.onComplete()
    }
  }

  override fun getImageUri(): Uri {
    return getImageBitmap().let { bitmap ->
      fileHandler.getCacheFile().outputStream().apply {
        bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapCompressQuality, this)
        flush()
        close()
      }
      Uri.fromFile(fileHandler.getCacheFile())
    }
  }

  override fun convertUriToBitmap(uri: Uri): Single<Bitmap> {
    return Single.create {
      with(context.contentResolver.openFileDescriptor(uri, readMode)) {
        BitmapFactory.decodeFileDescriptor(fileDescriptor).let { bitmap ->
          close()
          fileHandler.getCacheFile().outputStream().apply {
            bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapCompressQuality, this)
            flush()
            close()
            it.onSuccess(bitmap)
          }
        }
      }
    }
  }

  override fun convertImageToLowpoly(): Single<Bitmap> {
    return Single.create {
      LowPoly.generate(getImageBitmap(), gradientThresholdLowpoly).let { bitmap ->
        fileHandler.getCacheFile().outputStream().apply {
          bitmap.compress(Bitmap.CompressFormat.JPEG, bitmapCompressQuality, this)
          flush()
          close()
        }
        it.onSuccess(bitmap)
      }
    }
  }

}