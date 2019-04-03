package org.pascalcoin.pascalcoinofficial.tasks;

import android.app.Activity;
import android.widget.TextView;


import org.pascalcoin.pascalcoinofficial.services.rest.InfoServiceProvider;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by davidbolet on 17/1/18.
 */

public class ReloadPrice extends TimerTask {
    Activity context;
    Timer timer;
    TextView wv;
    InfoServiceProvider infoServiceProvider;
    String moneyCurrency;

    public ReloadPrice(Activity context, int seconds, String moneyCurrency) {
        this.context = context;
        this.wv = wv;
        this.moneyCurrency=moneyCurrency;
        infoServiceProvider = InfoServiceProvider.getInstance();
        timer = new Timer();
        /* execute the first task after seconds */
        timer.schedule(this,
                5000,  // initial delay
                seconds * 1000); // subsequent rate

    }

    public void setMoneyCurrency(String moneyCurrency) {
        this.moneyCurrency=moneyCurrency;
    }

    @Override
    public void run() {
        if(context == null || context.isFinishing()) {
            // Activity killed
            this.cancel();
            return;
        }
        infoServiceProvider.getPascPrice(moneyCurrency);
    }
}