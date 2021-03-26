package com.cover.common;


import java.util.*;
import java.util.stream.Collectors;

// 集合工具类
@SuppressWarnings({"rawtypes", "unused", "unchecked"})
public final class CollectionUtils {

    // 私有化方法
    private CollectionUtils() {
    }

    /**
     * @param collection 检查集合
     * @return 是否为空
     */
    public static boolean isEmpty(Collection collection) {
        return null == collection || 0 == collection.size();
    }

    /**
     * @param obj 检查 Array 集合
     * @return 判断是否为空
     */
    public static boolean isEmpty(Object[] obj) {
        return null == obj || 0 == obj.length;
    }

    /**
     * @param map 检查 Map 集合
     * @return 判断是否为空
     */
    public static boolean isEmpty(Map map) {
        return null == map || 0 == map.size();
    }

    /**
     * @param obj 检查对象
     * @return 判断是否数组对象
     */
    public static boolean isArray(Object obj) {
        if (null == obj) return false;
        return obj.getClass().isArray();
    }

    /**
     * 判断 Map Key 是否有不空的值
     *
     * @param map 处理 Map
     * @param key 对应的 Key
     * @return Map 的 Key 是否有非空的值
     */
    public static boolean hasValue(Map map, Object key) {
        if (isEmpty(map) || null == key) return false;
        Object value = map.get(key);
        return null != value && !StringUtils.isEmpty(StringUtils.get(value));
    }

    /**
     * 将参数变为 Map
     *
     * @param kv Key - Value, 必须一对出现
     * @return Map
     */
    public static Map ofMap(Object... kv) {
        Map map = new LinkedHashMap<>();
        if (isEmpty(kv)) return map;
        if (0 != kv.length % 2) return map;

        Object prevKey = null;
        for (Object item : kv) {
            if (null == prevKey) {
                prevKey = item;
                continue;
            }

            map.put(prevKey, item);
            prevKey = null;
        }
        return map;
    }

    /**
     * 抽取原来的 Map, 某些 Keys 组成新的 Map
     *
     * @param map  处理的 Map
     * @param keys 需要组合的 Keys
     * @return 只带有 keys 的新 Map
     */
    public static Map filterMap(Map map, Object... keys) {
        Map result = new HashMap();
        if (isEmpty(map)) return result;
        if (isEmpty(keys)) return result;

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
     * @return Key 对应的 Value
     */
    public static Object getValue(Map map, Object key) {
        if (isEmpty(map) || null == key) return null;
        return map.get(key);
    }

    /**
     * 把 List Map 某一项的值获取, 重新封装成 List, 有可能有空的
     *
     * @param list 处理的 List
     * @param key  对应每个 Map 里面的 Key
     * @return 每项 Map Key 对应的 Value
     */
    public static List<Object> parseList(List<Map> list, Object key) {
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
    public static List<Object> parseListNonNull(List<Map> list, Object key) {
        return parseList(list, key).stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 把 List Map 某一项的值获取, 重新封装成 Set, 有可能有空的
     *
     * @param list 处理的 List
     * @param key  对应每个 Map 里面的 Key
     * @return 每项 Map Key 对应的 Value
     */
    public static Set<Object> parseSet(List<Map> list, Object key) {
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
    public static Set<Object> parseSetNonNull(List<Map> list, Object key) {
        return parseSet(list, key).stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }
}
