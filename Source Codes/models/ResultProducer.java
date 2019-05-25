package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

import android.util.SparseArray;

import com.google.android.gms.vision.text.TextBlock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by kthar on 09/02/2018.
 */

public class ResultProducer {
    static ArrayList<String> name = new ArrayList<>();;
    static ArrayList<String> phoneNum = new ArrayList<>();
    static ArrayList<String> email = new ArrayList<>();
    static ArrayList<String> web = new ArrayList<>();
    static ArrayList<String> address = new ArrayList<>();
    static ArrayList<String> other = new ArrayList<>();
    static ArrayList<ArrayList> list = new ArrayList<>();
    static HashMap<String, String> hm = new HashMap<>();
    static ArrayList<String> subTextList = new ArrayList<>();
    static ArrayList<String> tagList = new ArrayList<>();
    static int startIndex;
    static int endIndex;

    public static HashMap<String,String> processOnStrings(ArrayList<String> items){
        for(int i=0; i<items.size(); i++){
            String item = items.get(i);
            processOnWord(item);
        }
        list.add(name);
        list.add(phoneNum);
        list.add(email);
        list.add(web);
        list.add(address);
        list.add(other);
        return hm;
    }
    public static void clearAll(){
        subTextList.clear();
        tagList.clear();
        hm.clear();
    }
    private static String processOnWord(String s){
        if(isName(s)){

        }
        else if(isPhoneNumber(s)){

        }
        else if(isEmail(s)){

        }
        else if(isWeb(s)){

        }
        else if(isAddress(s)){

        }
        else{
            subTextList.add(s);
            tagList.add("name");
        }
        return "";
    }

    public static ArrayList<String> getSubTextList() {
        return subTextList;
    }

    public static ArrayList<String> getTagList() {
        return tagList;
    }

    public static boolean isName(String s){
        if(performRegex("M(r|s)?s?\\.?[a-zA-Z ]*",s)){
            hm.put(s.substring(startIndex,endIndex),"name");
            subTextList.add(s.substring(startIndex,endIndex));
            tagList.add("name");
            return  true;
        }
        else{
            return false;
        }
    }
    public static boolean isPhoneNumber(String s){
        String str = "";
        String[] sList = s.split(" ");
        for(String str1 : sList){
            str+=str1;
        }
        if(performRegex(".*\\d{9,10}",str)){
            hm.put(s.substring(startIndex,endIndex),"phoneNum");
            subTextList.add(extractNum(s));
            tagList.add("phoneNum");
            return true;
        }
        else if(performRegex("\\(?\\d?\\d{2}\\)?.?\\d{3}.?\\d{4}",str)){
            hm.put(s.substring(startIndex,endIndex),"phoneNum");
            subTextList.add(extractNum(str));
            tagList.add("phoneNum");
            return true;
        }
        else if(hasCountryCode(str)){
            subTextList.add(changetoNumberString(str));
            tagList.add("phoneNum");
            return true;
        }
        else{
            return false;
        }
    }
    public static String changetoNumberString(String s){
        String toOut = "";
        for(int i = 0; i < s.length(); i++){
            if(s.charAt(i) == 's' || s.charAt(i) == 'S'){
                toOut+='5';
            }
            else if(s.charAt(i) == 'o' || s.charAt(i) == 'O'){
                toOut+='0';
            }
            else if(s.charAt(i) == 'l' || s.charAt(i) == 'i'){
                toOut+='1';
            }
            else if(s.charAt(i) == 'z' || s.charAt(i) == 'Z'){
                toOut+='2';
            }
            else{
                toOut+=s.charAt(i);
            }
        }
        return toOut;
    }
    public static boolean hasCountryCode(String s){
        if(s.indexOf("94")>-1){
            return true;
        }
        return false;
    }
    public static boolean isEmail(String s){
        if(performRegex("\\b[a-zA-Z0-9._-]+@[a-z]+\\.[a-z.]+\\b",s)){
            hm.put(s.substring(startIndex,endIndex),"mail");
            subTextList.add(s.substring(startIndex,endIndex));
            tagList.add("mail");
            return true;
        }
        else{
            return false;
        }
    }
    public static boolean isAddress(String s){
        if(performRegex("(\\bNo\\b|\\bcross\\b|\\bCross\\b|\\bFloor\\b|\\bfloor\\b|\\bbuilding\\b|\\bBuilding\\b|\\bRoad\\b|\\broad\\b|\\bstreet\\b|\\bst\\b|\\bcity\\b|\\d{2,6}|\\blane\\b)",s)){
            hm.put(s,"address");
            subTextList.add(s);
            tagList.add("address");
            return true;
        }

        else{
            return false;
        }
    }
    public static boolean isWeb(String s){
        if(performRegex("((http)?s?(://)?((www.)?[a-zA-Z]+\\.[a-zA-Z]+))",s)){
            hm.put(s.substring(startIndex,endIndex),"web");
            subTextList.add(s.substring(startIndex,endIndex));
            tagList.add("web");
            return true;
        }
        else if(performRegex("((http)?s?(://)?((www.)?[a-zA-Z]+ com))",s)){
            if(s.indexOf("com")+3 == s.length()){
                hm.put(s.substring(startIndex,endIndex),"web");
                subTextList.add(s.substring(startIndex,endIndex));
                tagList.add("web");
                return true;
            }
            else{
                if(s.charAt(s.indexOf("com")+3) == ' '){
                    hm.put(s.substring(startIndex,endIndex),"web");
                    subTextList.add(s.substring(startIndex,endIndex));
                    tagList.add("web");
                    return true;
                }
                else{
                    return false;
                }
            }

        }
        else {
            return false;
        }
    }
    public static boolean performRegex(String regex, String toCheck){
        boolean flag = false;
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(toCheck);
        while(matcher.find()){
            flag = true;
            startIndex = matcher.start();
            endIndex = matcher.end();
        }
        return flag;

    }
    public static int countNums(String s){
        int count = 0;
        for(int i = 0; i<s.length(); i++){
            if(s.charAt(i)<=57 && s.charAt(i)>=48){
                count++;
            }
        }
        return count;
    }
    public static String extractNum(String s){
        String number = "";
        for(int i = 0; i<s.length(); i++){
            if(s.charAt(i)<=57 && s.charAt(i)>=48){
                number+=s.charAt(i);
            }
        }
        return number;
    }

}
