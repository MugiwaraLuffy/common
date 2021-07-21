package com.cover.common;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

// 日期时间工具类
// 1. 最好是完整的年月日时间处理, 否则时间均以 1970-01-01 00:00:00.000 开始计算
@SuppressWarnings("unused")
public final class DateX {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_DATETIMES = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String FORMAT_SEQUENCE = "yyyyMMddHHmmssSSS";
    public static final String[] WEEK = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    public static final String[] WEEK_EN = {"Sun", "Mon", "Tues", "Wed", "Thur", "Fri", "Sat"};
    public static final String[] WEEK_CN = {"周日", "周一", "周二", "周三", "周四", "周五", "周六"};

    // 转 String ####################################################################################################
    // 日期 [年-月-日], 默认当前时间
    public static String getDate(Object obj) {
        return format(null == obj ? now() : obj, FORMAT_DATE);
    }

    // 时间 [时:分:秒]
    public static String getTime(Object obj) {
        return format(null == obj ? now() : obj, FORMAT_TIME);
    }

    // 日期: [年-月-日 时:分:秒]
    public static String getDateTime(Object obj) {
        return format(null == obj ? now() : obj, FORMAT_DATETIME);
    }

    // 日期: [年-月-日 时:分:秒.毫秒]
    // 当前时间完整序列
    public static String getSequence(Object obj) {
        return format(null == obj ? now() : obj, FORMAT_SEQUENCE);
    }

    // 获取日期星期几 中文
    public static String getWeek(Object obj) {
        return getWeekString(null == obj ? now() : obj, WEEK);
    }

    // 获取日期星期几 简单的中文
    public static String getWeekCN(Object obj) {
        return getWeekString(null == obj ? now() : obj, WEEK_CN);
    }

    // 获取日期星期几 英文
    public static String getWeekEN(Object obj) {
        return getWeekString(null == obj ? now() : obj, WEEK_EN);
    }

    // 获取日期星期几
    public static String getWeekString(Object obj, String[] weeks) {
        if (CollectionX.isEmpty(weeks)) return Const.STRING_EMPTY_VALUE;
        Calendar calendar = parseCalendar(obj);
        if (null == calendar) return Const.STRING_EMPTY_VALUE;
        int idx = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        if (idx > weeks.length - 1) return Const.STRING_EMPTY_VALUE; // 越界问题
        return weeks[idx];
    }

    /**
     * 日期转为字符串
     *
     * @param obj 转换对象
     * @return 默认完整日期格式
     */
    public static String format(Object obj) {
        return format(obj, null);
    }

    /**
     * 日期转为字符串
     *
     * @param obj      转换对象
     * @param formater 输出格式
     * @return 日期格式化后的字符串
     */
    public static String format(Object obj, String formater) {
        Date date = parse(obj, formater);
        if (null == date) return Const.STRING_EMPTY_VALUE;
        return new SimpleDateFormat(StringX.get(formater, FORMAT_DATETIME)).format(date);
    }

    // 转 Date ######################################################################################################
    // 返回当前时间
    public static Date now() {
        return new Date();
    }

    /**
     * 获取一个日期操作对象
     * 转换对象自适应, 默认返回当前日期操作对象
     *
     * @param obj 转换对象
     * @return 日期操作对象
     */
    public static Calendar parseCalendar(Object obj) {
        return parseCalendar(obj, null);
    }

    /**
     * 获取一个日期操作对象
     * 如果转换对象不符合, 默认返回当前日期操作对象
     *
     * @param obj      转换对象
     * @param formater 转换格式
     * @return 日期操作对象, 有可能返回空
     */
    public static Calendar parseCalendar(Object obj, String formater) {
        if (null == obj) return null;
        Date date = parse(obj, formater);
        if (null == date) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    /**
     * 转 1.8 LocalDateTime 对象
     *
     * @param obj 转换对象
     * @return LocalDateTime 对象, 有可能空
     */
    public static LocalDateTime parseLocalDateTime(Object obj) {
        return parseLocalDateTime(obj, null);
    }

    /**
     * 转 1.8 LocalDateTime 对象
     *
     * @param obj      转换对象
     * @param formater 转换格式
     * @return LocalDateTime 对象, 有可能空
     */
    public static LocalDateTime parseLocalDateTime(Object obj, String formater) {
        if (null == obj) return null;
        try {
            if (obj instanceof LocalDateTime) return (LocalDateTime) obj;
            if (obj instanceof LocalDate) return LocalDateTime.of((LocalDate) obj, LocalTime.now());
            if (obj instanceof LocalTime) return LocalDateTime.of(LocalDate.now(), (LocalTime) obj);
            if (obj instanceof Timestamp) return LocalDateTime.ofInstant(((Timestamp) obj).toInstant(), ZoneId.systemDefault());
            if (obj instanceof Date) return LocalDateTime.ofInstant(((Date) obj).toInstant(), ZoneId.systemDefault());
            if (obj instanceof Calendar) return LocalDateTime.ofInstant(((Calendar) obj).toInstant(), ZoneId.systemDefault());
            if (obj instanceof Long) return LocalDateTime.ofInstant((new Date((Long) obj)).toInstant(), ZoneId.systemDefault());
            if (obj instanceof String) {
                // 去除前后空格
                String dateString = StringX.get(obj);
                if (StringX.isEmpty(dateString)) return null;
                // 自适配, 但是必须从年份开始
                String format = StringX.isEmpty(formater) ? FORMAT_DATETIMES.substring(0, dateString.length() - 1) : formater;
                Date date = new SimpleDateFormat(format).parse(dateString);
                return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 字符串转日期
     *
     * @param obj 日期字符串, 必须从年份开始
     * @return 日期类型, 有可能为空
     */
    public static Date parse(Object obj) {
        return parse(obj, null);
    }

    /**
     * 字符串转日期
     *
     * @param obj      转换对象
     * @param formater 日期格式
     * @return 日期类型, 有可能为空
     */
    public static Date parse(Object obj, String formater) {
        if (null == obj) return null;
        try {
            if (obj instanceof Timestamp) return new Date(((Timestamp) obj).getTime());
            if (obj instanceof Date) return (Date) obj;
            if (obj instanceof Calendar) return ((Calendar) obj).getTime();
            if (obj instanceof LocalDateTime) return Date.from(((LocalDateTime) obj).toInstant(ZoneOffset.of("+8")));
            if (obj instanceof LocalDate) return Date.from(LocalDateTime.of((LocalDate) obj, LocalTime.now()).toInstant(ZoneOffset.of("+8")));
            if (obj instanceof LocalTime) return Date.from(LocalDateTime.of(LocalDate.now(), (LocalTime) obj).toInstant(ZoneOffset.of("+8")));
            if (obj instanceof Long) return (long) obj > 0 ? new Date((long) obj) : null;
            if (obj instanceof String) {
                // 去除前后空格
                String dateString = StringX.get(obj);
                if (StringX.isEmpty(dateString)) return null;
                // 自适配, 但是必须从年份开始
                String format = StringX.isEmpty(formater) ? FORMAT_DATETIMES.substring(0, dateString.length() - 1) : formater;
                return new SimpleDateFormat(format).parse(dateString);
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Java日期 => 数据库日期
     *
     * @param obj 转换对象
     * @return 数据库日期
     */
    public static Timestamp parseToTimestamp(Object obj) {
        if (null == obj) return null;
        if (obj instanceof Timestamp) return (Timestamp) obj;

        Date date = parse(obj);
        return null == date ? null : new Timestamp(date.getTime());
    }

    // 时间计算 ######################################################################################################
    // 操作类型:
    // 年: Calendar.YEAR
    // 月: Calendar.MONTH
    // 日: Calendar.DATE
    // 时: Calendar.HOUR_OF_DAY
    // 分: Calendar.MINUTE
    // 秒: Calendar.SECOND
    // 毫秒: Calendar.MILLISECOND

    // 秒
    public static Date addSecond(Object obj, int amount) {
        return add(obj, Calendar.SECOND, amount);
    }

    public static String addSecondToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.SECOND, amount);
    }

    public static String addSecondToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.SECOND, amount);
    }


    // 分钟
    public static Date addMinute(Object obj, int amount) {
        return add(obj, Calendar.MINUTE, amount);
    }

    public static String addMinuteToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.MINUTE, amount);
    }

    public static String addMinuteToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.MINUTE, amount);
    }

    // 小时
    public static Date addHour(Object obj, int amount) {
        return add(obj, Calendar.HOUR_OF_DAY, amount);
    }

    public static String addHourToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.HOUR_OF_DAY, amount);
    }

    public static String addHourToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.HOUR_OF_DAY, amount);
    }

    // 日
    public static Date addDate(Object obj, int amount) {
        return add(obj, Calendar.DATE, amount);
    }

    public static String addDateToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.DATE, amount);
    }

    public static String addDateToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.DATE, amount);
    }

    // 周
    public static Date addWeek(Object obj, int amount) {
        return add(obj, Calendar.WEEK_OF_MONTH, amount);
    }

    public static String addWeekToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.WEEK_OF_MONTH, amount);
    }

    public static String addWeekToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.WEEK_OF_MONTH, amount);
    }

    // 月
    public static Date addMonth(Object obj, int amount) {
        return add(obj, Calendar.MONTH, amount);
    }

    public static String addMonthToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.MONTH, amount);
    }

    public static String addMonthToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.MONTH, amount);
    }

    // 年
    public static Date addYear(Object obj, int amount) {
        return add(obj, Calendar.YEAR, amount);
    }

    public static String addYearToString(Object obj, int amount) {
        return addToString(obj, FORMAT_DATETIME, Calendar.YEAR, amount);
    }

    public static String addYearToString(Object obj, String outFormater, int amount) {
        return addToString(obj, outFormater, Calendar.YEAR, amount);
    }

    /**
     * 日期增减操作
     *
     * @param obj         转换对象
     * @param outFormater 输出格式
     * @param field       参考上面操作类型
     * @param amount      增量
     * @return 处理后的日期字符串
     */
    public static String addToString(Object obj, String outFormater, int field, int amount) {
        Calendar calendar = parseCalendar(obj);
        if (null == calendar) return Const.STRING_EMPTY_VALUE;

        Date date = add(calendar, field, amount);
        if (null == date) return Const.STRING_EMPTY_VALUE;
        return format(date, outFormater);
    }

    /**
     * 日期增减
     *
     * @param obj    转换对象
     * @param field  增减属性
     * @param amount 增量
     * @return 增减后的日期类型
     */
    public static Date add(Object obj, int field, int amount) {
        Calendar calendar = parseCalendar(obj);
        if (null == calendar) return null;
        calendar.add(field, amount);
        return calendar.getTime();
    }

    /**
     * 单独计算某一项时间差
     * 默认以当前时间 - date1
     *
     * @param date1 开始时间
     * @param field 获取时间类型, 年月日时分秒毫秒/周等数据
     * @return 时间差, 返回空则时间不匹配
     */
    public static Long between(Object date1, int field) {
        return between(date1, LocalDateTime.now(), field);
    }

    /**
     * 单独计算某一项时间差
     *
     * @param date1 开始时间
     * @param date2 结束时间 (默认当前时间)
     * @param field 获取时间类型, 年月日时分秒毫秒/周等数据
     * @return 时间差, 返回空则时间不匹配
     */
    public static Long between(Object date1, Object date2, int field) {
        if (null == date1) return null;
        LocalDateTime startInclusive = date1 instanceof LocalDateTime ? (LocalDateTime) date1 : parseLocalDateTime(date1);
        if (null == startInclusive) return null;

        LocalDateTime endExclusive = null == date2 || (date2 instanceof String && 0 == date2.toString().trim().length()) ? LocalDateTime.now()
                : date2 instanceof LocalDateTime ? (LocalDateTime) date2 : parseLocalDateTime(date2);

        if (field == Calendar.MILLISECOND) return ChronoUnit.MILLIS.between(startInclusive, endExclusive);
        if (field == Calendar.SECOND) return ChronoUnit.SECONDS.between(startInclusive, endExclusive);
        if (field == Calendar.MINUTE) return ChronoUnit.MINUTES.between(startInclusive, endExclusive);
        if (field == Calendar.HOUR_OF_DAY) return ChronoUnit.DAYS.between(startInclusive, endExclusive);
        if (field == Calendar.WEEK_OF_YEAR) return ChronoUnit.WEEKS.between(startInclusive, endExclusive);
        if (field == Calendar.MONTH) return ChronoUnit.MONTHS.between(startInclusive, endExclusive);
        if (field == Calendar.YEAR) return ChronoUnit.YEARS.between(startInclusive, endExclusive);
        return null;
    }

    /**
     * 计算 [before ~ after] 时间差
     * t 开头属性, 如果 > 0 则代表还有值, 就算等于0也是大于这个值的, 显示使用的
     * 默认以当前时间 - before
     *
     * @param before 开始时间
     * @return 时间数据错误, 返回空则时间不匹配
     */
    public static Map<String, Integer> interval(Object before) {
        return interval(before, now());
    }

    /**
     * 计算 [before ~ after] 时间差
     * t 开头属性, 如果 > 0 则代表还有值, 就算等于0也是大于这个值的, 显示使用的
     *
     * @param before 开始时间
     * @param after  结束时间 (默认当前时间)
     * @return 时间数据错误, 返回空则时间不匹配
     */
    public static Map<String, Integer> interval(Object before, Object after) {
        if (null == before) return null;

        // 开始
        Date startTime = before instanceof Date ? (Date) before : parse(before);

        // 结束时间
        Date endTime = null == after || (after instanceof String && 0 == after.toString().trim().length()) ? now()
                : after instanceof Date ? (Date) after : parse(after);
        if (null == endTime) return null;

        Map<String, Integer> result = CollectionX.ofMap("d", 0, "h", 0, "m", 0, "s", 0, "ms", 0, "td", 0, "th", 0, "tm", 0, "ts", 0, "tms", 0);
        long diff = endTime.getTime() - startTime.getTime(); // 时间差, 单位毫秒
        long d = diff / 24 / 60 / 60 / 1000; // 天
        if (0 != d) {
            result.put("d", (int) d);
            result.put("td", 1);
        }

        long h = (diff % (24 * 3600 * 1000)) / 3600 / 1000; // 小时
        if (0 != h) result.put("h", (int) h);
        if (0 != d || 0 != h) result.put("th", 1);

        long m = (diff % (3600 * 1000)) / 60 / 1000; // 分钟
        if (0 != m) result.put("m", (int) m);
        if (0 != d || 0 != h || 0 != m) result.put("tm", 1);

        long s = (diff % (60 * 1000)) / 1000; // 秒
        if (0 != s) result.put("s", (int) s);
        if (0 != d || 0 != h || 0 != m || 0 != s) result.put("ts", 1);

        long ms = diff % 1000; // 毫秒
        if (0 != ms) result.put("ms", (int) ms);
        if (0 != d || 0 != h || 0 != m || 0 != s || 0 != ms) result.put("tms", 1);
        return result;
    }

    /**
     * 计算 [before ~ after] 时间差
     * 显示格式: x天xx小时xx分xx秒
     *
     * @param before 开始时间
     * @param after  结束时间 (默认当前时间)
     * @return 时间数据错误, 返回空则时间不匹配
     */
    public static String intervalToString(Object before, Object after) {
        return _intervalToString(before, after, false);
    }

    /**
     * 计算 [before ~ after] 时间差
     * 显示格式: x天xx小时xx分xx秒xx毫秒
     *
     * @param before 开始时间
     * @param after  结束时间 (默认当前时间)
     * @return 时间数据错误, 返回空则时间不匹配
     */
    public static String intervalToStringByMs(Object before, Object after) {
        return _intervalToString(before, after, true);
    }

    private static String _intervalToString(Object before, Object after, boolean showMS) {
        Map<String, Integer> result = interval(before, after);
        if (CollectionX.isEmpty(result)) return Const.STRING_EMPTY_VALUE;
        Integer showDay = result.get("td");
        String dayText = showDay > 0 ? String.format("%s天", result.get("d")) : Const.STRING_EMPTY_VALUE;
        Integer showHour = result.get("th");
        String hourText = showHour > 0 ? formatNumber(result.get("h"), "00", "小时") : Const.STRING_EMPTY_VALUE;
        Integer showMinute = result.get("tm");
        String minuteText = showMinute > 0 ? formatNumber(result.get("m"), "00", "分") : Const.STRING_EMPTY_VALUE;
        Integer showSecond = result.get("ts");
        String secondText = showSecond > 0 ? formatNumber(result.get("s"), "00", "秒") : Const.STRING_EMPTY_VALUE;
        String milliSecondText = "";
        if (showMS) {
            Integer showMilliSecond = result.get("tms");
            milliSecondText = showMilliSecond > 0 ? formatNumber(result.get("ms"), "00", "毫秒") : Const.STRING_EMPTY_VALUE;
        }
        return String.format("%s%s%s%s%s", dayText, hourText, minuteText, secondText, milliSecondText);
    }

    // 格式化数字
    public static String formatNumber(Object number, String formater) {
        return formatNumber(number, formater, "");
    }

    // 格式化数字
    public static String formatNumber(Object number, String formater, String suffix) {
        if (null == number) return Const.STRING_EMPTY_VALUE;
        return String.format("%s%s", NumberX.format(number, formater), suffix);
    }

    // date1 在 date2 之前
    public static boolean before(Object date1, Object date2) {
        Integer result = compareTo(date1, date2);
        return null != result && -1 == result;
    }

    // date1 在 date2 之后
    public static boolean after(Object date1, Object date2) {
        Integer result = compareTo(date1, date2);
        return null != result && 1 == result;
    }

    /**
     * 比较时间先后
     *
     * @param date1 时间1
     * @param date2 时间2
     * @return null 数据错误, 相等 = 0, datetime1 > datetime2 = 1, datetime1 < datetime2 = -1, null 转换日期错误
     */
    public static Integer compareTo(Object date1, Object date2) {
        Date dateTime1 = parse(date1);
        Date dateTime2 = parse(date2);
        if (null == dateTime1 || null == dateTime2) return null;
        return dateTime1.compareTo(dateTime2);
    }

    public static void main(String[] args) {
        String result = intervalToString("2021-03-24 12:00:00.000", "2021-03-24 19:12:24.000");
        System.out.println("result = " + result);

        Long bw = between("2021-04-13 19:00:00.000", Calendar.SECOND);
        System.out.println("bw = " + bw);
    }
}
