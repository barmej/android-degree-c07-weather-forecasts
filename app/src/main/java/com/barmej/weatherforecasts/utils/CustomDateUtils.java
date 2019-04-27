package com.barmej.weatherforecasts.utils;

import android.content.Context;
import android.text.format.DateUtils;

import com.barmej.weatherforecasts.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static android.text.format.DateUtils.FORMAT_ABBREV_ALL;
import static android.text.format.DateUtils.FORMAT_NO_YEAR;
import static android.text.format.DateUtils.FORMAT_SHOW_DATE;
import static android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;

/**
 * Class for handling date conversions that are useful for Sunshine.
 */
public final class CustomDateUtils {


    /**
     * This method returns the number of days since the epoch (January 01, 1970, 12:00 Midnight UTC)
     * from the given time.
     *
     * @param timeInMillis time in milliseconds
     * @return The number of days from the epoch to the given time argument.
     */
    private static long elapsedDaysSinceEpoch(long timeInMillis) {
        return TimeUnit.MILLISECONDS.toDays(timeInMillis);
    }

    /**
     * Helper method to convert the time into something friendly to display to users.
     * <p/>
     * The day string for uses the following logic:
     * For today: "Today, June 8"
     * For tomorrow:  "Tomorrow
     * For the next 5 days: "Wednesday" (just the day name)
     * For all days after that: "Mon, Jun 8" (Mon, 8 Jun in UK, for example)
     *
     * @param context       Context to use for resource localization
     * @param timeInSeconds The date in milliseconds (UTC midnight)
     * @param showFullDate  Used to show a fuller-version of the date, which always contains either
     *                      the day of the week, today, or tomorrow, in addition to the date.
     * @return A user-friendly representation of the date such as "Today, June 8", "Tomorrow", or "Friday"
     */
    public static String getFriendlyDateString(Context context, long timeInSeconds, boolean showFullDate) {

        // Convert time in seconds to time in milliseconds
        long timeInMillis = timeInSeconds * 1000;

        // Get the number of days that have passed since the epoch until the given time
        long daysFromEpochToProvidedDate = elapsedDaysSinceEpoch(timeInMillis);

        // Get the number of days that have passed since the epoch until today
        long daysFromEpochToToday = elapsedDaysSinceEpoch(System.currentTimeMillis());

        if (daysFromEpochToProvidedDate == daysFromEpochToToday || showFullDate) {
            // Today, Tomorrow or the day name
            String dayName = getDayName(context, timeInMillis);
            String readableDate = getReadableDateString(context, timeInMillis);
            if (daysFromEpochToProvidedDate - daysFromEpochToToday < 2) {
                // Replace day name by "today" or "tomorrow"
                String localizedDayName = new SimpleDateFormat("EEEE", Locale.US).format(timeInMillis);
                return readableDate.replace(localizedDayName, dayName);
            } else {
                return readableDate;
            }
        } else if (daysFromEpochToProvidedDate < daysFromEpochToToday + 7) {
            // If the input date is less than a week in the future, just return the day name.
            return getDayName(context, timeInMillis);
        } else {
            int flags = FORMAT_SHOW_DATE | FORMAT_NO_YEAR | FORMAT_ABBREV_ALL | FORMAT_SHOW_WEEKDAY;
            return DateUtils.formatDateTime(context, timeInMillis, flags);
        }
    }

    /**
     * Returns a date string in the abbreviated date format without showing the year.
     *
     * @param context      Used by CustomDateUtils to format the date in the current locale
     * @param timeInMillis Time in milliseconds since the epoch (local time)
     * @return The formatted date string
     */
    private static String getReadableDateString(Context context, long timeInMillis) {
        int flags = FORMAT_SHOW_DATE | FORMAT_NO_YEAR | FORMAT_SHOW_WEEKDAY;
        return DateUtils.formatDateTime(context, timeInMillis, flags);
    }

    /**
     * Given a time, returns just the name to use for that day, e.g "today", "tomorrow", "Wednesday".
     *
     * @param context      Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return the string day of the week
     */
    private static String getDayName(Context context, long dateInMillis) {

        long daysFromEpochToProvidedDate = elapsedDaysSinceEpoch(dateInMillis);
        long daysFromEpochToToday = elapsedDaysSinceEpoch(System.currentTimeMillis());
        int daysAfterToday = (int) (daysFromEpochToProvidedDate - daysFromEpochToToday);
        switch (daysAfterToday) {
            case 0:
                return context.getString(R.string.today);
            case 1:
                return context.getString(R.string.tomorrow);
            default:
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE", Locale.US);
                return dayFormat.format(dateInMillis);
        }
    }

    /**
     * Get hour of the day from UTC time in milliseconds
     *
     * @param dateInMillis UTC time in milliseconds
     * @return clock hour of the given time
     */
    public static String getHourOfDayUTCTime(long dateInMillis) {
        DateFormat dateFormat = new SimpleDateFormat("hh:mm a", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(new Date(dateInMillis));
    }


}