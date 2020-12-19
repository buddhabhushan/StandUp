package com.example.standup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    ToggleButton alarmToggle;

    private NotificationManager mNotificationManager;

    private static final int NOTIFICATION_ID = 0;
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        alarmToggle = findViewById(R.id.alarmToggle);

        // Get the alarm manager
        final AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        final long repeatInterval = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        final long triggerTime = SystemClock.elapsedRealtime() + repeatInterval;

        Intent notifyIntent = new Intent(this, AlarmReceiver.class);
        // Check whether already an pendingIntent is present with the same matching intent
        boolean alarmUp = (PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_NO_CREATE) != null);
        // Set the value of the alarmToggle button according to the result of above statement
        alarmToggle.setChecked(alarmUp);


        final PendingIntent notifyPendingIntent = PendingIntent.getBroadcast(
                this, NOTIFICATION_ID, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        // Setting the onChecked ChangeListener
        alarmToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                String toastMessage = "";
                if (isChecked) {
                    if (alarmManager != null) {
                        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                                triggerTime, repeatInterval, notifyPendingIntent);
                    }
                    toastMessage = "Stand Up Alarm On!";
                } else {
                    if (alarmManager != null) {
                        alarmManager.cancel(notifyPendingIntent);
                    }
                    toastMessage = "Stand Up Alarm Off!";
                }
                Toast.makeText(MainActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
            }
        });

        createNotificationChannel();
    }

    private void createNotificationChannel() {

        // Get the instance of notification manager class
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    PRIMARY_CHANNEL_ID, "Stand up Notification", NotificationManager.IMPORTANCE_HIGH);

            // Adding the parameters to the notification channel
            notificationChannel.setDescription("Notifies every 15 minutes to stand up and walk!");
            notificationChannel.enableVibration(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableLights(true);

            mNotificationManager.createNotificationChannel(notificationChannel);
        }
    }


    public void showNextAlarm(View view) {

        String toastMessage = "No Alarm was set!!";
        // Check if the alarm is set or not and alarmManager is available
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        AlarmManager.AlarmClockInfo info = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            info = alarmManager.getNextAlarmClock();
            if (info != null) {
                long nextAlarm = info.getTriggerTime();
                toastMessage = (new Date(nextAlarm)).toString();
            }
        }


        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();

    }
}