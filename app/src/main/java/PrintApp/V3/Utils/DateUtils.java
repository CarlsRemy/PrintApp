package PrintApp.V3.Utils;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Utilidades para la presentacion y conversion de fechas
 */
public class DateUtils {
    /**
     * Cadena de formato dia, mes, año
     */
    public static final String DD_MM_YYYY = "dd-MM-yyyy";

    /**
     * Cadena de formato dia, mes con tres letras, año
     */
    public static final String DD_MMM_YYYY = "dd-MMM-yyyy";

    /**
     * Cadena de formato dia, mes, año, horas(24), minutos
     */
    public static final String DD_MM_YYYY_HH_mm = "dd-MM-yyyy HH:mm";


    /**
     * Cadena de formato dia, mes, año, horas(12), minutos
     */
    public static final String DD_MM_YYYY_hh_mm = "dd-MM-yyyy hh:mm";


    /**
     * Cadena de formato dia, mes, año, horas(12), minutos
     */
    public static final String DD_MM_YYYY_hh_mm_AM_PM = "dd-MM-yyyy hh:mm a";

    /**
     * Cadena de formato dia, mes, año, horas(24), minutos
     */
    public static final String DD_MM_YYYY_HH_mm_ss = "dd-MM-yyyy HH:mm:ss";

    /**
     * Cadena de formato dia, mes, año, horas(12), minutos
     */
    public static final String DD_MM_YYYY_hh_mm_ss = "dd-MM-yyyy hh:mm:ss";

    /**
     * Formato para almacenaje de fechas en formato ISO
     */
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    /**
     * Formato para almacenaje de fechas y tiempo en formato ISO
     */
    public static final String YYYY_MM_DD_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";

    /**
     * Formato para presentar en el formato nombre día, número del día mes y año
     */
    public static final String EEE_DD_MMM_YYYY  = "EEE, dd MMM yyyy";

    /**
     * Al igual que el formato EEE_DD_MMM_YYYY con hora y minutos en formato 12h
     */
    public static final String EEE_DD_MMM_YYYY_HH_mm  = "EEE, dd MMM yyyy hh:mm a";


    /**
     * Convierte una fecha en un string para visualizar con el formato especificado
     * @param date fecha a formatear
     * @param format formato en que es requerida la fecha
     * @return un string formateado
     */
    public static String formatDate(Date date, String format){
        if(date == null || format == null) return "";

        DateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Convierte un string a una fecha.
     * @param date string con la fecha.
     * @param format formato en el que se encuentra el string date.
     * @return Devuelve un objeto java.util.Date or null si el formato falla
     */
    public static Date convertToDate(String date, String format){
        if(date == null) return  null;

        DateFormat dateFormat = new SimpleDateFormat(format);
        Date d = null;

        try {
            d = dateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return d;
    }

    /**
     * Convierte un {@link Timestamp} a un objeto del tipo {@link Date}
     * @param timestamp
     * @return Un objeto {@link Date}
     */
    public static Date convertToDate(Timestamp timestamp){
        Date d = timestamp;
        return d;
    }

    public static Calendar getGregorianCalendarFrom(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar;
    }


    public static Date deleteTime(Date date){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);

        calendar.set(Calendar.HOUR,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        calendar.set(Calendar.HOUR_OF_DAY,24);

        return calendar.getTime();
    }


    public static class DateConverter{
        private Date startDate;
        private Date endDate;

        private long days;
        private long hours;
        private long minutes;
        private long seconds;

        public DateConverter(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
            //Swap dates
           if(startDate.after(endDate)){
                Date tmp = this.endDate;
                this.endDate = this.startDate;
                this.startDate = tmp;
            }

            runCalcs();
        }

        public long getDays() {
            return Math.abs(days);
        }

        public long getHours() {
            return Math.abs(hours);
        }

        public long getMinutes() {
            return Math.abs(minutes);
        }

        public long getSeconds() {
            return Math.abs(seconds);
        }


        public void runCalcs(){
            long startTime = startDate.getTime();
            long endTime = endDate.getTime();

            long diffTime = endTime - startTime;

            days =  TimeUnit.DAYS.convert(diffTime, TimeUnit.MILLISECONDS);

            diffTime -= TimeUnit.MILLISECONDS.convert(days, TimeUnit.DAYS);

            hours = TimeUnit.HOURS.convert(diffTime, TimeUnit.MILLISECONDS);

            diffTime -= TimeUnit.MILLISECONDS.convert(hours, TimeUnit.HOURS);

            minutes =  TimeUnit.MINUTES.convert(diffTime, TimeUnit.MILLISECONDS);

            diffTime -= TimeUnit.MILLISECONDS.convert(minutes, TimeUnit.MINUTES);

            seconds = TimeUnit.SECONDS.convert(diffTime, TimeUnit.MILLISECONDS);
        }

        @Override
        public String toString() {
            return "DateConverter{" + "startDate=" + startDate + ", endDate=" + endDate + ", days=" + days + ", hours=" + hours + ", minutes=" + minutes + ", seconds=" + seconds + '}';
        }
    }


    private Date getRandomDate(Date dateBase, int year){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(DateUtils.deleteTime(dateBase));
        calendar.set(Calendar.YEAR, year);

        Random random = new Random();

        calendar.set(Calendar.MONTH, random.nextInt(11));

        int daysMax = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);


        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + random.nextInt(daysMax));



        return calendar.getTime();
    }
}
