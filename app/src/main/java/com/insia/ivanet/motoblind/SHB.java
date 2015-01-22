package com.insia.ivanet.motoblind;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by yo on 20/01/2015.
 */
public class SHB extends Thread {

    private boolean finish;
    private ServerManager serverManager;
    private int period;

    public SHB (Context context, int period){
        this.serverManager = ((motoblind) context.getApplicationContext()).getServerManager();
        finish = false;
        this.period=period;
        Log.d("SHB", "constructor");
    }

    public void run() {

        try {
            Log.d("SHB", "init");
            Log.d("SHB", finish + " " +  serverManager.isConnect());
            while(!finish && serverManager.isConnect()) {
                //send SHB message
               // serverManager.sendMessage("REQUEST:1;;0;;SHB;false,0,214,0;0;;;;;;0x00;0;");
                serverManager.sendMessage("REQUEST:1;;0;;SHB;false,0,0,0;0;;;;;;0x00;1; ");
                Thread.sleep(period);
            }
            Log.d("SHB", "end ");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void end(){
        finish=true;
    }
}
