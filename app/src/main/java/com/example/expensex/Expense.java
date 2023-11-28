package com.example.expensex;

public class Expense {
    private int amount, id;
    private String type, date, time;

    public Expense(int amount, String type, String dateTo, String timeTo, int id) {

    }

    public Expense(int amount, int id, String type, String date, String time) {
        this.amount = amount;
        this.id = id;
        this.type = type;
        this.date = date;
        this.time = time;
    }

    public int getAmount() {
        return amount;
    }

    public int getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

}
