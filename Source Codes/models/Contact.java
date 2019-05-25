package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

import java.util.ArrayList;

/**
 * Created by kthar on 16/02/2018.
 * This class provide base detail of a contact.
 * id -> id of a contact in database
 * name -> name of the contact.
 * companyName -> companyName is the name of the company provided on visiting card.
 * numbers -> is a list of contact numbers that can be phone_num or fax or home number or office number. this list store list store string values.
 * -- each string value contains number and tag details. (tag detail eg: fax, home, office, etc)
 * others -> this is a string arraylist contains common details on the  visiting card and cannot be applied for any tags.
 * mail -> mail detail of the contact.
 * occupation -> occupation detail of the contact.
 * address -> this is a array list. this list contains small chunk details of extracted address.
 * -- OCR function will extract address as many small lines each line will be added to this list.
 * addressFinal -> addressFinal is the final address string that created by combining small chunks in address.
 */
public class Contact {
    private int id;
    private String name;
    private String companyName;
    private String web;
    private ArrayList<String> numbers;
    private ArrayList<String> others;
    private String mail;
    private String occupation;
    private ArrayList<String> address;
    private String addressFinal;
    public Contact(){
        id = 0;
        addressFinal = "notSet";
        name = "notSet";
        companyName = "notSet";
        web = "notSet";
        mail = "notSet";
        occupation = "notSet";
        address = new ArrayList<>();
        numbers = new ArrayList<>();
        others = new ArrayList<>();

    }

    public ArrayList<String> getAddress() {
        return address;
    }
    public String getAddressFinal() {
        return addressFinal;
    }

    public void setAddressFinal(String addressFinal) {                                              // setAddressfinal() will combine all the address component in the addresses list.
        this.addressFinal = addressFinal;
        address.clear();
        address.add(addressFinal);
    }
    public ArrayList<String> getNumbers() {
        return numbers;
    }
    public ArrayList<String> getOthers() {
        return others;
    }
    public String getCompanyName() {
        return companyName;
    }
    public String getMail() {
        return mail;
    }
    public String getName() {
        return name;
    }
    public String getOccupation() {
        return occupation;
    }
    public String getWeb() {
        return web;
    }
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public void setMail(String mail) {
        this.mail = mail;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setNumbers(ArrayList<String> numbers) {
        this.numbers = numbers;
    }
    public void addNumber(String number, String detail){                                            // detail of the phone number will be added to the number string for the later extraction.
        numbers.add(number+" "+detail);
    }
    public void addNumber(String number){

        numbers.add(number);
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    public void setOthers(ArrayList<String> others) {
        this.others = others;
    }
    public void addOthers(String data, String detail){
        others.add(data+" "+detail);
    }
    public void addOthers(String data){
        others.add(data);
    }
    public void setWeb(String web) {
        this.web = web;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    /**
     * this method override toString() method.
     * @return -> this method will return a string that has all the details about the contact
     *         -- and that string can later convert to a contact instant.
     */
    @Override
    public String toString(){
        String toOut = "";
        if(name.length() == 0){name = "notSet";}
        if(companyName.length() == 0){companyName = "notSet";}
        if(web.length() == 0){web = "notSet";}
        if(mail.length() == 0){mail = "notSet";}
        if(occupation.length() == 0){occupation = "notSet";}
        if(addressFinal.length() == 0){addressFinal = "notSet";}
        toOut+=name+"&&"+companyName+"&&"+web+"&&"+mail+"&&"+occupation+"&&"+addressFinal;
        toOut+="@#&";
        boolean flagForCut = false;
        for(String s : numbers){
            toOut+=s+"##";
            flagForCut = true;
        }
        if(flagForCut) {
            toOut = toOut.substring(0, toOut.length() - 2);
        }
        flagForCut = false;
        toOut+="@#&";
        for (String s : others){
            toOut+=s+"%%";
            flagForCut = true;
        }
        if(flagForCut) {
            toOut = toOut.substring(0, toOut.length() - 2);
        }
        toOut+="@#&";
        return toOut;
    }

    /**
     * @param s -> this parameter contains detail of a contact as a string.
     * @return  -> this return method return a contact instant that has relevent details from string
     */
    public static Contact createContactFromString(String s){
        String[] mainOptionList = s.substring(0,s.indexOf("@#&")).split("&&");
        try {
            Contact toOutContact = new Contact();
            toOutContact.setName(mainOptionList[0]);
            toOutContact.setCompanyName(mainOptionList[1]);
            toOutContact.setWeb(mainOptionList[2]);
            toOutContact.setMail(mainOptionList[3]);
            toOutContact.setOccupation(mainOptionList[4]);
            toOutContact.setAddressFinal(mainOptionList[5]);

            int index = s.indexOf("@#&");
            String[] subList = (s.substring(index)).split("@#&");
            if(subList.length==0){
                return toOutContact;
            }
            String numberString = subList[1];
            String othersString;
            try {
                othersString = subList[2];
            }
            catch (Exception e){
                othersString = "";
            }
            if(numberString.length()>0){
                if(numberString.indexOf("##")>-1){
                    String[] numberStringList = numberString.split("##");
                    for(String sss : numberStringList){
                        toOutContact.addNumber(sss);
                    }
                }
                else{
                    toOutContact.addNumber(numberString);
                }
            }
            if(othersString.length()>0){
                if(othersString.indexOf("%%")>-1){
                    String[] otherStringList = othersString.split("%%");
                    for(String sss : otherStringList){
                        toOutContact.addOthers(sss);
                    }
                }
                else{
                    toOutContact.addOthers(othersString);
                }
            }


            return toOutContact;
        }
        catch (Exception e){
            return new Contact();
        }
    }
}
