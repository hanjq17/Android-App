package com.example.a1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsDatabaseManager {
    private Context context;
    private static NewsDatabaseManager instance;
    // 操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;
    final int maxNewsNumber=100;

    private NewsDatabaseManager(Context context) {
        this.context = context;
        NewsDatabaseHelper dbHelper = new NewsDatabaseHelper(context, 8);
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
        contentValues.put("images",news.getStringImages());
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
            String allimages=cursor.getString(cursor.getColumnIndex("images"));
            String newsID=cursor.getString(cursor.getColumnIndex("newsID"));
            NewsMessage mes=new NewsMessage(title,content,time,publisher,newsID);
            mes.addKeywords(allkeywords);
            mes.addImages(allimages);
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

    public void addQueryMessage(String message){
        ContentValues contentValues = new ContentValues();
        Cursor cursor = writableDatabase.query("queryhistory", null, "message = ?", new String[]{message}, null, null, null, null);
        if(cursor.getCount()==0){
            contentValues.put("message",message);
            contentValues.put("times",1);
            contentValues.put("lastQueryTime",new Date().getTime());
            writableDatabase.insert("queryhistory", null, contentValues);
        }
        else{
            cursor.moveToNext();
            int currentTimes=cursor.getInt(cursor.getColumnIndex("times"));
            contentValues.put("times",currentTimes+1);
            contentValues.put("lastQueryTime",new Date().getTime());
            writableDatabase.update("queryhistory", contentValues, "message = ?", new String[]{message});
        }
    }

    public void delQueryMessage(String message){
        writableDatabase.delete("queryhistory", "message = ?", new String[]{message});
    }

    public ArrayList<String> selectAllQueryMessages(String searching,int limit){
        Cursor cursor;
        if(searching==null || searching.equals("")){
            cursor = writableDatabase.query("queryhistory", new String[]{"message"}, null, null, null, null, "times desc,lastQueryTime desc", limit+"");
        }
        else cursor = writableDatabase.query("queryhistory", new String[]{"message"}, "message LIKE ?", new String[]{"%"+searching+"%"}, null, null, "times desc,lastQueryTime desc", limit+"");
        ArrayList<String> ans=new ArrayList<>();
        while (cursor.moveToNext()){
            String word=cursor.getString(cursor.getColumnIndex("message"));
            ans.add(word);
        }
        return ans;
    }

    public void addGridItem(ChannelItem item){
        ContentValues contentValues = new ContentValues();
        Cursor cursor = writableDatabase.query("grid", null, "item = ?", new String[]{item.name}, null, null, null, null);
        if(cursor.getCount()==0){
            contentValues.put("item",item.name);
            contentValues.put("status",item.selected);
            contentValues.put("id",item.id);
            contentValues.put("orderid",item.orderId);
            writableDatabase.insert("grid",null,contentValues);
        }
        else{
            contentValues.put("status",item.selected);
            contentValues.put("orderid",item.orderId);
            writableDatabase.update("grid",contentValues,"item = ?",new String[]{item.name});
        }
    }

    public void delGridItem(String itemName){
        ContentValues contentValues=new ContentValues();
        contentValues.put("status",0);
        writableDatabase.update("grid",contentValues,"item = ?",new String[]{itemName});
    }

    public void setDefault(){
        addGridItem(new ChannelItem(1,"最新",1,1));
        //娱乐、军事、教育、文化、健康、财经、体育、汽车、科技、社会
        addGridItem(new ChannelItem(2,"军事",2,1));
        addGridItem(new ChannelItem(3,"教育",3,1));
        addGridItem(new ChannelItem(4,"文化",4,1));
        addGridItem(new ChannelItem(5,"健康",5,1));
        addGridItem(new ChannelItem(6,"财经",6,1));
        addGridItem(new ChannelItem(7,"体育",7,1));
        addGridItem(new ChannelItem(8,"汽车",8,0));
        addGridItem(new ChannelItem(9,"科技",9,0));
        addGridItem(new ChannelItem(10,"社会",10,0));
        addGridItem(new ChannelItem(11,"娱乐",11,0));

    }

    public void addGridItems(List<ChannelItem> items,int status){
        for(ChannelItem item:items) {
            item.selected=status;
            addGridItem(item);
        }
    }

    public void delAllItems(){
        ContentValues contentValues=new ContentValues();
        contentValues.put("status",0);
        writableDatabase.update("grid",contentValues,null,null);
    }

    public ArrayList<ChannelItem> selectItemByStatus(int status){
        Cursor cursor = writableDatabase.query("grid", null, "status = ?", new String[]{status+""}, null, null, "orderid", null);
        ArrayList<ChannelItem> ans=new ArrayList<>();
        while (cursor.moveToNext()){
            String name=cursor.getString(cursor.getColumnIndex("item"));
            int id=cursor.getInt(cursor.getColumnIndex("id"));
            int orderid=cursor.getInt(cursor.getColumnIndex("orderid"));
            int selected=status;
            ans.add(new ChannelItem(id,name,orderid,selected));
        }
        return ans;
    }

    public void clearMemory(){
        writableDatabase.delete("history", null, null);
        writableDatabase.delete("favorite", null, null);
        writableDatabase.delete("keywords", null, null);
        //setDefault();
    }

}
