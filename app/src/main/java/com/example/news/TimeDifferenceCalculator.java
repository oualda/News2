package com.example.news;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeDifferenceCalculator {

    public  String getTimeDifferenceMessage(String dateStr) {
        // Définir le format correspondant à la date donnée
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        try {
            Date givenDate = dateFormat.parse(dateStr);
            long givenTimeMillis = givenDate.getTime();
            long currentTimeMillis = System.currentTimeMillis();
            long differenceInSeconds = (currentTimeMillis - givenTimeMillis) / 1000;

            return formatTimeDifference(differenceInSeconds);
        } catch (ParseException e) {
            return "Erreur de parsing de la date : " + e.getMessage();
        }
    }


    private static String formatTimeDifference(long seconds) {
        if (seconds < 60) {
            return "منذ لحظات";
        } else if (seconds < 120) {
            return "منذ لحظات";
        } else if (seconds < 3600) {
            if((seconds/60)<=10 && (seconds/60)>2) {
                return "منذ " + (seconds / 60) + " دقائق";
            }
            else if((seconds/60)==2)
            {
                return "منذ دقيقتان";
            }
            else {
                return "منذ " + (seconds / 60) + " دقيقة";
            }

        } else if (seconds < 7200) {
            return "منذ ساعة";
        } else if (seconds < 86400) {
            if((seconds/3600)<=10 && (seconds/3600)>2)
            return "منذ " + (seconds / 3600) + " ساعات";
            else if((seconds/3600)==2)
                return "منذ ساعتين";
            else
                return "منذ " + (seconds / 3600) + " ساعات";
        } else if (seconds < 172800) {
            return "منذ يوم";
        } else {
            if((seconds/86400)<=10 && (seconds/86400)>2)
            return "منذ " + (seconds / 86400) + " ايام";
            else if((seconds/86400)==2)
                return "منذ يومين";
            else if((seconds/86400)<29 && (seconds/86400)>10)
                return "منذ " + (seconds / 86400) + " يوم";
            else
                return "منذ اكثر من شهر";
        }
    }
}