package com.cover.common;

/**
 * 系统常量配置
 */
@SuppressWarnings("unused")
public interface Const {

    // 默认字符串
    String STRING_EMPTY_VALUE = "";

    // 请求体格式
    String CONTENT_TYPE_VALUE = "Content-Type";
    String APPLICATION_JSON_VALUE = "application/json";
    String APPLICATION_JSON_UTF8_VALUE = "application/json;charset=UTF-8";

    // 字符集
    String UTF_8 = "UTF-8";

    // 正则表达式
    String REGEX_URL = "((https?|ftp|file)://[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|])";
    String REGEX_HTML_TAG = "(<[^>]+>)";
    String REGEX_MOBILE = "(1(\\d{9,10}))";
    String REGEX_PHONE = "(1(\\d{9,10}))$|^((\\d{3,4}\\-)?\\d{7,8}(\\-\\d{2,6})?)";
    String REGEX_FIXED = "(\\d{8,15})";
    String REGEX_EMAIL = "([a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+)";
    String REGEX_CHINESE_NAME = "([\\u4e00-\\u9fa5]{2,8})";
    String REGEX_ENGLISH_NAME = "([a-zA-Z]{1}([a-zA-Z]|[._]){0,29})";
    String REGEX_QQ = "([1-9]\\d{4,10})";
    String REGEX_WECHAT = "([a-zA-Z]{1}[a-zA-Z\\d_]{4,25})";
    String REGEX_POSTCODE = "(\\d{6})";
    String REGEX_PRICE = "(\\d+(.\\d{1,2})?)";
    String REGEX_NUMBER = "(\\d+)";

}
