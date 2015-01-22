package com.insia.ivanet.motoblind;

        import android.content.BroadcastReceiver;
        import android.content.Context;
        import android.content.Intent;
        import android.content.IntentFilter;
        import android.net.ConnectivityManager;
        import android.net.NetworkInfo;
        import android.net.wifi.ScanResult;
        import android.net.wifi.SupplicantState;
        import android.net.wifi.WifiConfiguration;
        import android.net.wifi.WifiInfo;
        import android.net.wifi.WifiManager;
        import android.provider.Settings;
        import android.util.Log;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.Spinner;
        import android.widget.Toast;

        import com.insia.ivanet.motoblind.R;

        import java.util.ArrayList;
        import java.util.Formatter;
        import java.util.List;

/**
 * Created by yo on 12/01/2015.
 */
public class ControladorWifi {


    private Context mainService;

    private int networkID = -1;
    private boolean isnew;

    //----- Cambiado por leer del GestorAjustes -> INICIO_WIFI
   public static boolean estadoWifiInicial;

    private WifiManager wifiManager;

    private boolean receiverRegistrado;


    public ControladorWifi(Context context) {
        mainService = context;
        iniciarWifi();

        receiverRegistrado = false;
    }

    private void iniciarWifi() {
        //----- WifiManager -----
        wifiManager = (WifiManager) mainService.getSystemService(Context.WIFI_SERVICE);

        //----- Guardar el estado del wifi para dejarlo como estaba -----
       estadoWifiInicial = wifiManager.isWifiEnabled();

        //----- Activar el WiFi si es necesario -----
        activarWifi();
    }

    public void activarWifi() {
        //----- Comprobar si hay que encender el WiFi -----
        if(!wifiManager.isWifiEnabled()) {
            Toast.makeText(mainService, "Activando WiFi", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }
        else {
            Toast.makeText(mainService, "WiFi ya activado ", Toast.LENGTH_SHORT).show();
        }

        //----- Buscar redes WiFi -----
        if(!wifiManager.isWifiEnabled()) {
            Toast.makeText(mainService, "No se pudo activar el WiFi abortando programa", Toast.LENGTH_SHORT).show();
            System.exit(0);
        }
        else {
            wifiManager.startScan();
        }
    }

    //=======================================================
    //--- Escanea el wifi para mostrar los AP disponibles ---
    //=======================================================
    public ArrayList<String> showAP(){
        List<ScanResult> results  = wifiManager.getScanResults();
        ArrayList<String> lista = new ArrayList<String>();
        if (results != null) {
            for (ScanResult result : results) {
                lista.add(result.SSID + " : " + result.capabilities);
            }
            return lista;
        }else
        return null;
    }


    //=======================================================
    //----- Llamado desde el constructor de MainService -----
    //=======================================================
    /*
    public void conectarRedesV2P() {
        mainService.registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        receiverRegistrado = true;
    }
*/
    //=========================================
    //----- Llamado desde el wifiReceiver -----
    //=========================================
    public void actualizarConexion() {
       /* mainService.nuevaAccion(new AccionLogModel(mainService.getString(R.string.comprobando_conexion)));

        //----- Si no esta conectado a una red V2P -> intentar conectarse a una -----
        if(!isV2PConnected()) {

            mainService.nuevaAccion(new AccionLogModel(mainService.getString(R.string.intentando_conectar)));

            //----- Si se ha conectado a una red V2P -> intentar establecer conexion con el servidor -----
            if(conectar()) {
                mainService.nuevaAccion(new AccionLogModel(mainService.getString(R.string.conectado_a) + wifiManager.getConnectionInfo().getSSID()));

                mainService.getConexionModulo().intentarEstablecerConexion();
            } else {
                mainService.nuevaAccion(new AccionLogModel(mainService.getString(R.string.no_se_pudo_conectar)));
                wifiManager.startScan();
            }
        }
        //----- Si ya esta conectado a una red V2P -> notificar que ya esta conectado -----
        else {
            mainService.nuevaAccion(new AccionLogModel(mainService.getString(R.string.ya_conectado_a) + wifiManager.getConnectionInfo().getSSID()));
        }*/
    }

    public boolean conectar(String networkSSID, String networkPass, String kindSec) {

        boolean conectado = false;
        boolean isWiFi = isConnected();

        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (isWiFi) {
            String ssid = wifiInfo.getSSID();
            if (networkSSID.equals(ssid)){
                return true;
            }else{
                wifiManager.disconnect();
            }
        }

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
       isnew = true;
        int netId=-1;
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"".concat(networkSSID).concat("\""))) {
                netId = i.networkId;
                isnew = false;
                break;
            }
        }
        if (isnew) {
            WifiConfiguration wfc = new WifiConfiguration();

            wfc.SSID = "\"".concat(networkSSID).concat("\"");
            wfc.status = WifiConfiguration.Status.DISABLED;
            wfc.priority = 40;

          //  **For networks using WEP; note that the WEP key is also enclosed in double quotes.**
            if (kindSec.contains("WEP")) {
                wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);

                if (networkPass.matches("[0-9A-F]+")) wfc.wepKeys[0] = networkPass;
                else wfc.wepKeys[0] = "\"".concat(networkPass).concat("\"");
                wfc.wepTxKeyIndex = 0;

           // **For networks using WPA and WPA2, we can set the same values for either**
            } else if (kindSec.contains("WPA")) {
                wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);

                wfc.preSharedKey = "\"".concat(networkPass).concat("\"");
            } else {
                // **Now for the more complicated part: we need to fill several members of WifiConfiguration to specify the network’s security mode. For open networks**
                wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                wfc.allowedAuthAlgorithms.clear();
                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            }
        // connect to and enable the connection


            netId = wifiManager.addNetwork(wfc);
        }

        if (!wifiManager.isWifiEnabled())
            wifiManager.setWifiEnabled(true);
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
        try {
            int intentos =0;
            Thread.sleep(1000);
            //while (!isConnected(mainService) && intentos<10){
                while (!isConnected() && intentos<10){
                Log.d("wifi", isConnected()+" "+ intentos);
                Thread.sleep(1000);
                    intentos++;
            }
            Log.d("wifi", "end "+ isConnected()+" "+ intentos);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (isConnected()) {
            Log.d("Msg","|"+wifiManager.getConnectionInfo().getSSID().toString()+"|");
            if (wifiManager.getConnectionInfo().getSSID().toString().equals(networkSSID)) {
                //----- Obtener el ID de la red para mas tarde comprobar si se ha conectado correctamente -----
                networkID = wifiManager.getConnectionInfo().getNetworkId();
                //----- Comprobar que se ha conectado a una red V2P -----
                conectado = isConnected();
                if (!conectado && isnew) {
                    wifiManager.removeNetwork(networkID);
                }
            }
        }else {
            if (!conectado) {
                if(isnew)
                    wifiManager.removeNetwork(netId);
                else {

                }
            }

        }
        return conectado;
    }


    public void desconectar() {
        //----- Terminar de recibir datos del servidor: condicion de salida del bucle de la clase ConexionModulo -> recibirDatos() -----
      //  mainService.getConexionModulo().finalizarConexion();


        //----- Si el WiFi estaba activado al iniciar la app y ahora esta conectado a una red V2P -> desconectar de la red -----
        if(isConnected()) {
            wifiManager.disconnect();
            if(isnew)
                wifiManager.removeNetwork(networkID);
        }

        //----- Si el WiFi no estaba activado al iniciar la app -> desactivar el WiFi -----
        if(!estadoWifiInicial) {
            wifiManager.setWifiEnabled(false);
        }

        //----- Si el WiFi estaba activado al iniciar la app -> desactivar y activar el WiFi para que vuelva a la configuracion inicial del usuario -----
        else{
            wifiManager.setWifiEnabled(false);
            wifiManager.setWifiEnabled(true);
        }



    }

    public boolean isWifiEnable(){
        return wifiManager.isWifiEnabled();
    }

    public String getNameAP(){
        return wifiManager.getConnectionInfo().getBSSID();
    }

    public void removeSSID(String ssid){
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for( WifiConfiguration i : list ) {
            if(i.SSID != null && i.SSID.equals("\"".concat(ssid).concat("\""))) {
                wifiManager.removeNetwork( i.networkId);
                break;
            }
        }
    }

    public boolean isConnected(){
        ConnectivityManager cm = (ConnectivityManager)mainService.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public String get_ip() {
        if (isConnected()){
           int ip = wifiManager.getConnectionInfo().getIpAddress();
            while (ip == 0){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ip = wifiManager.getConnectionInfo().getIpAddress();
            }
            return String.format("%d.%d.%d.%d", (ip & 0xff), (ip >> 8 & 0xff), (ip >> 16 & 0xff), (ip >> 24 & 0xff));
        }
        else
            return null;
    }
}
