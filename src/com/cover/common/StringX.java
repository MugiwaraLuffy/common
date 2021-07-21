package com.cover.common;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings({"unused", "RegExpRedundantEscape", "rawtypes"})
public final class StringX {

    public static final String STRING_EMPTY = ""; // 空字符串
    public static final String STRING_NULL = "null"; // null 字符串
    public static final String HTML_LEFT_TAG = "<"; // HTML 左标签
    public static final String HTML_RIGHT_TAG = ">"; // HTML 右标签
    public static final String HTML_CONVERT_LEFT_TAG = "&lt;"; // 转换 HTML 左标签
    public static final String HTML_CONVERT_RIGHT_TAG = "&gt;"; // 转换 HTML 右标签
    public static final String REGEX_CHINESE = "[\u0391-\uFFE5]"; // 中文字符正则表达式
    public static final String DEFAULT_SEPARATOR = ","; // 默认分隔符

    /**
     * 检查字符串是否为空
     *
     * @param str 检查字符串
     * @return 排除所有的空白字符, 再去除左右两边空格, 剩余的字符串是否为空
     */
    public static boolean isEmpty(String str) {
        if (null == str) return true;

        String handleString = str.trim();
        if (!hasLength(handleString) || STRING_NULL.equalsIgnoreCase(handleString)) return true;
        for (int i = 0; i < handleString.length(); i++)
            if (!Character.isWhitespace(handleString.charAt(i))) return false; // 有一个非空字符就不算是空
        return true;
    }

    /**
     * 判断字符串是否不为空
     *
     * @param str 检查字符串
     * @return 排除所有的空白字符, 再去除左右两边空格, 剩余的字符串是否不为空
     */
    public static boolean notEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 检查是否含有1个或多个参数为空
     *
     * @param strs 检查 Array 集合
     * @return 只要有一个是空的则返回 true
     */
    public static boolean isAnyEmpty(String... strs) {
        if (CollectionX.isEmpty(strs)) return true;
        for (String str : strs) if (isEmpty(str)) return true;
        return false;
    }

    /**
     * 检查是否所有参数都为空
     *
     * @param strs 检查 Array 集合
     * @return 全部都是空的, 则返回 true
     */
    public static boolean isAllEmpty(String... strs) {
        if (CollectionX.isEmpty(strs)) return true;
        for (String str : strs) if (notEmpty(str)) return false;
        return true;
    }

    /**
     * 检查所有字符都不为空
     *
     * @param strs 检查 Array 集合
     * @return 只有全部不为空才返回 true
     */
    public static boolean isAllNotEmpty(String... strs) {
        return !isAnyEmpty(strs);
    }

    /**
     * 格式化字符串
     *
     * @param obj 处理的对象
     * @return 字符串, List=[a,b,...], Map={a=1,b="b"}, 对象Object[a=b,c=null] 去除左右空格, 空则返回 ""
     */
    public static String get(Object obj) {
        return get(obj, STRING_EMPTY);
    }

    /**
     * 格式化字符串
     *
     * @param obj           处理的对象
     * @param defaultString 设置空则返回的默认值
     * @return 字符串, List=[a,b,...], Map={a=1,b="b"}, 对象Object[a=b,c=null] 去除左右空格, 空则返回 defaultString
     */
    public static String get(Object obj, String defaultString) {
        if (null == obj) return defaultString;
        String parseString = String.valueOf(obj);
        return isEmpty(parseString) ? defaultString : parseString.trim();
    }

    /**
     * 格式化字符串
     *
     * @param obj 处理对象
     * @return 返回转小写字符串
     */
    public static String getLowerCase(Object obj) {
        return get(obj, STRING_EMPTY).toLowerCase();
    }

    /**
     * 格式化字符串
     *
     * @param obj           处理对象
     * @param defaultString 设置空值则返回的默认值
     * @return 全部小写的字符串
     */
    public static String getLowerCase(Object obj, String defaultString) {
        return get(obj, defaultString).toLowerCase();
    }


    /**
     * 第一个字母转大写
     *
     * @param name 处理名字
     * @return 首字母大写
     */
    public static String capitalFirst(String name) {
        if (StringX.isEmpty(name)) return STRING_EMPTY;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * 获取字符串数字
     *
     * @param str 处理的字符串
     * @return 返回只有数字的字符串
     */
    public static String getNumberString(Object str) {
        return get(str).replaceAll("[^\\d]", STRING_EMPTY);
    }

    /**
     * 获取字符串英文, 包括标点符号和横杆下划线双引号
     *
     * @param str 处理的字符串
     * @return 返回只有英文字符的字符串
     */
    public static String getEnglishString(Object str) {
        return get(str).replaceAll("[^a-zA-Z_\\-\\.,;:'\"]", STRING_EMPTY);
    }

    /**
     * 获取字符串中的中文
     *
     * @param str 要处理的字符串
     * @return 返回只有中文的字符串
     */
    public static String getChineseString(Object str) {
        return get(str).replaceAll("[^\u0391-\uFFE5]", STRING_EMPTY);
    }

    /**
     * 去除所有的空白字符和左右两边的空格
     *
     * @param str 处理的字符串
     * @return 有效字符.trim()
     */
    public static String trimAllWhitespace(String str) {
        if (null == str) return Const.STRING_EMPTY_VALUE;
        if (!containsWhitespace(str)) return str.trim();

        final int len = str.length();
        StringBuilder buffer = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) buffer.append(c);
        }
        return buffer.toString().trim();
    }

    /**
     * 判断是否存在空白字符
     *
     * @param str 判断的字符
     * @return 是否存在空字符
     */
    public static boolean containsWhitespace(String str) {
        return containsWhitespace((CharSequence) str);
    }

    /**
     * 判断是否存在空白字符
     *
     * @param str 判断的字符
     * @return 是否存在空字符
     */
    public static boolean containsWhitespace(CharSequence str) {
        if (!hasLength(str)) return false;
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) return true;
        }
        return false;
    }

    /**
     * 非空字符
     *
     * @param str 判断字符
     * @return 是否非空字符(有可能包括空白字符)
     */
    public static boolean hasLength(CharSequence str) {
        return null != str && str.toString().trim().length() > 0;
    }

    /**
     * 非空字符
     *
     * @param str 判断字符
     * @return 是否非空字符(有可能包括空白字符)
     */
    public static boolean hasLength(String str) {
        return null != str && str.trim().length() > 0;
    }

    /**
     * 截取字符串方法
     *
     * @param str    需要截取的原字符串
     * @param length 截取长度, > 0 从前往后截取, < 0 从后往前截
     * @return 截取后的字符串
     */
    public static String cut(String str, int length) {
        return cut(str, length, STRING_EMPTY);
    }

    /**
     * 截取字符串方法
     *
     * @param str           需要截取的原字符串
     * @param length        截取长度, > 0 从前往后截取, < 0 从后往前截
     * @param defaultString 截取空字符串, 返回的默认值
     * @return 截取后的字符串
     */
    public static String cut(String str, int length, String defaultString) {
        if (isEmpty(str)) return defaultString;
        if (0 == length) return defaultString;
        if (length > 0) return get(String.format("%." + length + "s", str), defaultString); // 从第一个字符往后截取对应长度的字符串
        return str.length() > Math.abs(length) ? get(str.substring(str.length() - Math.abs(length)), defaultString) : str; // 从最后一个字符往前截取对应长度的字符串
    }

    /**
     * 找出匹配的字符串
     *
     * @param content 处理的字符串
     * @param begin   开始字符串标识
     * @param end     结束字符串标识
     * @return 匹配 开始和结束字符串中间的字符, 不匹配则返回原字符串
     */
    public static String find(String content, String begin, String end) {
        if (isAnyEmpty(content, begin, end)) return get(content);
        String regex = String.format("%s(.+?)%s", begin, end);
        return RegeX.getFindText(content, regex);
    }

    /**
     * 判断两个对象是否相等
     *
     * @param a 对象
     * @param b 对象
     * @return 是否匹配, java基础类型判断可行, List/Map/对象不能匹配
     */
    public static boolean equals(Object a, Object b) {
        return get(a).equals(get(b));
    }

    /**
     * 判断两个对象是否相等
     * 忽略大小写
     *
     * @param a 对象
     * @param b 对象
     * @return 是否匹配, java基础类型判断可行, List/Map/对象不能匹配
     */
    public static boolean equalsIgnoreCase(Object a, Object b) {
        return get(a).equalsIgnoreCase(get(b));
    }

    /**
     * 判断 original 是否包含 s
     *
     * @param original 比较参考对象
     * @param s        包含的值
     * @return a 是否包含 b
     */
    public static boolean contains(Object original, Object s) {
        return get(original).contains(get(s));
    }

    /**
     * 判断 original 是否包含 s
     * 忽略大小写
     *
     * @param original 比较参考对象
     * @param s        包含的值
     * @return a 是否包含 b
     */
    public static boolean containsIgnoreCase(Object original, Object s) {
        return getLowerCase(original).contains(getLowerCase(s));
    }

    /**
     * 前后填充分隔符
     *
     * @param content 需要处理的文本
     * @return 填充好的内容, 默认分隔符 ,
     */
    public static String setAroundSeparator(String content) {
        return setAroundSeparator(content, DEFAULT_SEPARATOR);
    }

    /**
     * 前后填充分隔符
     *
     * @param content   需要处理的文本
     * @param separator 分隔符
     * @return 填充好分隔符的内容
     */
    public static String setAroundSeparator(String content, String separator) {
        if (isAnyEmpty(content, separator)) return STRING_EMPTY;

        String result = get(content);
        if (!result.startsWith(separator)) result = String.format("%s%s", separator, result);
        if (!result.endsWith(separator)) result = String.format("%s%s", result, separator);
        return result;
    }

    /**
     * 删除字符串前后多余的分隔符
     *
     * @param content 需要处理的文本信息
     * @return 已删除前后多余分隔符字符串, 默认分隔符 ,
     */
    public static String removeAroundSeparator(String content) {
        return removeAroundSeparator(content, DEFAULT_SEPARATOR);
    }

    /**
     * 删除字符串前后多余的分隔符
     *
     * @param content   需要处理的文本信息
     * @param separator 分隔符, 不允许空
     * @return 已删除前后多余分隔符字符串
     */
    public static String removeAroundSeparator(String content, String separator) {
        if (isEmpty(content)) return STRING_EMPTY;

        String result = get(content);
        if (isEmpty(separator)) return result; // 没有传入删除的分隔符, 直接返回去除两边空格的字符串

        if (result.startsWith(separator)) result = result.replaceFirst(String.format("^(%s)+", separator), STRING_EMPTY); // 去掉前面的分隔符
        if (result.endsWith(separator)) result = result.replaceAll(String.format("(%s)+$", separator), STRING_EMPTY); // 去掉后面的分隔符, 连续多个也会去掉
        return result;
    }

    /**
     * 把 List 拼接成一个字符串返回
     *
     * @param collection 处理集合
     * @return 拼接后的字符串, 默认分隔符 ,
     */
    public static String join(Collection<?> collection) {
        return join(collection, DEFAULT_SEPARATOR);
    }

    /**
     * 把 List 拼接成一个字符串返回
     *
     * @param collection 处理集合
     * @param separator  分隔符, 允许null, ""
     * @return 拼接后的字符串, 默认分隔符 ,
     */
    public static String join(Collection<?> collection, String separator) {
        if (CollectionX.isEmpty(collection)) return STRING_EMPTY;
        return join(collection.toArray(), separator);
    }

    /**
     * 把数组拼接成一个字符串返回
     *
     * @param arr 数组
     * @return 拼接后的字符串, 默认分隔符 ,
     */
    public static String join(Object[] arr) {
        return join(arr, DEFAULT_SEPARATOR);
    }

    /**
     * 把数组拼接成一个字符串返回
     *
     * @param arr       数组
     * @param separator 分隔符, 允许null, ""
     * @return 拼接后的字符串
     */
    public static String join(Object[] arr, String separator) {
        if (CollectionX.isEmpty(arr)) return STRING_EMPTY;
        separator = get(separator); // 处理分隔符, 允许 ""

        StringBuilder buffer = new StringBuilder();
        for (Object item : arr) buffer.append(separator).append(item); // 拼接
        return isEmpty(separator) ? buffer.toString() : buffer.toString().replaceFirst(separator, STRING_EMPTY); // 处理第一个分隔符
    }

    public static String format(String template, Object... objs) {
        return format(template, "\\{.+?\\}", objs);
    }

    /**
     * 传递参数, 替换文本占位符
     *
     * @param template 模板
     * @param regex    替换正则表达式
     * @param objs     参数列表, 分1个参数, 多个参数[不允许Map类型]
     * @return 格式化后的字符串
     */
    public static String format(String template, String regex, Object... objs) {
        if (isEmpty(template)) return STRING_EMPTY;

        String replaceText = "{%s}";
        String replaceRegex = isEmpty(regex) ? "\\{.+?\\}" : regex;

        // 后面只有一个参数的情况 format("大家好, 我叫: {a}, 我今年: {b}岁了", data={a=Cover, b=10}), 输出 "大家好, 我叫: Cover, 我今年: 10岁了"
        if (1 == objs.length) {
            Object obj = objs[0];
            // Map 参数处理
            if (obj instanceof Map) {
                Map map = (Map) obj;
                if (CollectionX.isEmpty(map)) return template.replaceAll(replaceRegex, STRING_EMPTY);

                for (Object key : map.keySet())
                    template = template.replace(String.format(replaceText, key), get(map.get(key)));
                return template.replaceAll(replaceRegex, STRING_EMPTY);
            }
        }

        // 替换数字占位符, format("{0}, {1}, {3}", "a", "b", "c") 输出 "a, b, c" 注: Map不行, 转换后的{}, 最后会被替换掉
        Matcher matcher = RegeX.getMatcher(template, "\\{\\d+\\}");
        while (matcher.find()) {
            String match = matcher.group();
            String seq = get(find(match, "\\{", "\\}"));
            Integer idx = NumberX.getIntegerDefault(seq);
            if (null != idx && idx >= 0 && idx < objs.length) template = template.replaceAll(String.format("\\{%s\\}", idx), get(objs[idx]));
        }

        return template.replaceAll(replaceRegex, STRING_EMPTY);
    }

    /**
     * 获取字符集
     *
     * @param charset 字符集
     * @return 默认字符集 UTF-8, 不空则返回
     */
    public static String getCharset(String charset) {
        return get(charset, "UTF-8");
    }

    /**
     * URL 传递参数编码
     *
     * @param text 文本内容
     * @return 编码后的内容, 默认 UTF-8 字符集
     */
    public static String encode(String text) {
        return encode(text, null);
    }

    /**
     * URL 传递参数编码
     *
     * @param text    文本内容
     * @param charset 字符集, 默认 UTF-8
     * @return 编码后的内容
     */
    public static String encode(String text, String charset) {
        if (isEmpty(text)) return STRING_EMPTY;

        try {
            return URLEncoder.encode(get(text), getCharset(charset));
        } catch (Exception e) {
            return STRING_EMPTY;
        }
    }

    /**
     * URL 传递参数解码
     *
     * @param text 文本内容
     * @return 解码后的文本内容
     */
    public static String decode(String text) {
        return decode(text, null);
    }

    /**
     * URL 传递参数解码
     *
     * @param text    文本内容
     * @param charset 解码字符集, 默认 UTF-8
     * @return 解码后的文本内容
     */
    public static String decode(String text, String charset) {
        if (isEmpty(text)) return STRING_EMPTY;
        try {
            return URLDecoder.decode(text, getCharset(charset));
        } catch (Exception e) {
            return STRING_EMPTY;
        }
    }

    // ################################################## 其他处理方法 ##################################################

    /**
     * 获取特定格式中内容
     *
     * @param content 处理的文本内容
     * @return 符合特定格式的内容
     */
    public static String getCDATA(String content) {
        String result = get(content);
        Matcher matcher = RegeX.getMatcher(content, ".*<!\\[CDATA\\[(.*)\\]\\]>.*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        if (null == matcher) return result;
        if (matcher.matches()) result = matcher.group(1);
        return result;
    }

    /**
     * 获取 html 文本内容
     *
     * @param content 需要处理的文本内容
     * @return 返回去除 SQL 敏感字符, HTML 标签的文本信息
     */
    public static String getHtmlText(String content) {
        if (isEmpty(content)) return STRING_EMPTY;
        return removeHtmlTag(getHtmlDenoise(content));
    }

    /**
     * 转换 html 标签
     *
     * @param content 需要处理的内容
     * @return 把 html 标签前后的尖括号编码, 防止 html 注入
     */
    public static String getHtmlConvertTag(String content) {
        if (isEmpty(content)) return STRING_EMPTY;
        return get(content).replaceAll(HTML_LEFT_TAG, HTML_CONVERT_LEFT_TAG).replaceAll(HTML_RIGHT_TAG, HTML_CONVERT_RIGHT_TAG);
    }

    /**
     * html 文本去噪
     *
     * @param content 需要处理的文本内容
     * @return 去噪后的 html 文本, 去除 SQL 敏感字符, 转换 html 标签前后标志
     */
    public static String getHtmlDenoise(String content) {
        if (isEmpty(content)) return STRING_EMPTY;
        return removeSqlCharset(getHtmlConvertTag(content));
    }

    /**
     * 去除 SQL 敏感字符
     *
     * @param content 需要处理的内容
     * @return 去除 SQL 敏感字符内容
     */
    public static String removeSqlCharset(String content) {
        if (isEmpty(content)) return STRING_EMPTY;
        return get(content).replaceAll(".*([';]+|(--)+).*", STRING_EMPTY);
    }

    /**
     * 判断是否含有 SQL 敏感字符
     *
     * @param content 需要检测的内容
     * @return 是否含有 SQL 敏感字符
     */
    public static boolean hasSqlCharset(String content) {
        if (isEmpty(content)) return false;
        return !removeSqlCharset(content).equals(content);
    }

    /**
     * 删除内容中所有 html 标签
     *
     * @param content 需要处理的内容
     * @return 已删除所有 html 标签的内容信息
     */
    public static String removeHtmlTag(String content) {
        if (isEmpty(content)) return STRING_EMPTY;
        return content.replaceAll("<[^>]+>", STRING_EMPTY);
    }

    /**
     * 判断文本内, 是否含有中文字符
     *
     * @param content 需要验证的文本
     * @return 是否含有中文字符
     */
    public static boolean hasChinese(String content) {
        if (isEmpty(content)) return false;
        return RegeX.find(content, REGEX_CHINESE);
    }

    /**
     * 计算文本在数据库存储的长度
     *
     * @param content 需要验证的文本内容
     * @return 存储到数据库字符长度
     */
    public static int getDBContentLength(String content) {
        if (isEmpty(content)) return 0;
        if (!hasChinese(content)) return content.length(); // 没有中文字符, 直接返回文本长度
        return Arrays.stream(content.split(STRING_EMPTY)).map(letter -> letter.matches(REGEX_CHINESE) ? 3 : 1).reduce(0, Integer::sum);
    }


    public static void main(String[] args) {
        String a = "a";
        System.out.println("a = " + isEmpty(a));
    }
}
