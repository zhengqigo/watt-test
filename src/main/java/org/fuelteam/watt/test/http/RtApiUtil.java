package org.fuelteam.watt.test.http;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class RtApiUtil {

    private static final Logger logger = LoggerFactory.getLogger(RtApiUtil.class);

    // Token请求Url
    private static final String REUTERS_API_URL_TOKEN = "https://api.trkd.thomsonreuters.com/api/TokenManagement/TokenManagement.svc/REST/Anonymous/TokenManagement_1/CreateServiceToken_1";

    // Quotation请求Url
    private static final String REUTERS_API_URL_QUOTATION = "http://api.trkd.thomsonreuters.com/api/QuoteLists/QuoteLists.svc/REST/QuoteLists_1/GetSimpleData_2";

    // Stock请求Url
    private static final String REUTERS_API_URL_STOCK = "http://api.trkd.thomsonreuters.com/api/Quotes/Quotes.svc/REST/Quotes_1/RetrieveItem_3";

    // LME请求url
    private static final String REUTERS_API_URL_CHAIN = "http://api.trkd.thomsonreuters.com/api/QuoteChains/QuoteChains.svc/REST/QuoteChains_1/GetChain_1";

    public static String fetchToken(String applicationId, String username, String password) {
        Map<String, Object> param = new HashMap<String, Object>();
        Map<String, String> req = new HashMap<String, String>();
        req.put("ApplicationID", applicationId);
        req.put("Username", username);
        req.put("Password", password);
        param.put("CreateServiceToken_Request_1", req);

        HttpUtilNewModel response = HttpUtilNew.post(HttpUtilNew.setContentTypeJson(),
                new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))}, REUTERS_API_URL_TOKEN,
                HttpUtilNew.REQ_PARAMS_ENTITY);

        if (response.getStatusCode() != 200) {
            logger.error("RtApi getToken failed\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(response));
            return null;
        }

        JSONObject ret = JSON.parseObject(response.getHtml());
        if (ret.getJSONObject("Fault") != null) {
            logger.error("RtApi getToken failed\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(response));
            return null;
        }
        return ret.getJSONObject("CreateServiceToken_Response_1").getString("Token");
    }

    @SuppressWarnings("serial")
    public static JSONArray fetchQuotes(List<String> ricList, String token, String applicationId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("GetSimpleData_Request_2", new HashMap<String, Object>() {
            {

                // 数据查询模式: Tolerant如果传入的合约为无效不影响整个查询。
                // Strict一旦传入的合约列表中存在无效的数据,整个查询失败,返回的参数中会有无效的合约列表。
                put("validationMode", "Tolerant");

                // 控制要查哪些合约的数据
                put("RICs", new HashMap<String, Object>() {
                    {
                        put("RIC", ricList);
                    }
                });

                // 控制要返回哪些字段
                put("FIDs", new HashMap<String, Object>() {
                    {
                        put("FID", new ArrayList<String>() {
                            {
                                add("CF_DATE");
                                add("CF_TIME");

                                add("CF_LAST");
                                add("TRDPRC_1");

                                add("CF_OPEN");
                                add("OPEN_PRC");

                                add("CF_ASK");
                                add("ASK");
                                add("ASKSIZE");

                                add("CF_BID");
                                add("BID");
                                add("BIDSIZE");

                                add("UPLIMIT");
                                add("LOLIMIT");

                                add("NETCHNG_1");
                                add("CF_NETCHNG");

                                add("CF_HIGH");
                                add("HIGH_1");
                                add("CF_LOW");
                                add("LOW_1");

                                add("OPINT_1");

                                add("GEN_VAL1");
                                add("GEN_VAL2");

                                add("CF_VOLUME");
                                add("ACVOL_1");

                                add("TURNOVER");

                                add("SETTLE");

                                add("CF_CLOSE");
                                add("HST_CLOSE");
                                add("CLOSE_BID");

                                add("OPINT_2");
                                add("OPINTNC");

                                add("DSPLY_NAME");
                                add("CF_NAME");
                            }
                        });
                    }
                });
            }
        });

        Map<String, String> headers = HttpUtilNew.setContentTypeJson();
        headers.put("X-Trkd-Auth-Token", token);
        headers.put("X-Trkd-Auth-ApplicationID", applicationId);
        NameValuePair[] nnvps = new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))};
        HttpUtilNewModel page = HttpUtilNew.post(headers, nnvps, REUTERS_API_URL_QUOTATION, HttpUtilNew.REQ_PARAMS_ENTITY);

        String retJson = page.getHtml();
        if (retJson == null) {
            logger.error("RtApi fetchQuotes null\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(page));
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());
        if (ret.getJSONObject("Fault") != null) {
            logger.error("RtApi fetchQuotes failed\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(page));
            return null;
        }
        JSONArray resObj = ret.getJSONObject("GetSimpleData_Response_2").getJSONObject("SimpleDataResult")
                .getJSONArray("ItemResponse").getJSONObject(0).getJSONArray("Item");
        if (resObj == null || resObj.isEmpty()) {
            logger.error("RtApi fetchQuotes failed\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(page));
            return null;
        }
        return resObj;
    }

    @SuppressWarnings("serial")
    public static JSONObject fetch(String ric, String token, String applicationId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("RetrieveItem_Request_3", new HashMap<String, Object>() {
            {
                put("ItemRequest", new HashMap<String, Object>() {
                    {
                        put("Fields",
                                "DSPLY_NAME:NETCHNG_1:OPEN_PRC:LOTSZUNITS:PRIMACT_1:VALUE_DT1:GEN_TEXT16:GEN_VAL1:GEN_VAL2:GEN_VAL3:GEN_VAL4:VALUE_TS1:GEN_VAL5:GEN_VAL6:GEN_VAL7:GEN_VAL8:GEN_VAL9:GV5_TEXT:GN_TXT16_2:GN_TXT16_3:GN_TXT16_4:HSTCL2_DAT:CF_ASK:CF_BID:CF_CLOSE:CF_DATE:CF_EXCHNG:CF_HIGH:CF_LAST:CF_LOW:CF_NETCHNG:CF_OPEN:CF_SOURCE:CF_TIME:CF_VOLUME:CF_YIELD:CF_NAME:CF_CURRENCY");

                        put("RequestKey", new ArrayList<Object>() {
                            {
                                add(new HashMap<String, Object>() {
                                    {
                                        put("Name", ric);
                                        put("NameType", "RIC");
                                    }
                                });
                            }
                        });

                        put("Scope", "All");
                    }
                });
                put("TrimResponse", false);
                put("IncludeChildItemQoS", false);
            }
        });

        Map<String, String> headers = HttpUtilNew.setContentTypeJson();
        headers.put("X-Trkd-Auth-Token", token);
        headers.put("X-Trkd-Auth-ApplicationID", applicationId);
        NameValuePair[] nnvps = new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))};
        HttpUtilNewModel page = HttpUtilNew.post(headers, nnvps, REUTERS_API_URL_STOCK, HttpUtilNew.REQ_PARAMS_ENTITY);

        String retJson = page.getHtml();
        if (retJson == null) {
            logger.error("RtApi fetchStocks null\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(page));
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());

        JSONObject item = ret.getJSONObject("RetrieveItem_Response_3").getJSONArray("ItemResponse").getJSONObject(0)
                .getJSONArray("Item").getJSONObject(0);

        String code = item.getJSONObject("Status").getString("StatusCode");
        if (!code.equals("0")) {
            logger.error("RtApi fetchStocks failed\nParameters: {}\nResult: {}", JSON.toJSON(param), JSON.toJSON(page));
            return null;
        }

        // 参考： https://www.trkd.thomsonreuters.com/SupportSite/TestApi/Op?svc=Quotes_1&op=RetrieveItem_3, 输入ric: 0#MCUSTX-LOC进行测试
        return item;
    }

    // 获取1分钟、5分钟、半小时、1小时序列数据
    @SuppressWarnings("serial")
    public static JSONArray fetchIntradayTimeSeries(String ric, String token, String applicationId, String startTime,
                                                    String endTime) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("GetIntradayTimeSeries_Request_4", new HashMap<String, Object>() {
            {
                put("Symbol", ric);
                put("StartTime", startTime);
                put("EndTime", endTime);
                put("Interval", "MINUTE"); // The interval between samples. The choices are: MINUTE 5MINUTES 30MINUTES HOUR
                put("TrimResponse", true);
                put("Field", new ArrayList<String>() {
                    {
                        add("OPEN");
                        add("HIGH");
                        add("LOW");
                        add("CLOSE");
                        add("VOLUME");
                        add("BID");
                        add("ASK");
                    }
                });
            }
        });

        Map<String, String> headers = HttpUtilNew.setContentTypeJson();
        headers.put("X-Trkd-Auth-Token", token);
        headers.put("X-Trkd-Auth-ApplicationID", applicationId);
        NameValuePair[] nnvps = new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))};
        HttpUtilNewModel page = HttpUtilNew.post(headers, nnvps,
                "http://api.trkd.thomsonreuters.com/api/TimeSeries/TimeSeries.svc/REST/TimeSeries_1/GetIntradayTimeSeries_4",
                HttpUtilNew.REQ_PARAMS_ENTITY);

        String retJson = page.getHtml();
        if (retJson == null) {
            logger.error("RtApi fetchIntradayTimeSeries html null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());

        if (ret.getJSONObject("Fault") != null) {
            logger.error("RtApi fetchIntradayTimeSeries Fault not null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }

        JSONObject response4 = ret.getJSONObject("GetIntradayTimeSeries_Response_4");
        if (response4 == null) {
            logger.error("RtApi fetchIntradayTimeSeries Response_4 null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }
        JSONArray item = response4.getJSONArray("R");
        if (item == null || item.isEmpty()) {
            logger.error("RtApi fetchIntradayTimeSeries failed\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }

        // 参考： https://www.trkd.thomsonreuters.com/SupportSite/TestApi/Op?svc=TimeSeries_1&op=GetIntradayTimeSeries_4, 输入ric:
        // MCU3=LX进行测试
        return item;
    }

    // 获取1天，1周，1月，1季，1年度序列数据
    @SuppressWarnings("serial")
    public static JSONArray fetchInterdayTimeSeries(String ric, String token, String applicationId, String startTime,
                                                    String endTime) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("GetInterdayTimeSeries_Request_4", new HashMap<String, Object>() {
            {
                put("Symbol", ric);
                put("StartTime", startTime);
                put("EndTime", endTime);
                put("Interval", "DAILY"); // The interval between samples. The choices are: MINUTE 5MINUTES 30MINUTES HOUR
                put("TrimResponse", true);
                put("Field", new ArrayList<String>() {
                    {
                        add("OPEN");
                        add("HIGH");
                        add("LOW");
                        add("CLOSE");
                        add("VOLUME");
                        add("BID");
                        add("ASK");
                    }
                });
            }
        });

        Map<String, String> headers = HttpUtilNew.setContentTypeJson();
        headers.put("X-Trkd-Auth-Token", token);
        headers.put("X-Trkd-Auth-ApplicationID", applicationId);
        NameValuePair[] nnvps = new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))};
        HttpUtilNewModel page = HttpUtilNew.post(headers, nnvps,
                "http://api.trkd.thomsonreuters.com/api/TimeSeries/TimeSeries.svc/REST/TimeSeries_1/GetInterdayTimeSeries_4",
                HttpUtilNew.REQ_PARAMS_ENTITY);

        String retJson = page.getHtml();
        if (retJson == null) {
            logger.error("RtApi fetchIntradayTimeSeries html null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());

        if (ret.getJSONObject("Fault") != null) {
            logger.error("RtApi fetchIntradayTimeSeries Fault not null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }

        JSONObject response4 = ret.getJSONObject("GetInterdayTimeSeries_Response_4");
        if (response4 == null) {
            logger.error("RtApi fetchIntradayTimeSeries Response_4 null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }
        JSONArray item = response4.getJSONArray("R");
        if (item == null || item.isEmpty()) {
            logger.error("RtApi fetchIntradayTimeSeries failed\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }

        // 参考： https://www.trkd.thomsonreuters.com/SupportSite/TestApi/Op?svc=TimeSeries_1&op=GetInterdayTimeSeries_4, 输入ric:
        // MCU3=LX进行测试
        return item;
    }

    // LME路透
    @SuppressWarnings("serial")
    public static JSONObject fetchChainPage(String ric, String token, String applicationId, Long currentPage, Long pageSize) {
        if (currentPage == null) {
            currentPage = 1L;
        }
        if (pageSize == null) {
            pageSize = 100L;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("Page", currentPage);
        map.put("PageSize", pageSize);


        Map<String, Object> param = new HashMap<String, Object>();
        param.put("GetChain_Request_1", new HashMap<String, Object>() {
            {
                put("Instrument", ric);
                put("Fields", "CF_LAST: CF_HIGH: CF_LOW: CF_BID: CF_ASK: CF_YIELD: CF_DATE: CF_TIME: CF_VOLUME: CF_CLOSE: CF_OPEN: BIDSIZE: ASKSIZE:BID_TIME:ASK_TIME");
                put("Parameters", new HashMap<String, Object>() {
                    {
                        put("Paging", map);
                    }
                });
            }
        });

        Map<String, String> headers = HttpUtilNew.setContentTypeJson();
        headers.put("X-Trkd-Auth-Token", token);
        headers.put("X-Trkd-Auth-ApplicationID", applicationId);
        NameValuePair[] nnvps = new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))};
        HttpUtilNewModel page = HttpUtilNew.post(headers, nnvps, REUTERS_API_URL_CHAIN, HttpUtilNew.REQ_PARAMS_ENTITY);

        String retJson = page.getHtml();
        if (retJson == null) {
            logger.error("RtApi fetchIntradayTimeSeries html null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());

        if (ret.getJSONObject("Fault") != null) {
            logger.error("RtApi fetchIntradayTimeSeries Fault not null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }

        JSONObject response4 = ret.getJSONObject("GetChain_Response_1");

        return response4;
    }

    // LME期权
    public static JSONObject fetchOPtions(String ric, String token, String applicationId, Long currentPage, Long pageSize, String fields) {
        if (currentPage == null) {
            currentPage = 1L;
        }
        if (pageSize == null) {
            pageSize = 100L;
        }
        HashMap<String, Object> map = new HashMap<>();
        map.put("Page", currentPage);
        map.put("PageSize", pageSize);


        Map<String, Object> param = new HashMap<String, Object>();
        param.put("GetChain_Request_1", new HashMap<String, Object>() {
            {
                put("Instrument", ric);
                put("Fields", fields);
                put("Parameters", new HashMap<String, Object>() {
                    {
                        put("Paging", map);
                    }
                });
            }
        });

        Map<String, String> headers = HttpUtilNew.setContentTypeJson();
        headers.put("X-Trkd-Auth-Token", token);
        headers.put("X-Trkd-Auth-ApplicationID", applicationId);
        NameValuePair[] nnvps = new NameValuePair[]{new BasicNameValuePair("param", JSON.toJSONString(param))};
        HttpUtilNewModel page = HttpUtilNew.post(headers, nnvps, REUTERS_API_URL_CHAIN, HttpUtilNew.REQ_PARAMS_ENTITY);

        String retJson = page.getHtml();
        if (retJson == null) {
            logger.error("RtApi fetchIntradayTimeSeries html null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());

        if (ret.getJSONObject("Fault") != null) {
            logger.error("RtApi fetchIntradayTimeSeries Fault not null\nParameters: {}\nResult: {}", JSON.toJSON(param),
                    JSON.toJSON(page));
            return null;
        }

        JSONObject response4 = ret.getJSONObject("GetChain_Response_1");

        return response4;
    }

    // 上海交易所期权

    public static JSONObject fetchOPtionsSHFE(String url) {
        HttpUtilNewModel page = HttpUtilNew.get(url);
        if (page.getStatusCode() != 200) {
            return null;
        }
        JSONObject ret = JSON.parseObject(page.getHtml());
        return ret;
    }

    public static void main(String[] args) {

        String applicationId = "trkddemoappwm";
        String username = "trkd-demo-wm@thomsonreuters.com";
        String password = "h3n4z91za";
        String token = fetchToken(applicationId, username, password);
        // .DJI/CNH=/CNY=
        // SHHCU8
        // GC

        //JSONArray array = fetchInterdayTimeSeries("PLN9", token, applicationId, "2018-07-01T00:00:00", "2018-07-06T23:00:00");
        System.out.println(token);
    }

}
