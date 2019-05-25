package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models;

/**
 * Created by kthar on 09/02/2018.
 */

public class Info {
    static private boolean isCamFab;
    static private boolean isGalleryFab;
    static private boolean isSearchFab;
    static public boolean isDisplayContactFirstTime = false;
    static public boolean isDisplayCommonInfoFirstTime = false;
    static public boolean isDisplayMainActivityFirstTime = false;
    public static boolean flagForDisplayToolTipOnCardViewContacts = false;
    public static boolean flagForDisplayToolTipOnCardViewCommonInfo = false;
    public static boolean flagForStartSplashActivity = false;

    public static void setAllToTrue(){
        isDisplayContactFirstTime = true;
        isDisplayCommonInfoFirstTime = true;
        isDisplayMainActivityFirstTime = true;
    }


    public static boolean isIsCamFab() {
        return isCamFab;
    }

    public static boolean isIsGalleryFab() {
        return isGalleryFab;
    }

    public static boolean isIsSearchFab() {
        return isSearchFab;
    }

    public static void setIsCamFab(boolean isCamFab) {
        Info.isCamFab = isCamFab;
    }

    public static void setIsGalleryFab(boolean isGalleryFab) {
        Info.isGalleryFab = isGalleryFab;
    }

    public static void setIsSearchFab(boolean isSearchFab) {
        Info.isSearchFab = isSearchFab;
    }
}

