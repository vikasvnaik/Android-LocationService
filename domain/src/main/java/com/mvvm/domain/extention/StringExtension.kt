@file:JvmName("StringUtils")

package com.mvvm.domain.extention

import android.text.Editable
import android.util.Patterns
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.serialization.json.Json
import java.util.*


object G {
    val gson = GsonBuilder()
        .disableHtmlEscaping()
        .setPrettyPrinting()
        .create()
    val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

}

/**
 * Converts string to integer safely otherwise returns zero
 */
fun String.asInt(): Int = toInt().or(-1)

fun String.asBoolean(): Boolean = toBoolean().or(false)

val Int.isNegative get() = this < 0

val Boolean.intValue get() = if (this) 1 else 0

fun String.toCamelCase(): String {
    var titleText = ""
    if (!this.isEmpty()) {
        val words = this.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        words.filterNot { it.isEmpty() }
            .map {
                it.substring(0, 1).toUpperCase(Locale.getDefault()) + it.substring(1)
                    .toLowerCase(Locale.getDefault())
            }
            .forEach { titleText += "$it " }
    }
    return titleText.trim { it <= ' ' }
}

fun String?.isNotEmptyOrNull(string: String.() -> Unit) {
    this?.let {
        if (!it.isEmpty()) {
            string()
        }
    }
}

val String.containsLetters get() = matches(".*[a-zA-Z].*".toRegex())

val String.containsNumbers get() = matches(".*[0-9].*".toRegex())

val String.isAlphanumeric get() = matches("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$".toRegex())

val String.isAlphabetic get() = matches("^[a-zA-Z]*$".toRegex())

fun String.isEmail(): Boolean = Patterns.EMAIL_ADDRESS.matcher(this).matches()

fun String.isUrl(): Boolean = Patterns.WEB_URL.matcher(this).matches()

fun join(vararg params: Any?) = params.joinToString()

fun Any.toJson(): String = try {
    G.gson.toJson(this)
} catch (e: Exception) {
    e.printStackTrace()
    ""
}

inline fun <reified T : Any> String?.fromJson() = G.gson.fromJson<T>(this, T::class.java)

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)

fun Int.times(predicate: (Int) -> Unit) = repeat(this, predicate)

val String.isJson: Boolean
    get() {
        this.trim { it <= ' ' }
        if (this.startsWith("{")) {
            return true
        }
        if (this.startsWith("[")) {
            return true
        }
        return false
    }

fun <T> String?.fromTypedJson(): T? {
    val type = object : TypeToken<T>() {

    }.type
    return try {
        G.gson.fromJson<T>(this, type)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun <T> Any?.toTypedJson(): String {
    val type = object : TypeToken<T>() {

    }.type
    return G.gson.toJson(this, type)
}

inline fun <reified T> T.deepCopy(): T {
    val stringProject = G.gson.toJson(this, T::class.java)
    return G.gson.fromJson(stringProject, T::class.java)
}

fun String.upperCase(locale: Locale = Locale.getDefault()) = toUpperCase(locale)
fun String.lowerCase(locale: Locale = Locale.getDefault()) = toLowerCase(locale)



