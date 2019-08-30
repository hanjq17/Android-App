package com.example.a1;


import java.util.ArrayList;

class KeyWord{
    public String word;
    public double score;
    public KeyWord(String word,double score){
        this.word=word;
        this.score=score;
    }

    @Override
    public String toString() {
        return word+","+score;
    }
}

public class NewsMessage {
    private String title;
    private String content;
    private String time;
    private String publisher;
    private String newsID;
    private ArrayList<KeyWord> keyWords=new ArrayList<>();
    public NewsMessage(String title,String content,String time,String publisher,String newsID){
        this.title=title;
        this.content=content;
        this.time=time;
        this.publisher=publisher;
        this.newsID=newsID;
    }
    public String getTitle(){
        return title;
    }
    public String getContent(){
        return content;
    }
    public String getTime(){
        return time;
    }
    public String getPublisher(){return publisher;}
    public String getID(){return newsID;}

    public void addKeyword(KeyWord keyWord){
        keyWords.add(keyWord);
    }
    public void addKeyword(String word,double score){
        keyWords.add(new KeyWord(word,score));
    }
    public void addKeywords(String allkeywords){
        String[] keywords=allkeywords.split("\\|");
        for(String keyword:keywords){
            String[] wordAndScore=keyword.split(",");
            addKeyword(wordAndScore[0],Double.parseDouble(wordAndScore[1]));
        }
    }
    public void addKeywords(ArrayList<KeyWord> tmpKeywords){
        keyWords.addAll(tmpKeywords);
    }
    public String getStringKeyWords(){
        StringBuilder allKeywords=new StringBuilder();
        for(KeyWord keyWord:keyWords){
            allKeywords.append(keyWord+"|");
        }
        return allKeywords.toString();
    }
    public ArrayList<KeyWord> getKeyWords(){
        return keyWords;
    }
}
