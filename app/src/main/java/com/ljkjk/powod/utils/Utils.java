package com.ljkjk.powod.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.ljkjk.powod.R;

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

    // 日期转字符串
    public static String date2String(Date date){
        return df.format(date);
    }

    // 字符串转日期
    public static Date string2Date(String string){
        Date date = null;
        try{
            date = new Date(df.parse(string).getTime());
        } catch (Exception e){
            e.printStackTrace();
        }
        return date;
    }

    // 设置服务器地址
    public static void setUrl(String ip, String port, String proj){
        Utils.ip = ip;
        Utils.port = port;
        Utils.proj = proj;
        MAIN_URL = String.format("http://%s:%s/%s/", ip, port, proj);
        UPLOAD_URL = MAIN_URL + "upload";
        DOWNLOAD_URL = MAIN_URL + "download";
    }

    // 验证URL是否合法（当前为验证三个项目是否都填了)
    public static boolean isUrlValid(){
        return !(ip.isEmpty() && port.isEmpty() && proj.isEmpty());
    }

    // 计算字符串汉字的个数
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

    // 判断网络是否可用（待更新）
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

    // 计算toolbar高度，用来控制fab
    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    // 收起软键盘
    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        View focusedView = activity.getCurrentFocus();
        // 防止空指针
        if (focusedView != null) {
            inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }

    public static boolean isAlpha(char c) {
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    public static char toUpperCase(char c) {
        if (c >= 'a' && c <= 'z') {
            c -= 32;
        }
        return c;
    }
}
