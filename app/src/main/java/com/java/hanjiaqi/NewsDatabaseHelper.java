package com.java.hanjiaqi;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class NewsDatabaseHelper extends SQLiteOpenHelper {
    public static final String db_name = "news.db";

    public NewsDatabaseHelper(Context context, int version) {
        super(context, db_name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table history (" +
                " title text," +
                " content text," +
                " time text," +
                " publisher text," +
                " lastClickTime Long," +
                " newsID text," +
                " images text," +
                " video test," +
                " username test," +
                " keywords text," +
                " url text" +
                ")");
        db.execSQL("create table favorite (" +
                " title text," +
                " content text," +
                " time text," +
                " publisher text," +
                " lastClickTime Long," +
                " newsID text," +
                " images text," +
                " video test," +
                " username test," +
                " keywords text," +
                " url text" +
                ")");
        db.execSQL("create table keywords (" +
                " username test," +
                " word text," +
                " score double" +
                ")");
        db.execSQL("create table queryhistory (" +
                " message text," +
                " times int," +
                " username test," +
                " lastQueryTime Long" +
                ")");
        db.execSQL("create table grid (" +
                " id int,"+
                " orderid int,"+
                " item text,"+
                " username test," +
                " status int"+
                ")");
        db.execSQL("create table user (" +
                " username text,"+
                " password text,"+
                " lastLogin Long,"+
                " isLogin int"+
                ")");
        db.execSQL("create table settings(style int,username text)");
        db.execSQL("create table banWords (" +
                " username text,"+
                " banWord text"+
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (newVersion){
            case 1:
            case 2:
                db.execSQL("create table history (" +
                        " title text," +
                        " content text," +
                        " time text," +
                        " publisher text," +
                        " lastClickTime Long," +
                        " newsID text," +
                        " keywords text" +
                        ")");
                db.execSQL("create table favorite (" +
                        " title text," +
                        " content text," +
                        " time text," +
                        " publisher text," +
                        " lastClickTime Long," +
                        " newsID text," +
                        " keywords text" +
                        ")");
                break;
            case 3:
                db.execSQL("drop table news");
                db.execSQL("drop table history");
                db.execSQL("drop table favorite");
                break;
            case 4:
                db.execSQL("create table history (" +
                        " title text," +
                        " content text," +
                        " time text," +
                        " publisher text," +
                        " lastClickTime Long," +
                        " newsID text," +
                        " keywords text" +
                        ")");
                db.execSQL("create table favorite (" +
                        " title text," +
                        " content text," +
                        " time text," +
                        " publisher text," +
                        " lastClickTime Long," +
                        " newsID text," +
                        " keywords text" +
                        ")");
                break;
            case 5:
                db.execSQL("create table keywords (" +
                        " word text," +
                        " score double" +
                        ")");
                break;
            case 6:
                db.execSQL("create table queryhistory (" +
                        " message text," +
                        " times int," +
                        " lastQueryTime Long" +
                        ")");
                break;
            case 7:
                db.execSQL("create table grid (" +
                        " id int,"+
                        " orderid int,"+
                        " item text,"+
                        " status int"+
                        ")");
                break;
            case 8:
                db.execSQL("alter table history ADD images text");
                db.execSQL("alter table favorite ADD images text");
                break;
            case 9:
                db.execSQL("alter table history ADD video text");
                db.execSQL("alter table favorite ADD video text");
                break;
            case 10:
                db.execSQL("create table user (" +
                        " username text,"+
                        " password text,"+
                        " lastLogin Long,"+
                        " isLogin int"+
                        ")");
                db.execSQL("alter table history ADD username text");
                db.execSQL("alter table queryhistory ADD username text");
                db.execSQL("alter table grid ADD username text");
                db.execSQL("alter table keywords ADD username text");
                db.execSQL("alter table favorite ADD username text");
                break;
            case 11:
                db.execSQL("create table settings(style int,username text)");
                db.execSQL("create table banWords (" +
                        " username text,"+
                        " banWord text"+
                        ")");
        }
    }
}
