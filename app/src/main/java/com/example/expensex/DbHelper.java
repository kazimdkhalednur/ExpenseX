package com.example.expensex;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DbHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "Expense.db";
    private static final String TABLE_NAME = "Expense";

    public static String COL_TYPE = "Type";
    public static String COL_Amount = "Amount";
    public static String COL_Date = "Date";
    public static String COL_TIME = "Time";
    public static String COL_Document = "Document";
    public static String COL_ID = "Id";
    public static String TOT_EXPENSE = "Total";

    private static final int VERSION = 8;
    public DbHelper(Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "create table " + TABLE_NAME + "( Id INTEGER primary key AUTOINCREMENT ,Type TEXT,Amount INTEGER,Date INTEGER,Time TEXT,Document TEXT)";
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertData(String type, int amount, long date, String time) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_Amount, amount);
        contentValues.put(COL_Date, date);
        contentValues.put(COL_TIME, time);
        SQLiteDatabase db = this.getWritableDatabase();
        long id = db.insert(TABLE_NAME, null, contentValues);
        db.close();
        return id;
    }

    public Cursor showAllData() {
        SQLiteDatabase sqLiteDatabase = getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from Expense ", null);
        return cursor;
    }


    //delete
    public void deleteData(int id) {
        getWritableDatabase().delete(TABLE_NAME, "Id=?", new String[] {String.valueOf(id)});
    }

    //update
    public void update(String id, String type, int amount, long date, String time) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_Amount, amount);
        contentValues.put(COL_Date, date);
        contentValues.put(COL_TIME, time);
        sqLiteDatabase.update(TABLE_NAME, contentValues, "Id=?", new String[]{id});
    }
}
