package com.insia.ivanet.motoblind;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by yo on 19/01/2015.
 */
public class communiction extends DialogFragment {


    private ControladorWifi conWiFi;

    private List<String> lista;

    private TextView t_AP;
    private Spinner spin;
    private TextView t_psw;
    private EditText et_psw;
    private CheckBox cB_ap;
    private CheckBox cB_psw;
    private TextView t_ip;
    private EditText et_ip1;
    private EditText et_ip2;
    private EditText et_ip3;
    private EditText et_ip4;
    private TextView t_port;
    private EditText et_port;
    private TextView t_periodo;
    private EditText et_periodo;
    private Button b_serv;
    private Button b_wifi;

    private Context context;


    public communiction() {
        // Empty constructor required for DialogFragment

    }

    public communiction(Context context) {
        // Empty constructor required for DialogFragment
        conWiFi = ((motoblind) context.getApplicationContext()).getControladorWifi();
        this.context = context;
    }


    public interface NoticeDialogListener {
        public void takeinfo(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    /**
     * The system calls this only when creating the layout in a dialog.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // The only reason you might override this method when using onCreateView() is
        // to modify any dialog characteristics. For example, the dialog includes a
        // title by default, but your custom layout might not need it. So here you can
        // remove the dialog title, but you must call the superclass to get the Dialog.
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.connection, container);
        t_AP = (TextView) view.findViewById(R.id.tV_n_AP);
        spin = (Spinner) view.findViewById(R.id.spinner_AP);
        t_psw = (TextView) view.findViewById(R.id.tV_Psw);
        et_psw = (EditText) view.findViewById(R.id.eT_Psw);
        cB_ap = (CheckBox) view.findViewById(R.id.cB_new_AP);
        cB_psw = (CheckBox) view.findViewById(R.id.cB_show_pswd);
        t_ip = (TextView) view.findViewById(R.id.tV_IP);
        et_ip1 = (EditText) view.findViewById(R.id.eT_IP_1);
        et_ip2 = (EditText) view.findViewById(R.id.eT_IP_2);
        et_ip3 = (EditText) view.findViewById(R.id.eT_IP_3);
        et_ip4 = (EditText) view.findViewById(R.id.eT_IP_4);
        t_port = (TextView) view.findViewById(R.id.tV_port);
        et_port = (EditText) view.findViewById(R.id.eT_Port);
        t_periodo = (TextView) view.findViewById(R.id.t_periodo);
        et_periodo = (EditText) view.findViewById(R.id.eT_periodo);
        b_wifi = (Button) view.findViewById(R.id.b_wifi);
        b_serv = (Button) view.findViewById(R.id.b_Serv);

        update_AP();
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        cB_psw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cB_psw.isChecked()) {
                    et_psw.setTransformationMethod(null);
                    cB_psw.setText("Hidden psw");
                } else {
                    et_psw.setTransformationMethod(new PasswordTransformationMethod());
                    cB_psw.setText("Show psw");
                }
            }
        });

        b_wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (b_wifi.getText().toString().equals("Connect AP")) {
                    if (cB_psw.isChecked()) {
                        cB_psw.setChecked(false);
                        cB_psw.callOnClick();
                    }
                    String ssid = spin.getSelectedItem().toString();
                    Toast.makeText(context.getApplicationContext(), "Intentando conectarse al AP: " + ssid, Toast.LENGTH_SHORT).show();
                    String networkPass = "";
                    if (et_psw.isEnabled())
                        networkPass = et_psw.getText().toString();
                    if (conWiFi.conectar(ssid, networkPass, t_psw.getText().toString())) {
                        b_wifi.setText("Disconnect AP");
                        set_disp_wifi(false);
                        String stt = conWiFi.get_ip();
                        if (stt != null) {
                            StringTokenizer st = new StringTokenizer(stt, ".");
                            et_ip1.setHint(st.nextToken());
                            et_ip2.setHint(st.nextToken());
                            et_ip3.setHint(st.nextToken());
                            et_ip4.setHint("1");
                        }
                    }
                } else {
                    Toast.makeText(context.getApplicationContext(), "Desconectandose del AP: " + conWiFi.getNameAP(), Toast.LENGTH_SHORT).show();
                    b_wifi.setText("Connect AP");
                    conWiFi.desconectar();
                    set_disp_wifi(true);
                    update_AP();
                }
            }
        });

        b_serv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListener.takeinfo(communiction.this);

            }
        });
        return view;
    }

    public String get_addr() {
        String ip;
        if (et_ip1.getText().toString().equals(""))
            ip = et_ip1.getHint().toString() + ".";
        else
            ip = et_ip1.getText().toString() + ".";
        if (et_ip2.getText().toString().equals(""))
            ip += et_ip2.getHint().toString() + ".";
        else
            ip += et_ip2.getText().toString() + ".";
        if (et_ip3.getText().toString().equals(""))
            ip += et_ip3.getHint().toString() + ".";
        else
            ip += et_ip3.getText().toString() + ".";
        if (et_ip4.getText().toString().equals(""))
            ip += et_ip4.getHint().toString() + ".";
        else
            ip += et_ip4.getText().toString();

        return ip;
    }

    public int get_port() {
        if (et_periodo.getText().toString().equals(""))
            return Integer.parseInt(et_port.getHint().toString());
        else
            return Integer.parseInt(et_port.getText().toString());
    }

    public int get_period() {
        if (et_periodo.getText().toString().equals(""))
            return Integer.parseInt(et_periodo.getHint().toString());
        else
            return Integer.parseInt(et_periodo.getText().toString());
    }


    public void wifi_disconect() {
        Toast.makeText(context.getApplicationContext(), "Desconectandose del AP: " + conWiFi.getNameAP(), Toast.LENGTH_SHORT).show();
        b_wifi.setText("Connect AP");
        conWiFi.desconectar();
    }


    private void set_disp_wifi(boolean value) {
        t_AP.setEnabled(value);
        spin.setEnabled(value);
        t_psw.setEnabled(value);
        et_psw.setEnabled(value);
        cB_ap.setEnabled(value);
        cB_psw.setEnabled(value);
        t_ip.setEnabled(!value);
        et_ip1.setEnabled(!value);
        et_ip2.setEnabled(!value);
        et_ip3.setEnabled(!value);
        et_ip4.setEnabled(!value);
        t_port.setEnabled(!value);
        et_port.setEnabled(!value);
        t_periodo.setEnabled(!value);
        et_periodo.setEnabled(!value);
        b_serv.setEnabled(!value);
    }

    public void update_AP() {
        if (conWiFi.isWifiEnable()) {
            lista = conWiFi.showAP();
            ArrayList<String> AP = new ArrayList<String>();
            if (AP == null || lista.size() == 0)
                return;
            for (int i = 0; i < lista.size(); i++) {
                AP.add(((lista.get(i)).split(" : "))[0]);
            }
            ArrayAdapter<String> adaptador = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, AP);
            adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spin.setAdapter(adaptador);
            spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    for (int i = 0; i < lista.size(); i++) {
                        if (lista.get(i).contains(arg0.getItemAtPosition(arg2).toString())) {
                            if (lista.get(i).contains("WEP")) {
                                t_psw.setText("Password (WEP)");
                                et_psw.setEnabled(true);
                            } else if (lista.get(i).contains("PSK") || lista.get(i).contains("EAP")) {
                                t_psw.setText("Password (WPA)");
                                et_psw.setEnabled(true);
                            } else {
                                t_psw.setText("Password NONE");
                                et_psw.setEnabled(false);
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                }
            });
        }
    }

}
