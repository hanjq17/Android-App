package com.example.a1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

public class NewsDatabaseManager {
    private Context context;
    private static NewsDatabaseManager instance;
    // 操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;
    final int maxNewsNumber=100;

    private NewsDatabaseManager(Context context) {
        this.context = context;
        NewsDatabaseHelper dbHelper = new NewsDatabaseHelper(context, 5);
        writableDatabase = dbHelper.getWritableDatabase();
    }

    public static NewsDatabaseManager getInstance(Context context) {
        if (instance == null) {
            synchronized (NewsDatabaseManager.class) {
                if (instance == null) {
                    instance = new NewsDatabaseManager(context);
                }
            }
        }
        return instance;
    }


    public void add(NewsMessage news,String table){
        ContentValues contentValues = new ContentValues();
        contentValues.put("title",news.getTitle());
        contentValues.put("content",news.getContent());
        contentValues.put("time",news.getTime());
        contentValues.put("publisher",news.getPublisher());
        contentValues.put("newsID",news.getID());
        ArrayList<KeyWord> keyWords=news.getKeyWords();
        StringBuilder allKeywords=new StringBuilder();
        for(KeyWord keyWord:keyWords){
            allKeywords.append(keyWord+"|");
        }
        String ansKeywords=allKeywords.toString();
        contentValues.put("keywords",ansKeywords);
        contentValues.put("lastClickTime",new Date().getTime());
        writableDatabase.insert(table, null, contentValues);
    }

    public void del(String newsID,String table){
        writableDatabase.delete(table, "newsID = ?", new String[]{newsID});
    }

    public void update(String newsID,String table) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("lastClickTime", new Date().getTime());
        writableDatabase.update(table, contentValues, "newsID = ?", new String[]{newsID});
    }

    public ArrayList<NewsMessage> selectAll(String table){
        Cursor cursor = writableDatabase.query(table, null, null, null, null, null, "lastClickTime desc", null);
        int position = cursor.getPosition();

        ArrayList<NewsMessage> messages=new ArrayList<>();

        while (cursor.moveToNext()) {
            String title=cursor.getString(cursor.getColumnIndex("title"));
            String content=cursor.getString(cursor.getColumnIndex("content"));
            String time=cursor.getString(cursor.getColumnIndex("time"));
            String publisher=cursor.getString(cursor.getColumnIndex("publisher"));
            String allkeywords=cursor.getString(cursor.getColumnIndex("keywords"));
            String newsID=cursor.getString(cursor.getColumnIndex("newsID"));
            NewsMessage mes=new NewsMessage(title,content,time,publisher,newsID);
            mes.addKeywords(allkeywords);
            messages.add(mes);
        }
        return messages;
    }

    public boolean existsID(String newsID,String table){
        Cursor cursor = writableDatabase.query(table, null, "newsID = ?", new String[]{newsID}, null, null, null, null);
        return cursor.getCount()!=0;
    }

    public void addKeyword(KeyWord keyWord){
        ContentValues contentValues = new ContentValues();
        Cursor cursor = writableDatabase.query("keywords", null, "word = ?", new String[]{keyWord.word}, null, null, null, null);
        if(cursor.getCount()==0) {
            contentValues.put("word", keyWord.word);
            contentValues.put("score", keyWord.score);
            writableDatabase.insert("keywords", null, contentValues);
        }
        else{
            cursor.moveToNext();
            double currentScore=cursor.getDouble(cursor.getColumnIndex("score"));
            contentValues.put("score",currentScore+keyWord.score);
            writableDatabase.update("keywords", contentValues, "word = ?", new String[]{keyWord.word});
        }
    }

    public void addKeywords(ArrayList<KeyWord> keyWords){
        for(KeyWord keyWord:keyWords){
            addKeyword(keyWord);
        }
    }

    public ArrayList<KeyWord> selectKeywords(int limit){
        Cursor cursor = writableDatabase.query("keywords", null, null, null, null, null, "score desc", limit+"");
        ArrayList<KeyWord> ans=new ArrayList<>();
        while (cursor.moveToNext()){
            String word=cursor.getString(cursor.getColumnIndex("word"));
            double score=cursor.getDouble(cursor.getColumnIndex("score"));
            ans.add(new KeyWord(word,score));
        }
        return ans;
    }

}
