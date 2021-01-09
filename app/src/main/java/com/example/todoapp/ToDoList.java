package com.example.todoapp;

public class ToDoList {
    private String id;
    private String title, shortdesc, dater, timer, currentDate ,currentTime;

    public ToDoList(String id, String title, String shortdesc, String dater, String timer, String currentDate, String currentTime) {
        this.id = id;
        this.title = title;
        this.shortdesc = shortdesc;
        this.dater = dater;
        this.timer = timer;
        this.currentDate = currentDate;
        this.currentTime = currentTime;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public String getDater() {
        return dater;
    }

    public String getTimer() {
        return timer;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public String getCurrentTime() {
        return currentTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public void setDater(String dater) {
        this.dater = dater;
    }

    public void setTimer(String timer) {
        this.timer = timer;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }
}