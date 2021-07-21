package com.cover.common;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 操作对象工具类
 */
@SuppressWarnings("unused")
public final class ObjectX {

    /**
     * 默认Java基础类型
     */
    public static final List<String> DEFAULT_JAVA_CLASS_TYPE =
            Arrays.asList("boolean", "boolean[]", "byte", "byte[]", "char", "char[]", "double", "double[]",
                    "float", "float[]", "int", "int[]", "long", "long[]", "short", "short[]");

    /**
     * 将 Map 转对象
     * 如果 Map 是空或者Null, 也返回一个空对象
     *
     * @param map      处理的Map
     * @param benClass 转换的对象类型
     * @param <T>      响应对象类型
     * @return 转换类型
     */
    public static <T> T mapToObject(Map<?, ?> map, Class<T> benClass) {
        try {
            T bean = benClass.newInstance();
            if (CollectionX.isEmpty(map)) return bean;

            Field[] fields = bean.getClass().getDeclaredFields();
            for (Field field : fields) {
                int mod = field.getModifiers();
                // 过滤
                if (Modifier.isStatic(mod) || Modifier.isFinal(mod) || Modifier.isAbstract(mod)) continue;

                field.setAccessible(true); // 设置权限
                field.set(bean, map.get(field.getName())); // 赋值
            }
            return bean;
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将对象转 Map
     * 如果对象是空的, 也会返回空map
     *
     * @param obj 处理对象
     * @return 转换后的 Map
     */
    public static Map<String, Object> objectToMap(Object obj) {
        try {
            Map<String, Object> map = new LinkedHashMap<>();
            if (null == obj) return map;

            Field[] declaredFields = obj.getClass().getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                map.put(field.getName(), field.get(obj));
            }
            return map;
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * 判断是否 Java 类型
     *
     * @param t 判断对象
     * @return 是否Java类型
     */
    public static boolean isJavaClassType(Object t) {
        return isJavaClassType(t.getClass().getCanonicalName());
    }

    /**
     * 判断是否 Java 类型
     *
     * @param field 属性名
     * @return 是否Java类型
     */
    public static boolean isJavaClassType(Field field) {
        if (null == field) return false;
        return isJavaClassType(field.getType().getCanonicalName());
    }

    /**
     * 判断是否 Java 类型
     *
     * @param canonicalName 属性全名
     * @return 是否Java类型
     */
    public static boolean isJavaClassType(String canonicalName) {
        if (StringX.isEmpty(canonicalName)) return false;
        List<String> matchers = DEFAULT_JAVA_CLASS_TYPE.stream().filter(cl -> cl.equals(canonicalName)).collect(Collectors.toList());
        if (CollectionX.notEmpty(matchers)) return true;
        return canonicalName.startsWith("java.") || canonicalName.startsWith("javax.");
    }


    /**
     * 将对象转 String
     *
     * @param t   处理对象
     * @param <T> 对象类型
     * @return 转换后的字符串
     */
    public static <T> String toString(T t) {
        // 空对象处理
        if (null == t) return Const.STRING_EMPTY_VALUE;

        // Map 类型处理
        if (t instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) t;
            String mapString = concatString(map, "^, ", k -> String.format(", %s=%s", toString(k), toString(map.get(k))));
            return String.format("{%s}", mapString);
        }

        // 集合类型处理
        if (t instanceof Collection<?>) {
            Collection<?> list = (Collection<?>) t;
            String mapString = concatString(list, "^, ", k -> String.format(", %s", toString(k)));
            return String.format("[%s]", mapString);
        }

        // 数组类型
        if (t instanceof boolean[]) return Arrays.toString((boolean[]) t);
        if (t instanceof byte[]) return Arrays.toString((byte[]) t);
        if (t instanceof char[]) return Arrays.toString((char[]) t);
        if (t instanceof double[]) return Arrays.toString((double[]) t);
        if (t instanceof float[]) return Arrays.toString((float[]) t);
        if (t instanceof int[]) return Arrays.toString((int[]) t);
        if (t instanceof long[]) return Arrays.toString((long[]) t);
        if (t instanceof short[]) return Arrays.toString((short[]) t);
        // 对象数组类型
        if (t instanceof Object[]) return Arrays.deepToString((Object[]) t);

        // 其他情况
        return t.toString();
    }

    /**
     * 将 Map 拼接成字符串
     * concatString(map, "^, ", k -> String.format(", %s=%s", k, map.get(k))) => a=1, b=2
     *
     * @param map           处理的Map
     * @param replaceString 替换末尾的字符
     * @param fun           每项替换的函数
     * @param <K>           Map K
     * @param <V>           Map V
     * @return 拼接后的字符串
     */
    public static <K, V> String concatString(Map<K, V> map, String replaceString, Function<K, String> fun) {
        if (CollectionX.isEmpty(map)) return Const.STRING_EMPTY_VALUE;
        return map.keySet().stream().filter(Objects::nonNull).map(fun).reduce(String::concat).orElse(Const.STRING_EMPTY_VALUE).replaceAll(replaceString, Const.STRING_EMPTY_VALUE);
    }

    /**
     * 将 List 拼接成字符串
     *
     * @param list          处理的 List
     * @param replaceString 替换末尾的字符
     * @param fun           每项替换的函数
     * @param <V>           Collection V
     * @return 拼接后的字符串
     */
    public static <V> String concatString(Collection<V> list, String replaceString, Function<V, String> fun) {
        if (CollectionX.isEmpty(list)) return Const.STRING_EMPTY_VALUE;
        return list.stream().filter(Objects::nonNull).map(fun).reduce(String::concat).orElse(Const.STRING_EMPTY_VALUE).replaceAll(replaceString, Const.STRING_EMPTY_VALUE);
    }
}
