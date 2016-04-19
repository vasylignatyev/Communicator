package biz.atelecom.communicator;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class ChatActivity extends AppCompatActivity {

    String numberA = null;
    String numberB = null;

    private ChatFragment mChatFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("MyApp", "ChatActivity: onCreate");

        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //toolbar.setTitle("Chat");
        setSupportActionBar(toolbar);


        LocalBroadcastManager.getInstance(this).registerReceiver(
                mNewMessageReceiver, new IntentFilter(QuickstartPreferences.NEW_MESSAGE_RECEIVED));


        //stoolbar.setTitle("TEST");
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
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            numberA  = extras.getString(ChatFragment.ARG_NUMBER_A, null);
            numberB = extras.getString(ChatFragment.ARG_NUMBER_B, null);
        }
        if (savedInstanceState == null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            mChatFragment = ChatFragment.newInstance( numberA, numberB);
            fragmentManager.beginTransaction().add(R.id.fragment, mChatFragment).commit();
        }
    }

    private BroadcastReceiver mNewMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //String action = intent.getAction();
            //mGcmToken = intent.getStringExtra(GCM_TOKEN);

            //Log.d("MyApp", "MainActivity::mMessageReceiver action =" + action + ", mGcmToken =" + mGcmToken);
            String senderNumber = intent.getStringExtra(ChatFragment.ARG_NUMBER_A);

            Log.d("MyApp", "New message from: " + senderNumber);
            //Log.d("MyApp", "New message from: " + numberB);

            if(numberB.equals(senderNumber))
                mChatFragment.getMessageList();
        }
    };
}
