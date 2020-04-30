package com.ljkjk.powod.utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.ljkjk.powod.SortType;
import com.ljkjk.powod.entity.Word;

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

public class WordListUtils {
    private static List<Word> wordList = new LinkedList<>();

    public static List<Word> list() {
        return wordList;
    }

    public static List<Word> fullList(DatabaseUtils db) {
        return db.getAllWords();
    }

    public static boolean add(Word word) {
        System.out.println("add word: "+word.getCtnt());
        return wordList.add(word);
    }

    public static boolean delete(String ctnt) {
        return wordList.remove(get(ctnt));
    }

    public static void clear() {
        wordList.clear();
    }

    public static Word get(int position) {
        return wordList.get(position);
    }

    public static Word get(String ctnt) {
        for (Word word: wordList){
            if (word.getCtnt().contentEquals(ctnt)) {
                return word;
            }
        }
        return null;
    }

    public static void getWordList(DatabaseUtils db) {
        clear();
        wordList = db.getAllWords();
    }

    public static List<Word> getWordListByTag(DatabaseUtils db, String TAG){
        List<Word> result = new LinkedList<>();
        List<Word> tempWordList = db.getAllWords();
        for (Word word: tempWordList){
            String[] tags = word.getTags().split(" ");
            for (String tag: tags){
                if (TAG.contentEquals(tag)){
                    result.add(word);
                    break;
                }
            }
        }
        return result;
    }

    public static List<Word> getWordListByKey(DatabaseUtils db, String key) {
        List<Word> result = new LinkedList<>();
        List<Word> words = db.getAllWords();
        for (Word word: words) {
            if (word.getCtnt().contains(key)){
                result.add(word);
                continue;
            }

            if (word.getTags() != null) {
                String[] tags = word.getTags().split(" ");
                for (String tag: tags) {
                    if (tag.contains(key)) {
                        result.add(word);
                        break;
                    }
                }
            }
        }
        return result;
    }

    public static void sort(SortType sortType) {
        switch (sortType) {
            case DEFAULT:
                Collections.sort(wordList, new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return Collator.getInstance(Locale.CHINESE).compare(o1.getCtnt(), o2.getCtnt());
                    }
                });
                break;
            case DATE:
                Collections.sort(wordList, new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return -o1.getAddt().compareTo(o2.getAddt());
                    }
                });
                break;
            case FREQUENCY:
                Collections.sort(wordList, new Comparator<Word>() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public int compare(Word o1, Word o2) {
                        if (o1.getFreq() < o2.getFreq()) {
                            return -1;
                        } else if (o1.getFreq() > o2.getFreq()) {
                            return 1;
                        } else {
                            return Collator.getInstance(Locale.CHINESE).compare(o1.getCtnt(), o2.getCtnt());
                        }
                    }
                });
        }
    }

    public static String[] getTags(DatabaseUtils db){
        List<Word> words = db.getAllWords();
        Map<String, Boolean> map = new TreeMap<>();
        for (Word word: words){
            if (!word.getTags().isEmpty()){
                String[] tags = word.getTags().split(" ");
                for (String tag: tags){
                    map.put(tag, true);
                }
            }
        }

        String[] strings = new String[map.keySet().size()];
        return map.keySet().toArray(strings);
    }

    public static int size() {
        return wordList.size();
    }

    public static String nextWordCtnt(String ctnt) {
        int index = -1;
        for (int i = 0; i < size(); i++) {
            if (get(i).getCtnt().contentEquals(ctnt)) {
                index = i+1;
                if (index < size()) {
                    System.out.println(index + ": " + wordList.get(index).getCtnt());
                    return wordList.get(index).getCtnt();
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    public static String prevWordCtnt(String ctnt) {
        int index = -1;
        for (int i = 0; i < size(); i++) {
            if (get(i).getCtnt().contentEquals(ctnt)) {
                index = i-1;
                if (index >= 0) {
                    System.out.println(index + ": " + wordList.get(index).getCtnt());
                    return wordList.get(index).getCtnt();
                } else {
                    return null;
                }
            }
        }
        return null;
    }
}
