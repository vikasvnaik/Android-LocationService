package com.mvvm.ui.base

import android.content.Context
import androidx.startup.Initializer
import com.mvvm.di.dependency
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class KoinInitializer : Initializer<KoinApplication> {

    override fun create(context: Context): KoinApplication {
        return startKoin {
            androidLogger(Level.INFO)
            androidContext(context)
            koin.loadModules(dependency())
            koin.createRootScope()
        }
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}