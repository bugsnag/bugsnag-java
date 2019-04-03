package com.bugsnag;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

class DateUtils {

    private static final ThreadLocal<Calendar> CALENDAR_THREAD_LOCAL = new ThreadLocal<Calendar>() {
        @Override
        protected Calendar initialValue() {
            return new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        }
    };

    // SimpleDateFormat isn't thread safe, cache one instance per thread as needed.
    private static final ThreadLocal<DateFormat> ISO_8601_HOLDER = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            TimeZone tz = TimeZone.getTimeZone("UTC");
            DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
            iso8601.setTimeZone(tz);
            return iso8601;
        }
    };

    static String toIso8601(Date date) {
        return ISO_8601_HOLDER.get().format(date);
    }

    /**
     * Returns the time rounded down to the latest minute
     *
     * @param date the current date
     * @return the time in ms since Java epoch to the latest minute
     */
    static Date roundTimeToLatestMinute(Date date) {
        Calendar calendar = CALENDAR_THREAD_LOCAL.get();
        calendar.setTime(date);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
}
