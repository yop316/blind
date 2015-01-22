package com.insia.ivanet.motoblind;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity implements communiction.NoticeDialogListener {


    private ServerManager servMngr;
    private ControladorWifi conWiFi;
    private SHB shb;


    DialogFragment dialog_conex;

    private Button b_connect;
    private Button b_send;
    private EditText et_send;
    private ListView lv_output;

    private String addrServer;
    private int portServer;
    private int period;

    private Ouput_AA adapter;
    private ArrayList<String> output_list_view;

    private Menu menu;
    private MenuItem texto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        conWiFi = new ControladorWifi(this);
        ((motoblind) this.getApplication()).setControladorWifi(conWiFi);


       // b_connect = (Button) findViewById(R.id.b_Connect);
        b_send = (Button) findViewById(R.id.b_send);
        et_send = (EditText) findViewById(R.id.et_send);
        lv_output = (ListView) findViewById(R.id.lv_output);


        output_list_view = new ArrayList<String>();
        adapter = new Ouput_AA(this, output_list_view);
        ((motoblind) this.getApplication()).setMainActivity(this);
        lv_output.setAdapter(adapter);


        lv_output.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                //final String item = (String) parent.getItemAtPosition(position);
                //output_list_view.remove(item);
                //output_list_view.remove(position);
               adapter.remove_extra_elements();
            }
        });
        dialog_conex = new communiction(this);
    }

    public void server_start() {
            servMngr = new ServerManager(addrServer, portServer, this);
            ((motoblind) this.getApplication()).setServerManager(servMngr);
            servMngr.start();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (servMngr.isConnect()) {
                Log.d("sock", "conected Server");
                texto.setTitle("Start");
            }
    }

    /*
    public void onClick_connect (View view) {
        MenuItem texto =  menu.findItem( R.id.Menu_Connection);
        if (b_connect.getText().toString().equals("Connect")) {
            texto.setTitle("Start");
            dialog_conex.show(getFragmentManager(), "com");
        }else if(b_connect.getText().toString().equals("Start")) {
            shb = new SHB(this,period);
            shb.start();
            texto.setTitle("Disconnect");
            b_connect.setText("Disconnect");
        }else{
            if(servMngr!=null){
                servMngr.disconnect();
                servMngr = null;}
            conWiFi.desconectar();
            Log.d("sock","Connect Server");
            texto.setTitle("Connect");
            b_connect.setText("Connect");
            b_send.setVisibility(View.INVISIBLE);
            et_send.setVisibility(View.INVISIBLE);
        }
    }*/

    public void onClick_send (View view) {
        if (servMngr.isConnect()) {
            servMngr.sendMessage(et_send.getText().toString());
            et_send.setText("");
        }
    }

    @Override
    public void takeinfo(DialogFragment dialog) {
        addrServer=((communiction)dialog).get_addr();
        portServer = ((communiction)dialog).get_port();
        period = ((communiction)dialog).get_period();
        server_start();
        dialog_conex.dismiss();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu=menu;
        texto =  menu.findItem( R.id.Menu_Connection);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.Menu_Connection:
                if (texto.getTitle().toString().equals("Connect")) {
                    dialog_conex.show(getFragmentManager(), "com");
                }else if(texto.getTitle().toString().equals("Start")) {
                    shb = new SHB(this,period);
                    shb.start();
                    texto.setTitle("Disconnect");
                    b_send.setVisibility(View.VISIBLE);
                    et_send.setVisibility(View.VISIBLE);
                }else{
                    if(servMngr!=null){
                        servMngr.disconnect();
                        servMngr = null;}
                    conWiFi.desconectar();
                    adapter.add_element("Disonnect Server");
                    texto.setTitle("Connect");
                    b_send.setVisibility(View.INVISIBLE);
                    et_send.setVisibility(View.INVISIBLE);
                }
                break;
            case R.id.Menu_Map:
                if (item.isChecked()) item.setChecked(false);
                else item.setChecked(true);
                break;
            default:
                // En caso de que seleccionemos el 3º item del menú principal
                // también se va a lanzar este método, por lo que para evitar que
                // cambie el texto en este caso lo que haremos será recoger el valor
                // actual del TextView.
                break;
        }


        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public Activity getActivity() {
        return this;
    }



    public void add_element(final String mess){
        runOnUiThread(new Runnable() {
            public void run() {
                adapter.add_element(mess);
            }
        });
    }



}
