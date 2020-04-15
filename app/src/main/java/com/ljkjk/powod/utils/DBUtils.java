package com.ljkjk.powod.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ljkjk.powod.entity.Word;

import java.util.LinkedList;
import java.util.List;

public class DBUtils extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private static final String DATABASE_NAME = "Powod";

    private static final String TABLE_WORDS = "words";

    private static final String KEY_CTNT = "ctnt";
    private static final String KEY_PRON = "pron";
    private static final String KEY_MEAN = "mean";
    private static final String KEY_TAGS = "tags";
    private static final String KEY_SYNO = "syno";
    private static final String KEY_ANTO = "anto";
    private static final String KEY_FREQ = "freq";
    private static final String KEY_ADDT = "addt";



    public DBUtils(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Table Create Statements
    private static final String CREATE_WORDS_TABLE = "CREATE TABLE " + TABLE_WORDS + "("
            + KEY_CTNT + " TEXT PRIMARY KEY,"
            + KEY_PRON + " TEXT,"
            + KEY_MEAN + " TEXT,"
            + KEY_TAGS + " TEXT,"
            + KEY_SYNO + " TEXT,"
            + KEY_ANTO + " TEXT,"
            + KEY_FREQ + " INTEGER,"
            + KEY_ADDT + " TEXT" + ")";

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WORDS);
        onCreate(db);
    }

    public boolean insertWord(Word word) {
        if (getWord(word.getCtnt()) != null) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CTNT, word.getCtnt());
        values.put(KEY_PRON, word.getPron());
        values.put(KEY_MEAN, word.getMean());
        values.put(KEY_TAGS, word.getTags());
        values.put(KEY_SYNO, word.getSyno());
        values.put(KEY_ANTO, word.getAnto());
        values.put(KEY_FREQ, word.getFreq());
        values.put(KEY_ADDT, Utils.date2String(word.getAddt()));

        db.insert(TABLE_WORDS, null, values);
        db.close();
        return true;
    }

    public boolean updateWord(Word word) {
        if (getWord(word.getCtnt()) == null) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();

        updateValues.put(KEY_CTNT, word.getCtnt());
        updateValues.put(KEY_PRON, word.getPron());
        updateValues.put(KEY_MEAN, word.getMean());
        updateValues.put(KEY_TAGS, word.getTags());
        updateValues.put(KEY_SYNO, word.getSyno());
        updateValues.put(KEY_ANTO, word.getAnto());
        updateValues.put(KEY_FREQ, word.getFreq());
        updateValues.put(KEY_ADDT, Utils.date2String(word.getAddt()));

        db.update(TABLE_WORDS, updateValues, KEY_CTNT + "=?", new String[] { word.getCtnt() });
        db.close();
        return true;
    }

    public boolean updateWordFreq(String ctnt, int delta){
        Word word;
        if ((word = getWord(ctnt)) == null){
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();

        updateValues.put(KEY_FREQ, word.getFreq()+delta);

        db.update(TABLE_WORDS, updateValues, KEY_CTNT + "=?", new String[] { ctnt });
        db.close();
        return true;
    }

    public boolean updateWordByNet(String ctnt, String pron, String mean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();

        updateValues.put(KEY_PRON, pron);
        updateValues.put(KEY_MEAN, mean);

        db.update(TABLE_WORDS, updateValues, KEY_CTNT + "=?", new String[] { ctnt });
        db.close();
        return true;
    }

    public boolean updateWordByNet(String ctnt, String pron, String mean, String syno, String anto) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updateValues = new ContentValues();

        updateValues.put(KEY_PRON, pron);
        updateValues.put(KEY_MEAN, mean);
        updateValues.put(KEY_SYNO, syno);
        updateValues.put(KEY_ANTO, anto);

        db.update(TABLE_WORDS, updateValues, KEY_CTNT + "=?", new String[] { ctnt });
        db.close();
        return true;
    }

    public boolean deleteWord(String ctnt) {
        if (getWord(ctnt) == null) {
            return false;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, KEY_CTNT + "=?", new String[] { ctnt });
        db.close();
        return true;
    }

    public Word getWord(String ctnt) {
        Word word = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM words WHERE ctnt='" + ctnt + "'", null);
        cursor.move(1);
        if (cursor.getCount() > 0) {
            word = new Word();
            word.setCtnt(cursor.getString(0));
            word.setPron(cursor.getString(1));
            word.setMean(cursor.getString(2));
            word.setTags(cursor.getString(3));
            word.setSyno(cursor.getString(4));
            word.setAnto(cursor.getString(5));
            word.setFreq(cursor.getInt(6));
            word.setAddt(Utils.string2Date(cursor.getString(7)));
        }
        cursor.close();
        db.close();
        return word;
    }

    public List<Word> getAllWords(){
        List<Word> words = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM words", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            Word word = new Word();
            word.setCtnt(cursor.getString(0));
            word.setPron(cursor.getString(1));
            word.setMean(cursor.getString(2));
            word.setTags(cursor.getString(3));
            word.setSyno(cursor.getString(4));
            word.setAnto(cursor.getString(5));
            word.setFreq(cursor.getInt(6));
            word.setAddt(Utils.string2Date(cursor.getString(7)));
            words.add(word);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return words;
    }

    public void reset(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WORDS, null, null);
        db.close();
    }

}
