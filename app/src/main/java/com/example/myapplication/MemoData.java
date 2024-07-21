package com.example.myapplication;

import java.io.Serializable;

public class MemoData implements Serializable {
    public String title;
    public String content;
    public String firebaseKey;

    @SuppressWarnings("unused")
    private MemoData(){}
    public MemoData(String key, String title, String content) {
        this.firebaseKey = key;
        this.title = title;
        this.content = content;
    }
    public String getFirebaseKey() {
        return firebaseKey;
    }
    public String getTitle() {
        return title;
    }
    public String getContent() {
        return content;
    }

}
