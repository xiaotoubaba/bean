package com.jinan.xiaodou.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

/**
 * Created by Yale on 2017/4/18.
 */

public class WeatherCondition {

    public static String getIpUrl = "http://ip-api.com/json";
    public String getAddress = "http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=json&ip=";
    public String cityCode = "https://code.csdn.net/snippets/621277/master/blog_20150317_1_5052706/raw";
    public String weatherUrl = "http://wthrcdn.etouch.cn/WeatherApi?citykey=";
    //   http://m.weather.com.cn/mweather/101280601.shtml
    private String locaInfo;
    public SearchWeatherListner weatherListner;
    private String city;

    public interface SearchWeatherListner {

        public void weatherInfo(String city, String tem, String shidu, String pm);

        public void error(Exception e);

    }


    public WeatherCondition(SearchWeatherListner weatherListner) {
        super();
        this.weatherListner = weatherListner;
    }


    public void getWebIp(final String urlStr) {
        new Thread() {
            public void run() {
                String strForeignIP = "";
                try {
                    URL url = new URL(urlStr);

                    BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));

                    String s = "";
                    StringBuffer sb = new StringBuffer("");
                    while ((s = br.readLine()) != null) {
                        sb.append(s + "\r\n");
                    }
                    br.close();

                    String webContent = "";
                    webContent = sb.toString();
                    System.out.println("webContent:" + webContent);
                    if (urlStr.equals(getIpUrl)) {//1 得到ip
                        getIp(webContent);
                    } else if (urlStr.equals(cityCode)) {//3 得到所有cityCode
                        getAddress(locaInfo, new JSONObject(webContent));
                    } else if (urlStr.indexOf(weatherUrl) != -1) {//4 得到天气情况
                        webContent = sb.toString();
                        parserXml(webContent);
                    } else {//2 得到本地信息
                        locaInfo = webContent;
                        getWebIp(cityCode);
                    }
                } catch (Exception e) {
                    weatherListner.error(e);
                    e.printStackTrace();
                }
            }

            ;
        }.start();

    }

    public void parserXml(String str) {
        String high = str.substring(str.indexOf("<high>"), str.indexOf("</high>")).replace("<high>", "");
        String low = str.substring(str.indexOf("<low>"), str.indexOf("</low>")).replace("<low>", "");
        String type = str.substring(str.indexOf("<type>"), str.indexOf("</type>")).replace("<type>", "");

        weatherListner.weatherInfo(city, high, low, type);
    }


    public void getAddress(String str, JSONObject cityJson) {
        try {
            JSONObject json = new JSONObject(str);
            String country = json.getString("country");
            String province = json.getString("province");
            city = json.getString("city");
            JSONArray ja_province = cityJson.getJSONArray("城市代码");
            for (int i = 0; i < ja_province.length(); i++) {
                JSONObject jo_province = ja_province.getJSONObject(i);
                if (jo_province.getString("省").equals(province)) {
                    JSONArray ja_city = jo_province.getJSONArray("市");
                    for (int j = 0; j < ja_city.length(); j++) {
                        JSONObject jo_city = ja_city.getJSONObject(j);
                        if (jo_city.getString("市名").equals(city)) {
                            String code = jo_city.getString("编码");
                            getWebIp(weatherUrl + code);
                            System.out.println("code:" + code);
                            break;
                        }

                    }

                    break;
                }

            }


        } catch (JSONException e) {
            weatherListner.error(e);
            e.printStackTrace();
        }
    }

    public void getIp(String str) {
        try {
            JSONObject json = new JSONObject(str);
            String ip = json.getString("query");
            getWebIp(getAddress + ip);
        } catch (JSONException e) {
            weatherListner.error(e);
            e.printStackTrace();
        }


    }

}
