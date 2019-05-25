package com.ktharsanan.contactmangerversion1;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Display;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.*;


/**
 * Created by kthar on 13/05/2018.
 */
@RunWith(MockitoJUnitRunner.class)
public class UnitTestForDatabaseHandler {
    @Mock
    Context context;

    Databasehandler databasehandler;

    @Before
    public void setUp() {
        databasehandler = Databasehandler.getdatabaseHandler(context);
    }

    @Test
    public void checkIsExtractNumberFormStringWorkingFine() throws Exception {
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(context);
        assertEquals("743483438736", databasehandler.getNumbers("hjhbavshddfbjhxc7q434rbqq834rgqbg3g87vahdjfgq36"));
    }
    @Test
    public void checkRemovingZeroWorkingFine(){
        assertEquals("773139405", databasehandler.removeLeadinZero("0773139405"));
        assertEquals("s237562387", databasehandler.removeLeadinZero("s237562387"));
        assertEquals("034857646", databasehandler.removeLeadinZero("0034857646"));
        assertEquals("s237562387", databasehandler.removeLeadinZero("0s237562387"));
        //Contact contact = databasehandler.getContact(0);
    }

}
