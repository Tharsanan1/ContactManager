package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;


import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ClassForSavingSerializableObject;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Contact;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.NodeForSpeechRecognition;
import com.ktharsanan.contactmangerversion1.R;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * this is the main activity but not launcher activity. launcher activity is spalshActivity.
 * this activity control all the fab buttons and main activity action buttons.
 */
public class MainActivity extends AppCompatActivity {
    FloatingActionButton fabCam;
    FloatingActionButton fabGallery;
    FloatingActionButton fabCommonInfo;
    FloatingActionButton fabPlus;
    Animation fabOpen,fabClose,fabRotateClock,fabRotateAntiClock;
    boolean isOpen;
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    MyRecyclerViewAdapter mAdapter;
    ArrayList<Tooltip> tooltips;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.my_recycler_view1);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        System.gc();

        if (ContextCompat.checkSelfPermission(this,                                          // check whether all the needed permissions are available or not and ask for permission if not.
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    1);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.INTERNET},
                    2);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.CALL_PHONE},
                    2);
        }
        tooltips = new ArrayList<>();                                                               // tooltip arraylist which contaains all the tool tips created inside this class. because we can easily dismiss all the tooltips by traversing this arraylist.
        Databasehandler db = Databasehandler.getdatabaseHandler(this);
        //Databasehandler db = new Databasehandler(this);
        ClassForSavingSerializableObject classForSavingSerializableObject = new ClassForSavingSerializableObject(NodeForSpeechRecognition.getGlobalNodeList());
        fabPlus = findViewById(R.id.fabPlus);
        Tooltip tooltipPlus;
        if (Databasehandler.isFirstTime) {                                                            // on application first time on the device.
            db.addContact(new Contact());
            //db = new Databasehandler(this);
            db.addTags("Name mail companyName company occupation mobile fax phone num home address no".split(" "));
            //db = new Databasehandler(this);
            db.addRemovables("is are am was an does".split(" "));
            Info.isDisplayContactFirstTime = true;
            Info.isDisplayCommonInfoFirstTime = true;
            classForSavingSerializableObject.saveToFile(this);
            tooltipPlus = new Tooltip.Builder(findViewById(R.id.fabPlus))
                    .setText(ConstantClass.ToolTip.TOOLTIP_FOR_FABPLUS).setGravity(Gravity.TOP).setTextColor(Color.BLACK).setDismissOnClick(true).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                    .show();
            tooltips.add(tooltipPlus);

        }
        if (NodeForSpeechRecognition.flagForUpdateFromFile == false) {                              // store trainned model into the class
            NodeForSpeechRecognition.setGlobalNodeList(classForSavingSerializableObject.readFromFile(this).getNodeForSpeechRecognitions());
            NodeForSpeechRecognition.trainTreeUsing("i am tharsanan".toUpperCase(), "name".toUpperCase(), "tharsanan".toUpperCase());
        }
        Databasehandler.isFirstTime = false;
        NodeForSpeechRecognition.flagForUpdateFromFile = true;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fabCam = findViewById(R.id.fabCam);
        fabGallery = findViewById(R.id.fabGallery);
        fabCommonInfo = findViewById(R.id.fabSearch);

        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);
        fabRotateAntiClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_anti_clock);
        fabRotateClock = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_rotate_clock);
        isOpen = false;

        fabPlus.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //tempMethodFroTest();

                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "en-US");
                try {

                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException a) {

                    Toast.makeText(getApplicationContext(),

                            "Opps! Your device doesn’t support Speech to Text", Toast.LENGTH_SHORT).show();
                }
                dismissToolTips();
                return true;
            }
        });

        fabPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!isOpen) {
                    fabCam.startAnimation(fabOpen);
                    fabCommonInfo.startAnimation(fabOpen);
                    fabGallery.startAnimation(fabOpen);
                    fabPlus.startAnimation(fabRotateClock);
                    isOpen = true;
                    fabCam.setClickable(true);
                    fabGallery.setClickable(true);
                    fabCommonInfo.setClickable(true);
                    //dismissToolTips();
                    if (Info.isDisplayMainActivityFirstTime) {
                        Tooltip tooltipCamera = new Tooltip.Builder(findViewById(R.id.fabCam))
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_FABCAM).setGravity(Gravity.TOP).setTextColor(Color.BLACK).setDismissOnClick(true).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .show();
                        Tooltip tooltipGallery = new Tooltip.Builder(findViewById(R.id.fabGallery)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_FABGALLERY).setGravity(Gravity.TOP).setTextColor(Color.BLACK).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .show();
                        Tooltip tooltipCommonInfo = new Tooltip.Builder(findViewById(R.id.fabSearch)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_FABCOMMONINFO).setGravity(Gravity.TOP).setTextColor(Color.BLACK).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .show();
                        Tooltip tooltipProfile = new Tooltip.Builder(findViewById(R.id.action_profile)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_ACTION_PROFILE).setGravity(Gravity.BOTTOM).setTextColor(Color.BLACK).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .show();
                        Tooltip tooltipQR = new Tooltip.Builder(findViewById(R.id.action_qr_scanner)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_ACTION_QR).setGravity(Gravity.BOTTOM).setTextColor(Color.BLACK).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .show();
                        Tooltip tooltipSearch = new Tooltip.Builder(findViewById(R.id.action_search)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_ACTION_SEARCH).setGravity(Gravity.BOTTOM).setTextColor(Color.BLACK).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .show();
                        tooltips.add(tooltipSearch);
                        tooltips.add(tooltipCommonInfo);
                        tooltips.add(tooltipGallery);
                        tooltips.add(tooltipCamera);
                        tooltips.add(tooltipProfile);
                        tooltips.add(tooltipQR);
                        Info.isDisplayMainActivityFirstTime = false;
                    }
                } else {
                    fabCam.startAnimation(fabClose);
                    fabCommonInfo.startAnimation(fabClose);
                    fabGallery.startAnimation(fabClose);
                    fabPlus.startAnimation(fabRotateAntiClock);
                    isOpen = false;
                    fabCam.setClickable(false);
                    fabGallery.setClickable(false);
                    fabCommonInfo.setClickable(false);
                    dismissToolTips();
                }

            }
        });

        fabCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissToolTips();
                Info.setIsCamFab(true);
                Intent cameraIntent = new Intent(view.getContext(), ImageViewActivity.class);
                fabCam.startAnimation(fabClose);
                fabCommonInfo.startAnimation(fabClose);
                fabGallery.startAnimation(fabClose);
                fabPlus.startAnimation(fabRotateAntiClock);
                isOpen = false;
                fabCam.setClickable(false);
                fabGallery.setClickable(false);
                fabCommonInfo.setClickable(false);
                dismissToolTips();
                startActivity(cameraIntent);

            }
        });
        fabCam.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Info.setAllToTrue();
                return true;
            }
        });
        fabCommonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissToolTips();
                Intent commonInfoIntent = new Intent(view.getContext(), CommonInfoActivity.class);
                fabCam.startAnimation(fabClose);
                fabCommonInfo.startAnimation(fabClose);
                fabGallery.startAnimation(fabClose);
                fabPlus.startAnimation(fabRotateAntiClock);
                isOpen = false;
                fabCam.setClickable(false);
                fabGallery.setClickable(false);
                fabCommonInfo.setClickable(false);
                dismissToolTips();
                startActivity(commonInfoIntent);
            }
        });
        fabGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismissToolTips();
                Info.setIsGalleryFab(true);
                Intent cameraIntent = new Intent(view.getContext(), ImageViewActivity.class);
                fabCam.startAnimation(fabClose);
                fabCommonInfo.startAnimation(fabClose);
                fabGallery.startAnimation(fabClose);
                fabPlus.startAnimation(fabRotateAntiClock);
                isOpen = false;
                fabCam.setClickable(false);
                fabGallery.setClickable(false);
                fabCommonInfo.setClickable(false);
                dismissToolTips();
                startActivity(cameraIntent);
            }
        });





    }
    public ArrayList<DataObjectForCardView> getDataSet(){
        Databasehandler dh = Databasehandler.getdatabaseHandler(this);
        //Databasehandler dh = new Databasehandler(this);
        ArrayList<Contact> contactList = dh.getAllContacts();
        ArrayList<DataObjectForCardView> dataObjectList = new ArrayList<>();
        for(Contact c : contactList){
            if(c.getId() == 1){
                continue;
            }
            DataObjectForCardView dObject = new DataObjectForCardView(c.getName(),c.getMail(),c.getCompanyName(),c.getId());
            dataObjectList.add(dObject);
        }
        return dataObjectList;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        //Tooltip tooltipCommonInfo = new Tooltip.Builder(findViewById(R.id.action_profile)).setDismissOnClick(true)
          //      .setText("CommonInfo").setGravity(Gravity.BOTTOM).setBackgroundColor(Color.MAGENTA)
            //    .show();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.action_qr_scanner) {
            Intent QrIntent = new Intent(this, QrCodeScannerActivity.class);
            startActivity(QrIntent);
            return true;
        }
        if(id == R.id.action_profile){
            Intent profileIntent = new Intent(this,ProfileActivity.class);
            startActivity(profileIntent);
            return true;
        }
        if(id == R.id.action_search){
            Info.setIsSearchFab(true);
            Intent searchIntent = new Intent(getApplicationContext(), SearchActivity.class);
            startActivity(searchIntent);
        }

        return super.onOptionsItemSelected(item);
    }
    public void dismissToolTips(){
        for(Tooltip t : tooltips){
            t.dismiss();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if(text.get(0).length()>0){
                        String s = text.get(0).toUpperCase();
                        if(s.indexOf("FULL STOP")>-1){
                            ArrayList<String> sentenceList = new ArrayList<>(Arrays.asList(s.split("FULL STOP")));
                            DisplayTextRecognizedFromSpeech.setSentenceList(sentenceList);
                            Intent displaySpeechToText = new Intent(this, DisplayTextRecognizedFromSpeech.class);
                            startActivity(displaySpeechToText);
                        }

                    }

                }
                else{
                    ArrayList<String> sentenceList = new ArrayList<>();
                    sentenceList.add("i am name?");
                    sentenceList.add("i am from where?");
                    sentenceList.add("i am a profession?");
                    sentenceList.add("my mail address is mail?");
                    sentenceList.add("my fax number is faxNum?");
                    DisplayTextRecognizedFromSpeech.setSentenceList(sentenceList);
                    Intent displaySpeechToText = new Intent(this, DisplayTextRecognizedFromSpeech.class);
                    startActivity(displaySpeechToText);
                }
                break;
            }
        }
    }
    public void tempMethodFroTest(){
        String s = "i am tharsanan full stop i am a engineer full stop i am from trincomalee".toUpperCase();
        if(s.indexOf("FULL STOP")>-1){
            ArrayList<String> sentenceList = new ArrayList<>(Arrays.asList(s.split("FULL STOP")));
            DisplayTextRecognizedFromSpeech.setSentenceList(sentenceList);
            Intent displaySpeechToText = new Intent(this, DisplayTextRecognizedFromSpeech.class);
            startActivity(displaySpeechToText);
        }
    }
}
