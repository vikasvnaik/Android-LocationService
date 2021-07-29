@file:JvmName("FileUtils")

package com.mvvm.extension

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import android.provider.DocumentsContract
import android.provider.MediaStore
import java.io.File


fun Context.appPath(): File {
    val cacheFilePath = applicationContext.getExternalFilesDir(null).toString()
    var appCacheDir: File? = null
    if (MEDIA_MOUNTED == Environment.getExternalStorageState()) {
        appCacheDir = File(cacheFilePath)
    }
    if (appCacheDir == null || !appCacheDir.exists() && !appCacheDir.mkdirs()) {
        appCacheDir = cacheDir
    }
    return appCacheDir!!
}

fun Context.createFolder(path: String): File {
    val direct = File(appPath(), path)
    if (!direct.exists()) {
        if (direct.mkdir()) {
            return direct
        }
    }
    return direct
}

fun Context.getFolder(path: String): File? {
    val direct = File(appPath(), path)
    if (!direct.exists()) {
        return null
    }
    return direct
}

fun Context.createFile(path: String, name: String): File {
    val folder = createFolder(path)
    val file = File(folder, name)
    if (!file.exists()) {
        file.createNewFile()
    }
    return file
}

fun Context.getFile(path: String, name: String): File? {
    getFolder(path)?.let {
        val file = File(it, name)
        if (!file.exists()) {
            return null
        }
        return file
    }
    return null
}

fun Context.createImageFile(name: String): File {
    return createFile("images", name)
}

fun Context.getImageFile(name: String): File? {
    return getFile("images", name)
}

fun Context.createVideoFile(name: String): File {
    return createFile("videos", name)
}

fun Context.getVideoFile(name: String): File? {
    return getFile("videos", name)
}

fun Context.getPath(uri: Uri): String? {
    if (Build.VERSION.SDK_INT < 11) {
        return getRealPathBelowAPI11(uri)
    } else if (Build.VERSION.SDK_INT < 19) {
        return getRealPathAPI11to18(uri)
    } else {
        return getRealPathAPI19(uri)
    }
}

/**
 * Get the value of the data column for this Uri. This is useful for
 * MediaStore Uris, and other file-based ContentProviders.
 *
 * @param context       The context.
 * @param uri           The Uri to query.
 * @param selection     (Optional) Filter used in the query.
 * @param selectionArgs (Optional) Selection arguments used in the query.
 * @return The value of the _data column, which is typically a file path.
 */
private fun getDataColumn(
    context: Context, uri: Uri?, selection: String?,
    selectionArgs: Array<String>?
): String? {

    var cursor: Cursor? = null
    val column = "_data"
    val projection = arrayOf(column)

    try {
        cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)
        if (cursor != null && cursor.moveToFirst()) {
            val index = cursor.getColumnIndexOrThrow(column)
            return cursor.getString(index)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        cursor?.close()
    }
    return null
}

private fun Context.getRealPathBelowAPI11(contentUri: Uri): String {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor = contentResolver.query(contentUri, proj, null, null, null)
    var result = ""
    if (cursor != null) {
        val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        cursor.moveToFirst()
        result = cursor.getString(index)
        cursor.close()
        return result
    }
    return result
}

private fun Context.getRealPathAPI11to18(contentUri: Uri): String? {
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    var result: String? = null

    val cursorLoader =
        androidx.loader.content.CursorLoader(this, contentUri, proj, null, null, null)
    val cursor = cursorLoader.loadInBackground()

    cursor?.let {
        val index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        it.moveToFirst()
        result = it.getString(index)
        it.close()
    }
    return result
}

@SuppressLint("NewApi")
private fun Context.getRealPathAPI19(uri: Uri): String? {

    val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

    // DocumentProvider
    if (isKitKat && DocumentsContract.isDocumentUri(this, uri)) {
        // ExternalStorageProvider
        if (uri.isExternalStorageDocument()) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            if ("primary".equals(type, ignoreCase = true)) {
                return applicationContext.getExternalFilesDir(null).toString() + "/" + split[1]
            }

            // TODO handle non-primary volumes
        } else if (uri.isDownloadsDocument()) {

            val id = DocumentsContract.getDocumentId(uri)
            val contentUri = ContentUris.withAppendedId(
                Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)
            )

            return getDataColumn(
                this,
                contentUri,
                null,
                null
            )
        } else if (uri.isMediaDocument()) {
            val docId = DocumentsContract.getDocumentId(uri)
            val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val type = split[0]

            var contentUri: Uri? = null
            if ("image" == type) {
                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            } else if ("video" == type) {
                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
            } else if ("audio" == type) {
                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            }

            val selection = "_id=?"
            val selectionArgs = arrayOf(split[1])

            return getDataColumn(
                this,
                contentUri,
                selection,
                selectionArgs
            )
        }// MediaProvider
        // DownloadsProvider
    } else if ("content".equals(uri.scheme, ignoreCase = true)) {

        // Return the remote address
        return if (uri.isGooglePhotosUri()) uri.lastPathSegment else getDataColumn(
            this,
            uri,
            null,
            null
        )

    } else if ("file".equals(uri.scheme, ignoreCase = true)) {
        return uri.path
    }// File
    // MediaStore (and general)

    return null
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is ExternalStorageProvider.
 */
private fun Uri.isExternalStorageDocument(): Boolean {
    return "com.android.externalstorage.documents" == authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is DownloadsProvider.
 */
private fun Uri.isDownloadsDocument(): Boolean {
    return "com.android.providers.downloads.documents" == authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is MediaProvider.
 */
private fun Uri.isMediaDocument(): Boolean {
    return "com.android.providers.media.documents" == authority
}

/**
 * @param uri The Uri to check.
 * @return Whether the Uri authority is Google Photos.
 */
private fun Uri.isGooglePhotosUri(): Boolean {
    return "com.google.android.apps.photos.content" == authority
}

