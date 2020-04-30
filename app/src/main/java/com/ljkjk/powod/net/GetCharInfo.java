package com.ljkjk.powod.net;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class GetCharInfo implements Runnable {
    private String ctnt;
    private Handler handler;

    public GetCharInfo(String ctnt, Handler handler) {
        this.ctnt = ctnt;
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            Document doc = Jsoup.connect("http://webdict.youzhi.net/index.do")
                    .data("id", "65bd6dc3c1b7488a80dbc0c24fcbf0e5")
                    .data("words", ctnt)
                    .userAgent("Mozilla")
                    .post();

            Message msg = new Message();
            Bundle data = new Bundle();

            // 拿读音
            Elements elsPron = doc.select("li.pingyin");
            Element elPron = elsPron.first();
            data.putString("pron", elPron.text());

            StringBuilder sb = new StringBuilder();

/*
            // 拿到h2：解释标题
            Elements elsH2 = doc.getElementsByTag("h2");
            String h2 = elsH2.first().text();
            sb.append("-");
            sb.append(h2);
            sb.append("-");
            sb.append("\n");
*/

            // 拿到ul：解释内容

            Elements elsMean = doc.select("div.result-main2");
            Element elMean = elsMean.first();
            Elements elsLi = elMean.getElementsByTag("li");
            for (int i = 0; i < elsLi.size(); i++){
                sb.append(i+1);
                sb.append(". ");
                sb.append(elsLi.get(i).text());
                if (i != elsLi.size()-1){
                    sb.append("\n");
                }
            }

            data.putString("mean", sb.toString());
            data.putBoolean("has", true);

            msg.setData(data);
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
