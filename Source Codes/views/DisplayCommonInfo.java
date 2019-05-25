package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.R;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.CommonInfo;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.tooltip.Tooltip;

import java.util.ArrayList;

/**
 * this class is createed to display commonInfo details and to update them.
 * to use this class we need to set commonInfo static member using setCommonInfo() sattic method and start this activity.3
 */
public class DisplayCommonInfo extends AppCompatActivity {
    static CommonInfo commonInfo;                                                                   // here we can set common info instance before display that info
    ScrollView scrollView;
    LinearLayout layout_inner;
    boolean flagForEditTextTotextView;                                                              // this flag is for switch vuew between editable and non editable.
    ArrayList<CardViewWithIncludings> cardViewWithIncludingsList;                                   // this arrayList will store all the card view that are generated from the program when display common info
    Button updateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_common_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cardViewWithIncludingsList = new ArrayList<>();
        flagForEditTextTotextView = false;                                                          // initially view cannot be edited
        displayInfo();
        getSupportActionBar().setTitle("Details");
        Thread thread = new Thread() {                                                              // tooltip need to be called by a thread and after 1 sec because to create tooltip over actions they need to be initialized. that is why we use this threead.
            @Override
            public void run() {
                try {
                    if(Info.isDisplayCommonInfoFirstTime) {                                         // display tooltip if it is the first time use of this application on this device
                        sleep(1000);
                        new Tooltip.Builder(findViewById(R.id.action_edit)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_ACTION_EDIT).setGravity(Gravity.BOTTOM).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                .setTextColor(Color.BLACK).show();
                        Info.isDisplayCommonInfoFirstTime = false;
                    }
                    else{

                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }
    public static void setCommonInfo(CommonInfo commonInfo){
        DisplayCommonInfo.commonInfo = commonInfo;
    }
    public void displayInfo(){
        LinearLayout linearLayoutOut = findViewById(R.id.linear_Layout_out);
        scrollView = new ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        layout_inner = new LinearLayout(this);
        layout_inner.setOrientation(LinearLayout.VERTICAL);
        layout_inner.setLayoutParams(new ScrollView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        for(int k = 0; k < commonInfo.tagList.size(); k++){                                                 // for each detail in a common info it will create a cardview containing that information programatically
            CardView cardView = new CardView(this);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            cardView.setMinimumHeight(50);

            LinearLayout layout_inner_card = new LinearLayout(this);
            layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
            layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));

            TextView textviewLeft = new TextView(this);
            textviewLeft.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    50
            ));

            EditText editViewLeft = new EditText(this);
            editViewLeft.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            TextView textviewRight = new TextView(this);
            textviewRight.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    50
            ));

            EditText editViewRight = new EditText(this);
            editViewRight.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardView.getLayoutParams();
            params.setMargins(10,10,10,0);
            textviewLeft.setMinWidth(150);
            editViewLeft.setMinimumWidth(150);
            textviewLeft.setMaxWidth(200);
            editViewLeft.setMaxWidth(150);
            textviewLeft.setPadding(10,10,10,10);
            textviewLeft.setText(commonInfo.tagList.get(k));
            textviewRight.setText(commonInfo.valueList.get(k));
            editViewLeft.setText(commonInfo.tagList.get(k));
            editViewRight.setText(commonInfo.valueList.get(k));
            textviewLeft.setTypeface(textviewLeft.getTypeface(), Typeface.BOLD);
            textviewRight.setTypeface(textviewRight.getTypeface(), Typeface.BOLD);
            editViewLeft.setVisibility(View.GONE);
            editViewRight.setVisibility(View.GONE);
            editViewRight.setMinimumWidth(450);
            CardViewWithIncludings cardViewWithIncludings = new CardViewWithIncludings(cardView,textviewLeft,textviewRight,editViewLeft,editViewRight);
            cardViewWithIncludingsList.add(cardViewWithIncludings);
            layout_inner_card.addView(textviewLeft);
            layout_inner_card.addView(editViewLeft);
            layout_inner_card.addView(textviewRight);
            layout_inner_card.addView(editViewRight);
            cardView.setBackgroundColor(getResources().getColor(R.color.cardview_background));
            cardView.setRadius((float) 0.6);
            cardView.addView(layout_inner_card);
            layout_inner.addView(cardView);

        }
        CardView cardView = new CardView(this);                                                   // new cardview for enter new details
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        cardView.setMinimumHeight(100);
        cardView.setPadding(10,0,10,0);
        LinearLayout layout_inner_card = new LinearLayout(this);
        layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
        layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));

        TextView textviewLeft = new TextView(this);
        textviewLeft.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50
        ));

        final EditText editViewLeft = new EditText(this);
        editViewLeft.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        TextView textviewRight = new TextView(this);
        textviewRight.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                50
        ));

        EditText editViewRight = new EditText(this);
        editViewRight.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        textviewLeft.setMinWidth(150);
        editViewLeft.setMinimumWidth(150);
        textviewLeft.setMaxWidth(150);
        editViewLeft.setMaxWidth(150);
        editViewRight.setMinimumWidth(450);
        textviewLeft.setTypeface(textviewLeft.getTypeface(), Typeface.BOLD);
        textviewRight.setTypeface(textviewRight.getTypeface(), Typeface.BOLD);
        layout_inner_card.addView(editViewLeft);
        layout_inner_card.addView(textviewLeft);
        layout_inner_card.addView(editViewRight);
        layout_inner_card.addView(textviewRight);
        cardView.addView(layout_inner_card);
        cardView.setBackgroundColor(Color.TRANSPARENT);
        editViewLeft.setVisibility(View.GONE);

        editViewLeft.setHint("NewTag");
        editViewRight.setHint("NewValue");
        editViewRight.setVisibility(View.GONE);
        CardViewWithIncludings cardViewWithIncludings = new CardViewWithIncludings(cardView,textviewLeft,textviewRight,editViewLeft,editViewRight);
        cardViewWithIncludingsList.add(cardViewWithIncludings);
        cardViewWithIncludings.cardView.setVisibility(View.GONE);
        updateBtn = new Button(this);
        updateBtn.setText("Update");
        updateBtn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        updateBtn.setVisibility(View.GONE);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> tagList = new ArrayList<>();
                ArrayList<String> valueList = new ArrayList<>();
                for(CardViewWithIncludings cardViewWithIncludings1 : cardViewWithIncludingsList){
                    if(cardViewWithIncludings1.editTextLeft.getText().toString().trim().length()>0 && cardViewWithIncludings1.editTextRight.getText().toString().trim().length()>0) {
                        tagList.add(cardViewWithIncludings1.editTextLeft.getText().toString());
                        valueList.add(cardViewWithIncludings1.editTextRight.getText().toString());
                    }
                    else{
                        continue;
                    }
                }
                String encodedString = Databasehandler.prepareCommonInfoValue(tagList,valueList);
                Databasehandler databasehandler = Databasehandler.getdatabaseHandler(view.getContext());
                //Databasehandler databasehandler = new Databasehandler(view.getContext());
                if(databasehandler.updateCommonInfo(commonInfo.id,encodedString)){
                    Intent commonInfoIntent = new Intent(view.getContext(),CommonInfoActivity.class);
                    commonInfoIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(commonInfoIntent);
                    finish();
                }
                else{

                }

            }
        });
        updateBtn.setBackgroundColor(Color.TRANSPARENT);
        layout_inner.addView(cardView);
        layout_inner.addView(updateBtn);
        scrollView.addView(layout_inner);
        linearLayoutOut.addView(scrollView);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_display_contact, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_edit) {
            if(flagForEditTextTotextView){
                for(int k = 0; k < cardViewWithIncludingsList.size(); k++){
                    cardViewWithIncludingsList.get(k).textViewRight.setVisibility(View.VISIBLE);
                    cardViewWithIncludingsList.get(k).textViewLeft.setVisibility(View.VISIBLE);
                    cardViewWithIncludingsList.get(k).editTextRight.setVisibility(View.GONE);
                    cardViewWithIncludingsList.get(k).editTextLeft.setVisibility(View.GONE);

                }
                if(cardViewWithIncludingsList.size()>1){
                    cardViewWithIncludingsList.get(cardViewWithIncludingsList.size()-1).cardView.setVisibility(View.GONE);
                }

                updateBtn.setVisibility(View.GONE);
                //newDetail.setVisibility(View.GONE);
                flagForEditTextTotextView = false;
            }
            else{
                for(int k = 0; k < cardViewWithIncludingsList.size(); k++){
                    cardViewWithIncludingsList.get(k).editTextLeft.setVisibility(View.VISIBLE);
                    cardViewWithIncludingsList.get(k).editTextRight.setVisibility(View.VISIBLE);
                    cardViewWithIncludingsList.get(k).textViewLeft.setVisibility(View.GONE);
                    cardViewWithIncludingsList.get(k).textViewRight.setVisibility(View.GONE);
                }
                if(cardViewWithIncludingsList.size()>1){
                    cardViewWithIncludingsList.get(cardViewWithIncludingsList.size()-1).cardView.setVisibility(View.VISIBLE);
                }
                flagForEditTextTotextView = true;
                updateBtn.setVisibility(View.VISIBLE);
                //newDetail.setVisibility(View.VISIBLE);
            }
            if(Info.isDisplayCommonInfoFirstTime){
                /**new Tooltip.Builder(updateBtn).setDismissOnClick(true)
                        .setText("Tab here to update").setGravity(Gravity.BOTTOM).setBackgroundColor(Color.MAGENTA).setDismissOnClick(true)
                        .show();
                Info.isDisplayCommonInfoFirstTime = false;**/
            }
        }
        if(item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public class CardViewWithIncludings{
        CardView cardView;
        TextView textViewLeft;
        TextView textViewRight;
        EditText editTextLeft;
        EditText editTextRight;
        CardViewWithIncludings(CardView cardView, TextView textViewLeft, TextView textViewRight, EditText editTextLeft, EditText editTextRight){
            this.cardView = cardView;
            this.editTextLeft = editTextLeft;
            this.editTextRight = editTextRight;
            this.textViewLeft = textViewLeft;
            this.textViewRight = textViewRight;
        }
    }
}
