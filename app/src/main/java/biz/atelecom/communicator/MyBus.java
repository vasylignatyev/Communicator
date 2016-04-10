package biz.atelecom.communicator;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by vignatyev on 09.04.2016.
 */

public class MyBus extends Bus {
    private static Bus bus;
    //isRegistered is used to track the current registration status
    private static boolean isRegistered;
    private Handler handler = new Handler(Looper.getMainLooper());


    public MyBus() {
        if (bus == null) {
            //ANY will allow event bus to run even with services
            //and broadcast receivers
            bus = new Bus(ThreadEnforcer.ANY);
        }
    }

    public static MyBus getInstance() {
        return new MyBus();
    }
    @Override
    public void register(Object obj) {
        //The bus is registered when an activity starts
        bus.register(obj);
        isRegistered = true;
    }

    @Override
    public void unregister(Object obj) {
        //The bus is unregistered when an activity goes to background
        bus.unregister(obj);
        isRegistered = false;
    }

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            //post the event in main thread or background thread
            bus.post(event);
        } else {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    bus.post(event);
                }
            });
        }
    }

    public boolean isRegistered(){
        return isRegistered;
    }
}
