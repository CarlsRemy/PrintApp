package PrintApp.V3.Utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;




/**
 * Clase que provee utilidades para el manejo de numeros y su representacion.
 */
public class NumberUtils {
    /**
     * Cadena de formato para números decimales, si el número es 0 el formato devolverá 0.00
     */
    public final static String FORMAT_NUMER_DOUBLE = "###,###,##0.00";
    /**
     * Cadena de formato para números decimales pero cuando es 0 retornará una cadena vacía.
     */
    public final static String FORMAT_NUMER_DOUBLE_NO_ZERO = "###,###,###.##";
    /**
     * Cadena de formato para números enteneros, si es 0 el número devolverá 0
     */
    public final static String FORMAT_NUMER_INTEGER = "###,###,##0";

    /**
     * Separador decimal en este caso utiliazamos el punto (.)
     */
    public static final char DECIMAL_SEPARATOR = '.';

    /**
     * Separador de milesimas
     */
    public static final char MILLAR_SEPARATOR = ',';

    /**
     * Formatear un número con un {@link DecimalFormat}
     * @param number a formatear
     * @param decimalFormat utilizado para darle formato al número
     * @return el número formateado con el decimalFormat especificado
     */
    public static String formatNumber(double number, DecimalFormat decimalFormat){
        return decimalFormat.format(number);
    }

    /**
     * Formatea un número dado un patron
     * @param number
     * @param pattern
     * @return
     */
    public static String formatNumber(double number, String pattern){
        DecimalFormat format = getDefaultDecimalFormat(pattern);
        return formatNumber(number, format);
    }

    /**
     * Formatea un número con formato {@link  NumberUtils#FORMAT_NUMER_DOUBLE}
     * @param number
     * @return
     */
    public static String formatToDouble(double number){
        return formatNumber(number, FORMAT_NUMER_DOUBLE);
    }

    /**
     * Formatea un número con formato {@link NumberUtils#FORMAT_NUMER_INTEGER}
     * @param number
     * @return
     */
    public static String formatToInteger(double number){
        return formatNumber(number, FORMAT_NUMER_INTEGER);
    }

    /**
     * Decimal format por defecto para números con los symbolos de {@link NumberUtils#getStandardSymbols()}
     * @param pattern patron a utilizar para la clase {@link DecimalFormat}
     * @return
     */
    public static DecimalFormat getDefaultDecimalFormat(String pattern){
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.applyPattern(pattern);
        decimalFormat.setDecimalFormatSymbols(getStandardSymbols());
        return decimalFormat;
    }

    public static DecimalFormatSymbols getStandardSymbols(){
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(DECIMAL_SEPARATOR);
        symbols.setGroupingSeparator(MILLAR_SEPARATOR);
        return symbols;
    }

    public static int getPercent(float value, float sum){
        float p = ( value / sum) * 100.00f;
        return Math.round(p);
    }

    public static float getFloatPercent(float value, float sum){
        float p = ( value / sum) * 100.00f;
        return p;
    }


    public static String generateSequence(int nZeros, int sequence){
        String format = String.format(Locale.getDefault(), "%%0%dd",nZeros);
        return String.format(format,sequence);
    }
}
