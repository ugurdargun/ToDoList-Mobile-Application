package com.msu.todolist;

import java.util.Calendar;

public class DateConverter {

    public static String convertDate(String date){
        String [] dateSplit = date.split("\\-");
        String year = dateSplit[0];
        String month = dateSplit[1];
        String day = dateSplit[2];

        return day + "/" + month + "/" + year;
    }

    public static String convertToDBDate(String date){
        String [] dateSplit = date.split("\\/");
        String day = dateSplit[0];
        String month = dateSplit[1];
        String year = dateSplit[2];

        return year + "-" + month + "-" + day;
    }

    public static String convertTime(String time){
        return time.substring(0, time.length() - 3);
    }

    public static String convertToDBTime(String time){
        return time + ":00";
    }

    public static Calendar convertToCalendar(String date, String time){
        int [] dateTime = splitTaskDateTime(date, time);
        Calendar cal = Calendar.getInstance();
        cal.set(dateTime[2], dateTime[1], dateTime[0], dateTime[3], dateTime[4], 00);
        return cal;
    }


    public static int [] splitTaskDateTime(String date, String time){
        String [] dateSplit = date.split("\\/");
        String [] timeSplit = time.split("\\:");

        int mDay = Integer.parseInt(dateSplit[0]);
        int mMonth = (Integer.parseInt(dateSplit[1])-1);
        int mYear = Integer.parseInt(dateSplit[2]);
        int mHour = Integer.parseInt(timeSplit[0]);
        int mMin = Integer.parseInt(timeSplit[1]);;

        int [] dateTime = new int[]{mDay, mMonth, mYear, mHour, mMin};
        return  dateTime;
    }

    public static boolean pastDateIdentification(String date, String time){
        int[] dateTime = new int[5];
        dateTime = splitTaskDateTime(date, time);

        int mDay = dateTime[0];
        int mMonth = dateTime[1];
        int mYear = dateTime[2];
        int mHour = dateTime[3];
        int mMin = dateTime[4];

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMin, 00);

        Calendar now = Calendar.getInstance();
        if(calendar.before(now)){
            return true;
        }
        return false;
    }

    public static boolean todayDateIdentification(String date, String time){
        int[] dateTime = new int[5];
        dateTime = splitTaskDateTime(date, time);

        int mDay = dateTime[0];
        int mMonth = dateTime[1];
        int mYear = dateTime[2];
        int mHour = dateTime[3];
        int mMin = dateTime[4];

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMin, 00);

        Calendar now = Calendar.getInstance();
        if(now.get(Calendar.DAY_OF_MONTH) == mDay && calendar.after(now)){
            return true;
        }
        return false;
    }

    public static boolean FutureDateIdentification(String date, String time){
        int[] dateTime = new int[5];
        dateTime = splitTaskDateTime(date, time);

        int mDay = dateTime[0];
        int mMonth = dateTime[1];
        int mYear = dateTime[2];
        int mHour = dateTime[3];
        int mMin = dateTime[4];

        Calendar calendar = Calendar.getInstance();
        calendar.set(mYear, mMonth, mDay, mHour, mMin, 00);

        Calendar now = Calendar.getInstance();
        if(calendar.after(now)){
            return true;
        }
        return false;
    }

}