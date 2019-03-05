package org.fuelteam.watt.test.http;

import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.fuelteam.watt.httpclient.RequestExecutor;
import org.fuelteam.watt.lucky.utils.DateUtil;
import org.fuelteam.watt.lucky.utils.SafeCast;
import org.fuelteam.watt.lucky.utils.Vardump;
import org.fuelteam.watt.lucky.utils.WebServiceUtil;
import org.joda.time.DateTime;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component
public class HttpTest {

    public void testPostForm() throws Exception {
        String url = "http://srh.bankofchina.com/search/whpj/search.jsp";
        Map<String, String> params = Maps.newHashMap();
        params.put("pjname", "1316");
        params.put("erectDate", "2019-12-22");

        Pair<Integer, String> contents = new RequestExecutor<HttpPost>().build(HttpPost.class)
                .on(url, params, null, Maps.newHashMap()).timeout(1000, 1000).string();
        Integer code = SafeCast.cast(contents.getLeft()).to(Integer.class).orElse(null);
        if (code == null || code.intValue() != 200) return;
        Document doc = Jsoup.parse(String.valueOf(contents.getRight()));
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

    public void testGetWithParams() throws Exception {
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
        Pair<Integer, String> contents = new RequestExecutor<HttpGet>().build(HttpGet.class)
                .on(finalUrl, null, null, Maps.newHashMap()).timeout(1000, 1000).string();
        Integer code = SafeCast.cast(contents.getLeft()).to(Integer.class).orElse(null);
        if (code == null || code.intValue() != 200) return;
        Vardump.print(String.valueOf(contents.getRight()));
    }

    public void testAsmx() throws Exception {
        String format = "<a>%s</a><b>%s</b>";
        String params = String.format(format, "k0NHVwzZwwbefmiPKX5SvBgDfQAFXrL8xPVAfSgZqu1y87i5Sy3jWlEtL4oJfkNZ", 1);

        String envelope = WebServiceUtil.prepare("E", params);

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "text/xml;charset=UTF-8");
        Pair<Integer, String> response = WebServiceUtil.post("http://101.132.157.43:8023/Service1.asmx", headers, envelope, 1000,
                1000);
        Integer code = SafeCast.cast(response.getLeft()).to(Integer.class).orElse(null);
        if (code == null || code.intValue() != 200) return;
        String responseXml = SafeCast.cast(response.getRight()).to(String.class).orElse("");
        Pattern p = Pattern.compile("(?<=<EResult>).*(?=</EResult>)");
        Matcher m = p.matcher(responseXml);
        if (m.find()) Vardump.print(m.group(0));
    }
}
