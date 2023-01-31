package com.friendzrandroid.utils.fcm

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.NavDeepLinkBuilder
import com.friendzrandroid.R
import com.friendzrandroid.core.utils.UserSessionManagement
import com.friendzrandroid.home.MainActivity
import com.friendzrandroid.home.data.model.enum.FeedKeyStatus
import com.friendzrandroid.home.fragment.home.feed.FeedFragment
import com.friendzrandroid.home.fragment.home.messages.chat.ChatFragment
import com.friendzrandroid.home.fragment.home.messages.inbox.InboxFragment
import com.friendzrandroid.home.fragment.home.requestes.RequestsFragment
import com.friendzrandroid.splash.presentation.activity.SplashActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseMessagingServ"

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.data.isNotEmpty()) {
            handleDeepLink(remoteMessage)

            Log.e(TAG, "onMessageReceived Data: ${remoteMessage.data}")
        }

        super.onMessageReceived(remoteMessage)


    }


    private fun handleDeepLink(remoteMessage: RemoteMessage) {

        TriggerNotification(remoteMessage)
    }

    private fun TriggerNotification(remoteMessage: RemoteMessage) {

        var actionCode: String? = ""
        var action: String? = ""
        var messageType: String? = ""
        var isMute: Boolean?
        val pendingIntent: PendingIntent
        val bundle: Bundle


        if (UserSessionManagement.isUserLoggedIn()) {
            remoteMessage.data.values

            // Check if message contains a data payload.
            if (remoteMessage.getData().size > 0) {

                actionCode = remoteMessage.data.get("Action_code")
                action = remoteMessage.data.get("Action")
                messageType = remoteMessage.data.get("Messagetype")
                isMute = remoteMessage.data.get("muit").toBoolean()

            }



            if (action == FcmActions.FRIEND_REQUEST_ACTION) {
                bundle = Bundle()
                bundle.putString("userID", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setComponentName(MainActivity::class.java)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.userProfileFragment)
                    .setArguments(bundle)
                    .createPendingIntent()
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

                val currentRequestsCount = UserSessionManagement.getRequestsNumber()
                UserSessionManagement.updateFriendRequestNumber(currentRequestsCount + 1)

                if (MainActivity.isActive)
                    updateBadge(1)

                if (FeedFragment.isActive)
                    updateRequests(actionCode, FeedKeyStatus.OTHER_USER_SEND_REQUEST.key)


            } else if (action == FcmActions.ACCEPT_FRIEND_REQUEST_ACTION) {
                bundle = Bundle()
                bundle.putString("userID", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.userProfileFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)

                    .createPendingIntent()
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

                if (RequestsFragment.isActive)
                    updateRequests()

                if (FeedFragment.isActive)
                    updateRequests(actionCode, FeedKeyStatus.IS_FRIEND.key)

            } else if (action == FcmActions.BLOCK_FRIEND) {

                bundle = Bundle()
                bundle.putString("userID", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.userProfileFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)

                    .createPendingIntent()
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

                if (FeedFragment.isActive)
                    updateRequests(actionCode, FeedKeyStatus.OTHER_USER_BLOCKED_YOU.key)

            } else if (action == FcmActions.EVENT_CHAT_ACTION
                || action == FcmActions.USER_CHAT_ACTION
                || action == FcmActions.GROUP_CHAT_ACTION
            ) {

                val chatID = actionCode
                val isEvent = action == FcmActions.EVENT_CHAT_ACTION
                val isFriend = action == FcmActions.USER_CHAT_ACTION
                val isGroup = action == FcmActions.GROUP_CHAT_ACTION
                val isGroupAdmin = remoteMessage.data["isAdmin"].equals("True")
                val chatName = remoteMessage.data["name"]
                val chatImage = remoteMessage.data["ImageUrl"]
                val senderImage = remoteMessage.data["senderImage"]
                val senderName = remoteMessage.data["senderDisplayName"]


                if (ChatFragment.receiverIsActive && ChatFragment.receiverChatId.equals(chatID)) {
                    appendMessageToChat(remoteMessage)
                } else {

                    val currentInboxCount = UserSessionManagement.getInboxNumber()
                    UserSessionManagement.updateInboxNumber(currentInboxCount + 1)

                    when {
                        InboxFragment.isUserInInbox -> {
                            reloadInbox()
                            updateBadge(2)
                        }

                        MainActivity.isActive -> {
                            updateBadge(2)
                        }
                    }


                    bundle = Bundle()
                    bundle.putString("chatID", chatID)
                    bundle.putString("chatImage", chatImage)
                    bundle.putString("chatName", chatName)
                    bundle.putString("senderName", senderName)
                    bundle.putString("senderImage", senderImage)
                    bundle.putBoolean("chatIsEvent", isEvent)
                    bundle.putBoolean("isFriend", isFriend)
                    bundle.putBoolean("chatIsGroup", isGroup)
                    bundle.putBoolean("isAdminGroup", isGroupAdmin)
                    pendingIntent = NavDeepLinkBuilder(applicationContext)
                        .setGraph(R.navigation.main_navigation)
                        .setDestination(R.id.navigation_Chat)
                        .setArguments(bundle)
                        .setComponentName(MainActivity::class.java)

                        .createPendingIntent()

                    showNotification(
                        remoteMessage.data["Title"],
                        remoteMessage.data["Body"],
                        remoteMessage.data["ImageUrl"],
                        pendingIntent
                    )
                }


            }
//            else if (action == FcmActions.USER_CHAT_ACTION) {
//
//                when {
//                    ChatFragment.isActive -> appendMessageToChat(remoteMessage)
//                    else -> {
//                        messageType = remoteMessage.data.get("Messagetype")
//                        isMute = remoteMessage.data.get("muit").toBoolean()
//
//                        bundle = Bundle()
//                        bundle.putString("actionCode", actionCode)
//                        bundle.putString("messageType", messageType!!)
//                        bundle.putBoolean("messageType", isMute)
//                        pendingIntent = NavDeepLinkBuilder(applicationContext)
//                            .setGraph(R.navigation.main_navigation)
//                            .setDestination(R.id.navigation_Chat)
//                            .setArguments(bundle)
//                            .setComponentName(MainActivity::class.java)
//
//                            .createPendingIntent()
//
//                        showNotification(
//                            remoteMessage.notification?.title,
//                            remoteMessage.notification?.body,
//                            pendingIntent
//                        )
//                    }
//
//                }
//
//
//            }

            else if (action == FcmActions.EVENT_ATTEND_ACTION) {


                bundle = Bundle()
                bundle.putString("eventID", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.eventDetailsFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)

                    .createPendingIntent()
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

            } else if (action == FcmActions.CHECK_EVENT_NEAR_YOU_ACTION) {


                bundle = Bundle()
                bundle.putString("actionCode", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.navigation_map)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)

                    .createPendingIntent()

                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

            } else if (action == FcmActions.UPDATE_EVENT_ACTION) {
                bundle = Bundle()
                bundle.putString("eventID", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.eventDetailsFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)
                    .createPendingIntent()
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

            } else if (action == FcmActions.UPDATE_EVENT_DATA_ACTION) {
                bundle = Bundle()
                bundle.putString("eventID", actionCode)
                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.eventDetailsFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)

                    .createPendingIntent()
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

            } else if (action == FcmActions.EVENT_REMINDER_ACTION) {
                bundle = Bundle()
                bundle.putString("actionCode", actionCode)

                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.eventDetailsFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)

                    .createPendingIntent()

                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

            } else if (action == FcmActions.CANCEL_FRIEND_REQUEST_ACTION) {

                bundle = Bundle()
                bundle.putString("actionCode", actionCode)

                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.profileFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)
                    .createPendingIntent()

                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

                val currentRequestsCount = UserSessionManagement.getRequestsNumber()
                UserSessionManagement.updateFriendRequestNumber(currentRequestsCount - 1)

                if (MainActivity.isActive)
                    updateBadge(1)

                if (RequestsFragment.isActive)
                    updateRequests()

                if (FeedFragment.isActive)
                    updateRequests(actionCode, FeedKeyStatus.NORMAL_FEED_STATE.key)


            } else if (action == FcmActions.JOINED_GROUP) {


                if (InboxFragment.isUserInInbox) {
                    reloadInbox()
                    //updateBadge(2)
                }

                bundle = Bundle()
                bundle.putString("actionCode", actionCode)

                pendingIntent = NavDeepLinkBuilder(applicationContext)
                    .setGraph(R.navigation.main_navigation)
                    .setDestination(R.id.eventDetailsFragment)
                    .setArguments(bundle)
                    .setComponentName(MainActivity::class.java)
                    .createPendingIntent()

                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )


            } else {

                val intent = Intent(this, SplashActivity::class.java)

                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

                pendingIntent =
                    PendingIntent.getActivity(
                        this,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                showNotification(
                    remoteMessage.data["Title"],
                    remoteMessage.data["Body"],
                    remoteMessage.data["ImageUrl"],
                    pendingIntent
                )

            }


        } else {

            val intent = Intent(this, SplashActivity::class.java)

            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

            pendingIntent =
                PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
            showNotification(
                remoteMessage.data["Title"],
                remoteMessage.data["Body"],
                remoteMessage.data["ImageUrl"],
                pendingIntent
            )
        }


    }

    private fun updateRequests(userId: String? = null, key: Int? = null) {
        val appReceiverIntent = Intent("update-request")
        userId?.let {
            appReceiverIntent.putExtra("userId", it)
        }

        key?.let {
            appReceiverIntent.putExtra("key", it)
        }
        //appReceiverIntent.putExtra("type", typeOfRequest)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(appReceiverIntent)
    }


    private fun updateBadge(whichBadgeToUpdate: Int) {
        val appReceiverIntent = Intent("update-badge")
        appReceiverIntent.putExtra("update", whichBadgeToUpdate)
        //chatReceiverIntent.action = ChatFragment.MESSAGE_BROAD_CAST_ACTION
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(appReceiverIntent)
    }


    private fun reloadInbox() {
        val inboxReceiverIntent = Intent("reload-inbox")
        inboxReceiverIntent.putExtra("reload", true)
        //chatReceiverIntent.action = ChatFragment.MESSAGE_BROAD_CAST_ACTION
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(inboxReceiverIntent)
    }

    private fun appendMessageToChat(remoteMessage: RemoteMessage) {
        val chatReceiverIntent = Intent("local-message")
        chatReceiverIntent.putExtra("newMessage", remoteMessage)
        //chatReceiverIntent.action = ChatFragment.MESSAGE_BROAD_CAST_ACTION
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(chatReceiverIntent)
    }


    private fun showNotification(
        notificationTitle: String?,
        notificationContent: String?,
        notificationImage: String?,
        pendingIntent: PendingIntent
    ) {
        // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
        val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val bitmap = getBitmapFromURL(notificationImage)

        val builder =
            NotificationCompat.Builder(this, getString(R.string.default_notification_channel_id))
                .setSmallIcon(R.drawable.ic_logo_notification)
                .setLargeIcon(
                    BitmapFactory.decodeResource(
                        resources,
                        R.drawable.ic_logo_notification
                    )
                )
                .setContentTitle(notificationTitle)
                .setContentText(notificationContent)
                .setStyle(
                    NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null)
                )
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setSound(soundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = NotificationManagerCompat.from(this)
//        notificationManager.notify( /*Notification_base_id + 1*/System.currentTimeMillis()
//            .toInt(), builder.build()
//        )

        makeChanel()

        notificationManager.notify(R.string.default_notification_channel_id, builder.build())

    }


    fun makeChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = getString(R.string.channel_name)
            val color = getColor(R.color.primary_color)
            val description = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                getString(R.string.default_notification_channel_id),
                name,
                importance
            )
            channel.description = description

            channel.enableLights(true)
            channel.lightColor = Color.RED

            channel.setShowBadge(true)
            channel.vibrationPattern = null
            channel.enableVibration(false)
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    private fun getBitmapFromURL(strURL: String?): Bitmap? {
        return try {
            val url = URL(strURL)
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.doInput = true
            connection.connect()
            val input: InputStream = connection.inputStream
            BitmapFactory.decodeStream(input)
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onNewToken(fcmToken: String) {
        super.onNewToken(fcmToken)
        UserSessionManagement.saveUserFirebaseToken(fcmToken);
    }
}


