package com.cover.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 规则匹配工具类
// 1. 可传递参数调用 getMatcher 获取一个匹配器, 注意: Null
// 2. Matcher 每次必须 matcher.matches() 或者 matcher.find(), matcher.group(1) 才能获取内容
// 3. matcher.group(数字) 数字: 正则表达式里面的 ()
// 4. matcher.groupCount() 获取的正则表达式 () 个数
// 5. matches 方法代表完全匹配, find 方法代表部分匹配
// 逻辑是先匹配规则, 匹配通过后才能获取后面的值
@SuppressWarnings("unused")
public final class RegeX {

    // 数字
    public static boolean isNumber(String str) {
        return matches(str, Const.REGEX_NUMBER);
    }

    // 整数 / 2位小数以内的都是浮点型
    public static boolean isDouble(String str) {
        return matches(str, Const.REGEX_PRICE);
    }

    // 链接地址
    public static boolean isUrl(String str) {
        return matches(str, Const.REGEX_URL);
    }

    // 手机号码
    public static boolean isMobile(String str) {
        return matches(str, Const.REGEX_MOBILE);
    }

    // 手机号码 / 固话
    public static boolean isPhone(String str) {
        return matches(str, Const.REGEX_PHONE);
    }

    // 固话
    public static boolean isFixed(String str) {
        return matches(str, Const.REGEX_FIXED);
    }

    // 邮箱
    public static boolean isEmail(String str) {
        return matches(str, Const.REGEX_EMAIL);
    }

    // 中文名
    public static boolean isChineseName(String str) {
        return matches(str, Const.REGEX_CHINESE_NAME);
    }

    // 英文名
    public static boolean isEnglishName(String str) {
        return matches(str, Const.REGEX_ENGLISH_NAME);
    }

    // QQ 号码
    public static boolean isQQ(String str) {
        return matches(str, Const.REGEX_QQ);
    }

    // 微信号
    public static boolean isWechat(String str) {
        return matches(str, Const.REGEX_WECHAT);
    }

    // 邮政编码
    public static boolean isPostCode(String str) {
        return matches(str, Const.REGEX_POSTCODE);
    }

    // 价格
    public static boolean isPrice(String str) {
        return matches(str, Const.REGEX_PRICE);
    }

    /**
     * 检查文本是否匹配特定规则, 返回第一个 () 里面的文本内容
     * 部分匹配, 默认忽略大小写模式
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 返回匹配的文本内容
     */
    public static List<String> getFindTexts(String compareText, String regexString) {
        return getFindTexts(compareText, regexString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查文本是否匹配特定规则, 返回第一个 () 里面的文本内容
     * 部分匹配
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 返回匹配的文本内容
     */
    public static List<String> getFindTexts(String compareText, String regexString, int flags) {
        List<String> result = new ArrayList<>();
        if (StringX.isAnyEmpty(compareText, regexString)) return result;
        Matcher matcher = getMatcher(compareText, regexString, flags);
        if (null == matcher) return result;
        while (matcher.find()) result.add(matcher.group(1));
        return result;
    }

    /**
     * 检查文本是否匹配特定规则, 返回第一个 () 里面的文本内容
     * 部分匹配, 默认忽略大小写模式
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 返回匹配的文本内容
     */
    public static String getFindText(String compareText, String regexString) {
        return getFindText(compareText, regexString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查文本是否匹配特定规则, 返回第一个 () 里面的文本内容
     * 部分匹配
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @param flags       匹配模式
     * @return 返回匹配的文本内容
     */
    public static String getFindText(String compareText, String regexString, int flags) {
        if (StringX.isAnyEmpty(compareText, regexString)) return Const.STRING_EMPTY_VALUE;
        Matcher matcher = getMatcher(compareText, regexString, flags);
        return null != matcher && matcher.find() ? matcher.group(1) : Const.STRING_EMPTY_VALUE;
    }

    /**
     * 检查文本是否匹配特定规则, 返回第一个 () 里面的文本内容
     * 完全匹配, 默认忽略大小写模式
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 返回匹配的文本内容
     */
    public static String getMatchesText(String compareText, String regexString) {
        return getMatchesText(compareText, regexString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查文本是否匹配特定规则, 返回第一个 () 里面的文本内容
     * 完全匹配
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @param flags       匹配模式
     * @return 返回匹配的文本内容
     */
    public static String getMatchesText(String compareText, String regexString, int flags) {
        if (StringX.isAnyEmpty(compareText, regexString)) return Const.STRING_EMPTY_VALUE;
        Matcher matcher = getMatcher(compareText, regexString, flags);
        return null != matcher && matcher.matches() ? matcher.group(1) : Const.STRING_EMPTY_VALUE;
    }

    /**
     * 检查文本是否匹配特定规则
     * 部分匹配, 默认忽略大小写模式
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 有部分内容匹配就返回 True
     */
    public static boolean find(String compareText, String regexString) {
        return find(compareText, regexString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查文本是否匹配特定规则
     * 部分匹配
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @param flags       匹配模式
     * @return 有部分内容匹配就返回 True
     */
    public static boolean find(String compareText, String regexString, int flags) {
        if (StringX.isAnyEmpty(compareText, regexString)) return false;
        Matcher matcher = getMatcher(compareText, regexString, flags);
        if (null == matcher) return false;
        return matcher.find();
    }


    /**
     * 检查文本是否匹配特定规则
     * 完全匹配, 默认忽略大小写模式
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 完全匹配规则才返回 True
     */
    public static boolean matches(String compareText, String regexString) {
        return matches(compareText, regexString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 检查文本是否匹配特定规则
     * 完全匹配
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @param flags       匹配模式
     * @return 完全匹配规则才返回 True
     */
    public static boolean matches(String compareText, String regexString, int flags) {
        if (StringX.isAnyEmpty(compareText, regexString)) return false;
        Matcher matcher = getMatcher(compareText, regexString, flags);
        if (null == matcher) return false;
        return matcher.matches();
    }

    /**
     * 获取一个匹配器
     * 默认忽略大小写模式
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @return 匹配器, 可能为空
     */
    public static Matcher getMatcher(String compareText, String regexString) {
        return getMatcher(compareText, regexString, Pattern.CASE_INSENSITIVE);
    }

    /**
     * 获取一个匹配器
     *
     * @param compareText 匹配的文本
     * @param regexString 匹配的规则
     * @param flags       匹配模式
     * @return 匹配器, 可能为空
     */
    public static Matcher getMatcher(String compareText, String regexString, int flags) {
        if (StringX.isAnyEmpty(compareText, regexString)) return null;
        Pattern pattern = Pattern.compile(regexString, flags);
        return pattern.matcher(compareText);
    }

    public static void main(String[] args) {
        System.out.println("result = " + isDouble("1"));
        System.out.println("result = " + matches("17182888331878", Const.REGEX_MOBILE));
        System.out.println("result = " + getFindText("啊都 coer@s.sdf.com 啊都", Const.REGEX_EMAIL));

    }

}
