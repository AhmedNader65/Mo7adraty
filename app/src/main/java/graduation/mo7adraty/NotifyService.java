package graduation.mo7adraty;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.NotificationCompat;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;

import graduation.mo7adraty.activities.Home;
import graduation.mo7adraty.activities.HomeDr;

public class NotifyService extends IntentService {

    public NotifyService() {
        super("Lectures");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("here in notifications","out");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if(sp.getBoolean(getString(R.string.pref_enable_notifications_key),true)) {
            Log.e("here in notifications","in 1");
            noOtify(intent.getStringExtra("type"));
        }
    }

    public void noOtify(String type) {
        Context context = getApplicationContext();
        Intent resultIntent;
        if(type.equals("dr")) {
            resultIntent = new Intent(context, HomeDr.class);
        }else{
            resultIntent = new Intent(context,Home.class);
        }
        Log.e("here in notifications","in 2");
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
        notificationBuilder.setContentText("ﻻ تنسى ان تتابع جدول محاضراتك");
        notificationBuilder.setContentTitle("صباح الخير");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        notificationBuilder.setContentIntent(pendingIntent);
        notificationBuilder.setSmallIcon(R.drawable.logo);
        notificationBuilder.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.logo)).getBitmap());
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setDefaults( Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE|Notification.DEFAULT_SOUND);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notificationBuilder.build());
        //refreshing last sync

    }
}
