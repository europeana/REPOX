/*
 * DateUtil.java
 *
 * Created on 29 de Novembro de 2001, 20:40
 */
package pt.utl.ist.util;

import pt.utl.ist.util.datediff.DateDifference;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 *
 * @author  Nuno Freire
 */
public class DateUtil {

    static public String convert(String from, String to, String date){
        String ano=""; String mes=""; String dia="";
        StringTokenizer st= new StringTokenizer(date,"-");
        if (from.compareToIgnoreCase("YYYY-DD-MM")==0){
            ano=st.nextToken(); dia=st.nextToken(); mes=st.nextToken();
        }else if (from.compareToIgnoreCase("YYYY-MM-DD")==0){
            ano=st.nextToken(); mes=st.nextToken(); dia=st.nextToken();
        }else if (from.compareToIgnoreCase("DD-MM-YYYY")==0){
            dia=st.nextToken(); mes=st.nextToken(); ano=st.nextToken();
        }

        String ret="";
        if (to.compareToIgnoreCase("YYYY-DD-MM")==0){
            ret=ano+"-"+dia+"-"+mes;
        }else if (to.compareToIgnoreCase("YYYY-MM-DD")==0){
            ret=ano+"-"+mes+"-"+dia;
        }else if (to.compareToIgnoreCase("DD-MM-YYYY")==0){
            ret=dia+"-"+mes+"-"+ano;
        }
        return ret;
    }

    static public String todayDate(){
        Calendar now=Calendar.getInstance();
        now.setTime(new java.util.Date());
        return String.valueOf(now.get(Calendar.DAY_OF_MONTH))+"-"+
                String.valueOf(now.get(Calendar.MONTH)+1)+"-"+
                String.valueOf(now.get(Calendar.YEAR));
    }

    static public String todayDate(String format){
        Calendar now=Calendar.getInstance();
        now.setTime(new java.util.Date());
        return date2String(now.getTime(), format);
    }

    static public Date startOfDayDate(){
        return startOfDayDate(new java.util.Date());
    }

    static public Date startOfDayDate(Date date){
        Calendar now=Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }


    static public Date startOfMonthDate(Date date){
        Calendar now=Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DAY_OF_MONTH, 1);
        now.set(Calendar.HOUR_OF_DAY, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);
        return now.getTime();
    }
    static public Date endOfDayDate(Date date){
        Calendar now=Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.HOUR_OF_DAY, 23);
        now.set(Calendar.MINUTE, 59);
        now.set(Calendar.SECOND, 59);
        now.set(Calendar.MILLISECOND, 999);
        return now.getTime();
    }


    /* no part usar Calendar.MONTH DAY ou YEAR */
    public static int getPartOfDate(Date date, int part){
        Calendar cal=Calendar.getInstance();
        cal.setTime(date);
        int ret=cal.get(part);
        if (part==Calendar.MONTH)
            ret++;
        return ret;
    }





    public static int getPartOfDate(String date, String format, char part){
        String ret="";
        for( int i=0 ; i<date.length() ; i++) {
            char m=format.charAt(i);
            char v=date.charAt(i);
            if (m==part){
                ret += String.valueOf(v);
            }
        }
        return Integer.parseInt(ret);
    }

    public static int dateToInt(String date){
        int day=DateUtil.getPartOfDate(date, "YYYY-MM-DD",'D');
        int month=DateUtil.getPartOfDate(date, "YYYY-MM-DD",'M');
        int year=DateUtil.getPartOfDate(date, "YYYY-MM-DD",'Y');
        if (day>31)
            day=31;
        if (month>12)
            month=12;
        if (year>2049)
            year=2049;
        if (day==0)
            day=1;
        if (month==0)
            month=1;
        if (year==0)
            year=1975;
        return (new DateDifference(1970,1,1,year,month,day)).getDayDifference()+1;
    }

    public static int dateToInt(Date date){
        if (date==null)
            return -1;
        Calendar now=Calendar.getInstance();
        now.setTime(date);
        int day=now.get(Calendar.DAY_OF_MONTH);
        int month=now.get(Calendar.MONTH)+1;
        int year=now.get(Calendar.YEAR);
        if (day>31)
            day=31;
        if (month>12)
            month=12;
        if (year>2049)
            year=2049;
        if (day==0)
            day=1;
        if (month==0)
            month=1;
        if (year==0)
            year=1975;
        return (new DateDifference(1970,1,1,year,month,day)).getDayDifference()+1;
    }


    public static Date int2Date(int date){
        GregorianCalendar cal=new GregorianCalendar(1970,GregorianCalendar.JANUARY,1);
        cal.add(GregorianCalendar.DATE,date);
        return cal.getTime();
    }

    public static Date string2Date(String date) throws java.text.ParseException{
        return string2Date(date,"dd-MM-yyyy");

    }

    public static Date string2Date(String date, String format) throws java.text.ParseException{
        return new SimpleDateFormat(format).parse(date);
    }
    public static Date string2Date(String date, String format, Locale locale) throws java.text.ParseException{
        return new SimpleDateFormat(format, locale).parse(date);
    }

    public static String date2String(Date date){
        if (date==null)
            return "";
        SimpleDateFormat format=new SimpleDateFormat("dd-MM-yyyy");

        return format.format(date);
    }

    public static String date2String(Date date, String format){
        if (date==null)
            return "";
        SimpleDateFormat sdformat=new SimpleDateFormat(format);
        return sdformat.format(date);
    }

    private static boolean checkAnoBisseisto(String data){
        // todo
        throw new RuntimeException("to implement...");
//        Pattern p = Pattern.compile("(\\d{4})");
//		Matcher m = p.matcher(data);
//		if (m.find()){
//            
//            int dia=Integer.parseInt(data.substring(0,2));        
//            int mes=Integer.parseInt(data.substring(2,4));        
//            int ano=Integer.parseInt(data.substring(4));
//            if (ano % 4 != 0 && mes==2 && dia==29){
//                return false;            
//            }
//        }
//        return true;        
    }


    public static String toMonthNameAbreviated(int m){
        if (m==1)
            return "Jan";
        if (m==2)
            return "Fev";
        if (m==3)
            return "Mar";
        if (m==4)
            return "Abr";
        if (m==5)
            return "Mai";
        if (m==6)
            return "Jun";
        if (m==7)
            return "Jul";
        if (m==8)
            return "Ago";
        if (m==9)
            return "Set";
        if (m==10)
            return "Out";
        if (m==11)
            return "Nov";
        if (m==12)
            return "Dez";
        return "";
    }

    public static String toMonthName(int m){
        if (m==1)
            return "Janeiro";
        if (m==2)
            return "Fevereiro";
        if (m==3)
            return "Março";
        if (m==4)
            return "Abril";
        if (m==5)
            return "Maio";
        if (m==6)
            return "Junho";
        if (m==7)
            return "Julho";
        if (m==8)
            return "Agosto";
        if (m==9)
            return "Setembro";
        if (m==10)
            return "Outubro";
        if (m==11)
            return "Novembro";
        if (m==12)
            return "Dezembro";
        return "";
    }

    public static String getSeason(Date d){
        int m=getPartOfDate(d,Calendar.MONTH);
        if (m<=3)
            return "Inverno";
        if (m<=6)
            return "Primavera";
        if (m<=9)
            return "Verão";
        if (m<=12)
            return "Outono";
        return "";
    }

    public static String getDayOfWeek(Date d){
        Calendar cal=Calendar.getInstance();
        cal.setTime(d);
        int ret=cal.get(Calendar.DAY_OF_WEEK);
        if (ret==Calendar.MONDAY)
            return "Segunda-feira";
        if (ret==Calendar.TUESDAY)
            return "Terça-feira";
        if (ret==Calendar.WEDNESDAY)
            return "Quarta-feira";
        if (ret==Calendar.THURSDAY)
            return "Quinta-feira";
        if (ret==Calendar.FRIDAY)
            return "Sexta-feira";
        if (ret==Calendar.SATURDAY)
            return "Sábado";
        if (ret==Calendar.SUNDAY)
            return "Domingo";
        return ""+Calendar.DAY_OF_WEEK;
    }



    public static String getDayOfWeekAbreviated(Date d){
        Calendar cal=Calendar.getInstance();
        cal.setTime(d);
        int ret=cal.get(Calendar.DAY_OF_WEEK);
        if (ret==Calendar.MONDAY)
            return "Seg";
        if (ret==Calendar.TUESDAY)
            return "Ter";
        if (ret==Calendar.WEDNESDAY)
            return "Qua";
        if (ret==Calendar.THURSDAY)
            return "Qui";
        if (ret==Calendar.FRIDAY)
            return "Sex";
        if (ret==Calendar.SATURDAY)
            return "Sab";
        if (ret==Calendar.SUNDAY)
            return "Dom";
        return "";
    }


    public static boolean validate(String date, String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            sdf.setLenient(false);
            Date d=sdf.parse(date);
        }catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Date addDays(Date date, int numDays) {
        return add(date, numDays, Calendar.DAY_OF_WEEK);
    }

    public static Date add(Date date, int numberOfUnits, int calendarField) {//Calendar.DAY_OF_WEEK
        Calendar cal=new GregorianCalendar();
        cal.setTime(date);
        cal.add(calendarField, numberOfUnits);
        return cal.getTime();
    }



    public static void main(String[] args){
        System.err.println(dateToInt(new Date()));
        System.err.println(new Date());
        System.err.println(int2Date(dateToInt(new Date())));


        System.err.println("\u00c0 \u00c1");
    }

}
