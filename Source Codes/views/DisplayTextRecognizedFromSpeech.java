package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.R;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ClassForSavingSerializableObject;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.NodeForSpeechRecognition;

import java.util.ArrayList;

public class DisplayTextRecognizedFromSpeech extends AppCompatActivity {
    static ArrayList<String> sentenceList;
    int count;
    TextView textViewForSentence;
    TextView textViewForTag;
    TextView textViewForValue;
    EditText editTextforSentence;
    EditText editTextforValue;
    EditText editTextforTag;
    Button nextBtn;
    boolean flagForSave;
    ArrayList<String> tagList;
    ArrayList<String> valueList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_text_recognized_from_speech);
        count = 0;
        textViewForSentence = findViewById(R.id.textViewForSentence);
        textViewForTag = findViewById(R.id.textViewForTag);
        textViewForValue = findViewById(R.id.textViewForValue);
        editTextforSentence = findViewById(R.id.editTextforSentence);
        editTextforTag = findViewById(R.id.editTextforTag);
        editTextforValue = findViewById(R.id.editTextforValue);
        nextBtn = findViewById(R.id.buttonForNext);
        tagList = new ArrayList<>();
        valueList = new ArrayList<>();
        flagForSave = false;
        setViewsOnAFlow();
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flagForSave || sentenceList.size() == 1) {
                    Databasehandler databasehandler = Databasehandler.getdatabaseHandler(view.getContext());
                    //Databasehandler databasehandler = new Databasehandler(view.getContext());
                    tagList.add(editTextforTag.getText().toString());
                    valueList.add(editTextforValue.getText().toString());
                    String commonInfo = databasehandler.prepareCommonInfoValue(tagList,valueList);
                    NodeForSpeechRecognition.trainTreeUsing(editTextforSentence.getText().toString().toUpperCase(), editTextforTag.getText().toString().toUpperCase(), editTextforValue.getText().toString().toUpperCase());
                    databasehandler.addCommonInfo(commonInfo,view.getContext());
                    ClassForSavingSerializableObject classForSavingSerializableObject = new ClassForSavingSerializableObject(NodeForSpeechRecognition.getGlobalNodeList());
                    classForSavingSerializableObject.saveToFile(view.getContext());
                    finish();
                }
                else {
                    NodeForSpeechRecognition.trainTreeUsing(editTextforSentence.getText().toString().toUpperCase(), editTextforTag.getText().toString().toUpperCase(), editTextforValue.getText().toString().toUpperCase());
                    tagList.add(editTextforTag.getText().toString());
                    valueList.add(editTextforValue.getText().toString());
                    count++;

                    if (sentenceList.size() > count) {
                        if (count == sentenceList.size() - 1) {
                            nextBtn.setText("Save");
                            flagForSave = true;
                            setViewsOnAFlow();
                        }
                        else{
                            setViewsOnAFlow();
                        }
                    } else {
                        setViewsOnAFlow();
                    }
                }
            }
        });
    }

    public void setViewsOnAFlow(){
        editTextforSentence.setText(sentenceList.get(count));
        String[] array = NodeForSpeechRecognition.extractTagAndValue(sentenceList.get(count)).split(ConstantClass.DatabaseHandler.KEY_TAG_DEVIDER);
        if(array.length == 2){
            editTextforTag.setText(array[0]);
            editTextforValue.setText(array[1]);
        }
    }

    public static void setSentenceList(ArrayList<String> sentenceList) {
        ArrayList<String> temList = new ArrayList<>();
        for(String s :sentenceList){
            if(s.trim().length() != 0){
                temList.add(s);
            }
        }
        DisplayTextRecognizedFromSpeech.sentenceList = new ArrayList<>(temList);
    }
}
