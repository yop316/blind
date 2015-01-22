package com.insia.ivanet.motoblind;

import android.app.Application;

import java.util.ArrayList;

/**
 * Created by yo on 19/01/2015.
 */
public class motoblind extends Application {

    private ControladorWifi controladorWifi;
    private ServerManager serverManager;
    private MainActivity mainActivity;

    public ControladorWifi getControladorWifi() {
        return controladorWifi;
    }

    public void setControladorWifi(ControladorWifi controladorWifi) {
        this.controladorWifi = controladorWifi;
    }

    public ServerManager getServerManager() {
        return serverManager;
    }

    public void setServerManager(ServerManager serverManager) {
        this.serverManager = serverManager;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}