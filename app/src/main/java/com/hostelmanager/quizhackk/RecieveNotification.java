package com.hostelmanager.quizhackk;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

/**
 * Created by sudha on 17-Aug-18.
 */

public class RecieveNotification extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //super.onMessageReceived(remoteMessage);
        // ...
        //Toast.makeText(this, "Received some Message", Toast.LENGTH_LONG).show();


      //  if (remoteMessage.getData().size() > 0) {
            Log.d("key", "Message data " + remoteMessage.getNotification().getTitle());
            scheduleJob(remoteMessage.getData(), remoteMessage.getNotification().getBody(),remoteMessage.getNotification().getTitle());
       // }


        return;
        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        //Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        //if (remoteMessage.getData().size() > 0) {
        //Log.d(TAG, "Message data payload: " + remoteMessage.getData());

        //if (/* Check if data needs to be processed by long running job */ true) {
        // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
        //scheduleJob();
        // } else {
        // Handle message within 10 seconds
        //handleNow();
        //  }

        // }

        // Check if message contains a notification payload.
       /* if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }*/

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    public void scheduleJob(Map<String, String> data,String dat,String tit){
        String[]arr = new String[4];
        int i=0;
        Bundle bundle = new Bundle();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            bundle.putString(arr[i]=entry.getKey(), arr[i+1]=entry.getValue());
            i+=2;
        }

        Intent in =new Intent(Intent.ACTION_VIEW);
        Log.d("asdfgh",arr[1] +"///"+arr[3]);
        in.setData(Uri.parse("http://"+arr[1]));
        in.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,0 ,in, 0);
        String chid = getString(R.string.default_notification_channel_id);
        Uri d = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(this,chid)
                .setSmallIcon(R.drawable.icon)
                .setAutoCancel(true)
                .setContentTitle(tit)
                .setContentText(dat)
                .setSound(d)
                .setContentIntent(pendingIntent);

        NotificationManager n = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(chid,"Channel human Readable title", NotificationManager.IMPORTANCE_DEFAULT);
            n.createNotificationChannel(channel);
        }
        n.notify(0,noti.build());
    }
}
