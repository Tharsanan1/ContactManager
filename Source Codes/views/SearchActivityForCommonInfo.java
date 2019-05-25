package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views.DisplaySomeCommonInfos;
import com.ktharsanan.contactmangerversion1.R;

public class SearchActivityForCommonInfo extends AppCompatActivity {
    EditText edittextForSearch;
    Button searchbtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_for_common_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edittextForSearch = findViewById(R.id.editTextForSearchInCommonInfos);
        searchbtn = findViewById(R.id.buttonForsearch);
        searchbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(edittextForSearch.getText().toString().length()>0){
                    Intent intent = new Intent(view.getContext(), DisplaySomeCommonInfos.class);
                    DisplaySomeCommonInfos.setSearchString(edittextForSearch.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
