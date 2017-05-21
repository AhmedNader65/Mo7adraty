package graduation.mo7adraty.pushNotification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import graduation.mo7adraty.activities.Home;
import graduation.mo7adraty.R;
import graduation.mo7adraty.Utils;

import static android.content.ContentValues.TAG;

public class MyService extends FirebaseMessagingService {
    public MyService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            JSONObject obj = new JSONObject(remoteMessage.getData());
            try {
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(this)
                                .setSmallIcon(R.drawable.logo)
                                .setContentTitle(obj.getString("title"))
                                .setContentText(obj.getString("body"));
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                mBuilder.setSound(alarmSound);
                Intent notificationIntent = new Intent(this, Home.class);

                PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

                mBuilder.setContentIntent(contentIntent);
                mBuilder.setAutoCancel(false);
                mBuilder.setLights(Color.BLUE, 500, 500);
                long[] pattern = {500,500,500,500,500,500,500,500,500};
                mBuilder.setVibrate(pattern);
                int mNotificationId = Utils.getID();
// Gets an instance of the NotificationManager service
                NotificationManager mNotifyMgr =
                        (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
                mNotifyMgr.notify(mNotificationId, mBuilder.build());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }
}
