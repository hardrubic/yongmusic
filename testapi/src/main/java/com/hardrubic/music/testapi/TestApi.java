package com.hardrubic.music.testapi;

import com.google.gson.Gson;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class TestApi {
    public static final String PARAMS = "encText";
    public static final String ENC_SEC_KEY = "encSecKey";

    public static void main(String[] args) throws Exception {
        Connection.Response
                response2 = Jsoup.connect("http://music.163.com/weapi/v3/song/detail")
                .userAgent("Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:57.0) Gecko/20100101 Firefox/57.0")
                .header("Accept", "*/*")
                .header("Cache-Control", "no-cache")
                .header("Connection", "keep-alive")
                .header("Host", "music.163.com")
                .header("Accept-Language", "zh-CN,en-US;q=0.7,en;q=0.3")
                .header("DNT", "1")
                .header("Pragma", "no-cache")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .data(getDatas())
                .method(Connection.Method.POST)
                .ignoreContentType(true)
                .timeout(10000)
                .execute();
        String result = response2.body();
        System.out.println();
        System.out.println("result:");
        System.out.println(result);
        System.out.println();
    }

    static class Id {
        long id;

        public Id(long id) {
            this.id = id;
        }
    }

    public static String getParamStr() {
        List<Id> ids = new ArrayList<>();
        ids.add(new Id(298283L));
        ids.add(new Id(298284L));

        HashMap<String, String> param = new HashMap();
        param.put("c", new Gson().toJson(ids));
        return new Gson().toJson(param);
    }

    public static HashMap<String, String> getDatas() {
        Invocable inv = null;
        try {
            Path path = Paths.get("testapi/netease_encrypt.js");
            byte[] bytes = Files.readAllBytes(path);
            String js = new String(bytes);
            ScriptEngineManager factory = new ScriptEngineManager();
            ScriptEngine engine = factory.getEngineByName("JavaScript");
            engine.eval(js);
            inv = (Invocable) engine;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ScriptException e) {
            e.printStackTrace();
        }

        String paramStr = getParamStr();
        System.out.println("param:");
        System.out.println(paramStr);
        try {
            ScriptObjectMirror so = (ScriptObjectMirror) inv.invokeFunction("myFunc", paramStr);
            String params = so.get(PARAMS).toString();
            String encSecKey = so.get(ENC_SEC_KEY).toString();
            System.out.println();
            System.out.println("encrypt:");
            System.out.println("params:" + params);
            System.out.println("value:" + encSecKey);

            HashMap<String, String> datas = new HashMap<>();
            datas.put("params", params);
            datas.put("encSecKey", encSecKey);
            return datas;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
