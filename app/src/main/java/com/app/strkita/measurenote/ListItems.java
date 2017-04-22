package com.app.strkita.measurenote;

/**
 * ListView表示用データ補助クラス
 * Created by kitada on 2017/02/13.
 */

public class ListItems {

    private int id;
    private String body;
    private int elapsedTime;
    private int currentCount;
    private int goalCount;
    private int created;
    private int updated;

    public int getCurrentCount() {
        return currentCount;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBody() {return body;}

    public int getElapsedTime() {
        return elapsedTime;
    }

    public int getGoalCount() {
        return goalCount;
    }
}
