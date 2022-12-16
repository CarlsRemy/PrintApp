package PrintApp.V3.BluetoothPrinter

import PrintApp.V3.Utils.NumberUtils
import org.json.JSONArray


/**
 * Clase que exitiende de {@link AbstractTicket}
 */
class InvoiceTicket(Titles: String, SubTitles: String, Details: String) : AbstractTicket()  {
    val TAG = "InvoiceTicket"
    var mInvoice = ""
    private val mTitles = JSONArray(Titles).getJSONObject(0)
    private val mSubTitles =  JSONArray(SubTitles).getJSONObject(0)
    private val mDetails = JSONArray(Details)

   /// private val Name = mSubTitles.get("Name").toString()
    private val Mozo = "ft"
    private val Factura = mSubTitles.get("Factura").toString()
    private val Mesa = mSubTitles.get("Mesa").toString()
    private val Fecha = mSubTitles.get("Fecha").toString()
    private val Hora = mSubTitles.get("Hora").toString()

    private val Bruto = mSubTitles.get("Monto").toString().toDouble()
    private val Itebis = mSubTitles.get("Itbis").toString().toDouble()
    private val Ley =  mSubTitles.get("Ley").toString().toDouble()
    private val Neto =  mSubTitles.get("Neto").toString().toDouble()
    private val NetoEU =  mSubTitles.get("NetoEU").toString().toDouble()
    private val NetoUS = mSubTitles.get("NetoUS").toString().toDouble()


    val PRINTER_CHARACTERS_LINES = 32



    override  fun onBuildTicket(buffer: StringBuilder): String? {
        val divisorString: String = getStringWithCharacter('-', PRINTER_CHARACTERS_LINES)

        //val mTitles = JSONArray(this.Titles).getJSONObject(0)

        val mType = mTitles.get("type").toString()


        if (mType == "1"){

            var Direc =  mTitles.get("direc2").toString()



            buffer.append(String.format("{reset}{center}{b}%s{br}", mTitles.get("name")))
            buffer.append(String.format("{reset}{center}{b}%s{br}", mTitles.get("direc1")))

            if(Direc.length >= PRINTER_CHARACTERS_LINES){

                var Direc1 =  Direc.subSequence(0,29)
                buffer.append(String.format("{reset}{center}{b}%s{br}", Direc1))

                buffer.append(String.format("{reset}{center}{b}%s{br}", Direc.subSequence(30,Direc.length -1)))
            }


            buffer.append(String.format("{reset}{center}{b}%s{br}", mTitles.get("RCN")))
            buffer.append(String.format("{reset}{center}{b}%s{br}", mTitles.get("telf")))
        }else{
            val direc2 = mTitles.get("direc2")

            buffer.append(String.format("{reset}{center}{b}%s{br}", mTitles.get("name")))
            buffer.append(String.format("{reset}{center}{b}%s{br}", mTitles.get("direc1")))

            if(direc2 != ""){
                buffer.append(String.format("{reset}{center}{b}%s{br}", direc2))
            }


            buffer.append(String.format("{reset}{center}{b}%s / %s{br}", mTitles.get("telf"),  mTitles.get("telf2")))


        }




        buffer.append(divisorString)
        buffer.append(String.format("{reset}{center}{b}%s{br}", "ORDEN - DE - PAGO{br}"))
        buffer.append(divisorString)


        buffer.append(String.format("{reset}PEDIDO: %-10s   Mesa: %-5s{br}", Factura, Mesa))
        buffer.append(String.format("{reset}FECHA :%-10s HORA: %s{br}", Fecha, Hora))
        buffer.append(String.format("{reset}{b}CAMARERO: %22s{br}",""))

        buffer.append(divisorString)

        buffer.append("{reset}{left}{b}DESCRIPCION{br}")
        buffer.append("{reset}{b}CANTIDAD    PRECIO         TOTAL")
        buffer.append(divisorString)



        for (i in 0 until mDetails.length()) {
            val item = mDetails.getJSONObject(i)
            buffer.append(String.format("{reset}{left}%s{br}",item.get("Descrip")))
            buffer.append(String.format("{reset}%-11s %-11s %-11s{br}",item.get("Cantidad"), item.get("Precio"), item.get("Total")))
        }

        buffer.append("$divisorString{reset}{b}")


        buffer.append(createTotal("MONTO BRUTO:", NumberUtils.formatToDouble(Bruto)))
        buffer.append(createTotal("% ITBIS:", NumberUtils.formatToDouble(Itebis)))
        buffer.append(createTotal("% LEY:", NumberUtils.formatToDouble(Ley)))
        buffer.append(divisorString)
        buffer.append(createTotal("MONTO NETO:", NumberUtils.formatToDouble(Neto)))
        buffer.append("$divisorString{reset}{b}")
        buffer.append(createTotal("TOTAL EN DOLARES:", NumberUtils.formatToDouble(NetoUS)))
        buffer.append(createTotal("TOTAL EN EUROS:", NumberUtils.formatToDouble(NetoEU)))
        buffer.append(divisorString)
        buffer.append("{reset}()Valor Fiscal   ()Reg. Especial{br}")
        buffer.append("{reset}()Cons. Fiscal   ()Gubernamental{br}")
        buffer.append("{reset}{br}")

        buffer.append(String.format("{reset}%s ______________________{br}", "RNC:"))
        buffer.append(String.format("{reset}%s ______________________{br}", "NOMBRE:"))

        buffer.append("{reset}{br}")
        buffer.append("{reset}{center}{b}<< COPYRIGHT PROISA >>{br}")
        return buffer.toString()
    }

    open fun createTotal(label: String, totalFormatted: String): String? {
        return concatWithSpaces(label, totalFormatted, PRINTER_CHARACTERS_LINES)
    }

    override fun getBarcodeInfo(): String? {
        return null
        //return mInvoice.getId();
    }
}


