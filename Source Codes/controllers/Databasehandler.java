package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AlertDialog;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.CommonInfo;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Contact;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views.DisplayContactsActivity;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views.MainActivity;

import java.util.ArrayList;

/**
 * Created by kthar on 16/02/2018.
 * this class provide all the database related functions and features.
 */

public class Databasehandler extends SQLiteOpenHelper {

    private Context context;                                                                        // we can use this instant member context for any usage in this class for database operation
    public static boolean isFirstTime = false;                                                      // we can use this static member to find out that this application is opened on the specific phone for the first time. if first time this member will be true.
    private static ArrayList<Databasehandler> databasehandlersList = new ArrayList<>();
    private static Databasehandler databasehandlerStatic = null;
    /**
     * @param context -> this is the context that invoke this constructor.
     */
    private Databasehandler(Context context) {
        super(context, ConstantClass.DatabaseHandler.DATABASE_NAME, null, ConstantClass.DatabaseHandler.DATABASE_VERSION);
        this.context = context;
        databasehandlerStatic = this;
    }
    public static Databasehandler getdatabaseHandler(Context context){
        if(databasehandlerStatic == null){
            databasehandlerStatic = new Databasehandler(context);
            return databasehandlerStatic;
        }
        else{
            databasehandlerStatic.setContext(context);
            return databasehandlerStatic;
        }
    }
    private void setContext(Context context){
        this.context = context;
    }
    /**
     * this method will be invoked when specific database creation on the mobile.
     * --contacts, phone_numbers, other_details tables will be created on the method call.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + ConstantClass.DatabaseHandler.TABLE_CONTACTS + "("
                + ConstantClass.DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY," + ConstantClass.DatabaseHandler.KEY_NAME + " TEXT,"
                + ConstantClass.DatabaseHandler.KEY_COMPANY_NAME + " TEXT," + ConstantClass.DatabaseHandler.KEY_MAIL + " TEXT,"+ ConstantClass.DatabaseHandler.KEY_WEB + " TEXT,"
                + ConstantClass.DatabaseHandler.KEY_OCCUPATION + " TEXT, "+ ConstantClass.DatabaseHandler.KEY_ADDRESS+" TEXT"+")";
        String CREATE_PHONE_NUMBERS_TABLE = "CREATE TABLE IF NOT EXISTS " + ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS + "("
                + ConstantClass.DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY," + ConstantClass.DatabaseHandler.KEY_CONTACT_ID + " INTEGER,"+ "number VARCHAR(20),"
                +"detail TEXT, foreign key("+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID+") references "+ ConstantClass.DatabaseHandler.TABLE_CONTACTS+
                "("+ ConstantClass.DatabaseHandler.KEY_ID+")"+")";
        String CREATE_OTHER_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS + "("
                + ConstantClass.DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY,"+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID + " INTEGER," + "data TEXT,"
                + ConstantClass.DatabaseHandler.KEY_DETAIL+" TEXT, foreign key("+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID + ") references "
                + ConstantClass.DatabaseHandler.TABLE_CONTACTS+"("+ ConstantClass.DatabaseHandler.KEY_ID+")"+")";
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS " + ConstantClass.DatabaseHandler.TABLE_USER + "("
                + ConstantClass.DatabaseHandler.KEY_ID + " INTEGER PRIMARY KEY," + ConstantClass.DatabaseHandler.KEY_USER_PASSWORD + " TEXT )";
        String CREATE_COMMON_INFO_TABLE = "CREATE TABLE IF NOT EXISTS "+ ConstantClass.DatabaseHandler.TABLE_COMMON_INFO+" ("+
                ConstantClass.DatabaseHandler.KEY_ID +" INTEGER PRIMARY KEY," + ConstantClass.DatabaseHandler.KEY_INFO + " TEXT )";
        String CREATE_TAG_TABLE = "CREATE TABLE IF NOT EXISTS "+ ConstantClass.DatabaseHandler.TABLE_TAG+" ("+
                ConstantClass.DatabaseHandler.KEY_ID +" INTEGER PRIMARY KEY," + ConstantClass.DatabaseHandler.KEY_TAG_VALUE + " TEXT )";
        String CREATE_REMOVABLE_TABLE = "CREATE TABLE IF NOT EXISTS "+ ConstantClass.DatabaseHandler.TABLE_REMOVABLES+" ("+
                ConstantClass.DatabaseHandler.KEY_ID +" INTEGER PRIMARY KEY," + ConstantClass.DatabaseHandler.KEY_REMOVABLE_VALUE + " TEXT )";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_OTHER_DETAILS_TABLE);
        sqLiteDatabase.execSQL(CREATE_PHONE_NUMBERS_TABLE);
        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_COMMON_INFO_TABLE);
        sqLiteDatabase.execSQL(CREATE_REMOVABLE_TABLE);
        sqLiteDatabase.execSQL(CREATE_TAG_TABLE);
        Info.setAllToTrue();
        isFirstTime = true;
    }


    /**
     * this method will be called on when database upgraded. (version change)
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ConstantClass.DatabaseHandler.TABLE_CONTACTS);

        // Create tables again
        onCreate(sqLiteDatabase);
    }

    /**
     * this method allow other classes to add a contact on the database. this method will check specific contact is exist or not if exist
     * -- it will warn user.
     * @param contact
     */
    public void addContact(final Contact contact) {
        final ArrayList<Contact> contactList =  getSimilarContacts(contact);
        DialogInterface.OnClickListener dialogClickListener;
        if(contactList.size()>0){
            dialogClickListener = new DialogInterface.OnClickListener() {                           // allow user to decide that ha is going to add a phonenumber again or not or see list of availables.
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent mainIntent = new Intent(context, MainActivity.class);
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            addContactFromInnerClass(contact);
                            ((Activity)context).finish();
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(mainIntent);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            ((Activity)context).finish();
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            context.startActivity(mainIntent);
                            break;
                        case DialogInterface.BUTTON_NEUTRAL:
                            startContactView(contactList);
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setMessage("Phone numbers exist ?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).setNeutralButton("Show",dialogClickListener).show();

        }
        else{                                                                                       // if there is no similar contacts with same number it will add the contact.
            addContactFromInnerClass(contact);
            if(!isFirstTime) {
                ((Activity) context).finish();
                Intent mainIntent = new Intent(context, MainActivity.class);
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(mainIntent);
            }
        }

    }

    public void addContactWithoutValidation(Contact contact){
        addContactFromInnerClass(contact);
    }

    /**
     * this method will send contact list to display contacts class.
     * @param contactList -> this list contains to be displayed contacts.
     */
    private void startContactView(ArrayList<Contact> contactList){
        ArrayList<DataObjectForCardView> dataObjectList = new ArrayList<>();
        for(Contact c : contactList){
            DataObjectForCardView dObject = new DataObjectForCardView(c.getName(),c.getMail(),c.getCompanyName(),c.getId());
            dataObjectList.add(dObject);
        }
        DisplayContactsActivity.setDataObjectList(dataObjectList);
        Intent displayIntent = new Intent(context, DisplayContactsActivity.class);
        context.startActivity(displayIntent);
    }

    /**
     * this method provide all the functionality and add all the details to the database. this method created for access the class elements
     * * @param contact
     */
    private void addContactFromInnerClass(Contact contact){
        String address = "";
        for(int i = 0; i<contact.getAddress().size(); i++){
            address+=contact.getAddress().get(i);
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantClass.DatabaseHandler.KEY_NAME, contact.getName()); // Contact Name
        values.put(ConstantClass.DatabaseHandler.KEY_COMPANY_NAME, contact.getCompanyName()); // Contact Phone Number
        values.put(ConstantClass.DatabaseHandler.KEY_MAIL, contact.getMail());
        values.put(ConstantClass.DatabaseHandler.KEY_OCCUPATION, contact.getOccupation());
        values.put(ConstantClass.DatabaseHandler.KEY_WEB, contact.getWeb());
        values.put("address", address);
        long i = db.insert(ConstantClass.DatabaseHandler.TABLE_CONTACTS, null, values);
        Cursor c = db.rawQuery("SELECT id FROM contacts ORDER BY id DESC LIMIT 1 ", null);
        int k=-1;
        if (c.moveToFirst()){
            do {
                // Passing values
                k = c.getInt(0);
                // Do something Here with values
            } while(c.moveToNext());
        }
        if(i!=-1 && k!=-1){
            ContentValues values2 = new ContentValues();
            for(int j = 0; j<contact.getNumbers().size(); j++){
                try {
                    values2.put("contact_id", i);
                    values2.put("number", removeLeadinZero(contact.getNumbers().get(j).split(" ")[0]));
                    values2.put("detail", contact.getNumbers().get(j).split(" ")[1]);
                    long i1 = db.insert(ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS, null, values2);
                    i1 = 7;
                }
                catch(Exception e){

                }
            }
            ContentValues values3 = new ContentValues();
            for(int j = 0; j<contact.getOthers().size(); j++){
                try {

                    values3.put("contact_id", i);
                    values3.put("data", contact.getOthers().get(j));
                    values3.put("detail", "");
                    long i1 = db.insert(ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS, null, values3);
                    i1 = 7;
                }
                catch(Exception e){

                }
            }
        }
        c.close();
        db.close(); // Closing database connection
    }

    /**
     * to extract number leters form a string.
     * @param s
     * @return
     */
    public String getNumbers(String s){
        String toOut = "";
        for(int i = 0; i<s.length(); i++){
            if(Character.isDigit(s.charAt(i))){
                toOut+=s.charAt(i);
            }
        }
        return toOut;
    }

    /**
     * it will remove leading zero form give string if possible. we can use this to remove leading zero in a phone number  string because sqlite have some problem when compare string when it has leading zero
     * @param s
     * @return
     */
    public String removeLeadinZero(String s){
        if(s.charAt(0) == '0'){
            return s.substring(1,s.length());
        }
        return s;
    }

    /**
     * to get a list contacts that are similar to the given contact.
     * @param contact
     * @return
     */
    public ArrayList<Contact> getSimilarContacts(Contact contact){
        String querySubString = "";     // querySubString is for add filter SQL code.
        for (int i = 0; i<contact.getNumbers().size(); i++){
            querySubString += " "+ ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS+"."+ ConstantClass.DatabaseHandler.KEY_NUMBER+"="
                    +removeLeadinZero(getNumbers(contact.getNumbers().get(i)))+" OR";
        }
        ArrayList<Contact> contactList = new ArrayList<>();
        if(querySubString.trim().length()<3){
            return contactList;
        }
        querySubString=querySubString.substring(0,querySubString.length()-2);

        // Select All Query
        if(querySubString.trim().length()<3){
            return contactList;
        }
        String selectQuery ="SELECT DISTINCT "+ ConstantClass.DatabaseHandler.KEY_ID+","+ ConstantClass.DatabaseHandler.KEY_NAME+","
                + ConstantClass.DatabaseHandler.KEY_COMPANY_NAME+","+ ConstantClass.DatabaseHandler.KEY_MAIL+" FROM "
                + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_ID+" in (SELECT DISTINCT "
                + ConstantClass.DatabaseHandler.KEY_CONTACT_ID+" FROM "+ ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS+" WHERE "+querySubString+")";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact tempContact = new Contact();
                tempContact.setId(cursor.getInt(0));
                tempContact.setName(cursor.getString(1));
                tempContact.setCompanyName(cursor.getString(2));
                tempContact.setMail(cursor.getString(3));
                //tempContact.setWeb(cursor.getString(4));
                //tempContact.setOccupation(cursor.getString(5));
                //tempContact.setAddressFinal(cursor.getString(6));
                contactList.add(tempContact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contactList;
    }

    /**
     * @returnreturn all the contacts stored on the database.
     */
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" ORDER BY "+ConstantClass.DatabaseHandler.KEY_ID+" DESC";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setCompanyName(cursor.getString(2));
                contact.setMail(cursor.getString(3));
                contact.setWeb(cursor.getString(4));
                contact.setOccupation(cursor.getString(5));
                contact.setAddressFinal(cursor.getString(6));




                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        return contactList;
    }

    public ArrayList<Contact> getAllContactsWithNumbers() {
        ArrayList<Contact> contactList = new ArrayList<Contact>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setCompanyName(cursor.getString(2));
                contact.setMail(cursor.getString(3));
                contact.setWeb(cursor.getString(4));
                contact.setOccupation(cursor.getString(5));
                contact.setAddressFinal(cursor.getString(6));

                SQLiteDatabase db1 = this.getWritableDatabase();
                selectQuery = "SELECT * FROM "+ ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID+" = "+cursor.getInt(0)+"";
                Cursor cursor1 = db1.rawQuery(selectQuery, null);
                if (cursor1.moveToFirst()) {
                    do {
                        contact.addNumber(cursor1.getString(2),cursor1.getString(3));
                    } while (cursor1.moveToNext());
                }
                cursor1.close();
                selectQuery = "SELECT * FROM "+ ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID+" = "+cursor.getInt(0)+"";
                Cursor cursor2 = db1.rawQuery(selectQuery, null);
                if (cursor2.moveToFirst()) {
                    do {
                        contact.addOthers(cursor2.getString(2),cursor2.getString(3));
                    } while (cursor2.moveToNext());
                }
                cursor2.close();
                db1.close();


                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        db.close();
        return contactList;
    }




    /**
    public static void reCreateTables(Databasehandler dh) {
        SQLiteDatabase db = dh.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_OTHER_DETAILS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PHONE_NUMBERS);
        db.close();
        // Create tables again
        createTables(dh);
    }
    public static void createTables(Databasehandler dh){
        SQLiteDatabase sqLiteDatabase = dh.getWritableDatabase();
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_COMPANY_NAME + " TEXT," + KEY_MAIL + " TEXT,"+KEY_WEB + " TEXT,"+KEY_OCCUPATION + " TEXT, address TEXT"+")";
        //String CREATE_CONTACTS_TABLE = "CREATE TABLE contacts (id INTEGER PRIMARY KEY, name TEXT, company_name TEXT, mail TEXT, web TEXT, occupation TEXT)";
        String CREATE_PHONE_NUMBERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PHONE_NUMBERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + "contact_id" + " INTEGER,"+ "number TEXT,"
                +"detail TEXT, foreign key(contact_id) references contacts(id)"+")";
        String CREATE_OTHER_DETAILS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_OTHER_DETAILS + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"+ "contact_id" + " INTEGER," + "data TEXT,"
                +"detail TEXT, foreign key(contact_id) references contacts(id)"+")";
        String CREATE_USER_TABLE = "CREATE TABLE IF NOT EXISTS" + TABLE_USER + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"+ "mail" + " TEXT," + "name TEXT,"
                +"password TEXT"+")";
        sqLiteDatabase.execSQL(CREATE_CONTACTS_TABLE);
        sqLiteDatabase.execSQL(CREATE_OTHER_DETAILS_TABLE);
        sqLiteDatabase.execSQL(CREATE_PHONE_NUMBERS_TABLE);
        //sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.close();
    }**/
    /**
     * return specific contact that has given parameters as value.
     * @param name
     * @param companyName
     * @param mail
     * @param phoneNum
     * @param address
     * @return
     */
    public ArrayList<Contact> selectContact(String name, String companyName, String mail, String phoneNum ,String address){
        ArrayList<Contact> contactList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_NAME+" LIKE '%"+name+"%' "
                +"UNION "+
                "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_COMPANY_NAME+
                " LIKE '%"+companyName+"%' " +"UNION "+
                "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_MAIL+" LIKE '%"
                +mail+"%' " +"UNION "+
                "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_ADDRESS+" LIKE '%"
                +address+"%' " +"UNION "+
                "SELECT  "+ ConstantClass.DatabaseHandler.TABLE_CONTACTS+"."+ ConstantClass.DatabaseHandler.KEY_ID+","
                + ConstantClass.DatabaseHandler.KEY_NAME+","+ ConstantClass.DatabaseHandler.KEY_COMPANY_NAME+"," +
                ""+ ConstantClass.DatabaseHandler.KEY_MAIL+","+ ConstantClass.DatabaseHandler.KEY_WEB+","
                + ConstantClass.DatabaseHandler.KEY_OCCUPATION+","+ ConstantClass.DatabaseHandler.KEY_ADDRESS+" FROM "
                + ConstantClass.DatabaseHandler.TABLE_CONTACTS +" JOIN "+ ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS+" ON "
                + ConstantClass.DatabaseHandler.TABLE_CONTACTS+"."+ ConstantClass.DatabaseHandler.KEY_ID+" = "+ ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS+"."
                + ConstantClass.DatabaseHandler.KEY_CONTACT_ID+" WHERE "+ ConstantClass.DatabaseHandler.KEY_NUMBER+" LIKE '%"+phoneNum+"%' ";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setCompanyName(cursor.getString(2));
                contact.setMail(cursor.getString(3));
                contact.setWeb(cursor.getString(4));
                contact.setOccupation(cursor.getString(5));
                contact.setAddressFinal(cursor.getString(6));
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contactList;
    }

    /**
     * return contact that has specific id
     * @param id
     * @return
     */
    public Contact getContact(int id){
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_CONTACTS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_ID+" ="+id+"";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Contact contact = new Contact();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                contact.setId(cursor.getInt(0));
                contact.setName(cursor.getString(1));
                contact.setCompanyName(cursor.getString(2));
                contact.setMail(cursor.getString(3));
                contact.setWeb(cursor.getString(4));
                contact.setOccupation(cursor.getString(5));
                contact.setAddressFinal(cursor.getString(6));
            } while (cursor.moveToNext());
        }
        cursor.close();
        selectQuery = "SELECT * FROM "+ ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID+" = "+id+"";
        Cursor cursor1 = db.rawQuery(selectQuery, null);
        if (cursor1.moveToFirst()) {
            do {
                contact.addNumber(cursor1.getString(2),cursor1.getString(3));
            } while (cursor1.moveToNext());
        }
        cursor1.close();
        selectQuery = "SELECT * FROM "+ ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS+" WHERE "+ ConstantClass.DatabaseHandler.KEY_CONTACT_ID+" = "+id+"";
        Cursor cursor2 = db.rawQuery(selectQuery, null);
        if (cursor2.moveToFirst()) {
            do {
                contact.addOthers(cursor2.getString(2),cursor2.getString(3));
            } while (cursor2.moveToNext());
        }
        cursor2.close();
        db.close();
        return contact;
    }

    /**
     * this method will take a DataObjectForCardView object and delete respected contact detail on the database which can be used in deleting from recyclerview.
     * @param dObject
     * @return
     */
    public boolean deleteThisObject(DataObjectForCardView dObject){
        int  id  = dObject.getId();
        SQLiteDatabase db = this.getWritableDatabase();
        int i1 = db.delete(ConstantClass.DatabaseHandler.TABLE_CONTACTS, ConstantClass.DatabaseHandler.KEY_ID + "=" +id, null);
        int i2 = db.delete(ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS, ConstantClass.DatabaseHandler.KEY_CONTACT_ID + "=" + id, null);
        int i3 = db.delete(ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS, ConstantClass.DatabaseHandler.KEY_CONTACT_ID + "=" +id, null);
        return !(i1<0 || i2<0 || i3<0);
    }

    /**
     * this methhod will delete specific contactg from database which has given id value.
     * @param id
     * @return
     */
    public boolean  deleteContact(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int i1 = db.delete(ConstantClass.DatabaseHandler.TABLE_CONTACTS, ConstantClass.DatabaseHandler.KEY_ID + "=" +id, null);
        int i2 = db.delete(ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS, ConstantClass.DatabaseHandler.KEY_CONTACT_ID + "=" +id, null);
        int i3 = db.delete(ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS, ConstantClass.DatabaseHandler.KEY_CONTACT_ID + "=" +id, null);
        return !(i1<0 || i2<0 || i3<0);
    }

    /**
     * can get SQLiteDatabase instant.
     * @param context
     * @return
     */
    public SQLiteDatabase getDatabase(Context context){
        return new Databasehandler(context).getWritableDatabase();
    }

    /**
     * we can update user details who uses the application by giving a instant of the contact object
     * @param contact
     */
    public void updateUserDetails(Contact contact){
        ContentValues cv = new ContentValues();
        cv.put(ConstantClass.DatabaseHandler.KEY_NAME,contact.getName()); //These Fields should be your String values of actual column names
        cv.put(ConstantClass.DatabaseHandler.KEY_ADDRESS,contact.getAddressFinal());
        cv.put(ConstantClass.DatabaseHandler.KEY_COMPANY_NAME,contact.getCompanyName());
        cv.put(ConstantClass.DatabaseHandler.KEY_MAIL,contact.getMail());
        cv.put(ConstantClass.DatabaseHandler.KEY_OCCUPATION,contact.getOccupation());
        cv.put(ConstantClass.DatabaseHandler.KEY_WEB,contact.getWeb());

        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.update(ConstantClass.DatabaseHandler.TABLE_CONTACTS, cv, "id=1", null);
        int i2 = db.delete(ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS, ConstantClass.DatabaseHandler.KEY_CONTACT_ID + "=1" , null);
        int i3 = db.delete(ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS, ConstantClass.DatabaseHandler.KEY_CONTACT_ID + "=1" , null);
        ContentValues values2 = new ContentValues();
        for(int j = 0; j<contact.getNumbers().size(); j++){
            values2.put("contact_id",1);
            values2.put("number",removeLeadinZero(contact.getNumbers().get(j).split(" ")[0]));
            values2.put("detail",contact.getNumbers().get(j).split(" ")[1]);
            long i1 = db.insert(ConstantClass.DatabaseHandler.TABLE_PHONE_NUMBERS, null, values2);
            i1 =7;
        }
        ContentValues values3 = new ContentValues();
        for(int j = 0; j<contact.getOthers().size(); j++){
            values3.put("contact_id",1);
            values3.put("data",contact.getOthers().get(j));
            values3.put("detail","");
            long i1 = db.insert(ConstantClass.DatabaseHandler.TABLE_OTHER_DETAILS, null, values3);
            i1 = 7;
        }
        //Intent mainIntent = new Intent(context, MainActivity.class);
        //context.startActivity(mainIntent);
        ((Activity)context).finish();

    }

    /**
     * this method is to create string that has string derived by adding tags and values of the commonInfo.
     * @param tagList
     * @param valueList
     * @return
     */
    public static String prepareCommonInfoValue(ArrayList<String> tagList, ArrayList<String> valueList){
        String toOut = "";
        for(int i = 0; i<tagList.size(); i++){
            toOut+=ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+tagList.get(i)+ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+valueList.get(i);
        }
        return toOut;
    }
    public void addCommonInfo(String s, Context context){
        if(s.length()>0) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(ConstantClass.DatabaseHandler.KEY_INFO, s);
            long i = db.insert(ConstantClass.DatabaseHandler.TABLE_COMMON_INFO, null, values);
        }
    }
    public ArrayList<CommonInfo> getAllCommonInfo(){
        ArrayList<CommonInfo> commonInfoList = new ArrayList();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_COMMON_INFO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CommonInfo commonInfo = new CommonInfo(cursor.getInt(0),cursor.getString(1));

                commonInfoList.add(commonInfo);
            } while (cursor.moveToNext());
        }
        db.close();
        return commonInfoList;
    }
    public boolean deleteThisCommonInfoObject(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        int i1 = db.delete(ConstantClass.DatabaseHandler.TABLE_COMMON_INFO, ConstantClass.DatabaseHandler.KEY_ID + "=" +id, null);
        return !( i1<0 );
    }
    public CommonInfo getCommonInfo(int id){
        CommonInfo commonInfo;
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_COMMON_INFO+" WHERE "+ConstantClass.DatabaseHandler.KEY_ID+" = "+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                commonInfo = new CommonInfo(cursor.getInt(0),cursor.getString(1));

            } while (cursor.moveToNext());
        }
        else{
            commonInfo = new CommonInfo(0,ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+"Title"+ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+"not found");
        }
        db.close();
        return commonInfo;
    }
    public String getCommonInfoEncodedString(int id){
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_COMMON_INFO+" WHERE "+ConstantClass.DatabaseHandler.KEY_ID+" = "+id;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String s = "";
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                s =  cursor.getString(1);

            } while (cursor.moveToNext());
        }
        else{
            s =  ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+"Title"+ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER+"not found";
        }
        db.close();
        return s;
    }
    public boolean updateCommonInfo(int id, String value){
        ContentValues cv = new ContentValues();
        cv.put(ConstantClass.DatabaseHandler.KEY_INFO,value);

        SQLiteDatabase db = this.getWritableDatabase();
        int i = db.update(ConstantClass.DatabaseHandler.TABLE_COMMON_INFO, cv, "id= "+id, null);
        if(i>=0){
            return true;
        }
        else{
            return false;
        }
    }
    public ArrayList<CommonInfo> getMatchingCommonInfos(String searchString){
        ArrayList<CommonInfo> commonInfoList = new ArrayList();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_COMMON_INFO+" WHERE "+ConstantClass.DatabaseHandler.KEY_INFO+" LIKE '%"+searchString+"%'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                CommonInfo commonInfo = new CommonInfo(cursor.getInt(0),cursor.getString(1));

                commonInfoList.add(commonInfo);
            } while (cursor.moveToNext());
        }
        db.close();
        return commonInfoList;
    }
    public ArrayList<String> getAllCommonInfoStrings(){
        ArrayList<String> commonInfoStringList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_COMMON_INFO;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                commonInfoStringList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        return commonInfoStringList;
    }

    public void addTags(String[] tagList){
        SQLiteDatabase db = this.getWritableDatabase();
        for(String s : tagList) {
            ContentValues values = new ContentValues();
            values.put(ConstantClass.DatabaseHandler.KEY_TAG_VALUE, s);
            long i = db.insert(ConstantClass.DatabaseHandler.TABLE_TAG, null, values);
        }
    }
    public void addTag(String tag){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantClass.DatabaseHandler.KEY_TAG_VALUE, tag);
        long i = db.insert(ConstantClass.DatabaseHandler.TABLE_TAG, null, values);

    }
    public void addRemovables(String[] removableList){
        SQLiteDatabase db = this.getWritableDatabase();
        for(String s : removableList) {
            ContentValues values = new ContentValues();
            values.put(ConstantClass.DatabaseHandler.KEY_REMOVABLE_VALUE, s);
            long i = db.insert(ConstantClass.DatabaseHandler.TABLE_REMOVABLES, null, values);
        }
    }
    public void addRemovable(String removable){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ConstantClass.DatabaseHandler.KEY_REMOVABLE_VALUE, removable);
        long i = db.insert(ConstantClass.DatabaseHandler.TABLE_REMOVABLES, null, values);
    }
    public ArrayList<String> getTags(){
        ArrayList<String> tags = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_TAG;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                tags.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        return tags;
    }
    public ArrayList<String> getRemovabels(){
        ArrayList<String> removables = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + ConstantClass.DatabaseHandler.TABLE_REMOVABLES;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                removables.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }
        db.close();
        return removables;
    }

}
