package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Contact;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ShareEverythingActivity extends AppCompatActivity {
    static final String fileName = "fileForShare.txt";
    EditText editText;
    Button shareBtn;
    String allContentDetailsString;
    Button downloadBtn;
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_everything);
        shareBtn = findViewById(R.id.shareAllDetails);
        downloadBtn = findViewById(R.id.downloadDetails);
        allContentDetailsString = "";
        getSupportActionBar().setTitle("SHARE");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ShapeDrawable shapedrawable = new ShapeDrawable();
        shapedrawable.setShape(new RectShape());
        shapedrawable.getPaint().setColor(Color.BLACK);
        shapedrawable.getPaint().setStrokeWidth(4f);
        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
        downloadBtn.setBackground(shapedrawable);
        shareBtn.setBackground(shapedrawable);
        //downloadBtn.setBackgroundColor(Color.TRANSPARENT);
        //shareBtn.setBackgroundColor(Color.TRANSPARENT);
        downloadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, 1);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Databasehandler databasehandler = Databasehandler.getdatabaseHandler(view.getContext());
                //Databasehandler databasehandler = new Databasehandler(view.getContext());
                ArrayList<Contact> contactList = databasehandler.getAllContactsWithNumbers();
                ArrayList<String> commonInfoStringList = databasehandler.getAllCommonInfoStrings();
                createFileWithString(view.getContext(),fileName,preparetAllCOntactAndCommonDetails(contactList,commonInfoStringList));
                File file = new File(Environment.getExternalStorageDirectory() + "/" + "Notes/"+fileName);
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/*");
                sharingIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + file.getAbsolutePath()));
                startActivity(Intent.createChooser(sharingIntent, "share file with"));
            }
        });




    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && data != null){
            Uri uri = data.getData();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
                String line = null;
                allContentDetailsString = "";
                while ((line = br.readLine()) != null) {
                    allContentDetailsString += line;
                }
                br.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            storeEveryDowloadedDetailsOnDatabase();

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void storeEveryDowloadedDetailsOnDatabase(){
        if(allContentDetailsString.trim().length() != 0){
            int startIndexOfCommonInfo = allContentDetailsString.indexOf(ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_COMMON_INFO);
            String contactString = allContentDetailsString.substring(0,startIndexOfCommonInfo);
            String[] contactStringList = contactString.split(ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_CONTATCS);
            Databasehandler databasehandler = Databasehandler.getdatabaseHandler(this.getApplicationContext());
            //Databasehandler databasehandler = new Databasehandler(this.getApplicationContext());
            for(String contactStringChunk : contactStringList){
                if(contactStringChunk.length()>0){
                    Contact contact = Contact.createContactFromString(contactStringChunk);
                    databasehandler.addContactWithoutValidation(contact);
                }
            }
            String commonInfoString = allContentDetailsString.substring(startIndexOfCommonInfo);
            String[] commoninfoStringList = commonInfoString.split(ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_COMMON_INFO);
            for(String commonInfoChunk : commoninfoStringList){
                if(commonInfoChunk.length()>0){
                    databasehandler.addCommonInfo(commonInfoChunk,this);
                }
            }
            allContentDetailsString = "";
        }
        else{
            Toast.makeText(this.getApplicationContext(), "No Contacts Found", Toast.LENGTH_SHORT).show();
        }
        Intent mainIntent = new Intent(this.getApplicationContext(), MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        finish();
    }

    public String prepareAllDetailsForContacts(ArrayList<Contact> contactList){
        String toOut = ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_CONTATCS;
        for(Contact contact : contactList){
            toOut+=contact.toString();
            toOut+=ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_CONTATCS;;
        }
        return toOut;
    }
    public String prepareAllDetailsForCommonInfo(ArrayList<String> commonInfoList){
        String toOut = ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_COMMON_INFO;
        for(String s : commonInfoList){
            toOut+=s;
            toOut+=ConstantClass.ShareEveryThingActivity.TAG_DEVIDER_FOR_COMMON_INFO;;
        }
        return toOut;
    }
    public String preparetAllCOntactAndCommonDetails(ArrayList<Contact> contactList,ArrayList<String> commonInfoList){
        return prepareAllDetailsForContacts(contactList) + prepareAllDetailsForCommonInfo(commonInfoList);
    }

    public void createFileWithString(Context context, String sFileName, String sBody){
        try {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.write(sBody);
            writer.flush();
            writer.close();
            //Toast.makeText(context, "Saved", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String readFileAndGetString(String fileName){
        File sdcard = Environment.getExternalStorageDirectory();

//Get the text file
        File file = new File(sdcard+"/Notes",fileName);

//Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        }
        catch (IOException e) {
            System.out.println(e.toString());
            //You'll need to add proper error handling here
        }
        return text.toString();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
