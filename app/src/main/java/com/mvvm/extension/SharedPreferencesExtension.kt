@file:JvmName("PrefUtils")

package com.mvvm.extension

import android.content.Context
import android.content.SharedPreferences

object P {

    fun defaultPrefs(context: Context): SharedPreferences =
        context.getSharedPreferences("SharedPreferences", Context.MODE_PRIVATE)

    fun customPrefs(context: Context, name: String): SharedPreferences =
        context.getSharedPreferences(name, Context.MODE_PRIVATE)
}