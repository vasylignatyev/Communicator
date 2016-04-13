package biz.atelecom.communicator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONException;
import org.json.JSONObject;

import biz.atelecom.communicator.ajax.HTTPManager;
import biz.atelecom.communicator.ajax.RequestPackage;

public class LoginActivity extends AppCompatActivity implements
       LoginFragment.OnLoginInteractionListener  {

    public static final String GCM_TOKEN = "gcmToken";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String mGcmToken = null;

    private Context mContext;

    private static String mNumber;

    public static String getNumber() {
        return mNumber;
    }




    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            mGcmToken = intent.getStringExtra(GCM_TOKEN);

            Log.d("MyApp", "MainActivity::mMessageReceiver action =" + action + ", mGcmToken =" + mGcmToken);

            Intent mainActivityIntent = new Intent( context, MainActivity.class);
            mainActivityIntent.putExtra(MainActivity.ARG_NUMBER, mNumber);
            startActivity(mainActivityIntent);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mContext = this;


        LocalBroadcastManager.getInstance(this).registerReceiver(
                mMessageReceiver, new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));


/*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String gcmShortToken = sp.getString(QuickstartPreferences.GCM_SHORT_TOKEN, null);
        String number = sp.getString(QuickstartPreferences.REGISTERED_NUMBER, null);

        if((gcmShortToken!=null) && (number!=null) ) {
            mNumber = number;
            checkGcmShortToken(gcmShortToken, number);
        }
    }

    @Override
    public void loginSuccess(String number) {
        mNumber = number;

        //start GCM registration
        if ((mNumber != null) && checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            intent.putExtra( RegistrationIntentService.ARG_NUMBER, mNumber);
            startService(intent);
        }
    }
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.d("MyApp", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
    private void checkGcmShortToken(String gcmShortToken, String number) {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        rp.setMethod("GET");
        rp.setParam("functionName", "checkGcmShortToken");
        rp.setParam("gcm_short_token", gcmShortToken);
        rp.setParam("number", number);

        CheckGcmShortTokenAsyncTask task = new CheckGcmShortTokenAsyncTask();
        task.execute(rp);
    }
    private class CheckGcmShortTokenAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "checkGcmShortToken:" + s);
            try {
                JSONObject obj = new JSONObject(s);
                if(obj.has("result")) {
                    if(obj.getInt("result") == 1) {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        startActivity(intent);
                    } else {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
                        sp.edit().remove(QuickstartPreferences.GCM_SHORT_TOKEN);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
