package com.mvvm.service
import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.mvvm.utils.CommonUtils

class TrackingWorkManager(ctx: Context, params: WorkerParameters) : Worker(ctx, params) {
    override fun doWork(): Result {
        val appContext = applicationContext
        try {
            CommonUtils.startLocationService(appContext)
        } catch (e: Exception){
        }
        return Result.success()
    }

}