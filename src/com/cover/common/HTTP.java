package com.cover.common;


// import lombok.extern.slf4j.Slf4j;

import javax.net.ssl.*;
import java.io.*;
import java.net.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// 网络请求 工具类
@SuppressWarnings({"SameParameterValue", "unchecked", "RegExpRedundantEscape", "UnusedReturnValue", "unused"})
// @Slf4j
public class HTTP {

    // 常规网络请求方法 ##################################################################################################################
    public static final String METHOD_GET_VALUE = "GET"; // 获取资源
    public static final String METHOD_POST_VALUE = "POST"; // 传输内容
    public static final String METHOD_DELETE_VALUE = "DELETE"; // 删除资源
    public static final String METHOD_PUT_VALUE = "PUT"; // 更新资源
    public static final String METHOD_HEAD_VALUE = "HEAD"; // 获取报文头
    public static final String METHOD_OPTIONS_VALUE = "OPTIONS"; // 询问支持的方法

    // 常规 ContentType ################################################################################################################
    public static final String CONTENT_TYPE_TEXT_VALUE = "text/plain";
    public static final String CONTENT_TYPE_HTML_VALUE = "text/html";
    public static final String CONTENT_TYPE_XML_VALUE = "text/xml";
    public static final String CONTENT_TYPE_FORM_VALUE = "application/x-www-form-urlencoded";
    public static final String CONTENT_TYPE_JSON_VALUE = "application/json";

    // 常规 UserAgent ##################################################################################################################
    public static final String USER_AGENT_MAC_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/87.0.4280.67 Safari/537.36";
    public static final String USER_AGENT_MAC_SAFARI = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/12.0.3 Safari/605.1.15";
    public static final String USER_AGENT_WIN_CHROME = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";
    public static final String USER_AGENT_WIN_EDGE = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.140 Safari/537.36 Edge/18.17763";
    public static final String USER_AGENT_WIN_IE = "Mozilla/5.0 (Windows NT 10.0; WOW64; Trident/7.0; rv:11.0) like Gecko";

    // 文件参数 #######################################################################################################################
    public static final String SUFFIX_TEXT = "txt|pdf|xml";
    public static final String SUFFIX_VOICE = "mp3|wav|m4a|wma|mpg|mpeg";
    public static final String SUFFIX_MOVIE = "avi|mp4|mkv|rm|rmvb|mov|ogg|mod|fla|flv|flc|swf";
    public static final String SUFFIX_IMAGE = "bmp|jpg|jpeg|gif|png|psd";
    public static final String SUFFIX_OFFICE = "doc|docx|xls|xlsx|ppt|pptx|wps";
    public static final String SUFFIX_COMPRESS = "zip|rar|exe|apk|jar";

    public static final SimpleDateFormat FOLDER_FORMAT = new SimpleDateFormat("yyyy/MMdd"); // 日期分类文件夹
    public static final SimpleDateFormat FILE_FORMAT = new SimpleDateFormat("HHmmss"); // 文件后缀
    public static final Random random = new Random(); // 随机器

    // 请求参数 ########################################################################################################################

    private int connectionTimeout = 10 * 1000; // 请求连接超时时间
    private int readTimeout = 10 * 1000; // 读取内容超时时间
    private String encoding = "UTF-8"; // 处理字符集
    private String contentType = CONTENT_TYPE_FORM_VALUE; // 提交请求类型

    private String referer = ""; // Header 来源标识, 若空则使用请求地址作为来源标识
    private String userAgent = USER_AGENT_MAC_CHROME; // Header 客户端标识
    private boolean gzip = false; // 是否接受 gzip 格式
    private boolean followRedirects = false; // 是否允许当前请求自动跳转

    private final Map<String, Object> headers = new LinkedHashMap<>(); // 请求头信息
    private boolean useCookie = true; // 是否使用 Cookie
    private CookieManager cookieManager; // Cookie 管理器

    private int downloadMinSize = 100; // 下载文件下限, 小于此大小不下载
    private int downloadMaxSize = 50 * 1024 * 1024; // 下载文件上限, 大于此大小不下载

    // HTTPS 设置
    private boolean ignoreHttps = false; // 忽略 HTTPS 验证, 既信任所有证书
    private String httpsType = "TLS"; // HTTPS 证书类型, 默认 TLS, 不行试一下 SSL

    /**
     * GET 请求
     *
     * @param url 请求地址
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String get(String url) throws Exception {
        return request(url, METHOD_GET_VALUE);
    }

    /**
     * GET 请求
     *
     * @param uri 请求地址
     * @param kv  替换地址参数, 注重复 key 的问题, 替换参数不排除重复参数
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String get(String uri, Object... kv) throws Exception {
        String url = $parseUrlOfParameters(uri, kv); // 预先把参数封装到 url 中
        return request(url, METHOD_GET_VALUE);
    }

    /**
     * GET 请求
     *
     * @param uri             请求地址
     * @param queryParameters 替换地址参数, 注重复 key 的问题, 替换参数不排除重复参数
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String get(String uri, Map<?, ?> queryParameters) throws Exception {
        String url = $parseUrlOfParameters(uri, queryParameters); // 预先把参数封装到 url 中
        return request(url, METHOD_GET_VALUE);
    }

    /**
     * DELETE 请求
     *
     * @param url 请求地址
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String delete(String url) throws Exception {
        return request(url, METHOD_DELETE_VALUE);
    }

    /**
     * HEAD 请求
     *
     * @param url 请求地址
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String header(String url) throws Exception {
        return request(url, METHOD_HEAD_VALUE);
    }

    /**
     * OPTIONS 请求
     *
     * @param url 请求地址
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String options(String url) throws Exception {
        return request(url, METHOD_OPTIONS_VALUE);
    }

    /**
     * 处理简单的请求
     *
     * @param url    请求地址
     * @param method 连接方法
     * @return 响应 String
     * @throws Exception 执行错误
     */
    private String request(String url, String method) throws Exception {
        if ($isEmptyString(url)) throw new Exception("Url is Empty");

        // 处理连接
        HttpURLConnection connection = getConnection(url);

        // 处理请求方式
        handleMethod(connection, method);

        // 响应结果
        return handleResult(connection);
    }

    /**
     * POST 请求
     * 模拟表单提交
     *
     * @param url 请求地址
     * @param kv  提交的参数, 格式: (key, value, key, value)
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String post(String url, Object... kv) throws Exception {
        return post(url, false, $parseMapOfKV(kv));
    }

    /**
     * POST 请求
     * 模拟表单提交
     *
     * @param url     请求地址
     * @param putByte 是否以字节流提交
     * @param kv      提交的参数, 格式: (key, value, key, value)
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String post(String url, boolean putByte, Object... kv) throws Exception {
        return post(url, putByte, $parseMapOfKV(kv));
    }

    /**
     * POST 请求
     * 模拟表单提交
     *
     * @param url      请求地址
     * @param formData 提交的参数集合
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String post(String url, Map<?, ?> formData) throws Exception {
        return post(url, false, formData);
    }

    /**
     * POST 请求
     * 模拟表单提交
     *
     * @param url      请求地址
     * @param putByte  是否以字节流提交
     * @param formData 提交的参数集合
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String post(String url, boolean putByte, Map<?, ?> formData) throws Exception {
        String content = $parseFormDataToString(formData);
        return post(url, putByte, content);
    }

    /**
     * POST 请求
     * 默认提交字节流提交
     *
     * @param url     请求地址
     * @param content 传递文本内容
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String post(String url, String content) throws Exception {
        return post(url, true, content);
    }

    /**
     * POST 请求
     *
     * @param url     请求地址
     * @param putByte 是否以字节流提交
     * @param content 传递文本内容
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String post(String url, boolean putByte, String content) throws Exception {
        if ($isEmptyString(url)) throw new Exception("Url is Empty");
        if ($isEmptyString(content)) throw new Exception("Content is Empty");

        // 处理连接
        HttpURLConnection connection = getConnection(url);

        // 处理请求方式
        handleMethod(connection, METHOD_POST_VALUE);

        // 处理提交的内容
        handleContentData(connection, putByte, content);

        // 响应结果
        return handleResult(connection);
    }

    /**
     * POST 请求
     *
     * @param url  请求地址
     * @param path 本地文件路径
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String upload(String url, String path) throws Exception {
        if ($isEmptyString(path)) throw new Exception("Path is Empty");
        Map<String, String> uploadFiles = new HashMap<>();
        uploadFiles.put("file", path);
        return upload(url, uploadFiles);
    }

    /**
     * POST 请求
     *
     * @param url  请求地址
     * @param key  上传文件参数
     * @param path 本地文件路径
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String upload(String url, String key, String path) throws Exception {
        if ($isAnyEmptyString(key, path)) throw new Exception("Key or Path is Empty");
        Map<String, String> uploadFiles = new HashMap<>();
        uploadFiles.put(key, path);
        return upload(url, uploadFiles);
    }

    /**
     * POST 请求
     *
     * @param url         请求地址
     * @param uploadFiles 上传文件
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String upload(String url, Map<String, String> uploadFiles) throws Exception {
        if ($isEmptyCollection(uploadFiles)) throw new Exception("Upload Files is Empty");
        return postMultipart(url, null, uploadFiles);
    }

    /**
     * POST 请求
     *
     * @param url 请求地址
     * @param kv  表单数据, 格式: (key, value, key, value)
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String postMultipart(String url, Object... kv) throws Exception {
        return postMultipart(url, $parseMapOfKV(kv), null);
    }

    /**
     * POST 请求
     *
     * @param url      请求地址
     * @param postData 表单数据
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String postMultipart(String url, Map<?, ?> postData) throws Exception {
        return postMultipart(url, postData, null);
    }

    /**
     * POST 请求
     *
     * @param url      请求地址
     * @param postData 表单数据
     * @param postFile 上传的文件数据
     * @return 响应结果 => String
     * @throws Exception 执行错误
     */
    public String postMultipart(String url, Map<?, ?> postData, Map<String, String> postFile) throws Exception {
        if ($isEmptyString(url)) throw new Exception("Url is Empty");

        // 处理连接
        HttpURLConnection connection = getConnection(url);

        // 处理请求方式
        handleMethod(connection, METHOD_POST_VALUE);

        // 处理提交内容
        handleByteData(connection, postData, postFile);

        // 响应结果
        return handleResult(connection);
    }

    /**
     * 下载文件到指定的文件夹, 并且根据 年/月日 文件夹分割
     * 文件名根据时间创建, createFileName(), 后缀名通过 url 解析
     *
     * @param url            下载路径
     * @param realFolderPath 保存到的根目录, 例如: /opt => 自动生成 /opt/03/23 带文件夹分割的
     * @return 本地文件全路径, 业务需要的路径自己处理
     * @throws Exception 执行错误
     */
    public String download(String url, String realFolderPath) throws Exception {
        return download(url, realFolderPath, true, null, null, true);
    }

    /**
     * 下载文件到指定的文件夹
     * createFolder = true, 自动创建年/月日文件夹分割, 否则直接保存到 rootPath 文件夹下
     * 文件名根据时间创建, createFileName(), 后缀名通过 url 解析
     *
     * @param url            下载路径
     * @param realFolderPath 保存到的根目录
     * @param createFolder   是否创建 年/月日 文件夹分割
     * @return 本地文件全路径, 业务需要的路径自己处理
     * @throws Exception 执行错误
     */
    public String download(String url, String realFolderPath, boolean createFolder) throws Exception {
        return download(url, realFolderPath, createFolder, null, null, true);
    }

    /**
     * 下载文件到指定的文件夹
     * createFolder = true, 自动创建年/月日文件夹分割, 否则直接保存到 rootPath 文件夹下
     * 文件名优先通过 url 解析, 后缀名通过 url 解析
     *
     * @param url            下载路径
     * @param realFolderPath 保存到的根目录
     * @param createFolder   是否创建 年/月日 文件夹分割
     * @return 本地文件全路径, 业务需要的路径自己处理
     * @throws Exception 执行错误
     */
    public String downloadByOriginal(String url, String realFolderPath, boolean createFolder) throws Exception {
        return download(url, realFolderPath, createFolder, null, null, false);
    }

    /**
     * 下载文件到指定的文件夹
     *
     * @param url            下载路径
     * @param realFolderPath 保存到的根目录
     * @param createFolder   是否创建 年/月日 文件夹分割
     * @param fileBaseName   保存的文件名, 不带后缀名, 若空的话根据 url 解析文件名 => 如果还是空的, 按时间创建
     * @param fileSuffix     文件后缀名, 若空根据 url 解析文件名后缀 => 如果还是空的, 设置后缀为 .tmp
     * @param autoFileName   true = 根据时间创建文件名
     * @return 本地文件全路径, 业务需要的路径自己处理
     * @throws Exception 执行错误
     */
    public String download(String url, String realFolderPath, boolean createFolder, String fileBaseName, String fileSuffix, boolean autoFileName) throws Exception {
        if ($isEmptyString(url)) throw new Exception("Download Url is Empty");

        // 计算根路径
        String root = $getSaveRealPath(realFolderPath);
        if ($isEmptyString(root)) throw new Exception("Root Path is Empty");

        // 计算子文件夹
        String folder = $getSaveFolderPath(null, createFolder);

        // 计算文件名
        String name = autoFileName ? createFileName() : $getSaveFileBaseName(url, fileBaseName);
        if ($isEmptyString(name)) throw new Exception("Create File Name Error");

        // 计算文件后缀名
        String suffix = $getSaveFileSuffix(url, fileSuffix);
        if ($isEmptyString(suffix)) throw new Exception("Create File Suffix Error");

        // 本地保存文件完整路径
        String filePath = $concatSavePath(root, folder, name, suffix);
        if ($isEmptyString(filePath)) throw new Exception("Create File Path Error");
        return downloadFile(url, filePath);
    }

    /**
     * 下载文件
     *
     * @param url      下载路径
     * @param filePath 保存文件路径, 完整路径包括文件名后缀
     * @return 本地文件全路径, 业务需要的路径自己处理
     * @throws Exception 执行错误
     */
    public String downloadFile(String url, String filePath) throws Exception {
        if ($isEmptyString(url)) throw new Exception("Download Url is Empty");
        if ($isEmptyString(filePath)) throw new Exception("Save File Path is Empty");

        // 验证文件是否已经存在
        String savePath = $getString(filePath);
        File file = new File(savePath);
        if (file.exists()) throw new Exception(String.format("File Already Exists [%s]", savePath));

        HttpURLConnection connection = getConnection(url); // 获取连接

        // 文件大小
        int fileSize = connection.getContentLength();
        // 验证下载大小
        if (downloadMinSize > fileSize || fileSize > downloadMaxSize) {
            connection.disconnect();
            throw new Exception(String.format("Download Size Range [%s - %s], File Size [%s]", downloadMinSize, downloadMaxSize, fileSize));
        }

        byte[] writeBuffer = new byte[4096]; // 设置缓冲区大小
        boolean createSuccess = $createFolder(savePath); // 创建目录
        if (!createSuccess) {
            connection.disconnect();
            throw new Exception("Failed to Create Directory");
        }
        int saveSize = 0;
        String fileSizeString = $calcFileSize(fileSize); // 下载文件总大小
        // log.debug("Download Start [{}], File Size [{}]", url, fileSizeString);
        try (InputStream input = connection.getInputStream();
             FileOutputStream fos = new FileOutputStream(savePath)) {
            // log.debug("contentType = {}", connection.getContentType());
            // log.debug("name = {}", connection.getHeaderField("Content-Disposition"));
            int readLength;
            while (-1 != (readLength = input.read(writeBuffer))) {
                fos.write(writeBuffer, 0, readLength);
                saveSize += readLength;

                String percent = $calcPercent(saveSize, fileSize); // 下载百分比进度
                // log.debug("{}: [{} / {}] => {}", percent, $calcFileSize(saveSize), fileSizeString, savePath);
            }

            File successFile = new File(savePath);
            if (!successFile.exists()) throw new Exception(String.format("File Download Failed [%s]", url));
            // log.debug("Download Success, File Size [{}], File Path [ {} ]", fileSizeString, savePath);
            return savePath;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 根据 url, 获取一个 Http 连接
     *
     * @param url 请求地址
     * @return Http 连接
     */
    private HttpURLConnection getConnection(String url) throws Exception {
        if ($isEmptyString(url)) throw new Exception("Url is Empty");

        // 预处理 HTTPS
        if (isIgnoreHttps() && $getString(url).toLowerCase().startsWith("https")) {
            trustEveryone(); // 信任所有 HTTPS 请求
            setIgnoreHttps(false);
        }

        // Cookie 管理器
        initCookieManager();

        // 开启请求连接
        URL requestUrl = $getURL(url);
        final HttpURLConnection connection = url.startsWith("https") ?
                (HttpsURLConnection) requestUrl.openConnection() : (HttpURLConnection) requestUrl.openConnection();

        // 设置来源
        if ($isEmptyString(referer)) referer = url;
        connection.setRequestProperty("Referer", referer);
        referer = url; // 请求后就把当前请求地址做为下一个请求的来源

        connection.setConnectTimeout(connectionTimeout); // 设置连接超时时间
        connection.setReadTimeout(readTimeout); // 设置读取超时时间
        connection.setRequestProperty("User-Agent", userAgent); // 设置客户端标识
        if (gzip) connection.setRequestProperty("Accept-Encoding", "gzip"); // 设置接受 Gzip

        // 设置头部信息, 注意编码问题
        headers.forEach((key, value) -> connection.setRequestProperty($getEncodeString(key), $parseParameter(value)));

        // 设置是否跟随跳转
        connection.setInstanceFollowRedirects(followRedirects);
        return connection; // 响应 Connection 对象
    }

    // 信任所有 HTTPS
    public void trustEveryone() {
        try {
            HttpsURLConnection.setDefaultHostnameVerifier((host_name, session) -> true);
            SSLContext context = SSLContext.getInstance(httpsType);

            X509TrustManager manager = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            };
            context.init(null, new X509TrustManager[]{manager}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {
        }
    }

    /**
     * 设置 HTTPS 请求证书
     *
     * @param filePath 证书路径
     * @param password 证书密码
     * @param tlsType  TLS 类型, 如 TLSv1
     */
    public void setSSL(String filePath, String password, String tlsType) {
        try {
            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream(filePath), password.toCharArray());

            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, password.toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            KeyStore trustStore = KeyStore.getInstance("JKS");
            String cacertsPath = System.getProperty("java.home") + "/lib/security/cacerts";
            trustStore.load(new FileInputStream(cacertsPath), "changeit".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();

            SSLContext sslContext = SSLContext.getInstance(tlsType); // "TLSv1"
            sslContext.init(kms, tms, new SecureRandom());
            SSLSocketFactory ssl = sslContext.getSocketFactory();
            HttpsURLConnection.setDefaultSSLSocketFactory(ssl);
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | UnrecoverableKeyException | KeyManagementException e) {
            e.printStackTrace();
        }
    }

    /**
     * 默认: TLSv1 类型
     *
     * @param filePath 证书路径
     * @param password 证书密码
     */
    public void setSSL(String filePath, String password) {
        setSSL(filePath, password, "TLSv1");
    }

    // 添加 Cookie
    public HTTP addCookie(String uri, String domain, String path, String key, String value) throws URISyntaxException {
        if ($isAnyEmptyString(uri, domain, path, key, value)) return this;

        HttpCookie cookie = new HttpCookie($getString(key), $getString(value));
        cookie.setDomain($getString(domain));
        cookie.setPath($getString(path));
        return addCookie(uri, cookie);
    }

    // 添加 Cookie
    public HTTP addCookie(String uri, HttpCookie... cookies) throws URISyntaxException {
        if ($isEmptyString(uri) || $isEmptyCollection(cookies)) return this;
        return addCookie(new URI(uri), cookies);
    }

    // 添加 Cookie
    public HTTP addCookie(URI uri, HttpCookie... cookies) {
        if (null == uri || $isEmptyCollection(cookies)) return this;

        CookieStore store = getCookieStore();
        if (null == store) return null;

        for (HttpCookie cookie : cookies) store.add(uri, cookie);
        return this;
    }

    // 添加头部信息
    public HTTP addHeader(String key, Object value) {
        if ($isEmptyString(key) || null == value) return this;
        headers.put(key, value);
        return this;
    }

    // Cookie 管理, 初始化 Cookie 管理器
    private void initCookieManager() {
        if (isUseCookie() && null == CookieHandler.getDefault()) {
            cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
            CookieHandler.setDefault(cookieManager);
        }
    }

    // 请求前处理请求方法
    private void handleMethod(HttpURLConnection connection, String method) throws Exception {
        if (null == connection) throw new Exception("Connection is Empty");

        // 处理请求方法
        String connectionMethod = $getString(method, METHOD_GET_VALUE).toUpperCase();
        // log.debug("{} {}", connectionMethod, connection.getURL()); // 日志
        connection.setRequestMethod(connectionMethod);
        if (METHOD_POST_VALUE.equalsIgnoreCase(connectionMethod) || METHOD_PUT_VALUE.equalsIgnoreCase(connectionMethod)) { // POST PUT 请求处理
            String original = $getString(headers.get("Content-Type"));
            if ($isEmptyString(original)) headers.put("Content-Type", contentType); // 设置 Content-Type

            connection.setDoInput(true); // 可读
            connection.setDoOutput(true); // 可写
            connection.setUseCaches(false); // 不使用缓存
        }

        if (METHOD_HEAD_VALUE.equalsIgnoreCase(connectionMethod)) connection.setDoOutput(true); // 可写
    }

    // 默认提交行为
    private void handleContentData(HttpURLConnection connection, String content) throws IOException {
        handleContentData(connection, false, content);
    }

    // 提交数据 putByte = true, 以字节流提交数据
    private void handleContentData(HttpURLConnection connection, boolean putByte, String content) throws IOException {
        if (null == connection || $isEmptyString(content)) return;

        // 默认表单提交行为
        if (!putByte) {
            // log.debug("POST Stream: {}", content);
            try (OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream(), encoding)) {
                out.write(content);
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }


        // 默认字节流行为
        if (putByte) {
            // log.debug("POST Byte: {}", content);
            try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
                out.write(content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
                throw e;
            }
        }

    }

    // 提交表单 / 默认携带上传文件的数据处理
    private void handleByteData(HttpURLConnection connection, Map<?, ?> formData, Map<String, String> fileData) throws IOException {
        if (null == connection || $isEmptyCollection(formData) || $isEmptyCollection(fileData)) return;

        String boundary = String.format("------%s", System.currentTimeMillis());
        connection.setRequestProperty("Content-Type", String.format("multipart/form-data;boundary=%s", boundary));

        try (DataOutputStream out = new DataOutputStream(connection.getOutputStream())) {
            // FORM 字段
            if (!$isEmptyCollection(formData)) {
                // log.debug("POST Form: {}", formData);
                String formParameterContent = formData.keySet().stream().filter(key -> null != formData.get(key))
                        .map(key -> String.format("\r\n--%s\r\nContent-Disposition: form-data; name=\"%s\"\r\n\r\n%s", boundary, key, formData.get(key)))
                        .reduce(String::concat).orElse($STRING_EMPTY_VALUE);

                if ($isEmptyString(formParameterContent)) out.write(formParameterContent.getBytes(encoding)); // 写入
            }

            // 文件
            if (!$isEmptyCollection(fileData)) {
                // log.debug("POST File: {}", fileData);
                for (String name : fileData.keySet()) {
                    String path = $getString(fileData.get(name));
                    if ($isEmptyString(path)) continue;

                    File file = new File(path);
                    if (!file.exists()) continue;

                    String buffer = String.format("\r\n--%s\r\nContent-Disposition: form-data; name=\"%s\"; filename=\"%s\"\r\nContent-Type:application/octet-stream\r\n\r\n", boundary, name, file.getName());
                    out.write(buffer.getBytes(encoding));

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] cache = new byte[8192]; // 8k
                        int count;
                        while (-1 != (count = fis.read(cache))) out.write(cache, 0, count);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            out.write(String.format("\r\n--%s--\r\n", boundary).getBytes(encoding));
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    // 处理结果
    private String handleResult(HttpURLConnection connection) throws Exception {
        if (null == connection) throw new Exception("Connection is Empty");

        try (InputStream input = (connection.getResponseCode() < 400) ? connection.getInputStream() : connection.getErrorStream()) {
            // Head 请求响应体
            if (METHOD_HEAD_VALUE.equalsIgnoreCase(connection.getRequestMethod())) {
                Map<String, List<String>> responseHeader = connection.getHeaderFields();
                if ($isEmptyCollection(responseHeader)) return $STRING_EMPTY_VALUE;
                return responseHeader.keySet().stream().map(key -> String.format("\r\n%s=%s", key, responseHeader.get(key))).reduce(String::concat).orElse($STRING_EMPTY_VALUE);
            }

            // 其他方法响应
            return $parseString(input);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            connection.disconnect(); // 释放连接
        }
    }

    // 基础类型方法 ######################################################################################################################
    // String
    private static final String $STRING_EMPTY_VALUE = "";

    private static String $getString(Object obj) {
        return $getString(obj, $STRING_EMPTY_VALUE);
    }

    private static String $getString(Object obj, String defaultValue) {
        if (null == obj) return defaultValue;
        String parseString = String.valueOf(obj);
        return $isEmptyString(parseString) ? defaultValue : parseString.trim();
    }

    private static boolean $isEmptyString(String str) {
        if (null == str) return true;

        String handleString = str.trim();
        if (!$hasLength(handleString) || "null".equalsIgnoreCase(handleString)) return true;
        for (int i = 0; i < handleString.length(); i++)
            if (!Character.isWhitespace(handleString.charAt(i))) return false; // 有一个非空字符就不算是空
        return true;
    }

    // 只要有一个空则返回 True
    private static boolean $isAnyEmptyString(String... strs) {
        if ($isEmptyCollection(strs)) return true;
        for (String str : strs) if ($isEmptyString(str)) return true;
        return false;
    }

    public static String $trimAllWhitespace(String str) {
        if (null == str) return $STRING_EMPTY_VALUE;
        if (!containsWhitespace(str)) return str.trim();

        final int len = str.length();
        StringBuilder buffer = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if (!Character.isWhitespace(c)) buffer.append(c);
        }
        return buffer.toString().trim();
    }

    public static boolean containsWhitespace(CharSequence str) {
        if (!$hasLength(str)) return false;
        int strLen = str.length();
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(str.charAt(i))) return true;
        }
        return false;
    }

    public static boolean $hasLength(String str) {
        return null != str && str.trim().length() > 0;
    }

    public static boolean $hasLength(CharSequence str) {
        return null != str && str.toString().trim().length() > 0;
    }

    // 创建一个 年/月日的路径
    public static String createFolderPath() {
        return FOLDER_FORMAT.format(new Date());
    }

    // 创建一个保存的文件名
    public static String createFileName() {
        return String.format("%s_%s", FILE_FORMAT.format(new Date()), random.nextInt(999999));
    }

    // 计算文件大小
    private String $calcFileSize(int fileSize) {
        if (0 > fileSize) return "0Kb";
        if (fileSize > 1024f * 1024 * 1024) return String.format("%6.2fGb", fileSize / (1024f * 1024 * 1024));
        if (fileSize > 1024f * 1024) return String.format("%6.2fMb", fileSize / (1024f * 1024));
        return String.format("%6.2fKb", fileSize / 1024f);
    }

    // 计算下载进度 current: 已写入总量, total: 文件大小总量
    private String $calcPercent(int current, int total) {
        double percent = current * 1.0 / total * 100.0;
        return String.format("%6.2f%%", percent);
    }

    // 创建父目录文件夹
    private static boolean $createFolder(String path) {
        if ($isEmptyString(path)) return false;
        File file = new File(path);
        if (!file.getParentFile().exists()) return file.getParentFile().mkdirs();
        return true;
    }

    // 计算下载文件保存的本地根路径, 有值的话去掉最后的文件夹分隔符, 例如: /Users/xxx/Downloads/ => /Users/xxx/Downloads
    private String $getSaveRealPath(String realFolderPath) {
        String path = $getString(realFolderPath);
        if ($isEmptyString(path)) return $STRING_EMPTY_VALUE;
        // 去除后面带的分隔符
        path = path.replaceAll("/+$", $STRING_EMPTY_VALUE);
        return path.replaceAll("\\\\+$", $STRING_EMPTY_VALUE);
    }

    // 计算下载文件保存的子文件夹路径, 有值的话去掉最后的文件夹分隔符
    // 没有值的话 create = true, 根据时间创建子文件夹, create = false 返回空字符串, 既不创建子文件夹
    // 前后均不带文件夹分隔符, 例如: 03/23
    private String $getSaveFolderPath(String folderPath, boolean create) {
        String path = $getString(folderPath);
        if (create && $isEmptyString(path)) return createFolderPath();
        return $trimSeparator(path);
    }

    // 计算下载文件保存的文件名
    // 1. fileName 有值, 优先使用
    // 2. url 有值, 动态计算保存的名字, 没有按时间创建
    private String $getSaveFileBaseName(String url, String fileName) {
        String name = $getString(fileName);
        if (!$isEmptyString(name)) return name;

        // 按下载连接计算保存名字
        name = $getFileBaseNameOfPath(url);
        if (!$isEmptyString(name)) return name;

        // 空的话, 根据时间创建文件名
        return createFileName();
    }

    // 计算下载文件保存的文件后缀名
    // 后缀名不包括最开始的 .
    private String $getSaveFileSuffix(String url, String fileSuffix) {
        String suffix = $getString(fileSuffix).replaceAll("^(\\.)+", $STRING_EMPTY_VALUE); // 去除前面的.
        if (!$isEmptyString(suffix)) return suffix;

        // 按 url 地址计算 后缀名
        suffix = $getFileSuffixOfPath(url);
        if (!$isEmptyString(suffix)) return suffix;
        return "tmp"; // 临时文件后缀名
    }

    // 拼接完成下载文件保存的本地地址
    private String $concatSavePath(String rootPath, String folderPath, String fileName, String fileSuffix) {
        if ($isAnyEmptyString(rootPath, fileName, fileSuffix)) return $STRING_EMPTY_VALUE;
        String separator = File.separator; // 文件夹分隔符
        String root = $getString(rootPath); // 根路径
        String folder = $isEmptyString(folderPath) ? $STRING_EMPTY_VALUE : String.format("%s%s", separator, $getString(folderPath)); // 文件夹路径
        String name = String.format("%s%s", separator, $getString(fileName)); // 文件名, 前面添加文件夹分隔符
        String suffix = $getString(fileSuffix); // 文件后缀名
        String path = String.format("%s%s%s.%s", root, folder, name, suffix);
        return new File(path).exists() ? $STRING_EMPTY_VALUE : path; // 文件存在的话, 返回空字符串
    }

    // 去除两边系统分隔符
    private String $trimSeparator(String path) {
        String folder = $getString(path);
        if ($isEmptyString(folder)) return $STRING_EMPTY_VALUE;
        folder = folder.replaceAll("^/+", $STRING_EMPTY_VALUE);
        folder = folder.replaceAll("/+$", $STRING_EMPTY_VALUE);
        folder = folder.replaceAll("^\\\\+", $STRING_EMPTY_VALUE);
        folder = folder.replaceAll("\\\\+$", $STRING_EMPTY_VALUE);
        return folder;
    }

    // 根据 path 计算文件名, 包括后缀名
    private String $getFileNameOfPath(String path) {
        String formatPath = $getDecodeString(path);
        if ($isEmptyString(formatPath)) return $STRING_EMPTY_VALUE;

        String suffixes = String.format("%s|%s|%s|%s|%s|%s", SUFFIX_VOICE, SUFFIX_IMAGE, SUFFIX_MOVIE, SUFFIX_OFFICE, SUFFIX_COMPRESS, SUFFIX_TEXT);
        Pattern pat = Pattern.compile(String.format("[\\w]+[\\.](%s)", suffixes), Pattern.CASE_INSENSITIVE); // 正则判断
        Matcher mc = pat.matcher(formatPath); // 条件匹配
        if (mc.find()) return $getString(mc.group()); // 截取文件名后缀名
        return $STRING_EMPTY_VALUE;
    }

    // 根据 path 计算文件名, 不包括后缀名
    private String $getFileBaseNameOfPath(String path) {
        String name = $getFileNameOfPath(path);
        if ($isEmptyString(name)) return $STRING_EMPTY_VALUE;
        int p = name.lastIndexOf(".");
        if (p > -1) return $getString(name.substring(0, p));
        return $getString(name);
    }

    // 根据 path 计算文件后缀名
    private String $getFileSuffixOfPath(String path) {
        String name = $getFileNameOfPath(path);
        if ($isEmptyString(name)) return $STRING_EMPTY_VALUE;
        int p = name.lastIndexOf(".");
        if (p > -1) return $getString(name.substring(p + 1));
        return $getString(name).toLowerCase();
    }

    // 编码URL
    private URL $getURL(String hanleURL) throws Exception {
        if ($isEmptyString(hanleURL)) throw new Exception("Url is Empty");

        String url = $getString(hanleURL);
        URL action = new URL(url);
        String queryString = action.getQuery(); // 参数部分
        if ($isEmptyString(queryString)) return action;

        String uri = url.replace(queryString, $STRING_EMPTY_VALUE); // 没有参数部分的地址
        String query = $getString(queryString); // 参数部分

        Pattern pattern = Pattern.compile("[\u0391-\uFFE5]+"); // 匹配中文
        Matcher matcher = pattern.matcher(query);
        while (matcher.find()) {
            String value = matcher.group();
            if ($isEmptyString(value)) continue;

            query = query.replace(value, $getEncodeString(value));
        }
        return new URL(String.format("%s%s", uri, query.replace(" ", "%20"))); // 最后也需要把空格替换一下
    }

    // 编码参数
    private String $getEncodeString(Object obj) {
        if (null == obj) return $STRING_EMPTY_VALUE;
        return $getEncodeString($getString(obj));
    }

    private String $getEncodeString(String str) {
        if ($isEmptyString(str)) return $STRING_EMPTY_VALUE;
        try {
            return $getString(URLEncoder.encode($getString(str), encoding)).replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return $STRING_EMPTY_VALUE;
        }
    }

    // 解码参数
    private String $getDecodeString(String str) {
        if ($isEmptyString(str)) return $STRING_EMPTY_VALUE;
        try {
            return $getString(URLDecoder.decode($getString(str), encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return $STRING_EMPTY_VALUE;
        }
    }

    // 封装 URL 参数
    private String $parseUrlOfParameters(String url, Map<?, ?> parameters) {
        if ($isEmptyString(url)) return $STRING_EMPTY_VALUE;
        if ($isEmptyCollection(parameters)) return $getString(url);

        String action = $getString(url).replaceAll("\\?$", $STRING_EMPTY_VALUE); // 最后是 ? 替换掉
        String query = $concatString(parameters, $STRING_EMPTY_VALUE, key -> String.format("&%s=%s", $getEncodeString(key), $parseParameter(parameters.get(key))));
        query = action.contains("?") ? query : String.format("?%s", query.substring(1));
        return String.format("%s%s", action, query);
    }

    // 拼接请求地址
    private String $parseUrlOfParameters(String url, Object... kv) throws Exception {
        if ($isEmptyString(url)) return $STRING_EMPTY_VALUE;
        if ($isEmptyCollection(kv)) return $getString(url);

        if (0 != kv.length % 2) throw new Exception("Parameter Key / Value Error");

        String action = $getString(url).replaceAll("\\?$", $STRING_EMPTY_VALUE); // 最后是 ? 替换掉
        StringBuilder query = new StringBuilder(); // 拼接参数部分
        Object prevKey = null;
        for (Object item : kv) {
            if (null == prevKey) {
                prevKey = item;
                continue;
            }
            String key = $getEncodeString(prevKey);
            String value = $parseParameter(item);
            query.append(String.format("&%s=%s", key, value));
            prevKey = null;
        }

        query = new StringBuilder(action.contains("?") ? query.toString() : String.format("?%s", query.substring(1)));
        return String.format("%s%s", action, query.toString());
    }

    // 将 Input Stream 读去出来
    private String $parseString(InputStream input) throws Exception {
        if (null == input) throw new Exception("InputStream is Empty");

        StringBuilder buffer = new StringBuilder();
        try (InputStreamReader is = new InputStreamReader(input, encoding);
             BufferedReader reader = new BufferedReader(is)) {
            String line;
            while (null != (line = reader.readLine())) buffer.append(line).append("\r\n");
            return $getString(buffer.toString());
        }
    }

    // 封装参数,
    private String $parseParameter(Object value) {
        if (null == value) return $STRING_EMPTY_VALUE;
        // 数组处理
        if (value instanceof Object[]) return $concatString((Object[]) value, ",$", item -> String.format("%s,", $getEncodeString(item)));
        // List 处理
        if (value instanceof List) return $concatString((List<Object>) value, ",$", item -> String.format("%s,", $getEncodeString(item)));
        // Map 不处理
        if (value instanceof Map) return $STRING_EMPTY_VALUE;
        // 其他处理
        return $getEncodeString(value);
    }

    // 把表单数据转换成文本 格式: key=value&key=value
    private String $parseFormDataToString(Map<?, ?> formData) {
        if ($isEmptyCollection(formData)) return $STRING_EMPTY_VALUE;
        String content = $concatString(formData, $STRING_EMPTY_VALUE, key -> String.format("&%s=%s", $getEncodeString(key), $parseParameter(formData.get(key))));
        if (!$isEmptyString(content)) return content.substring(1);
        return $STRING_EMPTY_VALUE;
    }

    // 拼接字符串
    private static String $concatString(Object[] array, String replaceEndString, Function<Object, String> fun) {
        if ($isEmptyCollection(array)) return $STRING_EMPTY_VALUE;
        return $concatString(Arrays.asList(array), replaceEndString, fun);
    }

    // 拼接字符串
    private static String $concatString(List<Object> list, String replaceEndString, Function<Object, String> fun) {
        if ($isEmptyCollection(list)) return $STRING_EMPTY_VALUE;
        return list.stream().filter(Objects::nonNull).map(fun).reduce(String::concat).orElse($STRING_EMPTY_VALUE).replaceAll(replaceEndString, $STRING_EMPTY_VALUE);
    }

    // 拼接字符串
    private static String $concatString(Map<?, ?> map, String replaceEndString, Function<String, String> fun) {
        if ($isEmptyCollection(map)) return $STRING_EMPTY_VALUE;
        return map.keySet().stream().filter(Objects::nonNull).map(HTTP::$getString).sorted(String::compareToIgnoreCase).map(fun).reduce(String::concat).orElse($STRING_EMPTY_VALUE).replaceAll(replaceEndString, $STRING_EMPTY_VALUE);
    }

    // Collection
    private static boolean $isEmptyCollection(Map<?, ?> map) {
        return null == map || map.isEmpty();
    }

    private static boolean $isEmptyCollection(Object[] arr) {
        return null == arr || 0 == arr.length;
    }

    private static boolean $isEmptyCollection(Collection<?> list) {
        return null == list || list.isEmpty();
    }

    // 将 kv 转为 Map
    private static Map<String, Object> $parseMapOfKV(Object... kv) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
        if ($isEmptyCollection(kv)) return result;
        if (0 != kv.length % 2) throw new Exception("Parameter Key / Value Error");

        Object prevKey = null;
        for (Object item : kv) {
            if (null == prevKey) {
                prevKey = item;
                continue;
            }

            result.put($getString(prevKey), item);
            prevKey = null;
        }
        return result;
    }

    // get / set
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public HTTP setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HTTP setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    public String getEncoding() {
        return encoding;
    }

    public HTTP setEncoding(String encoding) {
        this.encoding = encoding;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public HTTP setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public String getReferer() {
        return referer;
    }

    public HTTP setReferer(String referer) {
        this.referer = referer;
        return this;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public HTTP setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    public boolean isGzip() {
        return gzip;
    }

    public HTTP setGzip(boolean gzip) {
        this.gzip = gzip;
        return this;
    }

    public boolean isFollowRedirects() {
        return followRedirects;
    }

    public HTTP setFollowRedirects(boolean followRedirects) {
        this.followRedirects = followRedirects;
        return this;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public boolean isUseCookie() {
        return useCookie;
    }

    public HTTP setUseCookie(boolean useCookie) {
        this.useCookie = useCookie;
        return this;
    }

    public CookieStore getCookieStore() {
        return null == cookieManager ? null : cookieManager.getCookieStore();
    }

    public int getDownloadMinSize() {
        return downloadMinSize;
    }

    public HTTP setDownloadMinSize(int downloadMinSize) {
        this.downloadMinSize = downloadMinSize;
        return this;
    }

    public int getDownloadMaxSize() {
        return downloadMaxSize;
    }

    public HTTP setDownloadMaxSize(int downloadMaxSize) {
        this.downloadMaxSize = downloadMaxSize;
        return this;
    }

    public boolean isIgnoreHttps() {
        return ignoreHttps;
    }

    public HTTP setIgnoreHttps(boolean ignoreHttps) {
        this.ignoreHttps = ignoreHttps;
        return this;
    }

    public String getHttpsType() {
        return httpsType;
    }

    public HTTP setHttpsType(String httpsType) {
        this.httpsType = httpsType;
        return this;
    }

    public static void main(String[] args) {
        // 1. 全局支持链式调用: HTTP http = new HTTP().setIgnoreHttps(true).addHeader(key, value);
        // 2. 设置地址栏参数, 默认提交表单拼接字符串等全部都有 Encoding
        // 3. 像 get, post 为终止操作, 配置需要提前设置
        // 4. 下载文件可解析 URL 获取文件名, 若获取后缀失败, 默认 .tmp 作为后缀
        // 5. upload 操作上传参数默认值 file

        // get 请求
        // get(String url): GET 请求获取内容
        // get(String uri, Object... kv): get请求地址栏后面增加 kv 的请求参数, 当 Map 使用就好, 必须键值对出现
        // get(String uri, Map<?, ?> queryParameters): 跟上面方法相仿

        // delete 请求
        // delete(String url): DELETE 请求

        // header 请求
        // header(String url): HEAD 请求, 获取头部响应体信息


        // options 请求
        // options(String url): OPTIONS 请求, 一般用于跨域检查

        // request 方法
        // request(String url, String method): 可自定义请求类型

        // post 请求
        // 1. post 方法均为简单的提交表单方式
        // 2. post 也可以提交字节流, putByte = true, 既提交字节流
        // 3. 所有方法参数均转为 String, 根据 putByte 是否转为字节流或者数据 提交到服务器
        // 4. 默认编码方式 application/x- www-form-urlencoded
        // 5. 可通过 new HTTP().setContentType(String contentType) 更改编码规则, 比如上传 application/json 等

        // post(String url, Object... kv)
        // post(String url, boolean putByte, Object... kv)
        // post(String url, Map<?, ?> formData)
        // post(String url, boolean putByte, Map<?, ?> formData)
        // post(String url, String content)
        // post(String url, boolean putByte, String content)

        // postMultipart 默认文件上传方式
        // 1. 使用场景, 需要提交表单和上传文件的, 可使用此方式
        // 2. 默认编码方式: multipart/form-data;boundary, 可把上传的文件读取数据流到提交的流里

        // postMultipart(String url, Object... kv)
        // postMultipart(String url, Map<?, ?> postData)
        // postMultipart(String url, Map<?, ?> postData, Map<String, String> postFile)

        // upload 方法
        // 默认使用 postMultipart 方法提交文件

        // upload(String url, String path): 上传单个文件, path 是文件路径, 默认参数 file
        // upload(String url, String key, String path)
        // upload(String url, Map<String, String> uploadFiles)

        // download 方法
        // createFolderPath(): 外部使用方法, 可获取 年/月日 文件夹路径
        // createFileName(): 外部使用方法, 可获取一个根据时间生成的文件名, 不带后缀名

        // download(String url, String realFolderPath): 下载到 realFolderPath 目录下, 默认创建时间分割目录, 文件名也按时间创建
        // download(String url, String realFolderPath, boolean createFolder): 跟上面方法相仿, 可自定义是否创建 年/月日 的目录
        // downloadByOriginal(String url, String realFolderPath, boolean createFolder): 跟上面方法相仿, 但文件名会根据url计算出来, 如果没有则按照时间创建
        // download(String url, String realFolderPath, boolean createFolder, String fileBaseName, String fileSuffix, boolean autoFileName): 完整的自定义下载文件方法
        // downloadFile(String url, String filePath): 下载一个文件到指定路径, filePath 是完整路径包括文件名后缀, 核心方法

        // HTTPS 方法
        // setIgnoreHttps(boolean ignoreHttps): 忽略所有 HTTPS, 既信任所有 HTTPS
        // setSSL(String filePath, String password, String tlsType): 设置 SSL 证书
        // setSSL(String filePath, String password): 设置 SSL 证书, 默认 TSLv1

        // Cookie 方法
        // addCookie(String uri, String domain, String path, String key, String value)
        // addCookie(String uri, HttpCookie... cookies)
        // addCookie(URI uri, HttpCookie... cookies)

        // Header 方法
        // addHeader(String key, Object value)
    }
}
