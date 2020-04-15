package com.ljkjk.powod.entity;

import androidx.annotation.NonNull;

import java.sql.Date;

public class Word {
    private String ctnt;    // 词条
    private String pron;    // 读音
    private String mean;    // 词义
    private String tags;    // 标签
    private String syno;    // 近义
    private String anto;    // 反义
    private int freq;       // 计次
    private Date addt;      // 日期

    public Word() {}

    public Word(String ctnt) {
        this.ctnt = ctnt;
    }

    public Word(String ctnt, String pron) {
        this.ctnt = ctnt;
        this.pron = pron;
    }

    public Word(String ctnt, String pron, String mean) {
        this.ctnt = ctnt;
        this.pron = pron;
        this.mean = mean;
    }

    public Word(String ctnt, String pron, String mean, String tags) {
        this.ctnt = ctnt;
        this.pron = pron;
        this.mean = mean;
        this.tags = tags;
    }

    public Word(String ctnt, String pron, String mean, String tags, String syno, String anto, int freq, Date addt) {
        this.ctnt = ctnt;
        this.pron = pron;
        this.mean = mean;
        this.tags = tags;
        this.syno = syno;
        this.anto = anto;
        this.freq = freq;
        this.addt = addt;
    }

    public Word(String ctnt, String pron, String mean, String tags, String syno, String anto, int freq) {
        this.ctnt = ctnt;
        this.pron = pron;
        this.mean = mean;
        this.tags = tags;
        this.syno = syno;
        this.anto = anto;
        this.freq = freq;
    }

    public String getCtnt() {
        return ctnt;
    }

    public void setCtnt(String ctnt) {
        this.ctnt = ctnt;
    }

    public String getPron() {
        return pron;
    }

    public void setPron(String pron) {
        this.pron = pron;
    }

    public String getMean() {
        return mean;
    }

    public void setMean(String mean) {
        this.mean = mean;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getSyno() {
        return syno;
    }

    public void setSyno(String syno) {
        this.syno = syno;
    }

    public String getAnto() {
        return anto;
    }

    public void setAnto(String anto) {
        this.anto = anto;
    }

    public int getFreq() {
        return freq;
    }

    public void setFreq(int freq) {
        this.freq = freq;
    }

    public Date getAddt() {
        return addt;
    }

    public void setAddt(Date addt) {
        this.addt = addt;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Word");
        sb.append("\nctnt: ");
        sb.append(ctnt);
        sb.append("\npron: ");
        sb.append(pron);
        sb.append("\nmean: ");
        sb.append(mean);
        sb.append("\ntags: ");
        sb.append(tags);
        sb.append("\nsyno: ");
        sb.append(syno);
        sb.append("\nanto: ");
        sb.append(anto);
        sb.append("\nfreq: ");
        sb.append(freq);
        sb.append("\naddt: ");
        sb.append(addt);
        sb.append("\n");
        return sb.toString();
    }
}
