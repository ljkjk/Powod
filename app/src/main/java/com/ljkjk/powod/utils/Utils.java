package com.ljkjk.powod.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    private static String ip = "";
    private static String port = "";
    private static String proj = "";
    public static String MAIN_URL = "";// = "http://192.168.124.5:8080/PowodServer_war_exploded/";
    public static String UPLOAD_URL = MAIN_URL + "upload";
    public static String DOWNLOAD_URL = MAIN_URL + "download";

    private static final SimpleDateFormat df = new SimpleDateFormat("yy-MM-dd");

    public static String date2String(Date date){
        return df.format(date);
    }

    public static Date string2Date(String string){
        Date date = null;
        try{
            date = new Date(df.parse(string).getTime());
        } catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    public static void setUrl(String ip, String port, String proj){
        Utils.ip = ip;
        Utils.port = port;
        Utils.proj = proj;
        MAIN_URL = String.format("http://%s:%s/%s/", ip, port, proj);
        UPLOAD_URL = MAIN_URL + "upload";
        DOWNLOAD_URL = MAIN_URL + "download";
    }

    public static boolean isUrlValid(){
        return !(ip.isEmpty() && port.isEmpty() && proj.isEmpty());
    }

    public static int getZhCharCount(String text) {
        int count = 0;
        String regEx = "[\u4e00-\u9fa5]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(text);
        while(m.find()) {
            count++;
        }
        return count;
    }

    public static boolean isNetworkAvailable(Activity activity)
    {
        Context context = activity.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null) {
            return false;
        } else {
            // 获取所有NetworkInfo对象
            NetworkInfo[] networkInfo = cm.getAllNetworkInfo();
            for (NetworkInfo info: networkInfo){
                if (info.getState() == NetworkInfo.State.CONNECTED){
                    return true;
                }
            }
        }
        return false;
    }
}
