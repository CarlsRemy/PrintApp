package PrintApp.V3.BluetoothPrinter;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class BluetoothUtils {
    public static final String IDENTIFICADOR_PUERTO_ESTANDAR = "00001101-0000-1000-8000-00805F9B34FB";//Identificador de Puerto Serial Estándar

    public static final int PRINT_BLUETOOTH_MAJOR = 1664;


    public static BluetoothSocket getBluetoothSocket(BluetoothDevice device) throws IOException {
        if (device ==  null) return null;

        BluetoothClass bluetoothClass = device.getBluetoothClass();
        //boolean isPrinter = bluetoothClass.getDeviceClass() == BluetoothClass.Device.
        UUID uuid = UUID.fromString(IDENTIFICADOR_PUERTO_ESTANDAR);
        BluetoothSocket bluetoothSocket = device.createRfcommSocketToServiceRecord(uuid);


        return bluetoothSocket;
    }

    //Provee una lista de dispositivos pareados con el actual dispositivo
    public static ArrayList<BluetoothDevice> getAllBluetoothDevices() throws NullPointerException {
        ArrayList<BluetoothDevice> dispositivos = new ArrayList<>();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if(defaultAdapter == null){
            throw new NullPointerException();
        }else if(!defaultAdapter.isEnabled()){
            return dispositivos;
        }

        Set<BluetoothDevice> dispositivosEmparejados = defaultAdapter.getBondedDevices();

        if(dispositivosEmparejados.size() > 0) {
            for(BluetoothDevice dispositivo : dispositivosEmparejados) {
                dispositivos.add(dispositivo);
            }
        }

        return dispositivos;
    }

    public static boolean isBluetoothPrinter(BluetoothDevice btDevice){
        if(btDevice == null){
            return false;
        }

        boolean containsPrintService = btDevice.getBluetoothClass().hasService(BluetoothClass.Service.RENDER);

        int type = btDevice.getBluetoothClass().getDeviceClass();
        int majorType = btDevice.getBluetoothClass().getMajorDeviceClass();


        return (type == PRINT_BLUETOOTH_MAJOR && containsPrintService) || majorType == BluetoothClass.Device.Major.UNCATEGORIZED;
    }

    public static ArrayList<BluetoothDevice> getPrintersBluetooth() throws NullPointerException {
        ArrayList<BluetoothDevice> dispositivos = new ArrayList<>();
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if(defaultAdapter == null){
            return dispositivos;
        }else if(!defaultAdapter.isEnabled()){
            return dispositivos;
        }

        Set<BluetoothDevice> dispositivosEmparejados = defaultAdapter.getBondedDevices();

        if(dispositivosEmparejados.size() > 0) {
            for(BluetoothDevice dispositivo : dispositivosEmparejados) {
              //  if(BluetoothUtils.isBluetoothPrinter(dispositivo)){

                    dispositivos.add(dispositivo);
              //  }
            }
        }

        return dispositivos;
    }


    public static BluetoothDevice getPrintersBluetoothByAddress(String Address) {
        ArrayList<BluetoothDevice> dispositivos = new ArrayList<>();
        BluetoothDevice Device = (BluetoothDevice) null;
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();

        if(defaultAdapter == null){
            return (BluetoothDevice) null;
        }


        Set<BluetoothDevice> dispositivosEmparejados = defaultAdapter.getBondedDevices();

        if(dispositivosEmparejados.size() > 0) {
            for(BluetoothDevice dispositivo : dispositivosEmparejados) {
                if(Address.trim().equals(dispositivo.getAddress().trim())){
                    Device = dispositivo;
                    break;
                }
            }
        }

        return Device;
    }

}