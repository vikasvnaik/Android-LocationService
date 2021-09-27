@file:JvmName("DrawableUtils")

package com.iplt.domain.extension

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import androidx.annotation.ColorInt
import androidx.core.graphics.drawable.DrawableCompat

var Drawable.tint: Int
    @ColorInt get() = tint
    set(value) {
        DrawableCompat.setTint(DrawableCompat.wrap(this).mutate(), value)
    }

var Drawable.tintList: ColorStateList
    get() = tintList
    set(value) {
        DrawableCompat.setTintList(DrawableCompat.wrap(this).mutate(), value)
    }

fun Drawable.tint(@ColorInt color: Int) {
    val drawable = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(drawable, color)
    drawable.invalidateSelf()
}