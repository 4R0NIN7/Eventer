package com.example.r0nin.eventer;

import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyTimer {
    protected long period; //mili sekundy
    protected int delay;
    protected final Timer myTimer = new Timer();

    public MyTimer(long period, int delay){
        this.period = period;
        this.delay = delay;
    }
    private void callTimer()
    {

        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                //twoja metoda na odświeżenie danych

                Log.d("callTimer: ","Timer poszedł");
            }
        }, delay,period);

    }
}
