package com.mvvm.extension

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import com.livinglifetechway.quickpermissions_kotlin.util.QuickPermissionsOptions

private fun permissionOptions(deny: (() -> Unit)? = null): QuickPermissionsOptions {
    return QuickPermissionsOptions(
        handleRationale = false,
        rationaleMethod = { _ ->
            deny?.invoke()
        },
        permanentDeniedMethod = { _ ->
            deny?.invoke()
        })
}

fun AppCompatActivity.callStoragePermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }

fun Fragment.callStoragePermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }

fun AppCompatActivity.callCameraPermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.CAMERA,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }

fun Fragment.callCameraPermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        //Manifest.permission.CAMERA,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }

fun AppCompatActivity.callDialPhonePermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(Manifest.permission.CALL_PHONE, options = permissionOptions(deny)) {
        allow.invoke()
    }

fun Fragment.callDialPhonePermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(Manifest.permission.CALL_PHONE, options = permissionOptions(deny)) {
        allow.invoke()
    }

fun AppCompatActivity.callLocationPermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }

@RequiresApi(Build.VERSION_CODES.Q)
fun AppCompatActivity.callBackgroundLocationPermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.ACCESS_BACKGROUND_LOCATION,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }


fun Fragment.callLocationPermission(allow: () -> Unit, deny: (() -> Unit)? = null) =
    runWithPermissions(
        Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }

fun AppCompatActivity.anyPermission(
    vararg permissions: String,
    allow: () -> Unit,
    deny: () -> Unit
) = runWithPermissions(
    *permissions,
    options = permissionOptions(deny)
) {
    allow.invoke()
}

fun Fragment.anyPermission(vararg permissions: String, allow: () -> Unit, deny: () -> Unit) =
    runWithPermissions(
        *permissions,
        options = permissionOptions(deny)
    ) {
        allow.invoke()
    }