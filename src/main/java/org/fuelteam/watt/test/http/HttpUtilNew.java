package org.fuelteam.watt.test.http;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * http请求工具类
 */
public class HttpUtilNew {

    public static final String REQ_TYPE_POST = "post";
    public static final String REQ_TYPE_GET = "get";

    public static final int GET_STREAM = 1;
    public static final int GET_HTML = 0;

    public static final double REQ_PARAMS_ARRAY = 1;
    public static final double REQ_PARAMS_ENTITY = 0;

    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";

    public static final String STRING_ENTITY_KEY = "content";

    public static Logger logger = LoggerFactory.getLogger(HttpUtilNew.class);

    public static HttpUtilNewModel get(String url) {
        return req(commonHeaders(), null, REQ_TYPE_GET, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(String url, boolean ignoreTrustCert) {
        return req(commonHeaders(), null, REQ_TYPE_GET, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, ignoreTrustCert);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, String url) {
        return req(headers, null, REQ_TYPE_GET, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, String url, boolean ignoreTrustCert) {
        return req(headers, null, REQ_TYPE_GET, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, ignoreTrustCert);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, String url, int retType) {
        return req(headers, null, REQ_TYPE_GET, null, url, retType, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(NameValuePair[] nvps, String url) {
        return req(commonHeaders(), null, REQ_TYPE_GET, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, NameValuePair[] nvps, String url) {
        return req(headers, null, REQ_TYPE_GET, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, NameValuePair[] nvps, String url, boolean ignoreTrustCert) {
        return req(headers, null, REQ_TYPE_GET, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, ignoreTrustCert);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, List<Cookie> cookies, String url) {
        return req(headers, cookies, REQ_TYPE_GET, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, List<Cookie> cookies, String url, boolean ignoreTrustCert) {
        return req(headers, cookies, REQ_TYPE_GET, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, ignoreTrustCert);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, List<Cookie> cookies, String url, int retType) {
        return req(headers, cookies, REQ_TYPE_GET, null, url, retType, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, List<Cookie> cookies, NameValuePair[] nvps, String url) {
        return req(headers, cookies, REQ_TYPE_GET, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel get(Map<String, String> headers, List<Cookie> cookies, NameValuePair[] nvps, String url, int retType) {
        return req(headers, cookies, REQ_TYPE_GET, nvps, url, retType, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(String url) {
        return req(commonHeaders(), null, REQ_TYPE_POST, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(String url, NameValuePair[] nvps) {
        return req(commonHeaders(), null, REQ_TYPE_POST, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(Map<String, String> headers, String url) {
        return req(headers, null, REQ_TYPE_POST, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(Map<String, String> headers, String url, NameValuePair[] nvps) {
        return req(headers, null, REQ_TYPE_POST, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(Map<String, String> headers, List<Cookie> cookies, String url) {
        return req(headers, cookies, REQ_TYPE_POST, null, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(Map<String, String> headers, List<Cookie> cookies, NameValuePair[] nvps, String url) {
        return req(headers, cookies, REQ_TYPE_POST, nvps, url, GET_HTML, REQ_PARAMS_ARRAY, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(Map<String, String> headers, NameValuePair[] nvps, String url, double reqParamType) {
        return req(headers, null, REQ_TYPE_POST, nvps, url, GET_HTML, reqParamType, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel post(Map<String, String> headers, List<Cookie> cookies, NameValuePair[] nvps, String url, double reqParamType) {
        return req(headers, cookies, REQ_TYPE_POST, nvps, url, GET_HTML, reqParamType, CHARSET_UTF8, false);
    }

    public static HttpUtilNewModel req(Map<String, String> headers, List<Cookie> cookies, String reqType, NameValuePair[] nvps, String url,
            int retType, double reqParamType, String charset, boolean ignoreAllCert) {

        // 请求方式
        RequestBuilder req = RequestBuilder.get();
        if (reqType.equals(REQ_TYPE_POST)) {
            req = RequestBuilder.post();
        }

        // 请求url
        req.setUri(url);

        // 添加header
        if (headers != null && !headers.isEmpty()) {
            for (String key : headers.keySet()) {
                req.addHeader(key, headers.get(key));
            }
        }

        // 设置请求参数
        if (nvps != null) {
            if (reqParamType == REQ_PARAMS_ENTITY) {
                req.setEntity(new StringEntity(nvps[0].getValue(), "UTF-8"));
            } else {
                req.addParameters(nvps);
            }
        }

        // 设置超时时间
        int timeout = 60000;
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectionRequestTimeout(timeout).setSocketTimeout(timeout)
                .setConnectTimeout(timeout).setCookieSpec(CookieSpecs.DEFAULT);
        req.setConfig(requestConfigBuilder.build());
        HttpUriRequest httpUriReq = req.build();

        CloseableHttpResponse httpResponse = null;
        try {

            HttpClientContext context = HttpClientContext.create();
            CloseableHttpClient client = generateClient(cookies, ignoreAllCert);

            httpResponse = client.execute(httpUriReq, context);

            HttpUtilNewModel ret = new HttpUtilNewModel();
            ret.setCookies(context.getCookieStore().getCookies());
            if (retType == GET_STREAM) {
                ret.setBytes(EntityUtils.toByteArray(httpResponse.getEntity()));
            } else {
                if (charset == null) {
                    ret.setHtml(EntityUtils.toString(httpResponse.getEntity(), "UTF-8"));
                } else {
                    ret.setHtml(EntityUtils.toString(httpResponse.getEntity(), charset));
                }
            }
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            ret.setStatusCode(statusCode);
            if (statusCode == 302) {
                ret.setLocation(httpResponse.getHeaders("Location")[0].getValue());
            }
            return ret;

        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("请求失败:", ex);
            return null;
        } finally {
            try {
                if (httpResponse != null) {
                    // ensure the connection is released back to pool
                    EntityUtils.consume(httpResponse.getEntity());
                }
            } catch (IOException e) {
                logger.warn("close response fail", e);
            }
        }
    }

    private static CloseableHttpClient generateClient(List<Cookie> cookies, boolean ignoreAllCert) {
        SSLConnectionSocketFactory sslFactory = null;
        if (ignoreAllCert) {
            sslFactory = createIgnoreVerifySSL();
        } else {
            sslFactory = SSLConnectionSocketFactory.getSocketFactory();
        }

        Registry<ConnectionSocketFactory> reg = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE).register("https", sslFactory).build();

        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(reg);
        connectionManager.setDefaultMaxPerRoute(100);

        HttpClientBuilder httpClientBuilder = HttpClients.custom().setConnectionManager(connectionManager);
        SocketConfig socketConfig = SocketConfig.custom().setSoKeepAlive(true).setTcpNoDelay(true).build();
        httpClientBuilder.setDefaultSocketConfig(socketConfig);
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(3, true));

        CookieStore cookieStore = new BasicCookieStore();
        if (cookies != null && !cookies.isEmpty()) {
            for (Cookie cookie : cookies) {
                cookieStore.addCookie(cookie);
            }
        }

        httpClientBuilder.setDefaultCookieStore(cookieStore);

        return httpClientBuilder.build();
    }

    public static SSLConnectionSocketFactory createSSLConnectionSocketFactory() {
        return new SSLConnectionSocketFactory(custom("src/main/java/com/test/moni/zxzq/wtid.cer", "tomcat"));
    }

    /**
     * 设置信任自签名证书
     * 
     * @param keyStorePath
     *            密钥库路径
     * @param keyStorepass
     *            密钥库密码
     * @return
     */
    public static SSLContext custom(String keyStorePath, String keyStorepass) {
        SSLContext sc = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            keyStorepass = "MIIEyDCCA7CgAwIBAgIDASnpMA0GCSqGSIb3DQEBBQUAMDwxCzAJBgNVBAYTAlVTMRcwFQYDVQQKEw5HZW9UcnVzdCwgSW5jLjEUMBIGA1UEAxMLUmFwaWRTU0wgQ0EwHhcNMTEwMzI3MTcwNjI2WhcNMTIwMzI5MjMxNDExWjCB4TEpMCcGA1UEBRMgaldWdk9pVDZqNWl3bzNGVFRsRVZHT01taDFXVUpQNS0xCzAJBgNVBAYTAkNOMRYwFAYDVQQKEw13dC5jaXRpY3MuY29tMRMwEQYDVQQLEwpHVDc4MzE0MzcwMTEwLwYDVQQLEyhTZWUgd3d3LnJhcGlkc3NsLmNvbS9yZXNvdXJjZXMvY3BzIChjKTExMS8wLQYDVQQLEyZEb21haW4gQ29udHJvbCBWYWxpZGF0ZWQgLSBSYXBpZFNTTChSKTEWMBQGA1UEAxMNd3QuY2l0aWNzLmNvbTCCASIwDQYJKoZIhvcNAQEBBQADggEPADCCAQoCggEBANN6oKe3vTPiAg7qtRW3OwMtK8ZoEJ4Sn+mR1Yi/AJKcmPYj0D4wrEMRDQakjUi5/eThvcCp8Y4N6fYT/m9EeFwb6nhpvsfyCE2c1skNVk634ewFNC2o8zBaeULD8Vmii6pCkxJ5ppe18V+JZ8PaTxOO1OU18DIjqMv0rB1lx/aoBx/N0jxw9/a27sWMuX1O/zZmBkWmKPxwRnWww0b2GbdFF6TkpaBOiuMhSic5FChifRf+qmuokMrSgU4XZ3ykX99hsrGPwEZsJONogUCGva4TqGNqXZzqPe1AhYQZ8LP02Wx0b0FWH0istA1niUCf4qbfYfwufNRva5mBdZc7EtcCAwEAAaOCASswggEnMB8GA1UdIwQYMBaAFGtpPWoYQkrdjwJlOf01JIZ4kRYwMA4GA1UdDwEB/wQEAwIFoDAdBgNVHSUEFjAUBggrBgEFBQcDAQYIKwYBBQUHAwIwGAYDVR0RBBEwD4INd3QuY2l0aWNzLmNvbTBDBgNVHR8EPDA6MDigNqA0hjJodHRwOi8vcmFwaWRzc2wtY3JsLmdlb3RydXN0LmNvbS9jcmxzL3JhcGlkc3NsLmNybDAdBgNVHQ4EFgQUd5CtX4AH4hs6kZS/mfLvDHx004wwDAYDVR0TAQH/BAIwADBJBggrBgEFBQcBAQQ9MDswOQYIKwYBBQUHMAKGLWh0dHA6Ly9yYXBpZHNzbC1haWEuZ2VvdHJ1c3QuY29tL3JhcGlkc3NsLmNydDANBgkqhkiG9w0BAQUFAAOCAQEAXrwTnxdZhE6t+WcqWPz+SwblJG3Q2ei6QnzDR2zVTTNF+Dpo/HvbGidEpNNMCg4at4et+oM1ghKnYbeQ1Uw65VILoeJ9Lb/D4L9dAUT6AjNDkP1eiLcoc2tLKUfKRYQVzBLRC2FwcUl+yaOA40fL9y5gWEPCddG9EWAipHIGb1Cd/PyHCHN+Q3EPDFiLJDHRD0TNmYtKKbD4VTx06aeq1SBGLPpkZH2v2PgzpuZQAoxxD7u6klaPmOF0Lscug210KDjbKuKHojzEHqu5UhPf7hEN5gvLpubkgT1NOLhDHrp/Y5UNrj0/QbeTrS4UJOj1UGFCRc8RT32ki5gIoD9wYQ==";
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                instream.close();
            } catch (IOException e) {}
        }
        return sc;
    }

    public static void configureHttpClient(HttpClientBuilder clientBuilder) {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) {
                    return true;
                }
            }).build();

            clientBuilder.setSSLContext(sslContext);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static SSLConnectionSocketFactory createIgnoreVerifySSL() {
        try {
            SSLContext sc = SSLContext.getInstance("SSLv3");
            // 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
            X509TrustManager trustManager = new X509TrustManager() {
                public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

                public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate, String paramString) {}

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            sc.init(null, new TrustManager[] { trustManager }, null);
            return new SSLConnectionSocketFactory(sc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Map<String, String> commonHeaders() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("Accept",
                "text/html, application/xhtml+xml, application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN");
        map.put("Cache-Control", "no-cache");
        map.put("Connection", "Keep-Alive");
        map.put("Content-Type", "application/x-www-form-urlencoded");
        map.put("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Win64; x64; Trident/4.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E; InfoPath.3)");
        return map;
    }

    public static Map<String, String> commonAjaxHeaders() {
        Map<String, String> map = new TreeMap<String, String>();
        map.put("Accept",
                "application/x-ms-application, image/jpeg, application/json, text/javascript, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Accept-Language", "zh-CN");
        map.put("Cache-Control", "no-cache");
        map.put("Connection", "Keep-Alive");
        map.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        map.put("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; Win64; x64; Trident/4.0; .NET CLR 2.0.50727; SLCC2; .NET CLR 3.5.30729; .NET CLR 3.0.30729; .NET4.0C; .NET4.0E; InfoPath.3)");
        map.put("x-requested-with", "XMLHttpRequest");
        return map;
    }

    public static Map<String, String> setContentTypeJson() {
        Map<String, String> headers = HttpUtilNew.commonAjaxHeaders();
        headers.put("Content-Type", "application/json; charset=utf-8");
        return headers;
    }

    public static String createCookies(List<Cookie> cookies) {
        String cookieStr = "";
        for (Cookie cookie : cookies) {
            cookieStr += (cookie.getName() + "=" + cookie.getValue() + "; ");
        }
        return cookieStr;
    }
}
