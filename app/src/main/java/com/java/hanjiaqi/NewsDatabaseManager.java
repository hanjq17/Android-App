package com.java.hanjiaqi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewsDatabaseManager {
    private Context context;
    private static NewsDatabaseManager instance;
    // 操作表的对象，进行增删改查
    private SQLiteDatabase writableDatabase;
    final int maxNewsNumber=100;

    public static String currentUser="";
    public static int style=0;

    public static String getHash(String password){
        /*
        try{
            MessageDigest digest=MessageDigest.getInstance("MD5");
            return digest.digest(password.getBytes()).toString();
        }catch (Exception e){
            return "";
        }*/
        return password;
    }

    private NewsDatabaseManager(Context context) {
        this.context = context;
        NewsDatabaseHelper dbHelper = new NewsDatabaseHelper(context, 11);
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
        contentValues.put("video",news.getVideo());
        contentValues.put("url",news.getUrl());
        contentValues.put("username",currentUser);
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
        writableDatabase.delete(table, "newsID = ? and username = ?", new String[]{newsID,currentUser});
    }

    public void update(String newsID,String table) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("lastClickTime", new Date().getTime());
        writableDatabase.update(table, contentValues, "newsID = ? and username = ?", new String[]{newsID,currentUser});
    }

    public ArrayList<NewsMessage> selectAll(String table){
        Cursor cursor = writableDatabase.query(table, null, "username = ?", new String[]{currentUser}, null, null, "lastClickTime desc", null);
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
            String video=cursor.getString(cursor.getColumnIndex("video"));
            String url=cursor.getString(cursor.getColumnIndex("url"));
            NewsMessage mes=new NewsMessage(title,content,time,publisher,newsID);
            mes.addKeywords(allkeywords);
            mes.addImages(allimages);
            mes.addVideo(video);
            mes.addUrl(url);
            messages.add(mes);
        }
        return messages;
    }

    public boolean existsID(String newsID,String table){
        Cursor cursor = writableDatabase.query(table, null, "newsID = ? and username = ?", new String[]{newsID,currentUser}, null, null, null, null);
        return cursor.getCount()!=0;
    }

    public void addKeyword(KeyWord keyWord){
        ContentValues contentValues = new ContentValues();
        Cursor cursor = writableDatabase.query("keywords", null, "word = ? and username = ?", new String[]{keyWord.word,currentUser}, null, null, null, null);
        if(cursor.getCount()==0) {
            contentValues.put("word", keyWord.word);
            contentValues.put("score", keyWord.score);
            contentValues.put("username",currentUser);
            writableDatabase.insert("keywords", null, contentValues);
        }
        else{
            cursor.moveToNext();
            double currentScore=cursor.getDouble(cursor.getColumnIndex("score"));
            contentValues.put("score",currentScore+keyWord.score);
            writableDatabase.update("keywords", contentValues, "word = ? and username = ?", new String[]{keyWord.word,currentUser});
        }
    }

    public void addKeywords(ArrayList<KeyWord> keyWords){
        for(KeyWord keyWord:keyWords){
            addKeyword(keyWord);
        }
    }

    public ArrayList<KeyWord> selectKeywords(int limit){
        Cursor cursor = writableDatabase.query("keywords", null, "username = ?", new String[]{currentUser}, null, null, "score desc", null);
        ArrayList<KeyWord> ans=new ArrayList<>();
        ArrayList<String> banWords=selectBanWords();
        int num=0;
        while (cursor.moveToNext()){
            String word=cursor.getString(cursor.getColumnIndex("word"));
            double score=cursor.getDouble(cursor.getColumnIndex("score"));
            if(!banWords.contains(word)){
                ans.add(new KeyWord(word,score));
                num++;
                if(num==limit) break;
            }
        }
        return ans;
    }

    public void addQueryMessage(String message){
        ContentValues contentValues = new ContentValues();
        Cursor cursor = writableDatabase.query("queryhistory", null, "message = ? and username = ?", new String[]{message,currentUser}, null, null, null, null);
        if(cursor.getCount()==0){
            contentValues.put("message",message);
            contentValues.put("times",1);
            contentValues.put("lastQueryTime",new Date().getTime());
            contentValues.put("username",currentUser);
            writableDatabase.insert("queryhistory", null, contentValues);
        }
        else{
            cursor.moveToNext();
            int currentTimes=cursor.getInt(cursor.getColumnIndex("times"));
            contentValues.put("times",currentTimes+1);
            contentValues.put("lastQueryTime",new Date().getTime());
            writableDatabase.update("queryhistory", contentValues, "message = ? and username = ?", new String[]{message,currentUser});
        }
    }

    public void delQueryMessage(String message){
        writableDatabase.delete("queryhistory", "message = ? and username = ?", new String[]{message,currentUser});
    }

    public ArrayList<String> selectAllQueryMessages(String searching,int limit){
        Cursor cursor;
        if(searching==null || searching.equals("")){
            cursor = writableDatabase.query("queryhistory", new String[]{"message"}, "username = ?", new String[]{currentUser}, null, null, "times desc,lastQueryTime desc", limit+"");
        }
        else cursor = writableDatabase.query("queryhistory", new String[]{"message"}, "message LIKE ? and username = ?", new String[]{"%"+searching+"%",currentUser}, null, null, "times desc,lastQueryTime desc", limit+"");
        ArrayList<String> ans=new ArrayList<>();
        while (cursor.moveToNext()){
            String word=cursor.getString(cursor.getColumnIndex("message"));
            ans.add(word);
        }
        return ans;
    }

    public void addGridItem(ChannelItem item){
        ContentValues contentValues = new ContentValues();
        Cursor cursor = writableDatabase.query("grid", null, "item = ? and username = ?", new String[]{item.name,currentUser}, null, null, null, null);
        if(cursor.getCount()==0){
            contentValues.put("item",item.name);
            contentValues.put("status",item.selected);
            contentValues.put("id",item.id);
            contentValues.put("orderid",item.orderId);
            contentValues.put("username",currentUser);
            writableDatabase.insert("grid",null,contentValues);
        }
        else{
            contentValues.put("status",item.selected);
            contentValues.put("orderid",item.orderId);
            writableDatabase.update("grid",contentValues,"item = ? and username = ?",new String[]{item.name,currentUser});
        }
    }

    public void delGridItem(String itemName){
        ContentValues contentValues=new ContentValues();
        contentValues.put("status",0);
        writableDatabase.update("grid",contentValues,"item = ? and username = ?",new String[]{itemName,currentUser});
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
        writableDatabase.update("grid",contentValues,"username = ?",new String[]{currentUser});
    }

    public ArrayList<ChannelItem> selectItemByStatus(int status){
        Cursor cursor = writableDatabase.query("grid", null, "status = ? and username = ?", new String[]{status+"",currentUser}, null, null, "orderid", null);
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
        writableDatabase.delete("history", "username = ?", new String[]{currentUser});
        writableDatabase.delete("favorite", "username = ?", new String[]{currentUser});
        writableDatabase.delete("queryhistory", "username = ?", new String[]{currentUser});
        writableDatabase.delete("keywords", "username = ?", new String[]{currentUser});
        writableDatabase.delete("banWords", "username = ?", new String[]{currentUser});
        //setDefault();
    }


    public boolean addUser(String username,String tmppassword){
        String password=getHash(tmppassword);
        Log.d("pas",password);
        Log.d("paspas",tmppassword);
        Cursor cursor = writableDatabase.query("user", null, "username = ?", new String[]{username}, null, null, null, null);
        if(cursor.getCount()>0) return false;
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",username);
        contentValues.put("password",password);
        contentValues.put("lastLogin",new Date().getTime());
        //Cursor cursor = writableDatabase.query("user", null, null, null, null, null, null, null);
        contentValues.put("isLogin",0);
        writableDatabase.insert("user", null, contentValues);
        return true;
    }
    public long getlogintime(){
        Cursor cursor = writableDatabase.query("user", null, "username = ? ", new String[]{currentUser}, null, null, null, null);
        cursor.moveToNext();
        return cursor.getLong(cursor.getColumnIndex("lastLogin"));
    }

    public boolean login(String username,String tmppassword){
        String password=getHash(tmppassword);
        Log.d("pas",password);
        Log.d("paspas",tmppassword);
        Cursor cursor = writableDatabase.query("user", null, "username = ? and password = ?", new String[]{username,password}, null, null, null, null);
        if(cursor.getCount()==0) return false;
        currentUser=username;
        ContentValues contentValues=new ContentValues();
        contentValues.put("isLogin",1);
        contentValues.put("lastLogin",new Date().getTime());
        writableDatabase.update("user",contentValues,"username = ?",new String[]{currentUser});
        Cursor cursor1 = writableDatabase.query("grid", null, "username = ?", new String[]{currentUser}, null, null, null, null);
        if(cursor1.getCount()==0) setDefault();
        getStyle();
        return true;
    }


    public void setUser(){
        Cursor cursor = writableDatabase.query("user", null, "isLogin = ?", new String[]{1+""}, null, null, null, null);
        if(cursor.getCount()==0) currentUser="";
        else {
            cursor.moveToNext();
            currentUser = cursor.getString(cursor.getColumnIndex("username"));
        }
        getStyle();
    }

    public void logout(){
        ContentValues contentValues=new ContentValues();
        contentValues.put("isLogin",0);
        writableDatabase.update("user",contentValues,"username = ?",new String[]{currentUser});
        currentUser="";
    }

    public void setStyle(int style_to_set){
        style=style_to_set;
        ContentValues contentValues=new ContentValues();
        contentValues.put("style",style);
        Cursor cursor=writableDatabase.query("settings",null,"username = ?",new String[]{currentUser},null,null,null,null);
        if(cursor.getCount()==0){
            contentValues.put("username",currentUser);
            writableDatabase.insert("settings", null, contentValues);
        }
        else{
            writableDatabase.update("settings",contentValues,"username = ?",new String[]{currentUser});
        }
    }


    private void getStyle(){
        if(currentUser.equals("")){
            style=0;
            return;
        }
        Cursor cursor=writableDatabase.query("settings",null,"username = ?",new String[]{currentUser},null,null,null,null);
        if(cursor.getCount()==0) setStyle(0);
        else{
            cursor.moveToNext();
            style=cursor.getInt(cursor.getColumnIndex("style"));
        }
    }


    public void addBanWord(String banWord){
        Cursor cursor=writableDatabase.query("banWords",null,"username = ? and banWord = ?",new String[]{currentUser,banWord},null,null,null,null);
        if(cursor.getCount()>0) return;
        ContentValues contentValues=new ContentValues();
        contentValues.put("username",currentUser);
        contentValues.put("banWord",banWord);
        writableDatabase.insert("banWords", null, contentValues);
    }

    public void delBanWord(String banWord){
        writableDatabase.delete("banWords", "banWord = ? and username = ?", new String[]{banWord,currentUser});
    }

    public ArrayList<String> selectBanWords(){
        Cursor cursor=writableDatabase.query("banWords",null,"username = ?",new String[]{currentUser},null,null,null,null);
        ArrayList<String> banWords=new ArrayList<>();
        while(cursor.moveToNext()){
            banWords.add(cursor.getString(cursor.getColumnIndex("banWord")));
        }
        return banWords;
    }

    public void delAllBanWords(){
        writableDatabase.delete("banWords","username = ?",new String[]{currentUser});
    }
}
