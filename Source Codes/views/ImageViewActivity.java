package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Contact;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.ktharsanan.contactmangerversion1.R;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ResultProducer;
import com.tooltip.Tooltip;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 *
 */
public class ImageViewActivity extends AppCompatActivity{
    ImageView imageView;
    static Context context;
    Button processOCRBtn;
    Button scanAsBoardButton;
    String mCurrentPhotoPath;
    ArrayList<Spinner> spinnerList;
    ArrayList<EditText> edittextList;
    ArrayList<Integer> spinnerSelectedNums;
    static final int REQUEST_TAKE_PHOTO = 1;
    static final int REQUEST_CHOOSE_PHOTO_FROM_GALLERY = 2;
    ArrayAdapter<String> adapter;
    LinearLayout layout_inner_last;
    int targetW ;
    int targetH ;
    SeekBar seekbar;
    Uri file;
    Bitmap image; //our image
    Bitmap imageToProcess;
    ProgressBar progressBar;
    //private TessBaseAPI mTess; //Tess API reference
    static SparseArray<TextBlock> sparseArrayForExtractedItems = new SparseArray<>();
    //int progress;
    String datapath = ""; //path to folder containing language data file
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_view);
        imageView = findViewById(R.id.imageView);
        processOCRBtn = findViewById(R.id.button);
        scanAsBoardButton = findViewById(R.id.buttonScanAsBoard);
        context = this.getApplicationContext();
        spinnerSelectedNums = new ArrayList<>();
        targetW = 200;
        targetH = 200;
        datapath = getFilesDir()+ "/tesseract/";
        checkFile(new File(datapath + "tessdata/"));
        String lang = "eng";
        //mTess = new TessBaseAPI();
        //mTess.init(datapath, lang);
        image = BitmapFactory.decodeResource(getResources(), R.drawable.test_image);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        ShapeDrawable shapedrawable = new ShapeDrawable();
        shapedrawable.setShape(new RectShape());
        shapedrawable.getPaint().setColor(Color.BLACK);
        shapedrawable.getPaint().setStrokeWidth(2f);
        shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
        scanAsBoardButton.setBackgroundColor(Color.TRANSPARENT);
        scanAsBoardButton.setBackground(shapedrawable);
        processOCRBtn.setBackgroundColor(Color.TRANSPARENT);
        //progress = 100;
        processOCRBtn.setBackground(shapedrawable);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        final int[] rotateCount = {0};
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Matrix matrix = new Matrix();
                if(rotateCount[0] == 0){
                    matrix.postRotate(-90);
                    rotateCount[0] = 1;
                }
                else if(rotateCount[0] == 1){
                    matrix.postRotate(-180);
                    rotateCount[0] = 2;
                }
                else if(rotateCount[0] == 2){
                    matrix.postRotate(-270);
                    rotateCount[0] = 3;
                }
                else if(rotateCount[0] == 3){
                    matrix.postRotate(-360);
                    rotateCount[0] = 0;
                }

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(imageToProcess,imageToProcess.getWidth(),imageToProcess.getHeight(),true);

                Bitmap rotatedBitmap = Bitmap.createBitmap(scaledBitmap , 0, 0, scaledBitmap .getWidth(), scaledBitmap .getHeight(), matrix, true);
                imageToProcess = rotatedBitmap;
                imageView.setImageBitmap(rotatedBitmap);
            }
        });
        seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if(imageToProcess != null) {
                    imageView.setImageBitmap(changeBitmapContrastBrightness(imageToProcess, (float) progress / 100f, 1));
                    //imageToProcess = changeBitmapContrastBrightness(imageToProcess, (float) progress / 100f, 1);
                    //this.progress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        seekbar.setMax(200);
        seekbar.setProgress(100);
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    1);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    2);
        }

        if(Info.isIsCamFab()){
            Info.setIsCamFab(false);
            dispatchTakePictureIntent();
        }
        else if(Info.isIsGalleryFab()){
            Info.setIsGalleryFab(false);
            Intent GalleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(GalleryIntent, REQUEST_CHOOSE_PHOTO_FROM_GALLERY);
        }
        processOCRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                applyOcrAsCard();
                processOCRBtn.setVisibility(View.GONE);
                scanAsBoardButton.setVisibility(View.GONE);
                seekbar.setVisibility(View.GONE);
            }
        });
        scanAsBoardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //scanAsBoardMethod();
                applyOcrAsCustom();
                processOCRBtn.setVisibility(View.GONE);
                scanAsBoardButton.setVisibility(View.GONE);
                seekbar.setVisibility(View.GONE);
            }
        });
    }
    public void applyOcrAsCustom(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!recognizer.isOperational()){
                    //Toast.makeText(getApplicationContext(), "not processed", Toast.LENGTH_LONG).show();

                }
                else {
                    int progress = seekbar.getProgress();
                    imageToProcess = changeBitmapContrastBrightness(imageToProcess, (float) progress / 100f, 1);
                    Frame frame = new Frame.Builder().setBitmap(imageToProcess).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    sparseArrayForExtractedItems = items;
                }
                ImageViewActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        scanAsBoardMethodNewForAddingProgressBar();
                    }
                });

            }
        };
        thread.start();
        progressBar.setVisibility(View.VISIBLE);


    }


    public void applyOcrAsCard(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
                if(!recognizer.isOperational()){
                    //Toast.makeText(getApplicationContext(), "not processed", Toast.LENGTH_LONG).show();

                }
                else {
                    int progress = seekbar.getProgress();
                    imageToProcess = changeBitmapContrastBrightness(imageToProcess, (float) progress / 100f, 1);
                    Frame frame = new Frame.Builder().setBitmap(imageToProcess).build();
                    SparseArray<TextBlock> items = recognizer.detect(frame);
                    sparseArrayForExtractedItems = items;
                }
                ImageViewActivity.this.runOnUiThread(new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.GONE);
                        useGoogleVisionForAddProgressBar();
                    }
                });

            }
        };
        thread.start();
        progressBar.setVisibility(View.VISIBLE);


    }



    public void scanAsBoardMethod(){
        final ArrayList<CardViewWithTextViews> cardViewWithIncludingList = new ArrayList<>();
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!recognizer.isOperational()){
            Toast.makeText(getApplicationContext(), "not processed", Toast.LENGTH_LONG).show();
        }
        else {
            Frame frame = new Frame.Builder().setBitmap(imageToProcess).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            //StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> extractedText = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                String s = item.getValue();
                String[] subTextList = s.split("\n");
                for (int j = 0; j < subTextList.length; j++) {
                    extractedText.add(subTextList[j]);
                }
                //stringBuilder.append(item.getValue());
                //stringBuilder.append("\n");
            }
            //Toast.makeText(getApplicationContext(), stringBuilder, Toast.LENGTH_LONG).show();
            HashMap<String, String> hm = ResultProducer.processOnStrings(extractedText);
            imageView.setVisibility(View.GONE);
            processOCRBtn.setVisibility(View.GONE);
            scanAsBoardButton.setVisibility(View.GONE);
            LinearLayout layout = findViewById(R.id.linear_Layout);

            ScrollView scrollView = new ScrollView(this);
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            LinearLayout layout_inner = new LinearLayout(this);
            layout_inner.setOrientation(LinearLayout.VERTICAL);
            layout_inner.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            ArrayList<String> tagList = ResultProducer.getTagList();
            ArrayList<String> valueList = ResultProducer.getSubTextList();

            for(int k = 0; k<valueList.size(); k++) {
                CardView cardView = new CardView(this);
                cardView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                cardView.setMinimumHeight(100);

                LinearLayout layout_inner_card = new LinearLayout(this);
                layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
                layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                EditText editTextLeft = new EditText(this);
                editTextLeft.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                editTextLeft.setText(tagList.get(k));
                editTextLeft.setMinimumWidth(150);
                editTextLeft.setMaxWidth(200);
                EditText editTextRight = new EditText(this);
                editTextRight.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                editTextRight.setText(valueList.get(k));

                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {

                                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which){
                                            case DialogInterface.BUTTON_POSITIVE:
                                                for(CardViewWithTextViews cardViewWithTextViews : cardViewWithIncludingList){
                                                    if(cardViewWithTextViews.cardView == view){
                                                        final int index = cardViewWithIncludingList.indexOf(cardViewWithTextViews);
                                                        if(index==cardViewWithIncludingList.size()-1){

                                                        }
                                                        else{
                                                            mergeTextBelow(cardViewWithIncludingList.get(index),cardViewWithIncludingList.get(index+1),cardViewWithIncludingList);

                                                            break;

                                                        }
                                                    }
                                                }
                                                break;

                                            case DialogInterface.BUTTON_NEGATIVE:
                                                break;
                                        }
                                    }
                                };

                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setMessage("Merge next text with this text? ?").setPositiveButton("Yes", dialogClickListener)
                                        .setNegativeButton("No", dialogClickListener).show();


                        return false;
                    }
                });
                cardViewWithIncludingList.add(new CardViewWithTextViews(cardView,editTextLeft,editTextRight));
                layout_inner_card.addView(editTextLeft);
                layout_inner_card.addView(editTextRight);
                cardView.addView(layout_inner_card);
                layout_inner.addView(cardView);

            }
            Button addToDatabaseBtn = new Button(this);
            addToDatabaseBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            addToDatabaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> tempTagList = new ArrayList<>();
                    ArrayList<String> tempValueList = new ArrayList<>();
                    for(CardViewWithTextViews cardViewWithTextViews: cardViewWithIncludingList){
                        if(cardViewWithTextViews.editTextRight.getText().toString().trim().length()==0){
                            continue;
                        }
                        tempTagList.add(cardViewWithTextViews.editTextLeft.getText().toString());
                        tempValueList.add(cardViewWithTextViews.editTextRight.getText().toString());
                    }
                    Databasehandler.getdatabaseHandler(view.getContext()).addCommonInfo(Databasehandler.prepareCommonInfoValue(tempTagList,tempValueList),view.getContext());
                }
            });
            addToDatabaseBtn.setText("AddThis");
            layout_inner.addView(addToDatabaseBtn);
            scrollView.addView(layout_inner);
            layout.addView(scrollView);
        }
        ResultProducer.clearAll();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public void scanAsBoardMethodNewForAddingProgressBar(){
        final ArrayList<CardViewWithTextViews> cardViewWithIncludingList = new ArrayList<>();
        //TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(sparseArrayForExtractedItems.size() == 0){
            Toast.makeText(getApplicationContext(), "not processed", Toast.LENGTH_LONG).show();
        }
        else {

            SparseArray<TextBlock> items = sparseArrayForExtractedItems;
            //StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> extractedText = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                TextBlock item = items.valueAt(i);
                String s = item.getValue();
                String[] subTextList = s.split("\n");
                for (int j = 0; j < subTextList.length; j++) {
                    extractedText.add(subTextList[j]);
                }
                //stringBuilder.append(item.getValue());
                //stringBuilder.append("\n");
            }
            //Toast.makeText(getApplicationContext(), stringBuilder, Toast.LENGTH_LONG).show();
            HashMap<String, String> hm = ResultProducer.processOnStrings(extractedText);
            imageView.setVisibility(View.GONE);
            processOCRBtn.setVisibility(View.GONE);
            scanAsBoardButton.setVisibility(View.GONE);
            LinearLayout layout = findViewById(R.id.linear_Layout);

            ScrollView scrollView = new ScrollView(this);
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            LinearLayout layout_inner = new LinearLayout(this);
            layout_inner.setOrientation(LinearLayout.VERTICAL);
            layout_inner.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            ArrayList<String> tagList = ResultProducer.getTagList();
            ArrayList<String> valueList = ResultProducer.getSubTextList();

            for(int k = 0; k<valueList.size(); k++) {
                CardView cardView = new CardView(this);
                cardView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                cardView.setMinimumHeight(100);

                LinearLayout layout_inner_card = new LinearLayout(this);
                layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
                layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                EditText editTextLeft = new EditText(this);
                editTextLeft.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                editTextLeft.setText(tagList.get(k));
                editTextLeft.setMinimumWidth(150);
                editTextLeft.setMaxWidth(200);
                EditText editTextRight = new EditText(this);
                editTextRight.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                editTextRight.setText(valueList.get(k));

                cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case DialogInterface.BUTTON_POSITIVE:
                                        for(CardViewWithTextViews cardViewWithTextViews : cardViewWithIncludingList){
                                            if(cardViewWithTextViews.cardView == view){
                                                final int index = cardViewWithIncludingList.indexOf(cardViewWithTextViews);
                                                if(index==cardViewWithIncludingList.size()-1){

                                                }
                                                else{
                                                    mergeTextBelow(cardViewWithIncludingList.get(index),cardViewWithIncludingList.get(index+1),cardViewWithIncludingList);

                                                    break;

                                                }
                                            }
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setMessage("Merge next text with this text? ?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();


                        return false;
                    }
                });
                cardViewWithIncludingList.add(new CardViewWithTextViews(cardView,editTextLeft,editTextRight));
                layout_inner_card.addView(editTextLeft);
                layout_inner_card.addView(editTextRight);
                cardView.addView(layout_inner_card);
                layout_inner.addView(cardView);

            }
            Button addToDatabaseBtn = new Button(this);
            addToDatabaseBtn.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            addToDatabaseBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ArrayList<String> tempTagList = new ArrayList<>();
                    ArrayList<String> tempValueList = new ArrayList<>();
                    for(CardViewWithTextViews cardViewWithTextViews: cardViewWithIncludingList){
                        if(cardViewWithTextViews.editTextRight.getText().toString().trim().length()==0){
                            continue;
                        }
                        tempTagList.add(cardViewWithTextViews.editTextLeft.getText().toString());
                        tempValueList.add(cardViewWithTextViews.editTextRight.getText().toString());
                    }
                    Databasehandler.getdatabaseHandler(view.getContext()).addCommonInfo(Databasehandler.prepareCommonInfoValue(tempTagList,tempValueList),view.getContext());
                    Intent intent = new Intent(view.getContext(), CommonInfoActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    finish();
                }
            });
            addToDatabaseBtn.setText("AddThis");
            addToDatabaseBtn.setBackgroundColor(Color.TRANSPARENT);
            ShapeDrawable shapedrawable = new ShapeDrawable();
            shapedrawable.setShape(new RectShape());
            shapedrawable.getPaint().setColor(Color.BLACK);
            shapedrawable.getPaint().setStrokeWidth(2f);
            shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
            addToDatabaseBtn.setBackground(shapedrawable);
            layout_inner.addView(addToDatabaseBtn);
            scrollView.addView(layout_inner);
            layout.addView(scrollView);
        }
        ResultProducer.clearAll();
    }

















    public void mergeTextBelow(final CardViewWithTextViews cardViewWithTextViewsAbove, final CardViewWithTextViews cardViewWithTextViewsBelow, final ArrayList<CardViewWithTextViews> cardViewWithTextViewsArrayList){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String toDisplay = cardViewWithTextViewsAbove.editTextRight.getText().toString()+" "+cardViewWithTextViewsBelow.editTextRight.getText().toString();
                cardViewWithTextViewsBelow.cardView.setVisibility(View.GONE);
                try {
                    cardViewWithTextViewsAbove.editTextRight.setText(toDisplay);
                    cardViewWithTextViewsArrayList.remove(cardViewWithTextViewsBelow);
                }
                catch (Exception e){
                    System.out.print("");
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void dispatchTakePictureIntent() {
        //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //startActivityForResult(intent, REQUEST_TAKE_PHOTO);

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                try {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "com.ktharsanan.contactmangerversion1.fileprovider",
                            photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
                catch ( Exception ex){
                    Toast.makeText(getApplicationContext(), ex.toString(), Toast.LENGTH_LONG).show();
                }

            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void useGoogleVision(){
        TextRecognizer recognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if(!recognizer.isOperational()){
            Toast.makeText(getApplicationContext(), "not processed", Toast.LENGTH_LONG).show();
        }
        else{
            Frame frame = new Frame.Builder().setBitmap(imageToProcess).build();
            SparseArray<TextBlock> items = recognizer.detect(frame);
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> extractedText = new ArrayList<>();
            for(int i=0; i<items.size(); i++){
                TextBlock item = items.valueAt(i);
                String s = item.getValue();
                String[] subTextList = s.split("\n");
                for(int j = 0; j<subTextList.length; j++){
                    extractedText.add(subTextList[j]);
                }
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            //Toast.makeText(getApplicationContext(), stringBuilder, Toast.LENGTH_LONG).show();
            HashMap<String, String> hm = ResultProducer.processOnStrings(extractedText);
            imageView.setVisibility(View.GONE);
            processOCRBtn.setVisibility(View.GONE);
            scanAsBoardButton.setVisibility(View.GONE);
            LinearLayout layout = findViewById(R.id.linear_Layout);
            ScrollView scrollView = new ScrollView(this);
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            LinearLayout layout_inner = new LinearLayout(this);
            layout_inner.setOrientation(LinearLayout.VERTICAL);
            layout_inner.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            List<String> spinnerArray =  new ArrayList<>();
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_NAME);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_PHONE_NUM);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_MAIL);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_NADDRESS);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_WEB);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_COMPANY);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_FAX);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_HOME_NUM);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_WORK_NUM);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_OTHER);


            adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerList = new ArrayList<>();
            edittextList = new ArrayList<>();
            ArrayList<String> subTextList = ResultProducer.getSubTextList();
            ArrayList<String> tagList = ResultProducer.getTagList();
            Boolean nameSet = false;
            Boolean comapanyNameSet = false;
                for(int k = 0; k<subTextList.size(); k++){
                    CardView cardView = new CardView(this);
                    cardView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    ));
                    cardView.setMinimumHeight(100);

                    LinearLayout layout_inner_card = new LinearLayout(this);
                    layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
                    layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                    ));
                    final Spinner spinner = new Spinner(this);
                    spinner.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    EditText textView = new EditText(this);
                    textView.setLayoutParams(new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    ));
                    textView.setText(subTextList.get(k));

                    spinner.setAdapter(adapter);
                    spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            checkSelectedIsValid(i,spinner);

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    String tag = tagList.get(k);
                    if(tag.equals("name")){
                        if(nameSet){
                            spinner.setSelection(ConstantClass.ShowExtracted.COMPANY);
                            if(comapanyNameSet){
                                spinner.setSelection(ConstantClass.ShowExtracted.OTHER);
                            }
                            comapanyNameSet = true;
                        }
                        else{
                            spinner.setSelection(ConstantClass.ShowExtracted.NAME);
                            nameSet = true;
                        }
                    }
                    else if(tag.equals("address")){
                        spinner.setSelection(ConstantClass.ShowExtracted.ADDRESS);
                    }
                    else if(tag.equals("mail")){
                        spinner.setSelection(ConstantClass.ShowExtracted.MAIL);
                    }
                    else if(tag.equals("web")){
                        spinner.setSelection(ConstantClass.ShowExtracted.WEB);
                    }
                    else if(tag.equals("phoneNum")){
                        spinner.setSelection(ConstantClass.ShowExtracted.PHONENUM);
                    }
                    else if(tag.equals("other")){
                        spinner.setSelection(ConstantClass.ShowExtracted.OTHER);
                    }
                    //cardView.setCardBackgroundColor(Color.LTGRAY);
                    layout_inner_card.addView(spinner);
                    layout_inner_card.addView(textView);
                    cardView.addView(layout_inner_card);
                    layout_inner.addView(cardView);
                    cardView.setBottom(20);
                    //cardView.setUseCompatPadding(true);
                    cardView.setBackgroundColor(Color.TRANSPARENT);
                    cardView.setMinimumWidth(100);
                    spinnerList.add(spinner);
                    edittextList.add(textView);
                }
            //}
            layout_inner_last = new LinearLayout(this);
            layout_inner_last.setOrientation(LinearLayout.VERTICAL);
            layout_inner_last.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            layout_inner.addView(layout_inner_last);
            final Button addNewDetail = new Button(this);
            addNewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewDetail();
                }
            });
            addNewDetail.setBackgroundColor(Color.TRANSPARENT);
            addNewDetail.setText("AddNewDetail");
            layout_inner.addView(addNewDetail);
            final Button addToContact = new Button(this);
            addToContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToContact();
                    addToContact.setVisibility(View.GONE);
                }
            });
            addToContact.setBackgroundColor(Color.TRANSPARENT);
            addToContact.setText("AddToPhone");
            layout_inner.addView(addToContact);
            final Button addToDatabase = new Button(this);
            addToDatabase.setBackgroundColor(Color.TRANSPARENT);
            addToDatabase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeOnDatabase();
                    //Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
                    //startActivity(mainIntent);
                }
            });
            addToDatabase.setText("AddToContact");
            layout_inner.addView(addToDatabase);
            scrollView.addView(layout_inner);
            layout.addView(scrollView);
            ResultProducer.clearAll();
            ShapeDrawable shapedrawable = new ShapeDrawable();
            shapedrawable.setShape(new RectShape());
            shapedrawable.getPaint().setColor(Color.BLACK);
            shapedrawable.getPaint().setStrokeWidth(2f);
            shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
            addNewDetail.setBackground(shapedrawable);
            addToContact.setBackground(shapedrawable);
            addToDatabase.setBackground(shapedrawable);
            addNewDetail.setPadding(5,5,5,5);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void useGoogleVisionForAddProgressBar(){
        if(sparseArrayForExtractedItems.size() == 0){
            Toast.makeText(getApplicationContext(), "not processed", Toast.LENGTH_LONG).show();
        }
        else{
            SparseArray<TextBlock> items = sparseArrayForExtractedItems;
            StringBuilder stringBuilder = new StringBuilder();
            ArrayList<String> extractedText = new ArrayList<>();
            for(int i=0; i<items.size(); i++){
                TextBlock item = items.valueAt(i);
                String s = item.getValue();
                String[] subTextList = s.split("\n");
                for(int j = 0; j<subTextList.length; j++){
                    extractedText.add(subTextList[j]);
                }
                stringBuilder.append(item.getValue());
                stringBuilder.append("\n");
            }
            //Toast.makeText(getApplicationContext(), stringBuilder, Toast.LENGTH_LONG).show();
            HashMap<String, String> hm = ResultProducer.processOnStrings(extractedText);
            imageView.setVisibility(View.GONE);
            processOCRBtn.setVisibility(View.GONE);
            scanAsBoardButton.setVisibility(View.GONE);
            LinearLayout layout = findViewById(R.id.linear_Layout);
            ScrollView scrollView = new ScrollView(this);
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            LinearLayout layout_inner = new LinearLayout(this);
            layout_inner.setOrientation(LinearLayout.VERTICAL);
            layout_inner.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            List<String> spinnerArray =  new ArrayList<>();
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_NAME);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_PHONE_NUM);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_MAIL);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_NADDRESS);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_WEB);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_COMPANY);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_FAX);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_HOME_NUM);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_WORK_NUM);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_OTHER);
            spinnerArray.add(ConstantClass.ShowExtracted.SPINNER_OCCUPATION);


            adapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_item, spinnerArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerList = new ArrayList<>();
            edittextList = new ArrayList<>();
            ArrayList<String> subTextList = ResultProducer.getSubTextList();
            ArrayList<String> tagList = ResultProducer.getTagList();
            Boolean nameSet = false;
            Boolean comapanyNameSet = false;
            for(int k = 0; k<subTextList.size(); k++){

                CardView cardView = new CardView(this);
                cardView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                cardView.setMinimumHeight(100);

                LinearLayout layout_inner_card = new LinearLayout(this);
                layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
                layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                ));
                final Spinner spinner = new Spinner(this);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                EditText textView = new EditText(this);
                if(subTextList.get(k).matches("[0-9]+")){
                    if(subTextList.get(k).indexOf("94") == 0){
                        if(subTextList.get(k).length() != 11){
                            Tooltip tooltipPlus = new Tooltip.Builder(textView)
                                    .setText("check this number").setGravity(Gravity.TOP).setTextColor(Color.BLACK).setDismissOnClick(true).setBackgroundColor(getResources().getColor(R.color.color_background_tooltip))
                                    .show();
                            Toast.makeText(ImageViewActivity.this,"Check whether phone numbers are correct!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                    else if(subTextList.get(k).indexOf("0") == 0){
                        if(subTextList.get(k).length() != 10){
                            Toast.makeText(ImageViewActivity.this,"Check whether phone numbers are correct!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        if(subTextList.get(k).length() != 9){
                            Toast.makeText(ImageViewActivity.this,"Check whether phone numbers are correct!!!",Toast.LENGTH_LONG).show();
                        }
                    }
                }
                textView.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                textView.setText(subTextList.get(k));

                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        checkSelectedIsValid(i,spinner);

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
                String tag = tagList.get(k);
                if(tag.equals("name")){
                    if(nameSet){
                        spinner.setSelection(ConstantClass.ShowExtracted.COMPANY);
                        if(comapanyNameSet){
                            spinner.setSelection(ConstantClass.ShowExtracted.OTHER);
                        }
                        comapanyNameSet = true;
                    }
                    else{
                        spinner.setSelection(ConstantClass.ShowExtracted.NAME);
                        nameSet = true;
                    }
                }
                else if(tag.equals("address")){
                    spinner.setSelection(ConstantClass.ShowExtracted.ADDRESS);
                }
                else if(tag.equals("mail")){
                    spinner.setSelection(ConstantClass.ShowExtracted.MAIL);
                }
                else if(tag.equals("web")){
                    spinner.setSelection(ConstantClass.ShowExtracted.WEB);
                }
                else if(tag.equals("phoneNum")){
                    spinner.setSelection(ConstantClass.ShowExtracted.PHONENUM);
                }
                else if(tag.equals("other")){
                    spinner.setSelection(ConstantClass.ShowExtracted.OTHER);
                }
                //cardView.setCardBackgroundColor(Color.LTGRAY);
                layout_inner_card.addView(spinner);
                layout_inner_card.addView(textView);
                cardView.addView(layout_inner_card);
                layout_inner.addView(cardView);
                cardView.setBottom(20);
                //cardView.setUseCompatPadding(true);
                cardView.setBackgroundColor(Color.TRANSPARENT);
                cardView.setMinimumWidth(100);
                spinner.setPopupBackgroundDrawable(getResources().getDrawable(R.drawable.background));
                spinnerList.add(spinner);
                edittextList.add(textView);
            }
            //}
            layout_inner_last = new LinearLayout(this);
            layout_inner_last.setOrientation(LinearLayout.VERTICAL);
            layout_inner_last.setLayoutParams(new ScrollView.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            ));
            layout_inner.addView(layout_inner_last);
            final Button addNewDetail = new Button(this);
            addNewDetail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addNewDetail();
                }
            });
            addNewDetail.setBackgroundColor(Color.TRANSPARENT);
            addNewDetail.setText("Add New Detail");
            layout_inner.addView(addNewDetail);
            final Button addToContact = new Button(this);
            addToContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addToContact();
                    addToContact.setVisibility(View.GONE);
                }
            });
            addToContact.setBackgroundColor(Color.TRANSPARENT);
            addToContact.setText("Add To Phone");
            layout_inner.addView(addToContact);
            final Button addToDatabase = new Button(this);
            addToDatabase.setBackgroundColor(Color.TRANSPARENT);
            addToDatabase.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    storeOnDatabase();
                    //Intent mainIntent = new Intent(view.getContext(), MainActivity.class);
                    //startActivity(mainIntent);
                }
            });
            addToDatabase.setText("Add To Contact");
            layout_inner.addView(addToDatabase);
            scrollView.addView(layout_inner);
            layout.addView(scrollView);
            ResultProducer.clearAll();
            ShapeDrawable shapedrawable = new ShapeDrawable();
            shapedrawable.setShape(new RectShape());
            shapedrawable.getPaint().setColor(Color.BLACK);
            shapedrawable.getPaint().setStrokeWidth(2f);
            shapedrawable.getPaint().setStyle(Paint.Style.STROKE);
            addNewDetail.setBackground(shapedrawable);
            addToContact.setBackground(shapedrawable);
            addToDatabase.setBackground(shapedrawable);
            addNewDetail.setPadding(5,5,5,5);

        }
    }












    private void storeOnDatabase(){
        Contact contact = new Contact();
        for(int i = 0; i<spinnerList.size(); i++) {
            String text = edittextList.get(i).getText().toString();
            if(text.length()<1){
                continue;
            }
            if (spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.NAME) {
                contact.setName(text);
            } else if (spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.MAIL) {
                contact.setMail(text);
            } else if (spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.COMPANY) {
                contact.setCompanyName(text);
            } else if (spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.PHONENUM) {
                contact.getNumbers().add(text+" Mobile");
            } else if (spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.FAX) {
                contact.getNumbers().add(text+" fax");
            } else if (spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.WEB) {
                contact.setWeb(text);
            } else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.OTHER){
                contact.getOthers().add(text);
            } else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.HOMENUM){
                contact.getNumbers().add(text+" Home");
            } else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.WORKNUM){
                contact.getNumbers().add(text+" Work");
            } else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.ADDRESS){
                contact.getAddress().add(text);
            }else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.OCCUPATION){
                contact.setOccupation(text);
            }
        }
        Databasehandler dh = Databasehandler.getdatabaseHandler(this);
        //Databasehandler dh = new Databasehandler(this);
        dh.addContact(contact);

    }

    private void addToContact(){
        ArrayList<ContentValues> data = new ArrayList<>();
        for(int i = 0; i<spinnerList.size(); i++){
            if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.NAME){
                ContentValues row5 = new ContentValues();
                row5.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
                row5.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM);
                row5.put(ContactsContract.CommonDataKinds.Email.LABEL, "Name");
                row5.put(ContactsContract.CommonDataKinds.Email.ADDRESS, edittextList.get(i).getText().toString());
                data.add(row5);
            }
            else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.MAIL){
                ContentValues row2 = new ContentValues();
                row2.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                row2.put(ContactsContract.CommonDataKinds.Email.TYPE, ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM);
                row2.put(ContactsContract.CommonDataKinds.Email.LABEL, "Mail");
                row2.put(ContactsContract.CommonDataKinds.Email.ADDRESS, edittextList.get(i).getText().toString());
                data.add(row2);
            }
            else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.COMPANY){
                ContentValues row1 = new ContentValues();
                row1.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE);
                row1.put(ContactsContract.CommonDataKinds.Organization.COMPANY, edittextList.get(i).getText().toString());
                data.add(row1);
            }
            else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.PHONENUM){
                ContentValues row3 = new ContentValues();
                row3.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                row3.put(ContactsContract.CommonDataKinds.Phone.LABEL, "Mobile");
                row3.put(ContactsContract.CommonDataKinds.Phone.NUMBER, edittextList.get(i).getText().toString());
                data.add(row3);
            }
            else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.FAX){
                ContentValues row3 = new ContentValues();
                row3.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                row3.put(ContactsContract.CommonDataKinds.Phone.LABEL, "Fax");
                row3.put(ContactsContract.CommonDataKinds.Phone.NUMBER, edittextList.get(i).getText().toString());
                data.add(row3);
            }
            else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.WEB){
                ContentValues row4 = new ContentValues();
                row4.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                row4.put(ContactsContract.CommonDataKinds.Website.URL, edittextList.get(i).getText().toString());
                data.add(row4);
            }
            else if(spinnerList.get(i).getSelectedItemPosition() == ConstantClass.ShowExtracted.WORKNUM){
                ContentValues row5 = new ContentValues();
                row5.put(ContactsContract.Contacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                row5.put(ContactsContract.CommonDataKinds.Phone.LABEL, "Work");
                row5.put(ContactsContract.CommonDataKinds.Phone.NUMBER, edittextList.get(i).getText().toString());
                data.add(row5);
            }

        }
        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);

        startActivity(intent);

    }
    private void addNewDetail(){
        CardView cardView = new CardView(this);
        cardView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        cardView.setMinimumHeight(100);

        LinearLayout layout_inner_card = new LinearLayout(this);
        layout_inner_card.setOrientation(LinearLayout.HORIZONTAL);
        layout_inner_card.setLayoutParams(new ScrollView.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        ));
        final Spinner spinner = new Spinner(this);
        spinner.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));
        EditText textView = new EditText(this);
        textView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        ));


        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                checkSelectedIsValid(i,spinner);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinner.setSelection(ConstantClass.ShowExtracted.OTHER);

        cardView.setCardBackgroundColor(Color.TRANSPARENT);
        layout_inner_card.addView(spinner);
        layout_inner_card.addView(textView);
        cardView.addView(layout_inner_card);
        layout_inner_last.addView(cardView);
        cardView.setBottom(20);
        spinnerList.add(spinner);
        edittextList.add(textView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            setPic();
            //processOCR();
        }
        else if(requestCode == REQUEST_CHOOSE_PHOTO_FROM_GALLERY && resultCode == RESULT_OK){
            handleGalleryResult(data);
        }
        else{
            finish();
        }
    }
    private Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; }

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }
    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        Bitmap ret = bmp;
        try {
            ColorMatrix cm = new ColorMatrix();
            float scale = contrast + 1.f;
            float translate = (-.5f * scale + .5f) * 255.f;
            cm.set(new float[]{
                    scale, 0, 0, 0, translate,
                    0, scale, 0, 0, translate,
                    0, 0, scale, 0, translate,
                    0, 0, 0, 1, 0});
            int width = bmp.getWidth();
            int height = bmp.getHeight();
            Bitmap.Config config= bmp.getConfig();
            ret = Bitmap.createBitmap(width, height,config );

            Canvas canvas = new Canvas(ret);

            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(cm));
            canvas.drawBitmap(bmp, 0, 0, paint);
        }
        catch (Exception e){
            System.out.println(e);
        }
        return ret;
    }
    private void handleGalleryResult(Intent data){
        Uri uri = data.getData();

        try {
            imageToProcess = toGrayscale(MediaStore.Images.Media.getBitmap(getContentResolver(), uri));
            imageView.setImageBitmap(imageToProcess);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private void setPic() {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();
        if(targetH == 0 || targetW == 0){
            targetW = 500;
            targetH = 600;
        }
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        BitmapFactory.Options bmOptions1 = new BitmapFactory.Options();
        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        bmOptions1.inJustDecodeBounds = false;
        bmOptions1.inSampleSize = 1;
        bmOptions1.inPurgeable = true;
        imageToProcess = toGrayscale(BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions1));
        image = changeBitmapContrastBrightness(toGrayscale(BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions)),(float)1.4,(float)1);
        imageView.setImageBitmap(image);
    }
    public Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
    public static Bitmap bitmapToBlackAndWhite(Bitmap src){
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());
        // color information
        int A, R, G, B;
        int pixel;
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                R = Color.red(pixel);
                G = Color.green(pixel);
                B = Color.blue(pixel);
                int gray = (int) (0.2989 * R + 0.5870 * G + 0.1140 * B);
                // use 128 as threshold, above -> white, below -> black
                if (gray > 150) {
                    gray = 255;
                }
                else{
                    gray = 0;
                }
                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, gray, gray, gray));
            }
        }
        return bmOut;
    }

    public void processOCR(){
        //String OCRresult = null;
        //mTess.setImage(image);
        //OCRresult = mTess.getUTF8Text();
        //Toast.makeText(getApplicationContext(), OCRresult, Toast.LENGTH_LONG).show();

    }
    private void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager
            AssetManager assetManager = getAssets();

            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "CameraDemo");

        if (!mediaStorageDir.exists()){
            if (!mediaStorageDir.mkdirs()){
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
    }

    private void checkSelectedIsValid(int selectedIndex ,Spinner view){
        if(selectedIndex == ConstantClass.ShowExtracted.NAME){
            if(indexSelected(selectedIndex, view)){
                for(Spinner s : spinnerList){
                    if((s.getSelectedItemPosition() == selectedIndex) && (view!=s)){
                        s.setSelection(ConstantClass.ShowExtracted.OTHER);
                    }
                }
            }
        }
        if(selectedIndex == ConstantClass.ShowExtracted.COMPANY){
            if(indexSelected(selectedIndex, view)){
                for(Spinner s : spinnerList){
                    if((s.getSelectedItemPosition() == selectedIndex) && (view!=s)){
                        s.setSelection(ConstantClass.ShowExtracted.OTHER);
                    }
                }
            }
        }
    }
    private boolean indexSelected(int i, Spinner view){
        for(Spinner s : spinnerList){
            if((s.getSelectedItemPosition() == i) && (view != s) ){
                return true;
            }

        }
        return false;
    }
    class CardViewWithTextViews {
        CardView cardView;
        EditText editTextLeft;
        EditText editTextRight;
        CardViewWithTextViews(CardView cardView, EditText editTextLeft, EditText editTextRight){
            this.cardView = cardView;
            this.editTextLeft = editTextLeft;
            this.editTextRight = editTextRight;
        }
    }
}
