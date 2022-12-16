package PrintApp.V3.BluetoothPrinter;

import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Message;


public class MainPrinterHandler extends Handler {

    private PrinterCallBack printerCallBack;

    public MainPrinterHandler(PrinterCallBack printerCallBack) {
        if(printerCallBack == null){
            throw new NullPointerException(PrinterCallBack.class.getSimpleName() + " is null");
        }
        this.printerCallBack = printerCallBack;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what) {
            case PrinterHandler.PRINTER_CONNECTING:
                if(msg.obj instanceof BluetoothDevice){
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    printerCallBack.onPrinterConnecting(bluetoothDevice);
                }

                break;

            case PrinterHandler.PRINTER_CONNECTED:
                printerCallBack.onPrinterConnected();
                break;

            case PrinterHandler.PRINTER_DISCCONECTED:
                printerCallBack.onPrinterDisconnected();
                break;

            case PrinterHandler.PRINTER_NOT_FOUND:
                if(msg.obj instanceof BluetoothDevice){
                    BluetoothDevice bluetoothDevice = (BluetoothDevice) msg.obj;
                    printerCallBack.onPrinterNotFound(bluetoothDevice);
                }
                break;
            case PrinterHandler.PRINTER_FINISH_PRINT:
                printerCallBack.onPrintingFinished();

                break;

            case PrinterHandler.PRINTER_PRINTING:
                printerCallBack.onPrinting();

                break;
        }


    }

    /**
     * CallBack que debe ser implementado en tu actividad de manera obligatoria
     */
    public interface PrinterCallBack {
        /**
         * Llamado cuando la impresora está intentando conectarse
         * @param bluetoothDevice dispositivo al cual se intenta conectar
         */
        public void onPrinterConnecting(BluetoothDevice bluetoothDevice);
        public void onPrinterConnected();
        public void onPrinterDisconnected();
        public void onPrinting();
        public void onPrintingFinished();

        /**
         * Cuando el intento de la conexion falle esté metodo es llamado
         * @param bluetoothDevice dispositivo que se trató de conectar
         */
        public void onPrinterNotFound(BluetoothDevice bluetoothDevice);
    }
}
