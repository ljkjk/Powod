package com.ljkjk.powod.net;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

public class GetWordInfo implements Runnable {

    private String ctnt;
    private Handler handler;

    public GetWordInfo(String ctnt, Handler handler) {
        this.ctnt = ctnt;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Message msg = new Message();
            Bundle data = new Bundle();

            if (getFromKKC(data) || getFromBD(data)){
                data.putBoolean("has", true);
            } else {
                data.putBoolean("has", false);
            }

            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean getFromKKC(Bundle data) throws IOException {
        Document doc = Jsoup.connect("http://webdict.youzhi.net/index.do")
                .data("id", "65bd6dc3c1b7488a80dbc0c24fcbf0e5")
                .data("words", ctnt)
                .userAgent("Mozilla")
                .post();

        // 如果没有结果，直接返回
        Elements elsNonResult = doc.select("p.noresult-text");
        if (!elsNonResult.isEmpty()){
            return false;
        }

        // 拿details，包含读音和可能的近反义词
        Elements elsDetails = doc.select("div.details");
        Element elDetail = elsDetails.first();
        Elements elsP = elDetail.getElementsByTag("p");
        List<String> detail = elsP.eachText();
        // 拿读音
        data.putString("pron", detail.get(0).split("：")[1]);
        // 拿近反义词
        data.putString("syno", "");
        data.putString("anto", "");
        for (int i = 1; i < detail.size(); i++) {
            String[] str = detail.get(i).split("：");
            if (str[0].contentEquals("同义词")){
                data.putString("syno", str[1].replaceAll(",", " "));
            } else {
                data.putString("anto", str[1].replaceAll(",", " "));
            }
        }

        // 拿到h2：解释标题
        Elements elsH2 = doc.getElementsByTag("h2");
        String[] h2List = new String[3];
        for (int i = 0; i < elsH2.size(); i++) {
            h2List[i] = "-" + elsH2.get(i).text() + "-";
        }
        // 拿到ul：解释内容
        StringBuilder sb = new StringBuilder();
        Elements elsUl = doc.getElementsByTag("ul");
        for (int i = 0; i < elsUl.size(); i++) {
            // 添加标题
            sb.append(h2List[i]);
            sb.append("\n");
            // 添加当前标题下具体解释
            Elements elsLi = elsUl.get(i).getElementsByTag("li");
            for (int j = 0; j < elsLi.size(); j++) {
                sb.append(j+1);
                sb.append(". ");
                sb.append(elsLi.get(j).text());
                if (!(i == elsUl.size()-1 && j == elsLi.size()-1)){
                    sb.append("\n");
                }
            }
            if (i != elsUl.size()-1){
                sb.append("\n");
            }
        }
        data.putString("mean", sb.toString());
        return true;
    }

    @SuppressLint("DefaultLocale")
    private boolean getFromBD(Bundle data) throws IOException {
        Document doc = Jsoup.connect("https://dict.baidu.com/s?ptype=zici&wd="+ctnt)
                //.data("ptype", "zici")
                //.data("wd", ctnt)
                .userAgent("Mozilla")
                .post();

        System.out.println(doc);
        // 拿pinyin
        Element elPinyin = doc.getElementById("pinyin");
        if (elPinyin != null) {
            Element elPron = elPinyin.getElementsByTag("b").first();
            data.putString("pron", elPron.text());
        } else {
            // 没有拼音，等于没有
            return false;
        }

        // 拿解释，快快查没有的，只拿基础解释
        int index = 1;
        StringBuilder sb = new StringBuilder();
        Element elBasicMean = doc.getElementById("basicmean-wrapper");
        if (elBasicMean != null) {
            Element elBasicP = elBasicMean.getElementsByTag("p").first();
            sb.append(String.format("%d. %s", index, elBasicP.text()));
            index++;
        }

        Element elBaikeMean = doc.getElementById("baike-wrapper");
        if (elBaikeMean != null) {
            Element elBaikeP = elBaikeMean.getElementsByTag("p").first();
            if (index != 1) {
                sb.append("\n");
            }
            sb.append(String.format("%d. %s", index, elBaikeP.text().replace("查看百科", "")));
        } else if (elBasicMean == null) {
            return false;
        }

        data.putString("mean", sb.toString());
        return true;
    }
}

