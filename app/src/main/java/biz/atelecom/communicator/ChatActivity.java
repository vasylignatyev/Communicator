package biz.atelecom.communicator;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class ChatActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

        //stoolbar.setTitle("TEST");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Bundle extras = getIntent().getExtras();
        String phone_from = null;
        String phone_to = null;

        if (extras != null) {
            phone_from = extras.getString(ChatFragment.ARG_PHONE_FROM, null);
            phone_to = extras.getString(ChatFragment.ARG_PHONE_TO, null);
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        ChatFragment chatFragment = ChatFragment.newInstance( phone_from, phone_to);
        //ChatFragment chatFragment = ChatFragment.newInstance( "380894770007", "380894770075");
        fragmentManager.beginTransaction().replace(R.id.fragment, chatFragment).commit();
    }
}
