package com.cover.common;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// 集合工具类
@SuppressWarnings({"unused", "unchecked", "BooleanMethodIsAlwaysInverted"})
public final class CollectionX {

    public static final String DEFAULT_SEPARATOR = ","; // 默认分隔符
    public static final String[] DEFAULT_STRING_ARRAY = new String[0]; // 空数组

    /**
     * 判断对象是否空集合
     *
     * @param obj 检查对象
     * @return 是否空集合
     */
    public static boolean isEmpty(Object obj) {
        if (null == obj) return true;
        if (obj instanceof Collection<?>) return isEmpty((Collection<?>) obj);
        if (obj instanceof Map<?, ?>) return isEmpty((Map<?, ?>) obj);
        if (obj instanceof Object[]) return isEmpty((Object[]) obj);
        if (obj instanceof boolean[]) return isEmpty((boolean[]) obj);
        if (obj instanceof byte[]) return isEmpty((byte[]) obj);
        if (obj instanceof char[]) return isEmpty((char[]) obj);
        if (obj instanceof double[]) return isEmpty((double[]) obj);
        if (obj instanceof float[]) return isEmpty((float[]) obj);
        if (obj instanceof int[]) return isEmpty((int[]) obj);
        if (obj instanceof long[]) return isEmpty((long[]) obj);
        if (obj instanceof short[]) return isEmpty((short[]) obj);
        return false;
    }

    /**
     * 判断是否非空集合
     *
     * @param obj 判断对象
     * @return 非空集合
     */
    public static boolean notEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * 是否存在一个或者多个空集合
     *
     * @param objs 判断集合对象
     * @return 有一个或者多个空集合
     */
    public static boolean isAnyEmpty(Object... objs) {
        if (isEmpty(objs)) return true;
        for (Object obj : objs) if (isEmpty(obj)) return true;
        return false;
    }

    /**
     * 全部都为空集合
     *
     * @param objs 判断集合对象
     * @return 全部都为空集合
     */
    public static boolean isAllEmpty(Object... objs) {
        if (isEmpty(objs)) return true;
        for (Object obj : objs) if (notEmpty(obj)) return false;
        return true;
    }

    /**
     * @param collection 检查集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.isEmpty();
    }

    /**
     * @param map 检查 Map 集合
     * @return 判断是否为空
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    /**
     * @param obj 检查 Array 集合
     * @return 判断是否为空
     */
    public static boolean isEmpty(Object[] obj) {
        return null == obj || 0 == obj.length;
    }

    /**
     * 基础数据类型数组校验是否为空
     *
     * @param arr 校验数据
     * @return 是否为空
     */
    public static boolean isEmpty(boolean[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(byte[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(char[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(double[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(float[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(int[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(long[] arr) {
        return null == arr || 0 == arr.length;
    }

    public static boolean isEmpty(short[] arr) {
        return null == arr || 0 == arr.length;
    }

    /**
     * 判断两个集合对象集合数是否相等
     *
     * @param a 集合
     * @param b 集合
     * @return a b 包含的几何数是否相等
     */
    public static boolean equalSize(Object a, Object b) {
        int aSize = 0;
        int bSize = 0;

        if (null != a) {
            if (!isCollectionType(a)) return false;
            if (a instanceof Collection<?>) aSize = ((Collection<?>) a).size();
            if (a instanceof Map) aSize = ((Map<?, ?>) a).size();
            if (a instanceof boolean[]) aSize = ((boolean[]) a).length;
            if (a instanceof byte[]) aSize = ((byte[]) a).length;
            if (a instanceof char[]) aSize = ((char[]) a).length;
            if (a instanceof double[]) aSize = ((double[]) a).length;
            if (a instanceof float[]) aSize = ((float[]) a).length;
            if (a instanceof int[]) aSize = ((int[]) a).length;
            if (a instanceof long[]) aSize = ((long[]) a).length;
            if (a instanceof short[]) aSize = ((short[]) a).length;
            if (a instanceof Object[]) aSize = ((Object[]) a).length;
        }

        if (null != b) {
            if (!isCollectionType(a)) return false;
            if (b instanceof Collection<?>) bSize = ((Collection<?>) b).size();
            if (b instanceof Map) bSize = ((Map<?, ?>) b).size();
            if (b instanceof boolean[]) bSize = ((boolean[]) b).length;
            if (b instanceof byte[]) bSize = ((byte[]) b).length;
            if (b instanceof char[]) bSize = ((char[]) b).length;
            if (b instanceof double[]) bSize = ((double[]) b).length;
            if (b instanceof float[]) bSize = ((float[]) b).length;
            if (b instanceof int[]) bSize = ((int[]) b).length;
            if (b instanceof long[]) bSize = ((long[]) b).length;
            if (b instanceof short[]) bSize = ((short[]) b).length;
            if (b instanceof Object[]) bSize = ((Object[]) b).length;
        }
        return StringX.equals(aSize, bSize);
    }

    /**
     * 判断是否集合对象, 或者 Map 对象
     *
     * @param obj 判断对象
     * @return true = 是集合对象或者 Map 对象
     */
    public static boolean isCollectionType(Object obj) {
        if (null == obj) return false;
        if (obj instanceof Collection<?>) return true;
        if (obj instanceof Map<?, ?>) return true;
        if (obj instanceof boolean[]) return true;
        if (obj instanceof byte[]) return true;
        if (obj instanceof char[]) return true;
        if (obj instanceof double[]) return true;
        if (obj instanceof float[]) return true;
        if (obj instanceof int[]) return true;
        if (obj instanceof long[]) return true;
        if (obj instanceof short[]) return true;
        return obj instanceof Object[];
    }

    // ################################################## 数组处理方法 ##################################################

    /**
     * 字符串切割成数组, 默认没有空值, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str 待分割字符
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static String[] parseArray(String str) {
        return parseArray(str, DEFAULT_SEPARATOR, true);
    }

    /**
     * 字符串切割成数组, 默认没有空值, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str       待分割字符
     * @param separator 分割符, 允许 "", null
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static String[] parseArray(String str, String separator) {
        return parseArray(str, separator, true);
    }

    /**
     * 字符串切割成数组, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str 待分割字符
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static String[] parseArrayOriginal(String str) {
        return parseArray(str, DEFAULT_SEPARATOR, false);
    }

    /**
     * 字符串切割成数组, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str       待分割字符
     * @param separator 分割符, 允许 "", null
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static String[] parseArrayOriginal(String str, String separator) {
        return parseArray(str, separator, false);
    }

    /**
     * 字符串切割成数组
     *
     * @param str         待分割字符
     * @param separator   分割符, 允许 "", null
     * @param filterEmpty 是否过滤空值
     * @return 分割后的数组, 默认 new String[0]
     */
    public static String[] parseArray(String str, String separator, boolean filterEmpty) {
        String text = StringX.get(str);
        if (StringX.isEmpty(text)) return DEFAULT_STRING_ARRAY;
        String symbol = StringX.get(separator); // 分割符号

        Stream<String> stream = Arrays.stream(text.split(symbol)).map(StringX::get);
        if (!filterEmpty) return stream.toArray(String[]::new);

        return stream.filter(StringX::notEmpty).toArray(String[]::new);
    }

    /**
     * @param obj 检查对象
     * @return 判断是否数组对象
     */
    public static boolean isArray(Object obj) {
        if (null == obj) return false;
        return obj.getClass().isArray();
    }


    // ################################################## Map 处理方法 ##################################################

    /**
     * 判断 Map Key 是否有不空的值
     *
     * @param map 处理 Map
     * @param key 对应的 Key
     * @return Map 的 Key 是否有非空的值
     */
    public static <K, V> boolean hasValue(Map<K, V> map, K key) {
        if (isEmpty(map) || null == key) return false;

        V value = map.get(key);
        if (value instanceof String) return StringX.notEmpty(value.toString());
        return null != value;
    }

    // 简单创建Map
    public static <K, V> Map<K, V> ofMap(K k1, V v1) {
        return convertToMap(k1, v1);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2) {
        return convertToMap(k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        return convertToMap(k1, v1, k2, v2, k3, v3);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    }

    public static <K, V> Map<K, V> ofMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return convertToMap(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    private static <K, V> Map<K, V> convertToMap(Object... input) {
        if ((input.length & 1) != 0) { // implicit nullcheck of input
            throw new InternalError("length is odd");
        }
        Map<K, V> map = new LinkedHashMap<>();
        for (int i = 0; i < input.length; i += 2) {
            K k = (K) input[i];
            V v = (V) input[i + 1];
            map.put(k, v);
        }
        return map;
    }

    // 简单添加Map k/v
    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1) {
        return putMapItem(original, k1, v1);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2) {
        return putMapItem(original, k1, v1, k2, v2);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9);
    }

    public static <K, V> Map<K, V> putMap(Map<K, V> original, K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6, K k7, V v7, K k8, V v8, K k9, V v9, K k10, V v10) {
        return putMapItem(original, k1, v1, k2, v2, k3, v3, k4, v4, k5, v5, k6, v6, k7, v7, k8, v8, k9, v9, k10, v10);
    }

    private static <K, V> Map<K, V> putMapItem(Map<K, V> original, Object... input) {
        if ((input.length & 1) != 0) { // implicit nullcheck of input
            throw new InternalError("length is odd");
        }

        Map<K, V> transformMap = original;
        if (null == transformMap) transformMap = new LinkedHashMap<>();

        for (int i = 0; i < input.length; i += 2) {
            K k = (K) input[i];
            V v = (V) input[i + 1];
            transformMap.put(k, v);
        }
        return transformMap;
    }


    /**
     * 删除 map 多个值
     *
     * @param original 原来的Map信息
     * @param keys     待删除的1个或者多个key
     * @param <K>      Map Key 类型
     * @param <V>      Map Value 类型
     * @return 删除后的 Map, 若 original 空则返回空Map
     */
    public static <K, V> Map<K, V> removeMapItem(Map<K, V> original, K... keys) {
        Map<K, V> transformMap = original;
        if (null == transformMap) transformMap = new LinkedHashMap<>();

        if (isEmpty(keys)) return transformMap;

        for (K key : keys) transformMap.remove(key);
        return transformMap;
    }

    /**
     * 强转 Map 类型
     *
     * @param original 原始的Map
     * @return Map key, value => String, 如果原始的 Map 是空, 也是返回空 Map
     */
    public static Map<String, String> parseMap(Map<?, ?> original) {
        Map<String, String> transformMap = new LinkedHashMap<>();
        if (isEmpty(original)) return transformMap;
        original.keySet().forEach(key -> transformMap.put(StringX.get(key), StringX.get(original.get(key))));
        return transformMap;
    }

    /**
     * 抽取原来的 Map, 某些 Keys 组成新的 Map
     *
     * @param map  处理的 Map
     * @param keys 需要组合的 Keys
     * @param <K>  Map Key 类型
     * @param <V>  Map Value 类型
     * @return 只带有 keys 的新 Map
     */
    public static <K, V> Map<K, V> filterMap(Map<K, V> map, K... keys) {
        Map<K, V> result = new HashMap<>();
        if (isEmpty(map) || isEmpty(keys)) return result;

        Arrays.stream(keys).filter(Objects::nonNull).forEach(key -> {
            if (map.containsKey(key)) result.put(key, map.get(key));
        });
        return result;
    }

    /**
     * 获取 Map 对应 Key 的值
     *
     * @param map 处理的 Map
     * @param key Map 里面的值
     * @param <K> Map Key 类型
     * @param <V> Map Value 类型
     * @return Key 对应的 Value
     */
    public static <K, V> V getValue(Map<K, V> map, K key) {
        return (isEmpty(map) || null == key) ? null : map.get(key);
    }


    // ################################################## Collection 处理方法 ##################################################

    /**
     * 字符串切割成数组, 默认没有空值, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str 待分割字符
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static List<String> parseList(String str) {
        return parseList(str, DEFAULT_SEPARATOR, true);
    }

    /**
     * 字符串切割成数组, 默认没有空值, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str       待分割字符
     * @param separator 分割符, 允许 "", null
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static List<String> parseList(String str, String separator) {
        return parseList(str, separator, true);
    }

    /**
     * 字符串切割成数组, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str 待分割字符
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static List<String> parseListOriginal(String str) {
        return parseList(str, DEFAULT_SEPARATOR, false);
    }

    /**
     * 字符串切割成数组, 且每项去掉左右空格
     * 默认使用 , 分割
     *
     * @param str       待分割字符
     * @param separator 分割符, 允许 "", null
     * @return 如果 str 为空, 则返回 new String[0]
     */
    public static List<String> parseListOriginal(String str, String separator) {
        return parseList(str, separator, false);
    }

    /**
     * 字符串切割成数组
     *
     * @param str         待分割字符
     * @param separator   分割符, 允许 "", null
     * @param filterEmpty 是否过滤空值
     * @return 分割后的数组, 默认 new String[0]
     */
    public static List<String> parseList(String str, String separator, boolean filterEmpty) {
        String text = StringX.get(str);
        if (StringX.isEmpty(text)) return new ArrayList<>();
        String symbol = StringX.get(separator); // 分割符号

        Stream<String> stream = Arrays.stream(text.split(symbol)).map(StringX::get);
        if (!filterEmpty) return stream.collect(Collectors.toList());

        return stream.filter(StringX::notEmpty).collect(Collectors.toList());
    }

    /**
     * 把 List Map 某一项的值获取, 重新封装成 List, 有可能有空的
     *
     * @param list 处理的 List
     * @param key  对应每个 Map 里面的 Key
     * @param <K>  Map Key 类型
     * @param <V>  Map Value 类型
     * @return 每项 Map Key 对应的 Value
     */
    public static <K, V> List<V> parseList(Collection<Map<K, V>> list, K key) {
        if (isEmpty(list) || null == key) return new ArrayList<>();
        return list.stream().map(item -> getValue(item, key)).collect(Collectors.toList());
    }

    /**
     * 把 List Map 某一项的值获取, 重新封装成 List, 返回不允许空值
     *
     * @param list 处理的 List
     * @param key  对应每个 Map 里面的 Key
     * @return 每项 Map Key 对应的 Value
     */
    public static <K, V> List<V> parseListNonNull(Collection<Map<K, V>> list, K key) {
        return parseList(list, key).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 把 List Map 某一项的值获取, 重新封装成 Set, 有可能有空的
     *
     * @param list 处理的 List
     * @param key  对应每个 Map 里面的 Key
     * @return 每项 Map Key 对应的 Value
     */
    public static <K, V> Set<V> parseSet(Collection<Map<K, V>> list, K key) {
        if (isEmpty(list) || null == key) return new HashSet<>();
        return list.stream().map(item -> getValue(item, key)).collect(Collectors.toSet());
    }

    /**
     * 把 List Map 某一项的值获取, 重新封装成 Set, 返回不允许空值
     *
     * @param list 处理的 List
     * @param key  对应每个 Map 里面的 Key
     * @return 每项 Map Key 对应的 Value
     */
    public static <K, V> Set<V> parseSetNonNull(Collection<Map<K, V>> list, K key) {
        return parseSet(list, key).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
