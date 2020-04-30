package com.ljkjk.powod.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

public class PinYinUtils {
    public static String getPinYin(String hanzi) {
        StringBuilder pinyin = new StringBuilder();

        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.UPPERCASE);
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);

        char[] arr = hanzi.toCharArray();
        for (char anArr : arr) {
            if (Character.isWhitespace(anArr)) continue;

            if (anArr > 127) {
                try {
                    String[] pinyinArr = PinyinHelper.toHanyuPinyinStringArray(anArr, format);

                    if (pinyinArr != null) {
                        pinyin.append(pinyinArr[0]);
                    } else {
                        pinyin.append(anArr);
                    }
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                    pinyin.append(anArr);
                }
            } else {
                pinyin.append(anArr);
            }
        }
        return pinyin.toString();
    }
}