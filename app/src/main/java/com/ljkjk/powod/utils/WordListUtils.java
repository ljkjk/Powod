package com.ljkjk.powod.utils;

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
    public static List<Word> wordList = new LinkedList<>();

    public static List<Word> list() {
        return wordList;
    }

    public static List<Word> fullList(DBUtils db) {
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

    public static void getWordList(DBUtils db) {
        clear();
        wordList = db.getAllWords();
    }

    public static List<Word> getWordListByKey(DBUtils db, String key) {
        List<Word> result = new LinkedList<>();
        List<Word> words = db.getAllWords();
        for (Word word: words) {
            if (word.getCtnt().contains(key)){
                result.add(word);
                break;
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

    public static void sort(String sortType) {
        switch (sortType) {
            case "默认":
                Collections.sort(wordList, new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return Collator.getInstance(Locale.CHINESE).compare(o1.getCtnt(), o2.getCtnt());
                    }
                });
                break;
            case "注音":
                Collections.sort(wordList, new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o1.getPron().compareToIgnoreCase(o2.getPron());
                    }
                });
                break;
            case "时间":
                Collections.sort(wordList, new Comparator<Word>() {
                    @Override
                    public int compare(Word o1, Word o2) {
                        return o1.getAddt().compareTo(o2.getAddt());
                    }
                });
                break;
        }
    }

    public static String[] getTags(DBUtils db){
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
}
