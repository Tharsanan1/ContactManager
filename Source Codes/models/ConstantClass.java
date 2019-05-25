package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

/**
 * Created by kthar on 18/04/2018.
 * This class stores details of constant values in this system.
 * Constants of the different class values are under same inner class name.
 */

public abstract class ConstantClass {
    /**
     * Inside this class we have constant values that are related to the Databasehandler class.
     */
    public class DatabaseHandler{
        public static final int DATABASE_VERSION = 1;                                               // version number of the database which is always integer
        public static final String DATABASE_NAME = "OnlineContactsManager";                         // name of the database we are going to use in sqlite
        public static final String TABLE_CONTACTS = "contacts";                                     // name of the table which is going to store contact information extracted from visiting cards
        public static final String TABLE_USER = "user";                                             // name of the table which is going to store information about the application user
        public static final String KEY_USER_PASSWORD = "password";                                  // name of the column in user table to store password of the application
        public static final String TABLE_PHONE_NUMBERS = "phone_numbers";                           // name of the table which is going to store phone numbers of the contacts
        public static final String TABLE_OTHER_DETAILS = "other_details";                           // name of the table which is going to store details of the contact that dont have any tag information
        public static final String KEY_ID = "id";                                                   // name of the column in all the tables. and this is going to be one of the primary key member in the table.
        public static final String KEY_NAME = "name";
        public static final String KEY_PHONE_NO = "phone_number";
        public static final String KEY_MAIL = "mail";
        public static final String KEY_OCCUPATION = "occupation";
        public static final String KEY_COMPANY_NAME = "company_name";
        public static final String KEY_WEB = "web";
        public static final String KEY_ADDRESS = "address";
        public static final String KEY_CONTACT_ID = "contact_id";
        public static final String KEY_DETAIL = "detail";
        public static final String KEY_NUMBER = "number";
        public static final String KEY_TAG_DEVIDER = "CAoPnPTLaECEt";                               // this is the string key will be added between tag and value so that we can devide tag and value when extracting.
        public static final String TABLE_COMMON_INFO = "common_infos";                              // name of the table which is going to store common info text which is extracted from none visiting card reference
        public static final String KEY_INFO = "info";
        public static final String TABLE_TAG = "tag";
        public static final String KEY_TAG_VALUE = "tag_value";
        public static final String TABLE_REMOVABLES = "removables";                                 // name of the table which is going to store removable values of a sentence for speech recognition.
        public static final String KEY_REMOVABLE_VALUE = "removable_value";
    }
    public class ShowExtracted{
        public static final int NAME = 0;                                                           // this is the order of the spinner list in DisplayContacts activity
        public static final int PHONENUM = 1;
        public static final int MAIL = 2;
        public static final int ADDRESS = 3;
        public static final int WEB = 4;
        public static final int OTHER = 9;
        public static final int WORKNUM = 8;
        public static final int COMPANY = 5;
        public static final int FAX = 6;
        public static final int HOMENUM = 7;
        public static final int OCCUPATION = 10;

        public static final String SPINNER_NAME = "name";
        public static final String SPINNER_PHONE_NUM = "phone_num";
        public static final String SPINNER_MAIL = "mail";
        public static final String SPINNER_NADDRESS = "address";
        public static final String SPINNER_WEB = "web";
        public static final String SPINNER_COMPANY = "company";
        public static final String SPINNER_FAX = "fax";
        public static final String SPINNER_HOME_NUM = "home";
        public static final String SPINNER_WORK_NUM = "workNum";
        public static final String SPINNER_OTHER = "other";
        public static final String SPINNER_OCCUPATION = "occupation";
    }
    public class MainActivity{
        public static final int SPLASH_TIME_OUT = 2000;
        public static final int SPLASH_TIME_OUT_SMALL = 500;
    }
    public class ShareEveryThingActivity{
        public static final String TAG_DEVIDER_FOR_CONTATCS = "ASDFGH3223GFDSAJK";
        public static final String TAG_DEVIDER_FOR_COMMON_INFO = "zxcvb3223bvcxz";
    }
    public class ToolTip{
        public static final String TOOLTIP_FOR_FABCAM = "CAMERA";
        public static final String TOOLTIP_FOR_FABPLUS = "Long Press Here for voice input don't forget to tell 'full stop' to complete a sentence";
        public static final String TOOLTIP_FOR_FABGALLERY = "GALLERY";
        public static final String TOOLTIP_FOR_FABCOMMONINFO = "COMMONINFO";
        public static final String TOOLTIP_FOR_ACTION_EDIT = "Tab here to edit details";
        public static final String TOOLTIP_FOR_FIRST_CARD = "LongPressHere for share and delete or tab here for detail";
        public static final String TOOLTIP_FOR_ACTION_PROFILE = "PROFILE";
        public static final String TOOLTIP_FOR_ACTION_QR = "QR_SCAN";
        public static final String TOOLTIP_FOR_ACTION_SEARCH = "SEARCH";
        public static final String TOOLTIP_FOR_TAB_ON_TAGS = "Tab these tags for some quick actions ex: CALL";
        //public static final String TOOLTIP_FOR_FABCAM = "";
        //public static final String TOOLTIP_FOR_FABCAM = "";
    }
}
