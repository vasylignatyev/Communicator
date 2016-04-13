package biz.atelecom.communicator; /**
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

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import biz.atelecom.communicator.ajax.HTTPManager;
import biz.atelecom.communicator.ajax.RequestPackage;

public class RegistrationIntentService extends IntentService {

    public static final String ARG_NUMBER = "number";

    private static final String TAG = "MyApp";
    private static final String[] TOPICS = {"global"};
    private SharedPreferences mSharedPreferences;

    private String mNumber;

    public RegistrationIntentService() {
        super(TAG);
     }


    @Override
    protected void onHandleIntent(Intent intent) {
        if(mSharedPreferences == null) {
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        }
        mNumber = intent.getStringExtra(ARG_NUMBER);

        try {
            // [START register_for_gcm]
            // Initially this call goes out to the network to retrieve the token, subsequent calls
            // are local.
            // R.string.gcm_defaultSenderId (the Sender ID) is typically derived from google-services.json.
            // See https://developers.google.com/cloud-messaging/android/start for details on this file.
            // [START get_token]
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            // [END get_token]
            Log.d(TAG, "GCM Registration Token: " + token);

            sendRegistrationToServer(token);

            // Subscribe to topic channels
            subscribeTopics(token);

             // [END register_for_gcm]
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
        }
        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(QuickstartPreferences.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    /**
     * Subscribe to any GCM topics of interest, as defined by the TOPICS constant.
     *
     * @param token GCM token
     * @throws IOException if unable to reach the GCM PubSub service
     */
    // [START subscribe_topics]
    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
    // [END subscribe_topics]


    /**
     * Persist registration to third-party servers.
     *
     * Modify this method to associate the user's GCM registration token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        rp.setMethod("GET");
        rp.setParam("functionName", "setGcmToken");
        rp.setParam("number", mNumber );
        rp.setParam("gcm_token", token);
        SetGcmTokenAsyncTask task = new SetGcmTokenAsyncTask();
        task.execute(rp);
    }

    private class SetGcmTokenAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "SetGcmTokenAsyncTask:" + s);

            try {
                JSONObject jObj = new JSONObject(s);
                if(jObj.has("gcm_short_token")) {
                    String gcm_short_token = jObj.getString("gcm_short_token");
                    // You should store a boolean that indicates whether the generated token has been
                    // sent to your server. If the boolean is false, send the token to your server,
                    // otherwise your server should have already received the token.
                    SharedPreferences.Editor ed = mSharedPreferences.edit();
                    ed.putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, true);
                    ed.putString(QuickstartPreferences.GCM_SHORT_TOKEN, gcm_short_token);
                    ed.apply();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // If an exception happens while fetching the new token or updating our registration data
                // on a third-party server, this ensures that we'll attempt the update at a later time.
                mSharedPreferences.edit().putBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false).apply();
            }

        }
    }

}
