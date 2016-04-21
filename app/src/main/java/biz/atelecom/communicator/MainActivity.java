package biz.atelecom.communicator;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import biz.atelecom.communicator.ajax.HTTPManager;
import biz.atelecom.communicator.ajax.RequestPackage;
import biz.atelecom.communicator.models.MessageStat;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        MessagesFragment.OnMessageListFragmentListener {

    public static final String AJAX = "http://psoap.atlantistelecom.net/android/ajax.php";

    public static final String ARG_NUMBER = "arg_number";

    public static final String MESSAGE_FRAGMENT_TAG = "MESSAGE_FRAGMENT";
    public static final String CONTACT_FRAGMENT_TAG = "CONTACT_FRAGMENT";
    public static final String CHAT_FRAGMENT_TAG = "CHAT_FRAGMENT";

    private static String mNumber = null;
    public static String getNumber() {
        return mNumber;
    }

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
/**
 *
 */
        Log.d("MyApp", "MainActivity::onCreate");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        String numberA, numberB;
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            numberA  = extras.getString(ChatFragment.ARG_NUMBER_A, null);
            numberB = extras.getString(ChatFragment.ARG_NUMBER_B, null);
            mNumber = extras.getString(ARG_NUMBER, null);

            Log.d("MyApp", "mNumber: " +  mNumber);

            Fragment fragment;
            FragmentManager fragmentManager = getSupportFragmentManager();
            String fragmentTag = null;

            if((savedInstanceState == null) && (numberA!=null)&&(numberB!=null)){
                fragment = ChatFragment.newInstance( numberA, numberB);
                fragmentTag = CHAT_FRAGMENT_TAG;
                fragmentManager.beginTransaction().add(R.id.container, fragment, CHAT_FRAGMENT_TAG).commit();
            } else {
                if (mNumber == null) {
                    mNumber = sp.getString(QuickstartPreferences.REGISTERED_NUMBER, null);
                } else {
                    sp.edit().putString(QuickstartPreferences.REGISTERED_NUMBER, mNumber).apply();
                }
                fragment = MessagesFragment.newInstance(1);
                fragmentTag = MESSAGE_FRAGMENT_TAG;
            }
            fragmentManager.beginTransaction().replace(R.id.container, fragment, fragmentTag).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag(MESSAGE_FRAGMENT_TAG);
            if (fragment != null && fragment.isVisible()) {
                Fragment fragmentNew = ContactsFragment.newInstance(1);
                fm.beginTransaction().replace(R.id.container, fragmentNew, CONTACT_FRAGMENT_TAG).commit();
            } else {
                fragment = fm.findFragmentByTag(CHAT_FRAGMENT_TAG);
                if (fragment != null && fragment.isVisible()) {
                    Fragment fragmentNew = MessagesFragment.newInstance(1);
                    fm.beginTransaction().replace(R.id.container, fragmentNew, MESSAGE_FRAGMENT_TAG).commit();
                } else {
                    if (doubleBackToExitPressedOnce) {
                        super.onBackPressed();
                        return;
                    }
                    this.doubleBackToExitPressedOnce = true;
                    Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 2000);
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentManager fm = getSupportFragmentManager();
        Fragment newFragment;
        String fragmentTag;

        switch (id){
            case R.id.nav_contacts:
                newFragment = ContactsFragment.newInstance(1);
                fragmentTag = CONTACT_FRAGMENT_TAG;
                break;
            case R.id.nav_messages:
                newFragment = MessagesFragment.newInstance(1);
                fragmentTag = MESSAGE_FRAGMENT_TAG;
                break;
            case R.id.nav_quit:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                String gsmShortToken = sp.getString(QuickstartPreferences.GCM_SHORT_TOKEN, null);
                String number = sp.getString(QuickstartPreferences.REGISTERED_NUMBER, null);
                if((gsmShortToken != null) && (number != null)) {
                    unsetGcmToken(number, gsmShortToken);
                }
                sp.edit().remove(QuickstartPreferences.GCM_SHORT_TOKEN).apply();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();

            default:
                newFragment = ContactsFragment.newInstance(1);
                fragmentTag = CONTACT_FRAGMENT_TAG;
                break;
        }

        fm.beginTransaction().replace(R.id.container, newFragment, fragmentTag).commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onMessageStatClick(MessageStat item) {
        Log.d("MyApp", "Click1 on: " + item.id);
        /*
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra(ChatFragment.ARG_NUMBER_A, getNumber());
        intent.putExtra(ChatFragment.ARG_NUMBER_B, item.id);
        startActivity(intent);
        */
        FragmentManager fm = getSupportFragmentManager();
        Fragment newFragment;
        String fragmentTag;
        newFragment = ChatFragment.newInstance(getNumber(), item.id);
        fragmentTag = CHAT_FRAGMENT_TAG ;
        fm.beginTransaction().replace(R.id.container, newFragment, fragmentTag).commit();


    }

    private void unsetGcmToken(String number, String gsmShortToken) {
        RequestPackage rp = new RequestPackage( MainActivity.AJAX );
        rp.setMethod("GET");
        rp.setParam("functionName", "unsetGcmToken");
        rp.setParam("gcm_short_token", gsmShortToken);
        rp.setParam("number", number);

        UnsetGcmTokenAsyncTask task = new UnsetGcmTokenAsyncTask();
        task.execute(rp);
    }

    private class UnsetGcmTokenAsyncTask extends AsyncTask<RequestPackage, Void, String> {
        @Override
        protected String doInBackground(RequestPackage... params) {
            return HTTPManager.getData(params[0]);
        }
        @Override
        protected void onPostExecute(String s) {
            Log.d("MyApp", "getHashString:" + s);
        }
    }

}
