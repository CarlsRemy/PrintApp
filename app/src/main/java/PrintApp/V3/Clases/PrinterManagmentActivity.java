package PrintApp.V3.Clases;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;


import PrintApp.V3.BluetoothPrinter.AbstractTicket;
import PrintApp.V3.BluetoothPrinter.MainPrinterHandler;
import PrintApp.V3.BluetoothPrinter.PrinterHandler;

public class PrinterManagmentActivity extends BaseCompatAcivity implements MainPrinterHandler.PrinterCallBack {
    private static final String TAG = "PrinterManagmentActivit";
    private static final String KEY_STAY_CONNECTION = "PrinterManagmentAcitivty.KEY_STAY_CONNECTION";

    /**
     * Maneja los eventos que requieren operaciones de larga duración
     */
    private PrinterHandler mPrinterHandler;

    /**
     * Maneja los callback de los estados de la impresora, está siendo ejecutado en el hilo principal
     */
    private MainPrinterHandler mMainPrinterHandler;

    /**
     * Bluetooth seleccionado, sino es encontrado {@link PrintApp.V3.Clases.PrinterManagmentActivity#isPrinterSelected()} devolverá false
     */
    private BluetoothDevice mBluetoohSelected;

    /**
     * HandlerThread creado para manejar los sucesos en segundo plano
     */
    private HandlerThread mHandlerThread;

    /**
     * Indica si la impresora está un conectada
     */
    private boolean mPrinterIsStillConnected;

    /**
     * {@link BroadcastReceiver} usado para detectar cambios en el estado del adaptador del bluetooth
     */
    private BroadcastReceiver mBluetoothStateReceiver;

    private boolean mStayConnection = false;


    /**
     * Establece una conexión con el dispositivo bluetooth deseado, en este caso una impresora bluetooth
     * @param printer seleccionado
     */
    protected void establishConnectionWithPrinter(BluetoothDevice printer){
        Message msg = new Message();
        msg.what = PrinterHandler.REQUEST_CONNECTION;
        msg.obj  = printer;

        mPrinterHandler.sendMessage(msg);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mMainPrinterHandler == null || mHandlerThread == null){
            mMainPrinterHandler = new MainPrinterHandler(this);

            mHandlerThread = new HandlerThread("PrinterHandlerThread");
            mHandlerThread.start();

            mPrinterHandler = new PrinterHandler(mHandlerThread.getLooper());
            mPrinterHandler.setMainThread(mMainPrinterHandler);
        }


        mBluetoothStateReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();

                if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                    int bluetoothState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

                    switch (bluetoothState){
                        case BluetoothAdapter.STATE_ON:
                            onBluetoothOn();
                            break;

                        case BluetoothAdapter.STATE_OFF:
                            onBluetoothOff();
                            break;
                    }
                }

            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);

        registerReceiver(mBluetoothStateReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(!this.mStayConnection || isFinishing()) {
            closeConnection();
        }

       unregisterReceiver(mBluetoothStateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(mHandlerThread != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mHandlerThread.quitSafely();
            }else{
                mHandlerThread.quit();
            }
        }
    }

    @Override
    public void onPrinterConnecting(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onPrinterConnecting");

        if(!isPrinterSelected()){
            this.mBluetoohSelected = bluetoothDevice;
        }
    }

    @Override
    public void onPrinterConnected() {
        Log.d(TAG, "onPrinterConnected");

        mPrinterIsStillConnected = true;
    }

    @Override
    public void onPrinterDisconnected() {
        Log.d(TAG, "onPrinterDisconnected");

        mPrinterIsStillConnected = false;
        mBluetoohSelected = null;
    }

    @Override
    public void onPrinting() {
        Log.d(TAG, "onPrinting");
    }

    @Override
    public void onPrintingFinished() {
        Log.d(TAG, "onPrintingFinished");
    }

    @Override
    public void onPrinterNotFound(BluetoothDevice bluetoothDevice) {
        Log.d(TAG, "onPrinterNotFound");

        if(isPrinterSelected()){
            this.mBluetoohSelected = null;
        }
    }

    /**
     * Bluetooth cuando el adaptador bluetooth sea encendido
     */
    protected void onBluetoothOn(){

    }

    /**
     * Bluetooth cuando el adaptador bluetooth sea apagado
     */
    protected void onBluetoothOff(){

    }

    /**
     * Verifica si la impresora está todavía conectada y mantiene un enlace activo
     * @return
     */
    public boolean isPrinterStillConnected(){
        return mPrinterIsStillConnected;
    }

    /**
     *
     * @return true si una impresora está aún seleccionada
     */
    public boolean isPrinterSelected(){
        return this.mBluetoohSelected != null;
    }

    public void sendMessageToPrint(String toPrint, boolean tagged){
        Message message = new Message();
        message.what = tagged ? PrinterHandler.PRINTER_PRINT_TEXT_TAGGED : PrinterHandler.PRINTER_PRINT_TEXT;
        message.obj = toPrint;

        mPrinterHandler.sendMessage(message);
    }

    public void sendMessageToPrintTagged(String toPrint){
        sendMessageToPrint(toPrint, true);
    }

    /**
     * Envía un ticket a imprimir a la impresora de la elección
     * @param toPrint ticket a imprimir
     */
    public void sendTicketToPrint(AbstractTicket toPrint){
        Message message = new Message();
        message.what = PrinterHandler.PRINTER_PRINT_TEXT_TAGGED;
        message.obj = toPrint;

        mPrinterHandler.sendMessage(message);
    }

    /**
     * Cierra la conexión con el dispositivo bluetooth conectado, si lo está
     */
    public void closeConnection(){
        if(isPrinterStillConnected() && mPrinterHandler != null){
            mPrinterHandler.sendEmptyMessage(PrinterHandler.PRINTER_CLOSE_CONNECTION);
        }
    }

    /**
     * Comprueba el estado del adaptador bluetooth
     * @return true si el adaptador está encendido
     */
    public boolean checkTheBluetoothState(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }


    public void makeBluetoothDiscoverable(int duration){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, duration);
        startActivity(intent);
    }

    public void makeBluetoothDiscoverable(){
        makeBluetoothDiscoverable(300);
    }

    public void setStayConnection(boolean allow) {
        this.mStayConnection = allow;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(KEY_STAY_CONNECTION, this.mStayConnection);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.containsKey(KEY_STAY_CONNECTION)){
            this.mStayConnection = savedInstanceState.getBoolean(KEY_STAY_CONNECTION);
        }
    }
}