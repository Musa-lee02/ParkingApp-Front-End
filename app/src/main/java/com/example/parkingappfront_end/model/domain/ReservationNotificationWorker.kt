package com.example.parkingappfront_end.model.domain

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.parkingappfront_end.MainActivity
import com.example.parkingappfront_end.R
import com.example.parkingappfront_end.model.ReservationWithDetails
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ReservationNotificationWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val reservationId = inputData.getLong("reservationId", -1)
        val title = inputData.getString("title") ?: return Result.failure()
        val message = inputData.getString("message") ?: return Result.failure()

        Log.d("NotificationWorker", "Executing notification for reservationId: $reservationId")
        showNotification(reservationId, title, message)
        return Result.success()
    }

    private fun showNotification(reservationId: Long, title: String, message: String) {
        val channelId = "reservation_channel"
        val notificationId = reservationId.toInt()

        createNotificationChannel(channelId)

        val intent = Intent(applicationContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(applicationContext).apply {
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notify(notificationId, notification)
            } else {
                Log.e("NotificationWorker", "Permission for POST_NOTIFICATIONS not granted")
            }
        }
    }

    private fun createNotificationChannel(channelId: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Prenotazioni Parcheggio"
            val descriptionText = "Notifiche per le prenotazioni del parcheggio"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = applicationContext
                .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        fun scheduleNotification(
            context: Context,
            reservationId: Long,
            startDate: LocalDateTime,
            spaceName: String
        ) {
            val workManager = WorkManager.getInstance(context)

            val zoneId = ZoneId.of("Europe/Rome")
            val currentTime = LocalDateTime.now(zoneId)
            val notificationTime = startDate.minusMinutes(15).atZone(zoneId)
            val delay = Duration.between(currentTime.atZone(zoneId), notificationTime)

            if (delay.isNegative) {
                Log.w("NotificationWorker", "Notification time has already passed")
                return
            }

            val notificationData = workDataOf(
                "reservationId" to reservationId,
                "title" to "Prenotazione in arrivo",
                "message" to "La tua prenotazione per $spaceName inizierà tra 15 minuti"
            )

            val notificationWork = OneTimeWorkRequestBuilder<ReservationNotificationWorker>()
                .setInitialDelay(delay)
                .setInputData(notificationData)
                .build()

            workManager.enqueueUniqueWork(
                "reservation_$reservationId",
                ExistingWorkPolicy.REPLACE,
                notificationWork
            )

            Log.d("NotificationWorker", "Scheduled notification for reservationId: $reservationId with delay: ${delay.seconds} seconds")
        }
    }
}

