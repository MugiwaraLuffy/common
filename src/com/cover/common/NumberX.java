package com.cover.common;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

// 数据计算工具类
@SuppressWarnings({"SameParameterValue", "unused"})
public final class NumberX {

    // 默认判空最小值, 小于默认值都为空
    public static final Integer DEFAULT_MIN_NUMBER = 0;
    public static final String SEED_NUMBER = "0123456789";
    public static final String SEED_LETTER = "abcdefghijklmnopqrstuvwxyz";
    public static final String SEED_MIX = String.format("%s%s%s", SEED_NUMBER, SEED_LETTER, SEED_LETTER.toUpperCase()); // 数字+大小写英文字母
    public static final String SEED_LETTER_EXCLUDE = SEED_LETTER.replaceAll("[i|o]", Const.STRING_EMPTY_VALUE); // 排除 i,o 相似字符
    public static final String SEED_CAPTCHA = String.format("%s%s%s", SEED_NUMBER, SEED_LETTER_EXCLUDE, SEED_LETTER_EXCLUDE.toUpperCase()); // 随机验证码字符, 排除 i,o 相似字符

    // Integer ####################################################################################################

    /**
     * 对象转换 Integer, 空则返回0
     *
     * @param obj 数字对象
     * @return Integer
     */
    public static Integer getInteger(Object obj) {
        return getInteger(obj, 0);
    }

    /**
     * 对象转换 Integer, 空则返回 Null
     *
     * @param obj 数字对象
     * @return Integer, 有可能为空
     */
    public static Integer getIntegerDefault(Object obj) {
        return getInteger(obj, null);
    }

    /**
     * 对象转换 Integer, 空则返回 defaultValue 默认值
     *
     * @param obj          数字对象
     * @param defaultValue 空则返回的默认值
     * @return Integer, 如果 defaultValue 是 Null, 数字对象是空的还是返回 Null
     */
    public static Integer getInteger(Object obj, Integer defaultValue) {
        try {
            if (null == obj) return defaultValue; // 直接返回默认值
            if (obj instanceof Integer) return (Integer) obj; // 原类型返回
            if (obj instanceof Long) return ((Long) obj).intValue(); // 可能会有精度丢失
            if (obj instanceof String) return Integer.parseInt(StringX.get(obj), 10); // 可能会抛异常
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 判断对象是否有效数字
     *
     * @param obj 数字对象
     * @return 是否 >0 的数字
     */
    public static boolean isInteger(Object obj) {
        if (null == obj) return false;
        if (obj instanceof Integer) return (Integer) obj > 0;
        if (obj instanceof Long) return (Long) obj > 0;
        if (obj instanceof String) return StringX.get(obj).matches("\\d+");
        return false;
    }

    // Long #######################################################################################################

    /**
     * 对象转换 Long, 空则返回0
     *
     * @param obj 数字对象
     * @return Long
     */
    public static Long getLong(Object obj) {
        return getLong(obj, 0L);
    }

    /**
     * 对象转换 Long, 空则返回 Null
     *
     * @param obj 数字对象
     * @return Long, 有可能为空
     */
    public static Long getLongDefault(Object obj) {
        return getLong(obj, null);
    }

    /**
     * 对象转换 Long, 空则返回 defaultValue 默认值
     *
     * @param obj          数字对象
     * @param defaultValue 空则返回的默认值
     * @return Long, 如果 defaultValue 是 Null, 数字对象是空的还是返回 Null
     */
    public static Long getLong(Object obj, Long defaultValue) {
        try {
            if (null == obj) return defaultValue; // 直接返回默认值
            if (obj instanceof Long) return (Long) obj; // 原类型返回
            if (obj instanceof Integer || obj instanceof String) return Long.parseLong(StringX.get(obj)); // 可能会抛异常
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }


    // Double #####################################################################################################

    /**
     * 对象转换 Double, 空则返回0
     *
     * @param obj 数字对象
     * @return Long
     */
    public static Double getDouble(Object obj) {
        return getDouble(obj, 0d);
    }

    /**
     * 对象转换 Double, 空则返回 Null
     *
     * @param obj 数字对象
     * @return Double, 有可能为空
     */
    public static Double getDoubleDefault(Object obj) {
        return getDouble(obj, null);
    }

    /**
     * 对象转换 Double, 空则返回 defaultValue 默认值
     *
     * @param obj          数字对象
     * @param defaultValue 空则返回的默认值
     * @return Long, 如果 defaultValue 是 Null, 数字对象是空的还是返回 Null
     */
    public static Double getDouble(Object obj, Double defaultValue) {
        try {
            if (null == obj) return defaultValue; // 直接返回默认值
            if (obj instanceof Double) return (Double) obj; // 原类型返回
            if (obj instanceof Float) return ((Float) obj).doubleValue();
            if (obj instanceof Integer) return ((Integer) obj).doubleValue();
            if (obj instanceof Long) return ((Long) obj).doubleValue();
            if (obj instanceof String) return Double.parseDouble(StringX.get(obj)); // 可能会抛异常
            return defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * 格式化浮点数, 保留 scale 位小数
     *
     * @param number 浮点数
     * @param scale  保留多少位小数
     * @return 小数四舍五入, 不够的话默认空, 例如: 1.0001 => 1.0, 1.045001 => 1.05
     */
    public static double getDouble(double number, int scale) {
        final BigDecimal bg = new BigDecimal(number);
        return bg.setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    // 随机数 ######################################################################################################
    // Random.nextXXX 默认是闭开区间, 例如: random.nextInt(100) => [0, 100), 包括0, 但不包括100

    // 创建一个随机序列, 去除 - 字符
    public static String uuid() {
        return uuidOriginal().replaceAll("-", Const.STRING_EMPTY_VALUE);
    }

    // 获取一个随机序列
    public static String uuidOriginal() {
        return UUID.randomUUID().toString();
    }

    // 获取一个随机器
    public static Random getRandom() {
        return ThreadLocalRandom.current();
    }

    /**
     * 获取一个随机的整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return [最小值, 最大值] 前后都是闭合区间, 也就随机数是包括 min, max 的值
     */
    public static int random(int min, int max) {
        return random(min, max, getRandom());
    }

    /**
     * 获取一个随机的整数
     *
     * @param min    最小值
     * @param max    最大值
     * @param random 随机器, null 会获取一个 ThreadLocalRandom.current() 随机器
     * @return [最小值, 最大值] 前后都是闭合区间, 也就随机数是包括 min, max 的值
     */
    public static int random(int min, int max, Random random) {
        if (null == random) random = getRandom();
        return (int) (min + random.nextDouble() * (max - min + 1));
    }

    /**
     * 获取一个 count 长度, 带数字/大小写英文字母的随机数
     *
     * @param count 获取随机字符的长度, 不能 <=0
     * @return count 长度的随机字符
     */
    public static String random(int count) {
        return random(count, SEED_MIX);
    }

    /**
     * 获取一个 count 长度, 带数字/大小写英文字母 (排除相似字符) 的随机数
     *
     * @param count 获取随机字符的长度, 不能 <=0
     * @return count 长度的随机字符
     */
    public static String randomCaptcha(int count) {
        return random(count, SEED_CAPTCHA);
    }

    /**
     * 根据 seedString 随机样板, 获取 count 长度的随机字符
     *
     * @param count 获取随机字符的长度, 不能 <=0
     * @param seeds 随机样板, 可自定义, 但不能为空
     * @return count 长度的随机字符
     */
    public static String random(int count, String seeds) {
        if (count <= 0 || StringX.isEmpty(seeds)) return Const.STRING_EMPTY_VALUE;
        return random(count, seeds, getRandom());
    }

    /**
     * 根据 seedString 随机样板, 获取 count 长度的随机字符
     *
     * @param count  获取随机字符的长度, 不能 <=0
     * @param seeds  随机样板, 可自定义, 但不能为空
     * @param random 随机器, 没有会自动获取一个线程随机器
     * @return count 长度的随机字符
     */
    public static String random(int count, String seeds, Random random) {
        if (count <= 0 || StringX.isEmpty(seeds)) return Const.STRING_EMPTY_VALUE;
        if (null == random) random = getRandom();

        char[] items = seeds.toCharArray(); // 随机样板
        int randomSeeds = items.length; // 随机样板的长度
        char[] buffer = new char[count]; // 随机字符
        for (int i = 0; i < count; i++) buffer[i] = items[random.nextInt(randomSeeds)];
        return new String(buffer);
    }

    // 通用方法 ####################################################################################################

    /**
     * 判断对象不空, 数字必须 > 0
     * 非 Integer, Long, Float, Dobule 返回 true
     *
     * @param obj 判断对象
     * @return 数字 > 0
     */
    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 判断对象是否为空
     * 空或者小于等于0
     * 非 Integer, Long, Float, Dobule 返回 false
     *
     * @param obj 判断对象
     * @return 是否空或者小于等于0
     */
    public static boolean isEmpty(Object obj) {
        return isEmpty(obj, DEFAULT_MIN_NUMBER);
    }

    /**
     * 判断数字对象是否为空
     * 非 Integer, Long, Float, Dobule 返回 false
     *
     * @param obj       数字对象
     * @param minNumber 最小值
     * @return 如果 obj = null 或者 obj <= minNumber
     */
    public static boolean isEmpty(Object obj, int minNumber) {
        if (null == obj) return true;
        if (obj instanceof Integer) return minNumber >= (Integer) obj;
        if (obj instanceof Long) return minNumber >= (Long) obj;
        if (obj instanceof Float || obj instanceof Double) return compare(obj, minNumber) < 1; // 浮点数比较
        if (obj instanceof String) {
            String value = StringX.get(obj);
            if (StringX.isEmpty(value)) return true;
            if (RegeX.isNumber(value)) return Long.parseLong(value) > 0;
            if (RegeX.isDouble(value)) return compare(value, minNumber) < 1;
            return false;
        }
        return false;
    }

    /**
     * 判断多个数字对象, 任何一个为空则返回true
     *
     * @param objs 判断的数字对象
     * @return 任意一个空数字
     */
    public static boolean isAnyEmpty(Object... objs) {
        if (CollectionX.isEmpty(objs)) return true;
        for (Object obj : objs) if (isEmpty(obj)) return true;
        return false;
    }

    /**
     * 判断多个数字对象, 所有数字对象都为空则返回 true
     *
     * @param objs 判断的数字对象
     * @return 所有均为空数字对象
     */
    public static boolean isAllEmpty(Object... objs) {
        if (CollectionX.isEmpty(objs)) return true;
        for (Object obj : objs) if (!isEmpty(obj)) return false;
        return true;
    }

    /**
     * 比较两个数字大小
     * null 算最小
     *
     * @param num1 数字1
     * @param num2 数字2
     * @return 1: num1 > num2, 0: num1 = num2, -1: num1 < num2
     */
    public static int compare(Object num1, Object num2) {
        String number1 = StringX.get(num1);
        String number2 = StringX.get(num2);

        if (StringX.isAllEmpty(number1, number2)) return 0;
        if (StringX.isEmpty(number1)) return -1;
        if (StringX.isEmpty(number2)) return 1;
        return new BigDecimal(number1).compareTo(new BigDecimal(number2));
    }


    /**
     * 格式化数字
     * 符号含义
     * 0: 一个数字
     * #: 一个数字, 不包括(0)
     * .: 小数的分隔符的占位符
     * ,: 分组分隔符的占位符
     * ;: 分隔格式
     * -: 缺省负数前缀
     * %: 乘以 100 和作为百分比显示
     * ?: 乘以 1000 和作为千进制货币符显示, 用货币符号代替
     * X: 前缀或后缀中使用的任何其它字符, 用来引用前缀或后缀中的特殊字符
     * 主要靠 # 和 0 两种占位符号来指定数字长度, 0 表示如果位数不足则以 0 填充, # 数字存在就写不存在不写
     *
     * @param number   数字
     * @param formater 格式
     * @return 格式化后字符串
     */
    public static String format(Object number, String formater) {
        if (StringX.isEmpty(formater)) return String.valueOf(number);
        return getFormater(formater).format(number);
    }

    /**
     * 返回数字格式化器
     *
     * @param format 格式
     * @return 数字格式化器
     */
    public static DecimalFormat getFormater(String format) {
        return new DecimalFormat(format);
    }

    public static void main(String[] args) {
        double pi = Math.PI; // 圆周率
        System.out.println("取一位整数: " + format(pi, "0")); // 取一位整数: 3
        System.out.println("取一位整数和两位小数: " + format(pi, "0.00")); // 取一位整数和两位小数: 3.14
        System.out.println("取所有整数部分: " + format(pi, "#")); // 取所有整数部分: 3
        System.out.println("以百分比方式计数, 并取两位小数: " + format(pi, "#.##%")); // 以百分比方式计数, 并取两位小数: 314.16%
        long c = 299792458; // 光速
        System.out.println("显示为科学计数法, 并取五位小数: " + format(c, "#.#####E0")); // 显示为科学计数法, 并取五位小数: 2.99792E8
        System.out.println("显示为两位整数的科学计数法, 并取四位小数: " + format(c, "00.####E0")); // 显示为两位整数的科学计数法, 并取四位小数: 29.9792E7
        System.out.println("每三位以逗号进行分隔: " + format(c, ",###")); // 每三位以逗号进行分隔: 299,792,458
        System.out.println("将格式嵌入文本: " + format(c, "光速大小为每秒,###米")); // 将格式嵌入文本: 光速大小为每秒299,792,458米
    }
}
