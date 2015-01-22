package com.insia.ivanet.motoblind;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Debug;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Created by yo on 14/01/2015.
 */
public class ServerManager extends Thread {

    private boolean connected;
    private int dstPort;
    private String dstAddress;

    private Socket socket;
    private PrintWriter output ;
    private BufferedReader input ;

    MainActivity mainActivity;

    ServerManager(String ip_serv, int port, Context  context){
        this.socket=null;
        this.dstAddress=ip_serv;
        this.dstPort=port;
        connected =false;
        this.mainActivity = ((motoblind) context.getApplicationContext()).getMainActivity();
    }

    @Override
    public void run() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(dstAddress, dstPort), 5000);
            if (socket != null) {
                //----- Inicializa los flujos de entrada y salida -----
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                connected = true;
            }else
                connected = false;
                String lineaRecibir = "";
                String output= "";
                while (connected){
                    if(input.ready()) {
                        //----- Recibir linea del servidor -----
                        lineaRecibir = input.readLine();
                        if (lineaRecibir.equals("END:")){
                            mainActivity.add_element(output);
                            output = "";
                        }else
                        if(output.equals(""))
                            output = lineaRecibir;
                        else
                            output += "\n" + lineaRecibir;
                    }else
                        Thread.sleep(500);
                }
        } catch (UnknownHostException e ) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch ( IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage (String message){
        if(connected){
            output.println(message);
            output.flush();
        }
    }

    public void disconnect(){
        if(socket != null && connected){
           this.connected =false;
        }
        if(output != null) {
            output.close();
        }
        if(input != null) {
            try {
                input.close();
            } catch (IOException ignored) {
            }
        }
        if(socket != null) {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        Log.d("socket", "Close connection");
    }

    public boolean isConnect(){
        return connected;
    }


}