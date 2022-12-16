package PrintApp.V3.BluetoothPrinter;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.datecs.api.printer.Printer;

import java.io.IOException;

public class PrinterHandler extends Handler implements Printer.ConnectionListener {
    private static final String TAG = "PrinterHandler";
    public static final int PRINTER_PRINT_TEXT   = 0x1;
    public static final int REQUEST_CONNECTION   = 0x2;
    public static final int PRINTER_CONNECTED    = 0x3;
    public static final int PRINTER_DISCCONECTED = 0x4;
    public static final int PRINTER_STATUS       = 0x5;
    public static final int PRINTER_NOT_FOUND    = 0x6;
    public static final int PRINTER_CLOSE_CONNECTION = 0x7;
    public static final int PRINTER_PRINT_TEXT_TAGGED   = 0x8;
    public static final int PRINTER_FINISH_PRINT = 0x9;
    public static final int PRINTER_PRINTING   = 0xA;
    public static final int PRINTER_CONNECTING   = 0xB;

    private Printer mPrinter;
    private BluetoothDevice mBluetoothDevice;
    private Handler mMainThread;
    private BluetoothSocket mBluetoothSocket;


    public PrinterHandler(Looper looper) {
        super(looper);
    }

    /**
     * Pasar como parametro un handler creado en el hilo de la actividad donde se esta trabajando
     * @param handler
     */
    public void setMainThread(Handler handler) {
        this.mMainThread = handler;
    }

    private  void connectToBluetooth(){
        try {
            mBluetoothSocket = BluetoothUtils.getBluetoothSocket(mBluetoothDevice);
            mBluetoothSocket.connect();

            mPrinter = PrinterUtils.connectToPrinter(mBluetoothSocket);
            mPrinter.setConnectionListener(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);

        switch (msg.what){
            case PRINTER_PRINT_TEXT:
                if(msg.obj instanceof String){
                    mMainThread.sendEmptyMessage(PRINTER_PRINTING);
                    String text = String.valueOf(msg.obj);
                    printText(text);
                    mMainThread.sendEmptyMessage(PRINTER_FINISH_PRINT);
                }

                break;

            case PRINTER_PRINT_TEXT_TAGGED:
                if(msg.obj instanceof String){
                    mMainThread.sendEmptyMessage(PRINTER_PRINTING);
                    String text = String.valueOf(msg.obj);
                    printTextTagged(text);
                    mMainThread.sendEmptyMessage(PRINTER_FINISH_PRINT);
                }else if(msg.obj instanceof AbstractTicket){
                    mMainThread.sendEmptyMessage(PRINTER_PRINTING);
                    AbstractTicket abstractTicket = (AbstractTicket) msg.obj;
                    printTextTaggedWithTicket(abstractTicket);
                    mMainThread.sendEmptyMessage(PRINTER_FINISH_PRINT);
                }
                break;

            case REQUEST_CONNECTION:
                mBluetoothDevice = (BluetoothDevice) msg.obj;

                if(mBluetoothDevice != null){
                    Message message = mMainThread.obtainMessage();
                    message.what = PRINTER_CONNECTING;
                    message.obj = mBluetoothDevice;

                    mMainThread.sendMessage(message);

                    connectToBluetooth();
                }

                sendPrinterStatus();
                break;

            case PRINTER_STATUS:
                sendPrinterStatus();
                break;

            case PRINTER_CLOSE_CONNECTION:
                closeSocket();
                break;
        }
    }

    private void closeSocket() {
        try {
            if(mBluetoothSocket != null){
                mBluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnect() {
        mMainThread.sendEmptyMessage(PrinterHandler.PRINTER_DISCCONECTED);
    }

    private void sendPrinterStatus(){
        if(mPrinter != null){
            mMainThread.sendEmptyMessage(PrinterHandler.PRINTER_CONNECTED);
        }else{
            Message message = new Message();
            message.what = PrinterHandler.PRINTER_NOT_FOUND;
            message.obj = mBluetoothDevice;
            mMainThread.sendMessage(message);
        }
    }

    private void printText(String textToPrint){
        try {
            mPrinter.reset();
            mPrinter.printText(textToPrint,PrinterUtils.ISO_8859_1);
            mPrinter.feedPaper(110);
            mPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTextTagged(String textToPrint){
        try {
            mPrinter.reset();
            mPrinter.printTaggedText(textToPrint,PrinterUtils.ISO_8859_1);
            mPrinter.feedPaper(110);
            mPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printTextTaggedWithTicket(AbstractTicket ticket){
        try {
            mPrinter.reset();
            mPrinter.printTaggedText(ticket.getTicket(),PrinterUtils.ISO_8859_1);

            String infoBarcode = ticket.getBarcodeInfo();

            if(infoBarcode != null && !(infoBarcode.trim().isEmpty())){
                mPrinter.setBarcode(Printer.ALIGN_CENTER, false, 2, Printer.HRI_BELOW, 50);
                mPrinter.printBarcode(Printer.BARCODE_CODE128, ticket.getBarcodeInfo());
            }

            mPrinter.feedPaper(110);
            mPrinter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
