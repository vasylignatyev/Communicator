/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package biz.atelecom.communicator;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.squareup.otto.Produce;

import biz.atelecom.communicator.models.Message;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyApp";

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("Message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);

        if(MyBus.getInstance().isRegistered()){
           // Log.d("MyApp", "app is alive");
            MyBus.getInstance().post(new Message(data));
        } else {
            String numberA = data.getString(ChatFragment.ARG_NUMBER_A);
            String numberB = data.getString(ChatFragment.ARG_NUMBER_B);
            data.putString(ChatFragment.ARG_NUMBER_A,numberB);
            data.putString(ChatFragment.ARG_NUMBER_B,numberA);
            sendNotification(data);
        }

    }

    private void sendNotification(Bundle data) {
        //Intent intent = new Intent(this, ChatActivity.class);
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtras(data);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.atelecom_ic_notification   )
                .setContentTitle("Atelecom Message")
                .setContentText("message")
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }
}
