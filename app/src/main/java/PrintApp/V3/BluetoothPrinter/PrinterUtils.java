package PrintApp.V3.BluetoothPrinter;

import android.bluetooth.BluetoothSocket;


import com.datecs.api.printer.Printer;
import com.datecs.api.printer.ProtocolAdapter;

import java.io.IOException;

public class PrinterUtils {
    public static final String ISO_8859_1 = "ISO-8859-1";

    public static Printer connectToPrinter(BluetoothSocket socket) throws IOException, NullPointerException {
        ProtocolAdapter mProtocolAdapter = new ProtocolAdapter(socket.getInputStream(), socket.getOutputStream());

        if(mProtocolAdapter.isProtocolEnabled()) {
            ProtocolAdapter.Channel mPrinterChannel = mProtocolAdapter.getChannel(ProtocolAdapter.CHANNEL_PRINTER);
            return new Printer(mPrinterChannel.getInputStream(), mPrinterChannel.getOutputStream());
        }else{
            return new Printer(mProtocolAdapter.getRawInputStream(),mProtocolAdapter.getRawOutputStream());
        }
    }
}
