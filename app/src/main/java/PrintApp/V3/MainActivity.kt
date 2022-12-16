package PrintApp.V3


import PrintApp.V3.BluetoothPrinter.AbstractTicket
import PrintApp.V3.BluetoothPrinter.BluetoothUtils
import PrintApp.V3.BluetoothPrinter.InvoiceTicket
import PrintApp.V3.Clases.PrinterManagmentActivity
import PrintApp.V3.Dialogs.BluetoothListFragment
import android.bluetooth.BluetoothDevice
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Gravity
import android.view.View
import android.webkit.JavascriptInterface
import android.webkit.WebViewClient
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList


class  MainActivity :  PrinterManagmentActivity(), BluetoothListFragment.OnBluetoothSelectedListener {
    private val key ="Ip_Server"
    private val key2 ="Port_Server"
    private var ipServer =""
    private var PortServer = "80"
    private var printerAddress = ""

    /* Variables para Factura */
    private var Titles = ""
    private var SubTitles = ""
    private var Details = ""

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Print.visibility = View.GONE

        val networkConnection = NetworkConnection(applicationContext)
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val Ip = prefs.getString(key, "")

        /** Validar si hay Internet */
       if(Ip == null || Ip.isEmpty()){

           /** Select (Combo) de Puertos */
           val languages = resources.getStringArray(R.array.Ports)
           val spinner = findViewById<Spinner>(R.id.Port)

           if (spinner != null) {
               val adapter = ArrayAdapter(this, R.layout.list_ports, languages)

               adapter.setDropDownViewResource(R.layout.list_ports)
               spinner.adapter = adapter

               /**valor por defecto*/
               val selection = "80"
               val spinnerPosition: Int = adapter.getPosition(selection)
               spinner.setSelection(spinnerPosition)
           }

           wb_view.visibility = View.GONE
           layoutDisconnect.visibility = View.GONE
           asConfigured.visibility = View.VISIBLE;
       }else {
            asConfigured.visibility = View.GONE;
            ipServer = Ip
            PortServer = prefs.getString(key2, "80").toString()

            networkConnection.observe(this, { isConnected ->
                if (isConnected) {
                    layoutDisconnect.visibility = View.GONE
                    wb_view.visibility = View.VISIBLE
                    webViewSetup()
                } else {
                    wb_view.visibility = View.GONE
                    layoutDisconnect.visibility = View.VISIBLE
                }
            })
        }
    }


    /** clase para invocar desde Js */
    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun showToast(mTitles: String, mSubTitles: String, mDetails: String){
            Titles = mTitles
            SubTitles = mSubTitles
            Details = mDetails

            /** valida si estamos conectados a uno impresora*/
            if (checkTheBluetoothState()) {
                printInvoice()
            } else {
                makeBluetoothDiscoverable()
            }
        }
    }

    /** WebView */
    @RequiresApi(Build.VERSION_CODES.O)
    private fun webViewSetup(){
        wb_view.webViewClient = WebViewClient()
        wb_view.clearCache(false)

        wb_view.apply {
            loadUrl("http://$ipServer:$PortServer/Mozo-Local/")
            settings.javaScriptEnabled = true
            settings.safeBrowsingEnabled =true
            settings.allowFileAccessFromFileURLs = true
            settings.allowUniversalAccessFromFileURLs = true
            settings.loadWithOverviewMode = true
            settings.loadsImagesAutomatically = true
            addJavascriptInterface(JavaScriptInterface(), "PrintingEvent")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun validIp(view: View) {
        val edit = findViewById<EditText>(R.id.Ip)
        val ipValue = edit.text.toString()
        val Portedit = findViewById<Spinner>(R.id.Port)
        val Ports = Portedit.getSelectedItem().toString()

        if( ipValue == null || ipValue.isEmpty()){
            val msg =  Toast.makeText(applicationContext, "Rellene El Campo", Toast.LENGTH_SHORT)
            msg.setGravity(Gravity.CENTER_VERTICAL, 200, 200)
            msg.show()
        }
        else{

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = prefs.edit()
            editor.putString(key, ipValue)
            editor.putString(key2, Ports)
            editor.apply()
            ipServer = prefs.getString(key, "0").toString()
            PortServer = prefs.getString(key2, "80").toString()


            asConfigured.visibility = View.GONE;
            layoutDisconnect.visibility = View.GONE
            wb_view.visibility = View.VISIBLE
            webViewSetup()
        }
    }


    override fun onBindUI() {
        super.onBindUI()
        setStayConnection(true)
    }


    /**
     * Imprime la factura con la que fue abierta esta actividad
     */
    private fun printInvoice() {
        if (!isPrinterSelected) {

            val prefs = PreferenceManager.getDefaultSharedPreferences(this)
            val Address = prefs.getString("printerAddress", "")
            val DevicePaired: ArrayList<BluetoothDevice> = BluetoothUtils.getPrintersBluetooth()

            
            if(!(Address == null || Address.isEmpty()) && DevicePaired.size > 0){
                val device: BluetoothDevice =  BluetoothUtils.getPrintersBluetoothByAddress(Address)


                Toast.makeText(this, getString(R.string.printer_connecting, device.name), Toast.LENGTH_SHORT).show()

               establishConnectionWithPrinter(device)
                super.onPrinterConnecting(device)

            }else {

                /**
                 * Abre lista de impresoras
                 */
                BluetoothListFragment.newInstance(BluetoothUtils.getPrintersBluetooth(), this).show(supportFragmentManager, "")
            }
        } else if (isPrinterStillConnected) {

            /**
             * Imprimir Ticket, para imprimir texto usar
             * sendMessageToPrint(Ticket, false);
             */
            sendTicketToPrint(getTicketToPrint())
        }
    }


    /**
     * Funcion que da formato al Ticket
     */
    private fun getTicketToPrint(): AbstractTicket {
        val  mInvoiceTicket: AbstractTicket = InvoiceTicket(Titles, SubTitles, Details)
        return mInvoiceTicket
    }


    override fun onBluetoothSelected(device: BluetoothDevice?) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val editor = prefs.edit()
        editor.putString("printerAddress", device!!.address)
        editor.apply()


        establishConnectionWithPrinter(device)
    }

    override fun onPrinterConnecting(bluetoothDevice: BluetoothDevice) {
        super.onPrinterConnecting(bluetoothDevice)
        Toast.makeText(this, getString(R.string.printer_connecting, bluetoothDevice.name), Toast.LENGTH_SHORT).show()
    }

    /**
    *  Funcion que se ejecuta luego de conectarte a un impresora
    */
    override fun onPrinterConnected() {
        super.onPrinterConnected()
        sendTicketToPrint(getTicketToPrint())
    }

    /**
     * Tenima las Imprecion */
    override fun onPrinting() {
        super.onPrinting()
        Toast.makeText(this, R.string.printing_ticket, Toast.LENGTH_SHORT).show()
    }

    override fun onPrinterDisconnected() {
        super.onPrinterDisconnected()
        Toast.makeText(this, R.string.printer_disconnected, Toast.LENGTH_SHORT).show()
    }

    override fun onPrinterNotFound(bluetoothDevice: BluetoothDevice) {
        super.onPrinterNotFound(bluetoothDevice)
        Toast.makeText(this, getString(R.string.check_printer_s, bluetoothDevice.name), Toast.LENGTH_LONG).show()
    }

    override fun onBluetoothOn() {
        Toast.makeText(this, "Bluetooth Encendido", Toast.LENGTH_SHORT).show()
    }

    override fun onBluetoothOff() {
        Toast.makeText(this, "Bluetooth Apagado", Toast.LENGTH_SHORT).show()
    }


    override fun onBackPressed() {

    }
}

