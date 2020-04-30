package com.ljkjk.powod.utils;

import com.ljkjk.powod.entity.Detail;
import com.ljkjk.powod.entity.Word;

import java.util.LinkedList;
import java.util.List;

public class DetailsListUtils {
    private static List<Detail> detailsList = new LinkedList<>();

    public static List<Detail> list(){
        return detailsList;
    }

    public static String getContent(String title){
        for (Detail di: detailsList){
            if (di.getTitle().contentEquals(title)){
                return di.getContent();
            }
        }
        return "";
    }

    public static void add(Detail di){
        detailsList.add(di);
    }

    public static void setDetailsList(Word word){
        clear();
        String mean = word.getMean();
        String tags = word.getTags();
        String syno = word.getSyno();
        String anto = word.getAnto();
        String freq = String.valueOf(word.getFreq());
        String addt = Utils.date2String(word.getAddt());

        if (!(mean == null || mean.isEmpty())){
            add(new Detail("词义", mean));
        }
        if (!(tags == null || tags.isEmpty())){
            add(new Detail("标签", tags));
        }
        if (!(syno == null || syno.isEmpty())){
            add(new Detail("近义词", syno));
        }
        if (!(anto == null || anto.isEmpty())){
            add(new Detail("反义词", anto));
        }
        add(new Detail("使用频次", freq));
        add(new Detail("创建日期", addt));
    }

    public static void clear(){
        detailsList.clear();
    }
}
