package com.friendzrandroid.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.friendzrandroid.R;
import com.friendzrandroid.core.utils.UserSessionManagement;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


public class FirebaseNotification extends FirebaseMessagingService {
    private String TAG = "OPTIMUM NOTIFICATION SERVICE";
    private SharedPreferences sharedPrefs;
    public boolean isBackgroundRestricted;
    private int Notification_base_id = 1;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
      //  Timber.e(body.toString());
        if (remoteMessage.getNotification() != null) {
      //      Timber.e("Message Notification Body: " + remoteMessage.getNotification().toString());
            if (remoteMessage.getData().size() > 0 && remoteMessage.getData().containsKey("notification_type")) {
                // TODO: 2019-12-05 this should open notifications Activity
                int notificationType = Integer.parseInt(remoteMessage.getData().get("notification_type").toString());
                handleDeepLink(notificationType, remoteMessage);
            } else {
                // TODO: 2019-12-05 this creates a new notification and opens the splash
                handleDeepLink(0, remoteMessage);
            }
     //       EventBus.getDefault().post(new DrawerActivity.NotificationEvent(1));
        }
    }

    private void handleDeepLink(int notificationType, RemoteMessage remoteMessage) {

        TriggerNotification(remoteMessage, notificationType);
    }

    private void TriggerNotification(RemoteMessage remoteMessage, int notificationType) {
        //Intent intent = null;
    /*    if (PrefUtils.isLoggedIn(getApplicationContext())) {
            // TODO: 2019-12-05 user is logged in and should open his messages or notifications page
            if (notificationType == 1) { // TODO: 2019-12-05 goToEventDetials to Notifications tab
                intent = new Intent();
                intent.setAction("com.waysgroup.vrou_Notification");
                intent.putExtra("direction", "notifications");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            if (notificationType == 2) { // TODO: 2019-12-05 goToEventDetials to Messages tab
                intent = new Intent();
                intent.setAction("com.waysgroup.vrou_Notification");
                intent.putExtra("direction", "messages");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            // TODO: 2019-12-08 this is the default notification
            if (notificationType == 0) {
                intent = new Intent(this, SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
        } else {*/
//            intent = new Intent(this, SplashActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      //  }
   /*     if (notificationType == 1) {
            intent.putExtra("direction", "notifications");
        } else if (notificationType == 2) {
            intent.putExtra("direction", "messages");
        }*/
        int currentBadgeNumber = 1;//PrefUtils.getCurrentBadgeNumber(getApplicationContext());
       // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
       // PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        // if Android Version above 8 then we need to create a chanel in the system.
        makeChanel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id));
        // int badgeCount = 1;
//        ShortcutBadger.applyCount(this, currentBadgeNumber+1); //for 1.1.4+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        //    builder.setSmallIcon(R.drawable.test_nn);
         //   builder.setColor(getResources().getColor(R.color.app_color));
            builder.setSmallIcon(R.drawable.ic_logo_svg);
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setSound(soundUri);
            builder.setAutoCancel(true);
//            builder.setContentIntent(pendingIntent)
//                    .setPriority(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? NotificationManager.IMPORTANCE_HIGH : Notification.PRIORITY_MAX);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
//            notificationManagerCompat.notify(/*Notification_base_id + 1*/(int) System.currentTimeMillis(), builder.build());
            notificationManagerCompat.notify(/*Notification_base_id + 1*/(int) System.currentTimeMillis(), builder.build());
        } else {
            // notification.setSmallIcon(R.drawable.icon);
            builder.setSmallIcon(R.drawable.ic_logo_svg);
            builder.setContentTitle(remoteMessage.getNotification().getTitle());
            builder.setContentText(remoteMessage.getNotification().getBody());
            builder.setSound(soundUri);
            builder.setAutoCancel(true);
//            builder.setContentIntent(pendingIntent)
//                    .setPriority(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ? NotificationManager.IMPORTANCE_HIGH : Notification.PRIORITY_MAX);
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(remoteMessage.getNotification().getBody()));
            builder.setPriority(NotificationCompat.PRIORITY_HIGH);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify((int) System.currentTimeMillis(), builder.build());
        }
    }

    void makeChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(getString(R.string.default_notification_channel_id), name, importance);

            channel.setDescription(description);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.setShowBadge(true);
            channel.setVibrationPattern(/*new long[]{0, 1000, 500, 1000}*/null);
            channel.enableVibration(false);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);


        }


    }

    @Override
    public void onNewToken(String fcmToken) {
        super.onNewToken(fcmToken);
//        UserSessionManagement.saveUserToken(fcmToken);

    }

  /*  public static boolean isAppRunning(final Context context, final String packageName) {
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningAppProcessInfo> procInfos = activityManager.getRunningAppProcesses();
        if (procInfos != null) {
            for (final ActivityManager.RunningAppProcessInfo processInfo : procInfos) {
                if (processInfo.processName.equals(packageName)) {
                    return true;
                }
            }
        }
        return false;
    }*/
}