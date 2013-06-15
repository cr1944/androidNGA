package cheng.app.nga.util;

import cheng.app.nga.R;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.time.DateUtils;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {
    static final String TAG = "TimeUtil";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
    private static SimpleDateFormat dateFormat2 = new SimpleDateFormat("MM-dd HH:mm");
    private static SimpleDateFormat timeFormat = new SimpleDateFormat(" HH:mm");
    static final long DAY = 24 * 60 * 60;
    static final long HOUR = 60 * 60;
    static final long MINUTE = 60;

    public static String formatTime(Context context, long time) {
        final long currentSeconds = System.currentTimeMillis();
        Date date = new Date(time);
        Date today = new Date(currentSeconds);
        Date yesterday = new Date(currentSeconds - DAY * 1000);
        Date yesterdayBefore = new Date(currentSeconds - DAY * 1000 * 2);
        final long timeGap = (currentSeconds - time) / 1000;
        if (timeGap < 0) {
            //wrong time
            return dateFormat.format(date);
        } else if (DateUtils.isSameDay(date, today)) {
            //today
            if (timeGap < MINUTE) {
                return context.getString(R.string.second_ago);
            } else if (timeGap < HOUR) {
                return context.getString(R.string.minute_ago, timeGap / MINUTE);
            } else {
                return context.getString(R.string.today, timeFormat.format(date));
            }
        } else if (DateUtils.isSameDay(date, yesterday)) {
            return context.getString(R.string.yesterday, timeFormat.format(date));
        } else if (DateUtils.isSameDay(date, yesterdayBefore)) {
            return context.getString(R.string.yesterday_before, timeFormat.format(date));
        } else if (date.getYear() == today.getYear()) {
            return dateFormat2.format(date);
        } else {
            return dateFormat.format(date);
        }
    }

}
