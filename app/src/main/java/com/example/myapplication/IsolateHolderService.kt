package com.example.myapplication

import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class IsolateHolderService : Service(){
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        startForeground(1, getNotification())
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.getLastLocation().addOnSuccessListener { location->
            if(location!=null)
            Log.d("location", location.toString())
        }
    }
    private fun getNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Notification channel is available in Android O and up
            val channel = NotificationChannel(
                "app.yukams/locator_plugin", "Flutter Locator Plugin",
                NotificationManager.IMPORTANCE_LOW
            )

            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }

        val intent = Intent(this, getMainActivityClass(this))
        intent.action ="com.yukams.background_locator_2.notification"

        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            this,
            1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this,"app.yukams/locator_plugin")
            .setContentTitle("Start Location Tracking")
            .setContentText("Track location in background")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Background location is on to keep the app up-tp-date with your location. This is required for main features to work properly when the app is not running.")
            )
            .setSmallIcon(0)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setOnlyAlertOnce(true) // so when data is updated don't make sound and alert in android 8.0+
            .setOngoing(true)
            .build()
    }

    private fun getMainActivityClass(context: Context): Class<*>? {
        val packageName = context.packageName
        val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
        val className = launchIntent?.component?.className ?: return null

        return try {
            Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            null
        }
    }

      fun onLocationUpdated(location: HashMap<Any, Any>?) {

    }
}