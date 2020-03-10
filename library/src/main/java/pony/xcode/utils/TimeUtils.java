package pony.xcode.utils;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/*时间工具类*/
public class TimeUtils {

    /**
     * 获取10位时间戳
     */
    public static String date2Timestamp(@Nullable Date date) {
        if (date == null) return "";
        Calendar calendar = Calendar.getInstance();
        long time = calendar.getTimeInMillis();
        return String.valueOf(time / 1000L);
    }

    /*获取开始时间10位时间戳，接口传参常用-从当天的0时0分0秒算起*/
    public static String getStartTimestamp(@Nullable Date date) {
        if (date == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return date2Timestamp(calendar.getTime());
    }

    /*获取结束时间10位时间戳，接口传参常用-最大值为当天的23时59分59秒*/
    public static String getEndTimestamp(@Nullable Date date) {
        if (date == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return date2Timestamp(calendar.getTime());
    }

    /**
     * @param date  传入date
     * @param isEnd 是否是获取结束时间
     * @return 获取10位时间戳
     */
    public static String getTimestamp(@Nullable Date date, boolean isEnd) {
        if (isEnd) {
            return getEndTimestamp(date);
        }
        return getStartTimestamp(date);
    }

    //10位时间戳转date
    public static Date timestamp2Date(@Nullable String source) {
        if (TextUtils.isEmpty(source)) return new Date();
        try {
            long time = Long.parseLong(source) * 1000;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(time);
            return calendar.getTime();
        } catch (Exception e) {
            return new Date();
        }
    }

    /*判断两个日期是否是同一天(年月日相等视为同一天)*/
    public static boolean isTheSameDay(@Nullable Date origin, @Nullable Date target) {
        if (origin == null || target == null) return false;
        Calendar c1 = Calendar.getInstance();
        c1.setTime(origin);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(target);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR) && c1.get(Calendar.MONTH) == c2.get(Calendar.MONTH) &&
                c1.get(Calendar.DAY_OF_MONTH) == c2.get(Calendar.DAY_OF_MONTH);
    }

    /*获取月份的第一天10位时间戳*/
    public static String getFirstDayOfMonthTimestamp(@Nullable Date date) {
        if (date == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return date2Timestamp(calendar.getTime());
    }

    /*获取月份的最后一天10位时间戳*/
    public static String getLastDayOfMonthTimestamp(@Nullable Date date) {
        if (date == null) return "";
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return date2Timestamp(calendar.getTime());
    }

    public static String dateFormat(@Nullable Date date) {
        return dateFormat(date, "yyyy-HH-dd");
    }

    public static String dateFormat(@Nullable Date date, @NonNull String pattern) {
        if (date == null) return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date parse(@Nullable String source) {
        return parse(source, "yyyy-MM-dd");
    }

    public static Date parse(@Nullable String source, @NonNull String pattern) {
        if (TextUtils.isEmpty(source)) return new Date();
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
            return dateFormat.parse(source);
        } catch (Exception e) {
            return new Date();
        }
    }

    /*时间戳转年月日输入*/
    public static String timeMillsFormat(String timeMills) {
        return timeMillsFormat(timeMills, "yyyy-MM-dd");
    }

    public static String timeMillsFormat(String timeMills, String pattern) {
        if (TextUtils.isEmpty(timeMills)) return "";
        try {
            long time = Long.parseLong(timeMills);
            return timeMillsFormat(time, pattern);
        } catch (Exception e) {
            return "";
        }
    }

    public static String timeMillsFormat(long time) {
        return timeMillsFormat(time, "yyyy-MM-dd");
    }

    public static String timeMillsFormat(long time, String pattern) {
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat(pattern, Locale.getDefault());
        return format.format(date);
    }

    /*秒数 转时分秒输出*/
    public static String second2Datetime(int second) {
        if (second < 10) {
            return "00:00:0" + second;
        }
        if (second < 60) {
            return "00:00:" + second;
        }
        if (second < 3600) {
            int minute = second / 60;
            second = second - minute * 60;
            if (minute < 10) {
                if (second < 10) {
                    return "00:" + "0" + minute + ":0" + second;
                }
                return "00:" + "0" + minute + ":" + second;
            }
            if (second < 10) {
                return "00:" + minute + ":0" + second;
            }
            return "00:" + minute + ":" + second;
        }
        int hour = second / 3600;
        int minute = (second - hour * 3600) / 60;
        second = second - hour * 3600 - minute * 60;
        if (hour < 10) {
            if (minute < 10) {
                if (second < 10) {
                    return "0" + hour + ":0" + minute + ":0" + second;
                }
                return "0" + hour + ":0" + minute + ":" + second;
            }
            if (second < 10) {
                return "0" + hour + minute + ":0" + second;
            }
            return "0" + hour + minute + ":" + second;
        }
        if (minute < 10) {
            if (second < 10) {
                return hour + ":0" + minute + ":0" + second;
            }
            return hour + ":0" + minute + ":" + second;
        }
        if (second < 10) {
            return hour + minute + ":0" + second;
        }
        return hour + minute + ":" + second;
    }

    /**
     * Milliseconds to fit time span.
     *
     * @param millis    The milliseconds.
     *                  <p>millis &lt;= 0, return null</p>
     * @param precision The precision of time span.
     *                  <ul>
     *                  <li>precision = 0, return null</li>
     *                  <li>precision = 1, return 天</li>
     *                  <li>precision = 2, return 天, 小时</li>
     *                  <li>precision = 3, return 天, 小时, 分钟</li>
     *                  <li>precision = 4, return 天, 小时, 分钟, 秒</li>
     *                  <li>precision &gt;= 5，return 天, 小时, 分钟, 秒, 毫秒</li>
     *                  </ul>
     * @return fit time span
     */
    public static String millis2FitTimeSpan(long millis, int precision) {
        if (millis <= 0 || precision <= 0) return "";
        StringBuilder sb = new StringBuilder();
        String[] units = {"天", "小时", "分钟", "秒", "毫秒"};
        int[] unitLen = {86400000, 3600000, 60000, 1000, 1};
        precision = Math.min(precision, 5);
        for (int i = 0; i < precision; i++) {
            if (millis >= unitLen[i]) {
                long mode = millis / unitLen[i];
                millis -= mode * unitLen[i];
                sb.append(mode).append(units[i]);
            }
        }
        return sb.toString();
    }
}
