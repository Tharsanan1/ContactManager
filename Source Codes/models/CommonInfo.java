package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

import java.util.ArrayList;

/**
 * Created by kthar on 04/05/2018.
 * This class created for giving a base structure for commonInfo that extracted from boards or any other text hard contents excluding visiting cards.
 * id is integer value of the commonInfo instant regarding to the sqlite database table common_infos id.
 * After extractInfoFromEncodedString(String s) call you can get tag and respected values in tagList and valueList which are extracted from string s.
 * Idea for storing tag and respected value is use only one text column in table.
 * To store tag and respected value, both will be added in a way that we can extract them separately later. see Databasehandler.prepareCommonInfoValue(ArrayList , ArrayList)
 * -- method for more information about the string preparation that contains both tag and value.
 * You can use extractInfoFromEncodedString(String s) method to extract tag and respected value from given string.
 */

public class CommonInfo {
    public int id;
    public ArrayList<String> tagList = new ArrayList<>();
    public ArrayList<String> valueList = new ArrayList<>();
    public CommonInfo(int id, String encodedString){
        this.id = id;
        extractInfoFromEncodedString(encodedString);
    }
    public void extractInfoFromEncodedString(String s){
        String[] splitedArray = s.split(ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER);
        for(int k = 0; k<splitedArray.length; k++){
            if(k==0){
                continue;
            }
            if(k%2==0){
                valueList.add(splitedArray[k]);
            }
            else{
                tagList.add(splitedArray[k]);
            }
        }
    }
    public int getId(){
        return id;
    }
}
