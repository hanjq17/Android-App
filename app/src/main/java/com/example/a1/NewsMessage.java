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
    private ArrayList<String> images=new ArrayList<>();
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
            if(keyword.equals("")) continue;
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

    public void addImage(String image){
        images.add(image);
    }
    public void addImages(String allImages){
        String[] images=allImages.split("\\|");
        for(String image:images){
            if(!image.equals(""))
            addImage(image);
        }
    }
    public void addImages(ArrayList<String> tmpImages){
        images.addAll(tmpImages);
    }
    public String getStringImages(){
        StringBuilder allImages=new StringBuilder();
        for(String image:images){
            allImages.append(image+"|");
        }
        return allImages.toString();
    }
    public ArrayList<String> getImages(){
        return images;
    }
}
