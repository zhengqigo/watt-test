package org.fuelteam.watt.test.http;

import java.util.Date;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.fuelteam.watt.httpclient.RequestExecutor;
import org.fuelteam.watt.lucky.utils.DateUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONPath;
import com.google.common.collect.ImmutableMap;

public class RtApiUtil {

    private static final Logger logger = LoggerFactory.getLogger(RtApiUtil.class);

    // Token请求Url
    private static final String REUTERS_API_URL_TOKEN = "https://api.trkd.thomsonreuters.com/api/TokenManagement/TokenManagement.svc/REST/Anonymous/TokenManagement_1/CreateServiceToken_1";

    private static final Map<String, String> headers = ImmutableMap.of("content-type", "application/json; charset=utf-8");

    public static String fetchToken(String applicationId, String username, String password) throws Exception {
        String json = "{\"CreateServiceToken_Request_1\":{\"ApplicationID\":\"%s\",\"Username\":\"%s\",\"Password\":\"%s\"}}";
        String param = String.format(json, applicationId, username, password);

        String contents = "";
        try {
            contents = new RequestExecutor<HttpPost>().build(HttpPost.class).on(REUTERS_API_URL_TOKEN, null, headers, param)
                    .timeout(3000, 3000).string();
        } catch (Exception ex) {
            logger.error("RtApiUtil fetchToken exception", ex.getMessage());
        }
        if (contents.length() <= 0) return null;
        
        if (JSONPath.extract(contents, "$.Fault") != null) {
            logger.error("RtApiUtil fetchToken failed reason: {}", JSONPath.extract(contents, "$.Fault.Reason.Text.Value"));
            return null;
        }
        String token = String.valueOf(JSONPath.extract(contents, "$.CreateServiceToken_Response_1.Token"));
        String expiration = String.valueOf(JSONPath.extract(contents, "$.CreateServiceToken_Response_1.Expiration"));
        String now = DateUtil.date2str(new Date(), "yyyy-MM-dd HH:mm:ss");
        logger.info("RtApiUtil fetchToken succeded with {} at {}", expiration, now);
        return token;
    }

    public static void main(String[] args) throws Exception {
        String applicationId = "trkddemoappwm";
        String username = "trkd-demo-wm@thomsonreuters.com";
        String password = "r5s1n27kr1";
        fetchToken(applicationId, username, password);
    }
}
