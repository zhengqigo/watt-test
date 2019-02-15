package org.fuelteam.watt.test.http;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpPost;
import org.fuelteam.watt.httpclient.Proxy;
import org.fuelteam.watt.httpclient.RequestExecutor;
import org.fuelteam.watt.lucky.utils.DateUtil;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.nacos.api.config.annotation.NacosConfigListener;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

@Component
@RefreshScope
public class ReuterAPI {

    private static final Logger logger = LoggerFactory.getLogger(ReuterAPI.class);

    private static final String RTURL_CreateServiceToken1 = "https://api.trkd.thomsonreuters.com/api/TokenManagement/TokenManagement.svc/REST/Anonymous/TokenManagement_1/CreateServiceToken_1";

    private static final String RTURL_GetSimpleData2 = "http://api.trkd.thomsonreuters.com/api/QuoteLists/QuoteLists.svc/REST/QuoteLists_1/GetSimpleData_2";

    private static final String RTURL_RetrieveItem3 = "http://api.trkd.thomsonreuters.com/api/Quotes/Quotes.svc/REST/Quotes_1/RetrieveItem_3";

    private static final String RTURL_GetChain1 = "http://api.trkd.thomsonreuters.com/api/QuoteChains/QuoteChains.svc/REST/QuoteChains_1/GetChain_1";

    private volatile Proxy proxy;
    
    @Autowired
    private RedissonClient redissonClient;
    
    private String PREFIX = "REUTER_KEY_";
    
    @NacosConfigListener(dataId = "PROXY", groupId = "REUTER_GROUP", timeout = 500)
    public void onChangeProxy(String str) throws Exception {
        this.proxy = proxy(str);
        logger.info("Event fired onChangeProxy str? {} this.proxy? {}", str, proxy);
    }

    @NacosConfigListener(dataId = "KEY_TRAIL", groupId = "REUTER_GROUP", timeout = 500)
   public void onChangeKeyTrail(String str) throws Exception {
        Token token = token(str, ReuterKeyEnum.TRAIL);
        String cachedKey = PREFIX + ReuterKeyEnum.TRAIL.getValue();
        // TODO write to cache
        redissonClient.set
        logger.info("Event fired onChangeKeyTrail KEY_TRAIL? {} token? {}", str, token);
    }
    
    @NacosConfigListener(dataId = "KEY_SELECT", groupId = "REUTER_GROUP", timeout = 500)
    public void onChangeKeySelect(String str) throws Exception {
        Token token = token(str, ReuterKeyEnum.SELECT);
        String cachedKey = PREFIX + ReuterKeyEnum.SELECT.getValue();
        // TODO write to cache
        logger.info("Event fired onChangeKeySelect KEY_SELECT? {} token? {}", str, token);
    }

    private Proxy proxy(String str) {
        String[] arrayOfProxy = str.split("|");
        if (arrayOfProxy.length < 3) return null;
        String host = String.valueOf(arrayOfProxy[0].trim());
        Integer port = null;
        try {
            port = Integer.parseInt(arrayOfProxy[1].trim());
        } catch (Exception ex) {
            logger.error("Proxy port is not an integer");
        }
        if (port == null) port = 80;
        String protocol = String.valueOf(arrayOfProxy[2].trim());
        Proxy proxy = new Proxy(host, port, protocol);
        this.proxy = proxy;
        return this.proxy;
    }

    private Token token(String str, ReuterKeyEnum reuterKeyEnum) throws Exception {
        String[] arrayOfRtkey = str.split("|");
        if (arrayOfRtkey.length < 3) return null;
        String applicationId = arrayOfRtkey[0].trim();
        String username = arrayOfRtkey[1].trim();
        String password = arrayOfRtkey[2].trim();
        org.json.JSONObject json = new org.json.JSONObject().put("CreateServiceToken_Request_1", new org.json.JSONObject()
                .put("ApplicationID", applicationId).put("Username", username).put("Password", password));
        Map<String, String> headers = ImmutableMap.of("content-type", "application/json; charset=utf-8");

        String contents = "";
        try {
            contents = new RequestExecutor<HttpPost>().build(HttpPost.class)
                    .on(RTURL_CreateServiceToken1, null, headers, json.toString()).timeout(3000, 3000).string(proxy);
        } catch (Exception ex) {
            logger.error("RtApiUtil token exception", ex);
        }
        if (contents.length() <= 0) return null;

        if (JSONPath.extract(contents, "$.Fault") != null) {
            logger.error("RtApiUtil token failed reason: {}", JSONPath.extract(contents, "$.Fault.Reason.Text.Value"));
            return null;
        }
        String tokenStr = String.valueOf(JSONPath.extract(contents, "$.CreateServiceToken_Response_1.Token"));
        String expiration = String.valueOf(JSONPath.extract(contents, "$.CreateServiceToken_Response_1.Expiration"));
        String now = DateUtil.date2str(new Date(), "yyyy-MM-dd HH:mm:ss.SSS");
        logger.info("RtApiUtil token succeded with {} at {}", expiration, now);

        Token token = new Token(applicationId, username, password, tokenStr, now, reuterKeyEnum);
        return token;
    }
    
    private Token get(ReuterKeyEnum reuterKeyEnum) {
        String cachedKey = PREFIX + reuterKeyEnum.getValue();
        // TODO: get cached token
        return null;
    }

    public JSONArray quotation(List<String> rics, ReuterKeyEnum reuterKeyEnum) {
        List<String> fields = Lists.newArrayList("CF_DATE", "CF_TIME", "CF_LAST", "TRDPRC_1", "CF_OPEN", "OPEN_PRC", "CF_ASK",
                "ASK", "ASKSIZE", "CF_BID", "BID", "BIDSIZE", "UPLIMIT", "LOLIMIT", "NETCHNG_1", "CF_NETCHNG", "CF_HIGH",
                "HIGH_1", "CF_LOW", "LOW_1", "OPINT_1", "GEN_VAL1", "GEN_VAL2", "CF_VOLUME", "ACVOL_1", "TURNOVER", "SETTLE",
                "CF_CLOSE", "HST_CLOSE", "CLOSE_BID", "OPINT_2", "OPINTNC", "DSPLY_NAME", "CF_NAME");

        Map<String, Object> body = ImmutableMap.of("validationMode", "Tolerant", "RICs", ImmutableMap.of("RIC", rics), "FIDs",
                ImmutableMap.of("FID", fields));
        Map<String, Map<String, Object>> request = ImmutableMap.of("GetSimpleData_Request_2", body);
        Token token = get(reuterKeyEnum);
        Map<String, String> headers = ImmutableMap.of("X-Trkd-Auth-Token", token.getToken(), "X-Trkd-Auth-ApplicationID",
                token.getApplicationId(), "content-type", "application/json; charset=utf-8");

        String contents = "";
        try {
            contents = new RequestExecutor<HttpPost>().build(HttpPost.class)
                    .on(RTURL_GetSimpleData2, null, headers, JSON.toJSONString(request)).timeout(3000, 3000).string(proxy);
        } catch (Exception ex) {
            logger.error("RtApiUtil quotation exception", ex);
        }
        if (contents.length() <= 0) return null;

        if (JSONPath.extract(contents, "$.Fault") != null) {
            logger.error("RtApiUtil quotation failed reason: {}", JSONPath.extract(contents, "$.Fault.Reason.Text.Value"));
            return null;
        }
        Object result = JSONPath.extract(contents, "$.GetSimpleData_Response_2.SimpleDataResult.ItemResponse[0].Item");

        JSONArray resultInArray = null;
        if (result != null || result instanceof JSONArray) {
            resultInArray = (JSONArray) result;
        }
        if (resultInArray == null || resultInArray.isEmpty()) {
            logger.error("RtApiUtil quotation failed result null or empty");
            return null;
        }
        String now = DateUtil.date2str(new Date(), "yyyy-MM-dd HH:mm:ss");
        logger.info("RtApiUtil quotation {} succeded result {} at {}", rics.toString(), resultInArray.size(), now);
        return resultInArray;
    }

    public JSONObject stock(String ric, ReuterKeyEnum reuterKeyEnum) {
        String fields = "DSPLY_NAME:NETCHNG_1:OPEN_PRC:LOTSZUNITS:PRIMACT_1:VALUE_DT1:GEN_TEXT16:GEN_VAL1:GEN_VAL2:GEN_VAL3:GEN_VAL4:"
                + "VALUE_TS1:GEN_VAL5:GEN_VAL6:GEN_VAL7:GEN_VAL8:GEN_VAL9:GV5_TEXT:GN_TXT16_2:GN_TXT16_3:GN_TXT16_4:HSTCL2_DAT:CF_ASK:CF_BID:"
                + "CF_CLOSE:CF_DATE:CF_EXCHNG:CF_HIGH:CF_LAST:CF_LOW:CF_NETCHNG:CF_OPEN:CF_SOURCE:CF_TIME:CF_VOLUME:CF_YIELD:CF_NAME:CF_CURRENCY";

        Map<String, Object> params = ImmutableMap.of("Fields", fields, "Scope", "All", "RequestKey",
                Lists.newArrayList(ImmutableMap.of("Name", ric, "NameType", "RIC")));
        Map<String, Object> body = ImmutableMap.of("TrimResponse", false, "IncludeChildItemQoS", false, "ItemRequest", params);
        Map<String, Map<String, Object>> request = ImmutableMap.of("RetrieveItem_Request_3", body);
        Token token = get(reuterKeyEnum);
        Map<String, String> headers = ImmutableMap.of("X-Trkd-Auth-Token", token.getToken(), "X-Trkd-Auth-ApplicationID", token.getApplicationId(),
                "content-type", "application/json; charset=utf-8");

        String contents = "";
        try {
            contents = new RequestExecutor<HttpPost>().build(HttpPost.class)
                    .on(RTURL_RetrieveItem3, null, headers, JSON.toJSONString(request)).timeout(3000, 3000).string(proxy);
        } catch (Exception ex) {
            logger.error("RtApiUtil stock exception", ex);
        }
        if (contents.length() <= 0) return null;

        if (JSONPath.extract(contents, "$.Fault") != null) {
            logger.error("RtApiUtil stock failed reason: {}", JSONPath.extract(contents, "$.Fault.Reason.Text.Value"));
            return null;
        }

        String statusCode = String
                .valueOf(JSONPath.extract(contents, "$.RetrieveItem_Response_3.ItemResponse[0].Item[0].Status.StatusCode"));
        if (!statusCode.equals("0")) {
            logger.error("RtApiUtil stock failed StatusCode: {}", statusCode);
            return null;
        }

        Object result = JSONPath.extract(contents, "$.RetrieveItem_Response_3.ItemResponse[0].Item[0]");
        JSONObject resultInObject = null;
        if (result != null || result instanceof JSONObject) {
            resultInObject = (JSONObject) result;
        }
        String now = DateUtil.date2str(new Date(), "yyyy-MM-dd HH:mm:ss");
        logger.info("RtApiUtil stock {} succeded at {}", ric, now);
        return resultInObject;
        // 参考： https://www.trkd.thomsonreuters.com/SupportSite/TestApi/Op?svc=Quotes_1&op=RetrieveItem_3, 输入ric: 0#MCUSTX-LOC进行测试
    }

    public JSONObject spread(String ric, Long p, Long size, ReuterKeyEnum reuterKeyEnum) {
        if (p == null) p = 1L;
        if (size == null) size = 100L;
        Map<String, Object> paging = ImmutableMap.of("Page", p, "PageSize", size);

        String fields = "CF_LAST:CF_HIGH:CF_LOW:CF_BID:CF_ASK:CF_YIELD:CF_DATE:CF_TIME:CF_VOLUME:CF_CLOSE:CF_OPEN:BIDSIZE:ASKSIZE:BID_TIME:ASK_TIME";
        Map<String, Object> param = ImmutableMap.of("Instrument", ric, "Fields", fields, "Parameters",
                ImmutableMap.of("Paging", paging));
        Map<String, Object> request = ImmutableMap.of("GetChain_Request_1", param);
        Token token = get(reuterKeyEnum);
        Map<String, String> headers = ImmutableMap.of("X-Trkd-Auth-Token", token.getToken(), "X-Trkd-Auth-ApplicationID", token.getApplicationId(),
                "content-type", "application/json; charset=utf-8");

        String contents = "";
        try {
            contents = new RequestExecutor<HttpPost>().build(HttpPost.class)
                    .on(RTURL_GetChain1, null, headers, JSON.toJSONString(request)).timeout(3000, 3000).string(proxy);
        } catch (Exception ex) {
            logger.error("RtApiUtil spread exception", ex);
        }
        if (contents.length() <= 0) return null;

        if (JSONPath.extract(contents, "$.Fault") != null) {
            logger.error("RtApiUtil spread failed reason: {}", JSONPath.extract(contents, "$.Fault.Reason.Text.Value"));
            return null;
        }

        Object result = JSONPath.extract(contents, "$.GetChain_Response_1");
        JSONObject resultInObject = null;
        if (result != null || result instanceof JSONObject) {
            resultInObject = (JSONObject) result;
        }
        String now = DateUtil.date2str(new Date(), "yyyy-MM-dd HH:mm:ss");
        logger.info("RtApiUtil spread {} succeded at {}", ric, now);
        return resultInObject;
    }

    public JSONObject options(String ric, Long p, Long size, String fields, ReuterKeyEnum reuterKeyEnum) {
        if (p == null) p = 1L;
        if (size == null) size = 100L;
        Map<String, Object> paging = ImmutableMap.of("Page", p, "PageSize", size);

        Map<String, Object> param = ImmutableMap.of("Instrument", ric, "Fields", fields, "Parameters",
                ImmutableMap.of("Paging", paging));
        Map<String, Object> request = ImmutableMap.of("GetChain_Request_1", param);
        Token token = get(reuterKeyEnum);
        Map<String, String> headers = ImmutableMap.of("X-Trkd-Auth-Token", token.getToken(), "X-Trkd-Auth-ApplicationID", token.getApplicationId(),
                "content-type", "application/json; charset=utf-8");

        String contents = "";
        try {
            contents = new RequestExecutor<HttpPost>().build(HttpPost.class)
                    .on(RTURL_GetChain1, null, headers, JSON.toJSONString(request)).timeout(3000, 3000).string(proxy);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error("RtApiUtil options exception", ex);
        }
        if (contents.length() <= 0) return null;

        if (JSONPath.extract(contents, "$.Fault") != null) {
            logger.error("RtApiUtil options failed reason: {}", JSONPath.extract(contents, "$.Fault.Reason.Text.Value"));
            return null;
        }

        Object result = JSONPath.extract(contents, "$.GetChain_Response_1");
        JSONObject resultInObject = null;
        if (result != null || result instanceof JSONObject) {
            resultInObject = (JSONObject) result;
        }
        String now = DateUtil.date2str(new Date(), "yyyy-MM-dd HH:mm:ss");
        logger.info("RtApiUtil options {} succeded at {}", ric, now);
        return resultInObject;
    }

    public static void main(String[] args) throws Exception {
        String applicationId = "trkddemoappwm";
        String username = "trkd-demo-wm@thomsonreuters.com";
        String password = "r5s1n27kr";
        
        ReuterAPI api = new ReuterAPI();

        JSONArray quotes = api.quotation(Lists.newArrayList(".SZSC1", "MCU3=LX"), ReuterKeyEnum.TRAIL);
        System.out.println(quotes);

        JSONObject stock = api.stock("0#MCUSTX-LOC", ReuterKeyEnum.TRAIL);
        System.out.println(stock);

        JSONObject spread = api.spread("0#CMCU:", null, null,ReuterKeyEnum.TRAIL);
        System.out.println(spread);

        JSONObject options = api.options("0#MCU+", null, null, "CF_LAST: CF_NAME",ReuterKeyEnum.TRAIL);
        System.out.println(options);
    }
}
