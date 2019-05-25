package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

import android.content.Context;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;

import java.util.ArrayList;

/**
 * Created by kthar on 04/05/2018.
 */

public class SpeechToText {
    ArrayList<String> tagList;
    ArrayList<String> removableList;
    ArrayList<String> tagListLocal;
    ArrayList<String> valueListLocal;
    boolean foundSomething;
    public SpeechToText(Context context){
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(context);
        //Databasehandler databasehandler = new Databasehandler(context);
        tagList = databasehandler.getTags();
        removableList = databasehandler.getRemovabels();
        foundSomething = false;
    }
    public void processOnThisSentence(String sentence){
        tagListLocal = new ArrayList<>();
        valueListLocal = new ArrayList<>();
        sentence = sentence.toUpperCase();
        String[] words = sentence.split(" ");
        int count = -1;
        String tempString = "";
        boolean flag = true;
        for(int i = 0; i < words.length; i++){
            if(tagList.contains(words[i])){
                if(tagListLocal.size()==0){
                    tagListLocal.add("");
                    valueListLocal.add(tempString);
                    tempString = "";
                }
                String s = words[i].toLowerCase();
                tagListLocal.add(s.substring(0, 1).toUpperCase() + s.substring(1));
            }
            else if(removableList.contains(words[i])){
                continue;
            }
            else{
                foundSomething = true;
                String s = words[i].toLowerCase();
                tempString+=(s.substring(0, 1).toUpperCase() + s.substring(1));
                if(i == words.length - 1){
                    if(tagListLocal.size() == 0){
                        tagListLocal.add("");
                        valueListLocal.add(tempString);
                        tempString = "";
                    }
                    else{
                        valueListLocal.add(tempString);
                    }
                }


                //count++;

            }
        }

    }
    public boolean isFoundSomething(){
        return foundSomething;
    }
    public void changeWordsToSuitableTags(String[] words){
        for(int i = 0; i < words.length; i++ ){
            if(words[i].toUpperCase().equals("I")){
                words[i] = "NAME";
            }
            else if(words[i].toUpperCase().equals("I'm")){
                words[i] = "NAME";
            }
            else if(words[i].toUpperCase().equals("FROM")){
                words[i] = "ADDRESS";
            }
        }
    }
}
