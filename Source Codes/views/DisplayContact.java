package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.Manifest;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
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
import android.widget.Toast;

import com.ktharsanan.contactmangerversion1.R;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Contact;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.tooltip.Tooltip;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * this method provides basic functionality to display a specific contact. to display a contact toBeDisplayed static int
 * --variable need to be updated.
 */
public class DisplayContact extends AppCompatActivity {
    static int toBeDispalyedId;                                                                     // this specific id related contact will be displayed when this activity called
    static int toBeDeleted;                                                                         // this id need to be set before update contacts bec specific contact will be deleted and inserted again.
    int toBeDisplayedIdInstant;                                                                     // contact id which is going to be displayed
    boolean flagForEditTextTotextView;                                                              // flag for switch between editable and non editable mode
    EditText newDetail;
    LinearLayout layout;
    ScrollView scrollView;
    LinearLayout layout_inner;
    Button updateBtn;
    ArrayList<TextViewWithTagNameAndStringValue> textViewdetailList;                                // to maintain a list of views generated programatically
    ArrayList<TextView> textViewsAlterList;
    ArrayList<EditText> editTextViewListForAlteration;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Databasehandler dh = Databasehandler.getdatabaseHandler(this);
                //Databasehandler dh = new Databasehandler(this);
        textViewdetailList = new ArrayList<>();
        textViewsAlterList = new ArrayList<>();
        editTextViewListForAlteration = new ArrayList<>();
        flagForEditTextTotextView = false;
        Contact contact = dh.getContact(toBeDispalyedId);
        toBeDisplayedIdInstant = toBeDispalyedId;
        toBeDeleted = toBeDispalyedId;
        setContentView(R.layout.activity_display_contact);
        getSupportActionBar().setTitle("CONTACT");
        layout = findViewById(R.id.linear_Layout_out);
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
        final ArrayList<String> subTextList = new ArrayList<>();
        final ArrayList<String> tagList = new ArrayList<>();
        subTextList.add(contact.getName());
        tagList.add("Name");
        subTextList.add(contact.getCompanyName());
        tagList.add("CompanyName");
        subTextList.add(contact.getMail());
        tagList.add("E-Mail");
        int count = 0;
        for(int k = 0; k<contact.getNumbers().size(); k++){
            subTextList.add(contact.getNumbers().get(k).split(" ")[0]);
            tagList.add(contact.getNumbers().get(k).split(" ")[1]);
            count++;
        }
        final int index1 = count+2;
        subTextList.add(contact.getWeb());
        tagList.add("Web");
        subTextList.add(contact.getAddressFinal());
        tagList.add("Address");
        subTextList.add(contact.getOccupation());
        tagList.add("Occupation");
        for(int k = 0; k<contact.getOthers().size(); k++){
            subTextList.add(contact.getOthers().get(k));
            tagList.add("OtherDetails");
            count++;
        }
        final int index2 = count+5;


        final ArrayList<EditText> editTextList = new ArrayList<>();

        for(int k = 0; k<subTextList.size(); k++){
            CardView cardView = new CardView(this);
            cardView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            cardView.setMinimumHeight(50);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) cardView.getLayoutParams();
            params.setMargins(10,10,10,0);
            LinearLayout layout_inner_card = new LinearLayout(this);
            layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
            layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));

            TextView textview = new TextView(this);
            textview.setLayoutParams(new LinearLayout.LayoutParams(
                    150,
                    50
            ));
            EditText editView = new EditText(this);
            editView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textview.setText(tagList.get(k));
            textViewdetailList.add(new TextViewWithTagNameAndStringValue(textview,tagList.get(k),subTextList.get(k)));
            textview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for(TextViewWithTagNameAndStringValue object : textViewdetailList){
                        if(object.textView == view){
                            if(object.stringValue.trim().matches("[0-9]+") && object.stringValue.trim().length()>7){
                                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                    String toCall = chengeCallable(object.stringValue.trim());
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:"+toCall));
                                    startActivity(callIntent);
                                }
                            }
                            else if(object.tagName.equals("Web") || object.tagName.equals("Address") || object.tagName.equals("CompanyName")){
                                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                                intent.putExtra(SearchManager.QUERY, object.stringValue); // query contains search string
                                startActivity(intent);
                            }
                            else if(object.tagName.equals("E-Mail")){
                                Intent i = new Intent(Intent.ACTION_SEND);
                                i.setType("message/rfc822");
                                i.putExtra(Intent.EXTRA_EMAIL  , new String[]{object.stringValue});
                                i.putExtra(Intent.EXTRA_SUBJECT, "");
                                i.putExtra(Intent.EXTRA_TEXT   , "");
                                try {
                                    startActivity(Intent.createChooser(i, "Send mail..."));
                                } catch (android.content.ActivityNotFoundException ex) {
                                    Toast.makeText(view.getContext(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                            break;
                        }
                    }
                }
            });

            textview.setTypeface(null, Typeface.BOLD);
            editView.setText(subTextList.get(k));
            TextView textViewAlter = new TextView(this);
            textViewAlter.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            textViewAlter.setText(subTextList.get(k));
            editView.setVisibility(View.GONE);
            textViewAlter.setVisibility(View.VISIBLE);
            textViewAlter.setTypeface(null, Typeface.BOLD);
            textViewsAlterList.add(textViewAlter);
            cardView.setCardBackgroundColor(getResources().getColor(R.color.cardview_background));
            cardView.setCardElevation(0);
            cardView.setPreventCornerOverlap(true);
            cardView.setContentPadding(10,10,10,10);
            layout_inner_card.addView(textview);
            layout_inner_card.addView(editView);
            layout_inner_card.addView(textViewAlter);
            cardView.addView(layout_inner_card);
            layout_inner.addView(cardView);
            editTextList.add(editView);
            editTextViewListForAlteration.add(editView);

        }
        updateBtn = new Button(this);
        updateBtn.setText("Update");
        updateBtn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        updateBtn.setBackgroundColor(Color.TRANSPARENT);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /**String message = "Text I want to share.";
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);

                startActivity(Intent.createChooser(share, "Title of the dialog the system will open"));**/
                try {
                    Contact contact1 = new Contact();
                    contact1.setName(editTextList.get(0).getText().toString());
                    contact1.setCompanyName(editTextList.get(1).getText().toString());
                    contact1.setMail(editTextList.get(2).getText().toString());
                    for (int i = 3; i <= index1; i++) {
                        if(editTextList.get(i).getText().toString().trim().length()>4) {
                            contact1.addNumber(editTextList.get(i).getText().toString(), tagList.get(i));
                        }
                    }
                    contact1.setWeb(editTextList.get(index1 + 1).getText().toString());
                    contact1.setAddressFinal(editTextList.get(index1 + 2).getText().toString());
                    contact1.setOccupation(editTextList.get(index1 + 3).getText().toString());
                    for (int i = index1 + 4; i <= index2; i++) {
                        if(editTextList.get(i).getText().toString().trim().length()>0) {
                            contact1.addOthers(editTextList.get(i).getText().toString());
                        }
                    }
                    ArrayList<String> temp = checkStringForPhoneNumber(newDetail.getText().toString().trim());
                    if(temp.get(0).equals("true")){
                        contact1.addNumber(temp.get(1),temp.get(2));
                    }
                    else if(newDetail.getText().toString().trim().length()>0){
                        contact1.addOthers(newDetail.getText().toString());
                    }
                    Databasehandler databasehandler = Databasehandler.getdatabaseHandler(view.getContext());
                    //Databasehandler databasehandler = new Databasehandler(view.getContext());
                    boolean b = databasehandler.deleteContact(toBeDeleted);
                    if(b){
                        databasehandler.addContact(contact1);
                    }


                }
                catch(Exception e){
                    System.out.print("");
                }
            }
        });
        newDetail = new EditText(this);
        updateBtn.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));

        updateBtn.setVisibility(View.GONE);
        newDetail.setVisibility(View.GONE);
        layout_inner.addView(newDetail);
        layout_inner.addView(updateBtn);
        scrollView.addView(layout_inner);
        layout.addView(scrollView);
        if(Info.isDisplayContactFirstTime ) {
            Tooltip tooltipCommonInfo = new Tooltip.Builder(textViewdetailList.get(0).textView).setDismissOnClick(true)
                    .setText(ConstantClass.ToolTip.TOOLTIP_FOR_TAB_ON_TAGS).setGravity(Gravity.TOP).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip)).setTextColor(Color.BLACK)
                    .show();

        }

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if(Info.isDisplayContactFirstTime) {
                        sleep(1000);
                        new Tooltip.Builder(findViewById(R.id.action_edit)).setDismissOnClick(true)
                                .setText(ConstantClass.ToolTip.TOOLTIP_FOR_ACTION_EDIT).setGravity(Gravity.BOTTOM).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip)).setTextColor(Color.BLACK)
                                .show();
                        Info.isDisplayContactFirstTime = false;
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

    private String chengeCallable(String stringValue) {
        String toOut = stringValue;
        if(stringValue.indexOf("94") == 0){
            toOut = "0"+stringValue.substring(2);
        }
        return toOut;
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
                for(int k = 0; k < editTextViewListForAlteration.size(); k++){
                    editTextViewListForAlteration.get(k).setVisibility(View.GONE);
                    textViewsAlterList.get(k).setVisibility(View.VISIBLE);
                }
                updateBtn.setVisibility(View.GONE);
                newDetail.setVisibility(View.GONE);
                flagForEditTextTotextView = false;
            }
            else{
                for(int k = 0; k < editTextViewListForAlteration.size(); k++){
                    editTextViewListForAlteration.get(k).setVisibility(View.VISIBLE);
                    textViewsAlterList.get(k).setVisibility(View.GONE);
                }
                flagForEditTextTotextView = true;
                updateBtn.setVisibility(View.VISIBLE);
                newDetail.setVisibility(View.VISIBLE);
            }

        }


        return super.onOptionsItemSelected(item);
    }

    public static int getToBeDispalyedId() {
        return toBeDispalyedId;
    }

    public static void setToBeDispalyedId(int toBeDispalyedId) {
        DisplayContact.toBeDispalyedId = toBeDispalyedId;
    }
    private ArrayList<String> checkStringForPhoneNumber(String string){
        ArrayList<String> toOut = new ArrayList<>();
        String[] list = string.split(" ");
        for(String s : list){
            if(s.matches("[0-9]+") && s.length() > 7 && s.length()<12){
                if(list.length == 2){
                    if(Arrays.asList(list).indexOf(s) == 0){
                        toOut.add("true");
                        toOut.add(s);
                        toOut.add(list[1]);
                        return toOut;
                    }
                    else{
                        toOut.add("true");
                        toOut.add(s);
                        toOut.add(list[0]);
                        return toOut;
                    }
                }
                else{
                    toOut.add("true");
                    toOut.add(s);
                    toOut.add("phone_num");
                    return toOut;
                }
            }

        }
        toOut.add("false");
        return toOut;
    }
    class TextViewWithTagNameAndStringValue{
        public TextView textView;
        public String tagName;
        public String stringValue;
        public TextViewWithTagNameAndStringValue(TextView textView, String tagName, String stringValue){
            this.textView = textView;
            this.tagName = tagName;
            this.stringValue = stringValue;
        }

    }
}

