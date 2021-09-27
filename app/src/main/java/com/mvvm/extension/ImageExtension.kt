@file:JvmName("ImageUtils")

package com.mvvm.extension

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.text.SpannableString
import android.text.Spanned
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.appcompat.widget.AppCompatImageView
import coil.ImageLoader
import coil.request.CachePolicy
import coil.request.LoadRequest
import com.iplt.domain.extension.DrawableSpan
import com.mvvm.ui.base.AppApplication
import org.koin.android.ext.android.getKoin


/*------------------------------------ImageView-----------------------------------------------*/

fun String.drawableSpan(drawable: Drawable?, start: Int, end: Int): SpannableString {
    return SpannableString(this).apply {
        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val imageSpan = DrawableSpan(it, 5, 5)
            setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

fun String.drawableSpanStart(drawable: Drawable?): SpannableString {
    return SpannableString(this).apply {
        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val imageSpan = DrawableSpan(it, 5, 5)
            setSpan(imageSpan, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

fun String.drawableSpanEnd(drawable: Drawable?): SpannableString {
    return SpannableString(this).apply {
        drawable?.let {
            it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
            val imageSpan = DrawableSpan(it, 5, 5)
            setSpan(imageSpan, length - 1, length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}

var ImageView.foregroundTint: Int
    @ColorRes get() = foregroundTint
    @SuppressLint("RestrictedApi")
    set(value) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.imageTintList = context.resColorStateList(value)
        } else {
            (this as AppCompatImageView).supportImageTintList = context.resColorStateList(value)
        }
    }

@SuppressLint("CheckResult")
fun ImageView.load(image: String, @DrawableRes placeHolder: Int = 0) {
    val imageLoader: ImageLoader = (context?.applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(context).apply {
        data(image)
        target(this@load)
        placeholder(placeHolder)
        fallback(placeHolder)
        error(placeHolder)
    }.build()
    imageLoader.execute(loadRequest)
}

@SuppressLint("CheckResult")
fun ImageView.load(
    image: String,
    @DrawableRes placeHolder: Int = 0,
    transformations: coil.transform.Transformation?
) {
    val imageLoader: ImageLoader = (context?.applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(context).apply {
        data(image)
        target(this@load)
        placeholder(placeHolder)
        fallback(placeHolder)
        error(placeHolder)
        transformations?.let {
            transformations(it)
        }
    }.build()
    imageLoader.execute(loadRequest)
}


@SuppressLint("CheckResult")
fun ImageView.loadSkipCache(
    image: Uri,
    @DrawableRes placeHolder: Int = 0
) {
    val imageLoader: ImageLoader = (context?.applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(context).apply {
        data(image)
        target(this@loadSkipCache)
        placeholder(placeHolder)
        fallback(placeHolder)
        error(placeHolder)
        memoryCachePolicy(CachePolicy.DISABLED)
    }.build()
    imageLoader.execute(loadRequest)
}

@SuppressLint("CheckResult")
fun ImageView.loadSkipCache(image: String, @DrawableRes placeHolder: Int = 0) {
    val imageLoader: ImageLoader = (context?.applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(context).apply {
        data(image)
        target(this@loadSkipCache)
        placeholder(placeHolder)
        fallback(placeHolder)
        error(placeHolder)
        memoryCachePolicy(CachePolicy.DISABLED)
    }.build()
    imageLoader.execute(loadRequest)
}

@SuppressLint("CheckResult")
inline fun ImageView.load(
    image: String,
    @DrawableRes placeHolder: Int = 0,
    crossinline f: Drawable.() -> Unit
) {
    val imageLoader: ImageLoader = (context?.applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(context).apply {
        data(image)
        target(onSuccess = {
            this@load.setImageDrawable(it)
            f(it)
        })
        placeholder(placeHolder)
        fallback(placeHolder)
        error(placeHolder)
    }.build()
    imageLoader.execute(loadRequest)
}

@SuppressLint("CheckResult")
inline fun ImageView.loadSkipCache(
    image: String,
    @DrawableRes placeHolder: Int = 0,
    crossinline f: Drawable.() -> Unit
) {
    val imageLoader: ImageLoader = (context?.applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(context).apply {
        data(image)
        target(onSuccess = {
            this@loadSkipCache.setImageDrawable(it)
            f(it)
        })
        placeholder(placeHolder)
        fallback(placeHolder)
        error(placeHolder)
        memoryCachePolicy(CachePolicy.DISABLED)
    }.build()
    imageLoader.execute(loadRequest)
}

@SuppressLint("CheckResult")
fun Context.load(image: Uri?, f: (Drawable?) -> Unit) {
    val imageLoader: ImageLoader = (applicationContext as AppApplication).getKoin().get()
    val loadRequest = LoadRequest.Builder(applicationContext).apply {
        data(image)
        target(onSuccess = {
            f(it)
        },onError = {
            f(null)
        })
    }.build()
    imageLoader.execute(loadRequest)
}

/*
@SuppressLint("CheckResult")
fun ImageView.load(image: String, @DrawableRes placeHolder: Int = -1) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .apply(requestOptions)
            .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.load(image: String, @DrawableRes placeHolder: Int = -1, transformations: Transformation<Bitmap>?) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    transformations?.also {
        requestOptions.transform(it)
    }
    Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .apply(requestOptions)
            .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadSkipCache(image: String, @DrawableRes placeHolder: Int = -1, transformations: Transformation<Bitmap>?) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    requestOptions.skipMemoryCache(true)
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    transformations?.also {
        requestOptions.transform(it)
    }
    Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .apply(requestOptions)
            .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadSkipCache(image: Uri, @DrawableRes placeHolder: Int = -1, transformations: Transformation<Bitmap>?) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    requestOptions.skipMemoryCache(true)
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    transformations?.also {
        requestOptions.transform(it)
    }
    Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .apply(requestOptions)
            .into(this)
}

@SuppressLint("CheckResult")
fun ImageView.loadSkipCache(image: String, @DrawableRes placeHolder: Int = -1) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    requestOptions.skipMemoryCache(true)
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    Glide.with(context.applicationContext)
            .asBitmap()
            .load(image)
            .apply(requestOptions)
            .into(this)
}

@SuppressLint("CheckResult")
inline fun <reified T> ImageView.load(image: String, @DrawableRes placeHolder: Int = -1, crossinline f: T?.() -> Unit) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    Glide.with(context.applicationContext)
            .`as`(T::class.java)
            .load(image)
            .apply(requestOptions)
            .listener(object : RequestListener<T> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
                    f(null)
                    return false
                }

                override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    f(resource)
                    return true
                }

            })
            .into(this)
}

@SuppressLint("CheckResult")
inline fun <reified T> ImageView.loadSkipCache(image: String, @DrawableRes placeHolder: Int = -1, crossinline f: T?.() -> Unit) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    requestOptions.skipMemoryCache(true)
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    Glide.with(context.applicationContext)
            .`as`(T::class.java)
            .load(image)
            .apply(requestOptions)
            .listener(object : RequestListener<T> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
                    f(null)
                    return false
                }

                override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    f(resource)
                    return true
                }

            })
            .into(this)
}

@SuppressLint("CheckResult")
inline fun <reified T> ImageView.load(image: String, @DrawableRes placeHolder: Int = -1, transformations: Transformation<Bitmap>?, crossinline f: T?.() -> Unit) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    transformations?.also {
        requestOptions.transform(it)
    }
    Glide.with(context.applicationContext)
            .`as`(T::class.java)
            .load(image)
            .apply(requestOptions)
            .listener(object : RequestListener<T> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
                    f(null)
                    return false
                }

                override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    f(resource)
                    return true
                }

            })
            .into(this)
}

@SuppressLint("CheckResult")
inline fun <reified T> ImageView.loadSkipCache(image: String, @DrawableRes placeHolder: Int = -1, transformations: Transformation<Bitmap>?, crossinline f: T?.() -> Unit) {
    val requestOptions = RequestOptions()
    requestOptions.placeholder(placeHolder)
    requestOptions.error(placeHolder)
    requestOptions.skipMemoryCache(true)
    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE)
    transformations?.also {
        requestOptions.transform(it)
    }
    Glide.with(context.applicationContext)
            .`as`(T::class.java)
            .load(image)
            .apply(requestOptions)
            .listener(object : RequestListener<T> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<T>?, isFirstResource: Boolean): Boolean {
                    f(null)
                    return false
                }

                override fun onResourceReady(resource: T?, model: Any?, target: Target<T>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    f(resource)
                    return true
                }

            })
            .into(this)
}

@SuppressLint("CheckResult")
fun Context.downloadBitmap(image: String, f: Bitmap?.() -> Unit) {
    Glide.with(applicationContext)
            .asBitmap()
            .load(image)
            .listener(object : RequestListener<Bitmap> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {
                    f(null)
                    return false
                }

                override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    f(resource)
                    return true
                }
            })
            .into(BaseTarget<Bitmap>())
}

@SuppressLint("CheckResult")
fun Context.downloadFile(image: String, f: File?.() -> Unit) {
    Glide.with(applicationContext)
            .downloadOnly()
            .load(image)
            .listener(object : RequestListener<File> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<File>?, isFirstResource: Boolean): Boolean {
                    f(null)
                    return false
                }

                override fun onResourceReady(resource: File?, model: Any?, target: Target<File>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    f(resource)
                    return true
                }
            })
            .into(BaseTarget<File>())
}

fun Context.clearImageCache() {
    val glide = Glide.get(this)
    glide.clearMemory()
    object : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg voids: Void): Void? {
            glide.clearDiskCache()
            return null
        }
    }.execute()
}

private class BaseTarget<T>(val width: Int = SIZE_ORIGINAL, val height: Int = SIZE_ORIGINAL) : Target<T> {
    override fun onResourceReady(resource: T, transition: Transition<in T>?) {
    }

    private var request: Request? = null
    override fun onLoadStarted(placeholder: Drawable?) {
    }

    override fun onLoadFailed(errorDrawable: Drawable?) {
    }

    override fun getRequest(): Request? {
        return request
    }

    override fun onStop() {
    }

    override fun setRequest(request: Request?) {
        this.request = request
    }

    override fun onLoadCleared(placeholder: Drawable?) {
    }

    override fun onStart() {
    }

    override fun onDestroy() {
    }

    override fun getSize(cb: SizeReadyCallback) {
        cb.onSizeReady(width, height)
    }

    override fun removeCallback(cb: SizeReadyCallback) {
    }
}*/
