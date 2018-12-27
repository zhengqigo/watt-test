package org.fuelteam.watt.test.http;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.fuelteam.watt.httpclient.RequestExecutor;
import org.fuelteam.watt.lucky.utils.DateUtil;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Test {

    public static void main(String[] args) throws Exception {
        testGetWithParams();
        testPostForm();
    }

    private static void testPostForm() throws Exception {
        String url = "http://srh.bankofchina.com/search/whpj/search.jsp";
        Map<String, String> params = Maps.newHashMap();
        params.put("pjname", "1316");
        params.put("erectDate", "2019-12-22");

        String contents = new RequestExecutor<HttpPost>().build(HttpPost.class).on(url, params, null, null)
                .timeout(1000, 1000).string(null);
        Document doc = Jsoup.parse(contents);
        Elements container = doc.select("div.BOC_main");
        Elements trs = container.select("tr");
        Elements ths = trs.get(0).select("th");
        List<List<String>> list = Lists.newArrayList();
        List<String> thList = Lists.newArrayList();
        for (int i = 0; i < ths.size(); i++) {
            String thText = ths.get(i).text();
            thList.add(thText);
        }
        list.add(thList);
        for (Element element : trs) {
            Elements tds = element.select("td");
            if (tds == null || tds.size() <= 0) continue;
            List<String> tdList = Lists.newArrayList();
            for (int i = 0; i < tds.size(); i++) {
                String tdText = tds.get(i).text();
                tdList.add(tdText);
            }
            list.add(tdList);
        }
        Vardump.print(list);
    }

    private static void testGetWithParams() throws Exception {
        String url = "https://www.exbxg.com:8443/jsonp?";
        Map<String, Object> params = Maps.newHashMap();
        params.put("service", "U_D_FindDeliveryList");
        String dateStr = DateUtil.date2str(new DateTime().minusDays(2).toDate(), "yyyy-MM-dd");
        Map<String, String> body = Maps.newHashMap();
        body.put("breedId", "1");
        body.put("breedName", "电解镍");
        body.put("beginDate", dateStr);
        body.put("endDate", dateStr);
        body.put("status", "delivery");
        params.put("body", body);
        String finalUrl = url + URLEncoder.encode(JSON.toJSONString(params), "UTF-8");
        String contents = new RequestExecutor<HttpGet>().build(HttpGet.class).on(finalUrl, null, null, null)
                .timeout(1000, 1000).string(null);
        Vardump.print(contents);
    }
}
