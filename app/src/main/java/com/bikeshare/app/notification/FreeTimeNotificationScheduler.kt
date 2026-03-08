package com.bikeshare.app.notification

import android.content.Context
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Schedules a notification 5 minutes before the free rental period ends.
 * Call when user has rented bikes and freeTimeMinutes is known; cancel when they have no bikes.
 */
object FreeTimeNotificationScheduler {

    private const val FIVE_MINUTES_SECONDS = 5 * 60
    const val WORK_TAG = "free_time_notification"

    /**
     * Schedules one notification per rented bike: 5 min before free time ends.
     * @param rentedSeconds seconds since rent started (from me/bikes)
     * @param freeTimeMinutes free period length in minutes (from me/limits), default 30
     */
    fun schedule(
        context: Context,
        bikeNumber: Int,
        rentedSeconds: Int,
        freeTimeMinutes: Int = 30,
    ) {
        if (freeTimeMinutes <= 5) return
        val freeTimeSeconds = freeTimeMinutes * 60
        val notificationAtSeconds = freeTimeSeconds - FIVE_MINUTES_SECONDS
        val delaySeconds = (notificationAtSeconds - rentedSeconds).toLong()
        if (delaySeconds <= 0) return
        val data = Data.Builder().putInt(FreeTimeNotificationWorker.KEY_BIKE_NUMBER, bikeNumber).build()
        val request = OneTimeWorkRequestBuilder<FreeTimeNotificationWorker>()
            .setInitialDelay(delaySeconds, TimeUnit.SECONDS)
            .setInputData(data)
            .addTag(WORK_TAG)
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "${FreeTimeNotificationWorker.WORK_NAME_PREFIX}$bikeNumber",
            ExistingWorkPolicy.REPLACE,
            request,
        )
    }

    fun cancelForBike(context: Context, bikeNumber: Int) {
        WorkManager.getInstance(context).cancelUniqueWork("${FreeTimeNotificationWorker.WORK_NAME_PREFIX}$bikeNumber")
    }

    fun cancelAll(context: Context) {
        WorkManager.getInstance(context).cancelAllWorkByTag(WORK_TAG)
    }
}
