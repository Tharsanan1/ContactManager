package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Contact;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.R;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {
    EditText nameTxt,companyNameTxt,districtTxt,phoneNumTxt,mailTxt;
    Button searchBtn;
    static ArrayList<DataObjectForCardView> dataObjectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        nameTxt = findViewById(R.id.nameText);
        companyNameTxt = findViewById(R.id.companyNameText);
        districtTxt = findViewById(R.id.districtText);
        searchBtn = findViewById(R.id.searchBtn);
        phoneNumTxt = findViewById(R.id.phoneNumText);
        mailTxt = findViewById(R.id.mailText);
        getSupportActionBar().setTitle("SEARCH");
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(nameTxt.getText().toString().equals("")){
                    nameTxt.setText("***");
                }
                if(mailTxt.getText().toString().equals("")){
                    mailTxt.setText("***");
                }
                if(districtTxt.getText().toString().equals("")){
                    districtTxt.setText("***");
                }
                if(companyNameTxt.getText().toString().equals("")){
                    companyNameTxt.setText("***");
                }
                if(phoneNumTxt.getText().toString().equals("")){
                    phoneNumTxt.setText("***");
                }
                search();
            }
        });
    }
    private void search(){
        Databasehandler dh = Databasehandler.getdatabaseHandler(this);
        //Databasehandler dh = new Databasehandler(this);
        ArrayList<Contact> contactList = dh.selectContact(nameTxt.getText().toString(),companyNameTxt.getText().toString(),mailTxt.getText().toString(),phoneNumTxt.getText().toString(),districtTxt.getText().toString());
        dataObjectList = new ArrayList<>();
        for(Contact c : contactList){
            DataObjectForCardView dObject = new DataObjectForCardView(c.getName(),c.getMail(),c.getCompanyName(),c.getId());
            dataObjectList.add(dObject);
        }
        DisplayContactsActivity.setDataObjectList(dataObjectList);
        Intent displayIntent = new Intent(this, DisplayContactsActivity.class);
        startActivity(displayIntent);
    }


}
