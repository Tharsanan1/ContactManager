package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.ktharsanan.contactmangerversion1.R;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.CommonInfo;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;

import java.util.ArrayList;

public class DisplaySomeCommonInfos extends AppCompatActivity {
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerViewForCommonInfo mAdapter;
    static String searchString = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_some_common_infos);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.my_recycler_view2);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewForCommonInfo(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        getSupportActionBar().setTitle("COMMON");
    }

    public static void setSearchString(String searchString) {
        DisplaySomeCommonInfos.searchString = searchString;
    }

    private ArrayList<DataObjectForCardView> getDataSet() {
        ArrayList<DataObjectForCardView> dataObjectForCardViewList = new ArrayList<>();
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(this);
        //Databasehandler databasehandler = new Databasehandler(this);
        ArrayList<CommonInfo> commonInfos = databasehandler.getMatchingCommonInfos(searchString);
        for(CommonInfo c : commonInfos){
            String s1 = "",s2 = "",s3 = "";
            if(c.tagList.size()>=3){
                s1 = c.valueList.get(0);
                s2 = c.valueList.get(1);
                s3 = c.valueList.get(2);
            }
            else if(c.tagList.size()>=2){
                s1 = c.valueList.get(0);
                s2 = c.valueList.get(1);
            }
            else if(c.tagList.size()>=1){
                s1 = c.valueList.get(0);
            }
            else{
            }
            DataObjectForCardView dataObjectForCardView = new DataObjectForCardView(s1,s2,s3,c.getId());
            dataObjectForCardViewList.add(dataObjectForCardView);
        }
        return dataObjectForCardViewList;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
