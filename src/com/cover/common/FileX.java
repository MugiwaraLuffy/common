package com.cover.common;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// IO 工具类
// 节流没有缓冲区, 是直接输出的, 而字符流是输出到缓冲区的
// 因此在输出时, 字节流不调用colse()方法时, 信息已经输出了, 而字符流只有在调用close()方法关闭缓冲区时, 信息才输出
// 要想字符流在未关闭时输出信息, 则需要手动调用flush()方法
// 只要是处理纯文本数据, 就优先考虑使用字符流. 除此之外都使用字节流
@SuppressWarnings({"UnusedReturnValue", "unchecked", "unused"})
public final class FileX {

    public static final long ONE_KB = 1024;
    public static final long ONE_MB = ONE_KB * ONE_KB;
    public static final long ONE_GB = ONE_KB * ONE_MB;
    public static final long ONE_TB = ONE_KB * ONE_GB;
    public static final long ONE_PB = ONE_KB * ONE_TB;
    public static final long ONE_EB = ONE_KB * ONE_PB;


    public static final int BUFFER_SIZE = 4096;
    private static final byte[] EMPTY_CONTENT = new byte[0];

    public static final int EOF = -1;
    public static final char DIR_SEPARATOR_UNIX = '/';
    public static final char DIR_SEPARATOR_WINDOWS = '\\';
    public static final char DIR_SEPARATOR = File.separatorChar;
    public static final String LINE_SEPARATOR_UNIX = "\n";
    public static final String LINE_SEPARATOR_WINDOWS = "\r\n";
    public static final String DEFAULT_CHARSET = "UTF-8";

    // 系统路径 ####################################################################################################

    // Java IO 临时路径
    public static String getTempDirectoryPath() {
        return System.getProperty("java.io.tmpdir");
    }

    // Java IO 临时路径 文件对象
    public static File getTempDirectory() {
        return new File(getTempDirectoryPath());
    }

    // 系统用户目录路径
    public static String getUserDirectoryPath() {
        return System.getProperty("user.home");
    }

    // 系统用户目录路径 文件对象
    public static File getUserDirectory() {
        return new File(getUserDirectoryPath());
    }

    // 检查方法 ####################################################################################################


    // 文件操作方法 #################################################################################################

    // 创建文件
    public static File createFile(String path) throws IOException {
        if (StringX.isEmpty(path)) throw new IllegalArgumentException("No path specified");
        File file = new File(path); // 目标文件
        return createFile(file);
    }

    public static File createFile(File file) throws IOException {
        if (file.exists()) {
            if (file.isDirectory()) throw new IOException(String.format("%s exists, but is directory", file)); // 不创建文件夹
            return file;
        }

        createDirectory(file.getParentFile()); // 创建父文件夹
        boolean success = file.createNewFile();
        if (!success) throw new IOException(String.format("%s creation failed", file)); // 可能有权限文件, 创建文件失败
        if (file.isDirectory()) throw new IOException(String.format("%s exists, but is directory", file)); // 不创建文件夹
        return file;
    }

    // 创建父目录
    public static void createDirectory(String directoryPath) throws IOException {
        if (StringX.isEmpty(directoryPath)) throw new IllegalArgumentException("No directoryPath specified");
        createDirectory(new File(directoryPath));
    }

    public static void createDirectory(File directory) throws IOException {
        if (null == directory) return;
        if (directory.exists()) {
            if (!directory.isDirectory()) throw new IOException(String.format("File %s exists and is not a directory. Unable to create directory", directory));
        } else {
            if (!directory.mkdirs()) {
                if (!directory.isDirectory()) throw new IOException(String.format("Unable to create directory %s", directory));
            }
        }
    }

    // 写入 [byte[], String, Object, Stream, Properties]

    /**
     * 把 byte 重新写到到文件
     *
     * @param data       写入文件数据
     * @param targetPath 目标文件路径
     * @return 写入长度
     * @throws IOException IO异常
     */
    public static int writeByte(byte[] data, String targetPath) throws IOException {
        return writeByte(data, targetPath, false);
    }

    /**
     * 把 byte 写到到文件, append = true 后面追加
     *
     * @param data       写入文件数据
     * @param targetPath 目标文件路径
     * @param append     是否追加到最后
     * @return 写入长度
     * @throws IOException IO异常
     */
    public static int writeByte(byte[] data, String targetPath, boolean append) throws IOException {
        File target = createFile(targetPath);
        FileOutputStream out = null;
        try {
            out = openFileOutputStream(target, append);
            return wirteByteArrayToStream(data, out);
        } finally {
            closeQuietly(out);
        }
    }

    /**
     * 将字符串写到流
     *
     * @param content    字符串内容
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void writeString(String content, String targetPath) throws IOException {
        writeString(content, DEFAULT_CHARSET, targetPath, false);
    }

    /**
     * 将字符串写到流
     *
     * @param content    字符串内容
     * @param targetPath 目标文件路径
     * @param append     是否追加到最后
     * @throws IOException IO异常
     */
    public static void writeString(String content, String targetPath, boolean append) throws IOException {
        writeString(content, DEFAULT_CHARSET, targetPath, append);
    }

    /**
     * 将字符串写到流
     *
     * @param content    字符串内容
     * @param charset    输出的字符集
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void writeString(String content, String charset, String targetPath) throws IOException {
        writeString(content, charset, targetPath, false);
    }

    /**
     * 将字符串写到流
     *
     * @param content    字符串内容
     * @param charset    输出的字符集
     * @param targetPath 目标文件路径
     * @param append     是否追加到最后
     * @throws IOException IO异常
     */
    public static void writeString(String content, String charset, String targetPath, boolean append) throws IOException {
        if (StringX.isEmpty(content)) throw new IllegalArgumentException("No content specified");
        if (StringX.isEmpty(charset)) throw new IllegalArgumentException("No charset specified");
        if (StringX.isEmpty(targetPath)) throw new IllegalArgumentException("No targetPath specified");

        FileOutputStream out = null;
        try {
            out = openFileOutputStream(targetPath, append);
            wirteStringToStream(content, charset, out);
        } finally {
            closeQuietly(out);
        }
    }

    /**
     * 本地序列化对象
     * 保存到 java.io.tmpdir 目录下, 文件名使用对象名称
     *
     * @param object 处理对象
     * @return 保存后地址
     * @throws IOException IO异常
     */
    public static String writeObject(Object object) throws IOException {
        return writeObject(object, null);
    }

    /**
     * 本地序列化对象
     * 对象必须实现 Serializable 接口
     *
     * @param object     处理对象
     * @param targetPath 目标文件路径
     * @return 保存后地址
     * @throws IOException IO异常
     */
    public static String writeObject(Object object, String targetPath) throws IOException {
        if (null == object) throw new IllegalArgumentException("No object specified");

        String filename = object.getClass().getSimpleName();
        String filePath = StringX.isEmpty(targetPath) ? String.format("%s%s", getTempDirectoryPath(), filename) : targetPath;

        FileOutputStream out = null;
        ObjectOutputStream oos = null;
        try {
            out = openFileOutputStream(filePath);
            oos = new ObjectOutputStream(out); // 包装流
            oos.writeObject(object); // 写入数据
            return filePath;
        } finally {
            closeQuietly(oos, out);
        }
    }

    /**
     * 将流保存到本地文件
     *
     * @param in         待处理的流数据
     * @param targetPath 目标文件路径
     * @return 写入长度
     * @throws IOException IO异常
     */
    public static int writeStream(InputStream in, String targetPath) throws IOException {
        return writeStream(in, targetPath, false);
    }

    /**
     * 将流保存到本地文件
     *
     * @param in         待处理的流数据
     * @param targetPath 目标文件路径
     * @param append     是否追加到最后
     * @return 写入长度
     * @throws IOException IO异常
     */
    public static int writeStream(InputStream in, String targetPath, boolean append) throws IOException {
        if (null == in) throw new IllegalArgumentException("No InputStream specified");
        if (StringX.isEmpty(targetPath)) throw new IllegalArgumentException("No targetPath specified");

        FileOutputStream out = null;
        try {
            out = openFileOutputStream(targetPath, append);
            return wirteStreamToStream(in, out);
        } finally {
            closeQuietly(in, out);
        }
    }

    /**
     * 把配置序列化到本地文件
     *
     * @param properties 配置信息
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void writeProperties(Properties properties, String targetPath) throws IOException {
        if (null == properties) throw new IllegalArgumentException("No properties specified");
        if (StringX.isEmpty(targetPath)) throw new IllegalArgumentException("No targetPath specified");

        FileOutputStream out = null;
        OutputStreamWriter writer = null;
        BufferedWriter br = null;
        try {
            out = openFileOutputStream(targetPath);
            writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
            br = new BufferedWriter(writer);
            properties.store(out, null);
        } finally {
            closeQuietly(br, writer, out);
        }
    }

    // 读取 [byte[], String, String Lines, Object, Properties]

    /**
     * 将文件读成字节流
     *
     * @param sourcePath 源文件地址
     * @return 字节流
     * @throws IOException IO异常
     */
    public static byte[] readByte(String sourcePath) throws IOException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");
        FileInputStream in = null;
        try {
            in = openFileInputStream(sourcePath);
            return readStreamToByteArray(in);
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * 读取文件内容转成String, 默认行分隔符 \r\n
     *
     * @param sourcePath 源文件地址
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static String readString(String sourcePath) throws IOException {
        return readString(sourcePath, DEFAULT_CHARSET, null);
    }

    /**
     * 读取文件内容转成String, 默认行分隔符 \r\n
     *
     * @param sourcePath 源文件地址
     * @param filter     过滤器
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static String readString(String sourcePath, Predicate<String> filter) throws IOException {
        return readString(sourcePath, DEFAULT_CHARSET, filter);
    }

    /**
     * 读取文件内容转成String, 默认行分隔符 \r\n
     *
     * @param sourcePath 源文件地址
     * @param charset    读取字符集
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static String readString(String sourcePath, String charset) throws IOException {
        return readString(sourcePath, charset, null);
    }

    /**
     * 读取文件内容转成String, 默认行分隔符 \r\n
     *
     * @param sourcePath 源文件地址
     * @param charset    读取字符集
     * @param filter     过滤器
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static String readString(String sourcePath, String charset, Predicate<String> filter) throws IOException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");

        FileInputStream in = null;
        try {
            in = openFileInputStream(sourcePath);
            StringBuilder buffer = new StringBuilder();
            readStreamToString(in, charset, filter, line -> buffer.append(line).append(LINE_SEPARATOR_WINDOWS));
            return buffer.toString();
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * 读取文件内容转成List String, 每一行一条
     *
     * @param sourcePath 源文件地址
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static List<String> readStringLines(String sourcePath) throws IOException {
        return readStringLines(sourcePath, DEFAULT_CHARSET, null);
    }

    /**
     * 读取文件内容转成List String, 每一行一条
     *
     * @param sourcePath 源文件地址
     * @param filter     过滤器
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static List<String> readStringLines(String sourcePath, Predicate<String> filter) throws IOException {
        return readStringLines(sourcePath, DEFAULT_CHARSET, filter);
    }

    /**
     * 读取文件内容转成String, 默认行分隔符 \r\n
     *
     * @param sourcePath 源文件地址
     * @param charset    读取字符集
     * @return 文本内容
     * @throws IOException IO异常
     */
    public static List<String> readStringLines(String sourcePath, String charset) throws IOException {
        return readStringLines(sourcePath, charset, null);
    }

    /**
     * 读取文件内容转成List String, 每一行一条
     *
     * @param sourcePath 源文件地址
     * @param charset    读取字符集
     * @param filter     过滤器
     * @return 文本行
     * @throws IOException IO异常
     */
    public static List<String> readStringLines(String sourcePath, String charset, Predicate<String> filter) throws IOException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");

        FileInputStream in = null;
        try {
            in = openFileInputStream(sourcePath);
            List<String> lines = new ArrayList<>();
            readStreamToString(in, charset, filter, lines::add);
            return lines;
        } finally {
            closeQuietly(in);
        }
    }

    /**
     * 读取本地序列化文件转换成对象
     *
     * @param sourcePath 源文件地址
     * @param clazz      转换类型
     * @param <T>        返回对象类型
     * @return 返回对象
     * @throws IOException            IO异常
     * @throws ClassNotFoundException 类型找不到异常
     */
    public static <T> T readObject(String sourcePath, Class<T> clazz) throws IOException, ClassNotFoundException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");
        if (null == clazz) throw new IllegalArgumentException("No clazz specified");

        FileInputStream in = null;
        ObjectInputStream oin = null;
        try {
            in = openFileInputStream(sourcePath);
            oin = new ObjectInputStream(in);
            return (T) oin.readObject();
        } finally {
            closeQuietly(oin, in);
        }
    }

    /**
     * 读取本地文件转换配置信息
     *
     * @param sourcePath 源文件地址
     * @return 配置信息
     * @throws IOException IO异常
     */
    public static Properties readProperties(String sourcePath) throws IOException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");

        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader br = null;
        try {
            in = openFileInputStream(sourcePath);
            reader = new InputStreamReader(in, StandardCharsets.UTF_8);
            br = new BufferedReader(reader);
            Properties properties = new Properties();
            properties.load(br);

            return properties;
        } finally {
            closeQuietly(br, reader, in);
        }
    }

    // 复制 / 文件 / 目录

    /**
     * 传统复制方式
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void copy(String sourcePath, String targetPath) throws IOException {
        _copy(sourcePath, targetPath, 1);
    }

    /**
     * NIO 复制方式
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void copyByNIO(String sourcePath, String targetPath) throws IOException {
        _copy(sourcePath, targetPath, 2);
    }

    /**
     * FileChannel 复制方式
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径
     * @throws IOException IO异常
     */
    public static void copyByChannel(String sourcePath, String targetPath) throws IOException {
        _copy(sourcePath, targetPath, 3);
    }

    /**
     * 复制文件核心方法
     *
     * @param sourcePath 源文件路径
     * @param targetPath 目标文件路径 [不覆盖]
     * @param type       1 默认复制方式, 2 nio复制方式, 3 fileChannel 复制方式
     * @throws IOException IO异常
     */
    private static void _copy(String sourcePath, String targetPath, int type) throws IOException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");
        if (StringX.isEmpty(targetPath)) throw new IllegalArgumentException("No targetPath specified");

        File source = new File(sourcePath);
        if (!source.exists()) throw new IOException(String.format("%s does not exists", source)); // 文件不存在
        if (!source.isFile()) throw new IOException(String.format("%s is not a file", source)); // 不是文件

        File target = new File(targetPath);
        if (target.exists()) throw new IOException(String.format("%s already exists", target)); // 目标文件已存在

        FileInputStream in = null;
        FileOutputStream out = null;
        try {
            in = openFileInputStream(source);
            out = openFileOutputStream(target);
            if (1 == type) wirteStreamToStream(in, out);
            else if (2 == type) wirteStreamToStreamByNIO(in, out);
            else if (3 == type) wirteStreamToStreamByChannel(in, out);
            else throw new IOException(String.format("Unknow %d type parameter", type));
        } finally {
            closeQuietly(in, out);
        }
    }


    // 移动, 重命名 / 文件 / 目录
    public static boolean moveFile(String sourcePath, String targetPath) throws IOException {
        if (StringX.isEmpty(sourcePath)) throw new IllegalArgumentException("No sourcePath specified");
        if (StringX.isEmpty(targetPath)) throw new IllegalArgumentException("No targetPath specified");

        File source = new File(sourcePath);
        if (!source.exists()) throw new IOException(String.format("%s does not exists", source)); // 文件不存在
        if (!source.isFile()) throw new IOException(String.format("%s is not a file", source)); // 不是文件

        File target = new File(targetPath);
        if (target.exists()) throw new IOException(String.format("%s already exists", target)); // 目标文件已存在
        createDirectory(target.getParentFile()); // 创建目录
        return source.renameTo(target);
    }

    // 删除 / 文件 / 目录

    /**
     * 根据路径, 自适应删除文件/文件夹
     *
     * @param filePath 待删除文件/文件夹
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    public static boolean delete(String filePath) throws IOException {
        if (StringX.isEmpty(filePath)) throw new IllegalArgumentException("No filePath specified");

        File file = new File(filePath);
        if (!file.exists()) throw new IOException(String.format("%s does not exists", file)); // 文件不存在

        if (file.isFile()) return deleteFile(file); // 删除文件
        return deleteDirectory(file); // 删除目录
    }

    /**
     * 根据路径,删除文件
     *
     * @param filePath 待删除文件路径 [绝对路径]
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    public static boolean deleteFile(String filePath) throws IOException {
        if (StringX.isEmpty(filePath)) throw new IllegalArgumentException("No filePath specified");
        return deleteFile(new File(filePath));
    }

    /**
     * 根据文件对象, 删除文件
     *
     * @param file 待删除文件
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    public static boolean deleteFile(File file) throws IOException {
        if (!file.exists()) throw new IOException(String.format("%s does not exists", file)); // 文件不存在
        if (!file.isFile()) throw new IOException(String.format("%s is not a file", file)); // 不是文件
        return file.delete();
    }

    /**
     * 根据路径,删除文件
     * 必须把文件夹所有文件删除后才可以删除文件夹
     *
     * @param directoryPath 待删除文件夹目录 [绝对路径]
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    public static boolean deleteDirectory(String directoryPath) throws IOException {
        if (StringX.isEmpty(directoryPath)) throw new IllegalArgumentException("No directoryPath specified");
        return deleteDirectory(new File(directoryPath));
    }

    /**
     * 根据文件对象, 删除文件
     * 必须把文件夹所有文件删除后才可以删除文件夹
     *
     * @param file 待删除文件夹
     * @return 是否删除成功
     * @throws IOException IO异常
     */
    public static boolean deleteDirectory(File file) throws IOException {
        if (!file.exists()) throw new IOException(String.format("%s does not exists", file)); // 文件不存在
        if (!file.isDirectory()) throw new IOException(String.format("%s is not a directory", file)); // 不是文件夹

        File[] files = file.listFiles();
        if (!CollectionX.isEmpty(files)) {
            boolean result;
            for (File child : files) {
                if (child.isDirectory()) {
                    result = deleteDirectory(child);
                } else {
                    result = child.delete();
                }

                if (!result) throw new IOException(String.format("%s Delete failed", child));
            }
        }
        return file.delete();
    }

    // 按名称升序
    public static Comparator<File> sortByName = (file1, file2) -> file1.getName().compareToIgnoreCase(file2.getName());
    // 按大小升序
    public static Comparator<File> sortBySize = (file1, file2) -> {
        long d = sizeOf(file1) - sizeOf(file2);
        if (0 == d) return 0;
        return d > 0 ? 1 : -1;
    };

    // 按最后修改时间升序
    public static Comparator<File> sortByModified = Comparator.comparing(file -> new Date(file.lastModified()));

    /**
     * 找出文件夹所有文件, 可过滤
     *
     * @param directoryPath 文件夹目录, 也可以是文件
     * @param filter        文件过滤器
     * @return 处理后的文件列表
     * @throws IOException IO异常
     */
    public static List<File> list(String directoryPath, Predicate<File> filter) throws IOException {
        return list(directoryPath, filter, null);
    }

    /**
     * 找出文件夹所有文件, 可排序
     *
     * @param directoryPath 文件夹目录, 也可以是文件
     * @param sort          文件排序器
     * @return 处理后的文件列表
     * @throws IOException IO异常
     */
    public static List<File> list(String directoryPath, Comparator<? super File> sort) throws IOException {
        return list(directoryPath, null, sort);
    }

    /**
     * 找出文件夹所有文件, 可过滤/排序
     *
     * @param directoryPath 文件夹目录, 也可以是文件
     * @param filter        文件过滤器
     * @param sort          文件排序器
     * @return 处理后的文件列表
     * @throws IOException IO异常
     */
    public static List<File> list(String directoryPath, Predicate<File> filter, Comparator<? super File> sort) throws IOException {
        if (StringX.isEmpty(directoryPath)) throw new IllegalArgumentException("No directoryPath specified");

        File directory = new File(directoryPath);
        if (!directory.exists()) throw new IOException(String.format("%s does not exists", directory)); // 文件不存在

        List<File> list = new ArrayList<>(); // 响应文件
        if (directory.isDirectory()) { // 文件夹处理
            File[] files = directory.listFiles();
            if (CollectionX.isEmpty(files)) throw new IOException(String.format("%s No files were found", directory));
            list = Arrays.asList(files);
        }
        if (directory.isFile()) list.add(directory); // 文件处理
        if (CollectionX.isEmpty(list)) throw new IOException(String.format("%s No files were found", directory));

        // 过滤文件
        if (null != filter) list = list.stream().filter(filter).collect(Collectors.toList());

        // 排序
        if (null != sort) list = list.stream().sorted(sort).collect(Collectors.toList());

        return list;
    }

    /**
     * 计算文件/文件夹大小
     *
     * @param filePath 文件/文件夹 路径
     * @return length 大小, 可能会跟系统计算有差异
     */
    public static long sizeOf(String filePath) {
        if (StringX.isEmpty(filePath)) return 0L;
        return sizeOf(new File(filePath));
    }


    /**
     * 计算文件/文件夹大小
     *
     * @param file 文件/文件夹
     * @return length 大小, 可能会跟系统计算有差异
     */
    public static long sizeOf(File file) {
        if (!file.exists()) return 0L;

        // 目录大小
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (CollectionX.isEmpty(files)) return 0L;

            long size = 0;
            for (File f : files) size += sizeOf(f);
            return size;
        }

        // 文件大小
        return file.length();
    }

    // 计算文件大小
    public static String getDisplaySize(File file) {
        long size = sizeOf(file);
        return getDisplaySize(size);
    }

    public static String getDisplaySize(String filePath) {
        if (StringX.isEmpty(filePath)) return "0 bytes";
        long size = sizeOf(filePath);
        return getDisplaySize(size);
    }

    public static String getDisplaySize(long size) {
        if (size / ONE_EB > 0) return String.format("%.2f EB", (1.0 * size / ONE_EB));
        if (size / ONE_PB > 0) return String.format("%.2f PB", (1.0 * size / ONE_PB));
        if (size / ONE_TB > 0) return String.format("%.2f TB", (1.0 * size / ONE_TB));
        if (size / ONE_GB > 0) return String.format("%.2f GB", (1.0 * size / ONE_GB));
        if (size / ONE_MB > 0) return String.format("%.2f MB", (1.0 * size / ONE_MB));
        if (size / ONE_KB > 0) return String.format("%.2f KB", (1.0 * size / ONE_KB));
        return String.format("%s bytes", size);
    }

    // IO操作方法 ##################################################################################################

    public static FileInputStream openFileInputStream(String filePath) throws IOException {
        if (StringX.isEmpty(filePath)) throw new IllegalArgumentException("No filePath specified");
        return openFileInputStream(new File(filePath));
    }

    public static FileInputStream openFileInputStream(File file) throws IOException {
        if (!file.exists()) throw new IOException(String.format("%s does not exists", file)); // 文件不存在
        if (file.isDirectory()) throw new IOException(String.format("%s is not a file", file)); // 不是文件
        if (!file.canRead()) throw new IOException(String.format("File '%s' cannot be read", file)); // 不能读
        return new FileInputStream(file);
    }

    // 获取输出流, 包括创建文件
    public static FileOutputStream openFileOutputStream(String filePath) throws IOException {
        return openFileOutputStream(filePath, false);
    }

    public static FileOutputStream openFileOutputStream(String filePath, boolean append) throws IOException {
        if (StringX.isEmpty(filePath)) throw new IllegalArgumentException("No filePath specified");
        return openFileOutputStream(new File(filePath), append);
    }

    public static FileOutputStream openFileOutputStream(File file) throws IOException {
        return openFileOutputStream(file, false);
    }

    public static FileOutputStream openFileOutputStream(File file, boolean append) throws IOException {
        File targetFile = createFile(file);
        if (!targetFile.canWrite()) throw new IOException(String.format("File '%s' cannot be written", file)); // 不能写
        return new FileOutputStream(targetFile, append);
    }

    // 将 byte 写入到 Stream
    public static int wirteByteArrayToStream(byte[] in, OutputStream out) throws IOException {
        if (CollectionX.isEmpty(in)) throw new IllegalArgumentException("No input byte array specified");
        if (null == out) throw new IllegalArgumentException("No OutputStream specified");
        out.write(in);
        return in.length;
    }

    // 将 String 写入到 Stream
    public static void wirteStringToStream(String in, String charset, OutputStream out) throws IOException {
        if (StringX.isEmpty(in)) throw new IllegalArgumentException("No input String specified");
        if (StringX.isEmpty(charset)) throw new IllegalArgumentException("No Charset specified");
        if (null == out) throw new IllegalArgumentException("No OutputStream specified");

        Writer writer = null;
        try {
            writer = new OutputStreamWriter(out, charset);
            writer.write(in);
            writer.flush();
        } finally {
            closeQuietly(writer);
        }
    }

    // 将 Stream 写入到 Stream
    public static int wirteStreamToStream(InputStream in, OutputStream out) throws IOException {
        if (null == in) throw new IllegalArgumentException("No InputStream specified");
        if (null == out) throw new IllegalArgumentException("No OutputStream specified");

        int readedCount = 0;
        byte[] buffer = new byte[BUFFER_SIZE];
        int readed;
        while (EOF != (readed = in.read(buffer))) {
            out.write(buffer, 0, readed);
            readedCount += readed;
        }
        out.flush();
        return readedCount;
    }

    // 使用 NIO 方式, 将 Stream 写入到 Stream
    public static void wirteStreamToStreamByNIO(FileInputStream in, FileOutputStream out) throws IOException {
        if (null == in) throw new IllegalArgumentException("No FileInputStream specified");
        if (null == out) throw new IllegalArgumentException("No FileOutputStream specified");

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate(BUFFER_SIZE);
            inChannel = in.getChannel();
            outChannel = out.getChannel();
            while (EOF != inChannel.read(byteBuffer)) {
                byteBuffer.flip(); // 读取模式转换写入模式
                outChannel.write(byteBuffer); // 写入
                byteBuffer.clear(); // 清空缓存，等待下次写入
            }
        } finally {
            closeQuietly(inChannel, outChannel);
        }
    }

    // 使用 FileChannel 方式, 将 Stream 写入到 Stream
    public static void wirteStreamToStreamByChannel(FileInputStream in, FileOutputStream out) throws IOException {
        if (null == in) throw new IllegalArgumentException("No FileInputStream specified");
        if (null == out) throw new IllegalArgumentException("No FileOutputStream specified");

        FileChannel inChannel = null;
        FileChannel outChannel = null;
        try {
            inChannel = in.getChannel();
            outChannel = out.getChannel();
            // 连接两个通道, 并且从in通道读取, 然后写入out通道
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } finally {
            closeQuietly(inChannel, outChannel);
        }
    }

    // 将 Stream 读出 Byte
    public static byte[] readStreamToByteArray(InputStream in) throws IOException {
        if (null == in) return EMPTY_CONTENT;

        ByteArrayOutputStream bos = new ByteArrayOutputStream(BUFFER_SIZE);
        try {
            wirteStreamToStream(in, bos);
            return bos.toByteArray();
        } finally {
            closeQuietly(bos);
        }
    }


    // 将 Stream 读取 String
    public static void readStreamToString(InputStream in, String charset, Predicate<String> filter, Consumer<String> wrapper) throws IOException {
        if (null == in) throw new IllegalArgumentException("No InputStream specified");
        charset = StringX.getCharset(charset); // 默认 UTF-8

        InputStreamReader ir = null;
        BufferedReader br = null;
        try {
            ir = new InputStreamReader(in, charset);
            br = new BufferedReader(ir);
            String line;
            while (null != (line = br.readLine())) {
                if (null == filter) { // 全量输出
                    wrapper.accept(line);
                    continue;
                }

                // 带过滤器
                if (filter.test(line)) wrapper.accept(line);
            }
        } finally {
            closeQuietly(br, ir);
        }
    }

    // 禁默释放资源
    public static void closeQuietly(Closeable... resources) {
        if (CollectionX.isEmpty(resources)) return;
        for (Closeable resource : resources) {
            try {
                resource.close();
            } catch (IOException ignored) {
            }
        }
    }

    public static void main(String[] args) throws IOException {
        String directoryPath = "/Users/cover/Downloads/spiiker-article-bak";
        List<File> files = list(directoryPath, file -> !file.getName().equalsIgnoreCase(".DS_Store"), sortBySize);
        files.forEach(file -> System.out.println(file.getAbsolutePath() + " = " + getDisplaySize(file) + " = " + DateX.format(file.lastModified())));
    }
}
