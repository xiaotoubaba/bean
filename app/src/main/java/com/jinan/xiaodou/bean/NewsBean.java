package com.jinan.xiaodou.bean;

import java.io.Serializable;

/**
 * Created by Yale on 16/9/28.
 */
public class NewsBean implements Serializable {

    private int id;
    private String title;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
