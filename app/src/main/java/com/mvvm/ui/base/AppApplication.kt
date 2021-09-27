package com.mvvm.ui.base

import androidx.multidex.MultiDexApplication
import com.google.android.libraries.places.api.Places
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mvvm.BuildConfig
import timber.log.Timber


class AppApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            Timber.plant(LineNumberDebugTree())
        } else {
            FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
        }
    }
}

class LineNumberDebugTree : Timber.DebugTree() {
    override fun createStackElementTag(element: StackTraceElement): String? {
        return "${element.fileName}:${element.lineNumber}:${element.methodName}"
    }
}