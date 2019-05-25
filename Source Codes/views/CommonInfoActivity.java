package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.CommonInfo;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.ktharsanan.contactmangerversion1.R;

import java.util.ArrayList;

/**
 * This activity is created to show all the commonInfo that are stored in table(common_infos).
 * RecyclerViewForCommonInfo is the recyclerview adapter that created for this activity. use that always.
 *
 */
public class CommonInfoActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    LinearLayoutManager mLayoutManager;
    RecyclerViewForCommonInfo mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.my_recycler_view2);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new RecyclerViewForCommonInfo(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        getSupportActionBar().setTitle("COMMON");
    }

    /**
     * This method is created to get a arrayList that contains DataObjectForCardView objects.
     * In onCreate() method we will call this method for get all the common info objects in a form of DataObjectForCardView object.
     * @return
     */
    private ArrayList<DataObjectForCardView> getDataSet() {
        ArrayList<DataObjectForCardView> dataObjectForCardViewList = new ArrayList<>();
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(this);
        //Databasehandler databasehandler = new Databasehandler(this);
        ArrayList<CommonInfo> commonInfos = databasehandler.getAllCommonInfo();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_common_info, menu);
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
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        if (id == R.id.action_qr_scanner) {
            Intent QrIntent = new Intent(this, QrScannerForCommonInfo.class);
            startActivity(QrIntent);
            return true;
        }


        if(id == R.id.action_search_common_info){
            Info.setIsSearchFab(true);
            Intent searchIntent = new Intent(getApplicationContext(), SearchActivityForCommonInfo.class);
            startActivity(searchIntent);
        }

        return super.onOptionsItemSelected(item);
    }
}
