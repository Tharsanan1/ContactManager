package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

/**
 * Created by kthar on 20/02/2018.
 * this class provide base details of the object that is needed for the cardview expected by recycler view
 */

public class DataObjectForCardView {

        private String mText1; // name. // first available tag value iin case commonInfo recycling
        private String mText2; // companyName. // second available tag value iin case commonInfo recycling
        private String mText3; // mail. // third available tag value iin case commonInfo recycling
        private int id;

        public DataObjectForCardView (String text1, String text2, String text3 , int id){
            mText1 = text1;
            mText2 = text2;
            mText3 = text3;
            this.id = id;

        }

    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }
    public String getmText1() {
            return mText1;
        }
    public void setmText1(String mText1) {
        this.mText1 = mText1;
    }
    public String getmText3() {
        return mText3;
    }
    public void setmText3(String mText1) {
        this.mText3 = mText1;
    }
    public String getmText2() {
        return mText2;
    }
    public void setmText2(String mText2) {
        this.mText2 = mText2;
    }
}

