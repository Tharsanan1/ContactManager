package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.R;

import java.util.ArrayList;

public class DisplayContactsActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    MyRecyclerViewAdapter mAdapter;
    static ArrayList<DataObjectForCardView> staticDataObjectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_contacts);
        mRecyclerView = findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        getSupportActionBar().setTitle("CONTACTS");
    }

    public ArrayList<DataObjectForCardView> getDataSet() {

        return staticDataObjectList;

    }
    public static void setDataObjectList(ArrayList<DataObjectForCardView> dataObjectList){
        staticDataObjectList = dataObjectList;
    }
}

