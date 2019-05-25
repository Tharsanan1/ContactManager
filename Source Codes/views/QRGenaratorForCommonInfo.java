package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.R;

public class QRGenaratorForCommonInfo extends AppCompatActivity {
    Bitmap bitmap ;
    String string;
    ImageView imageView;
    TextView textView;
    QRGenaratorForCommonInfo qrGenerator;
    static int generateId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrgenarator_for_common_info);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        textView = findViewById(R.id.textViewForDescription);
        imageView = findViewById(R.id.image);
        qrGenerator = this;
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(this);
        //Databasehandler databasehandler = new Databasehandler(this);
        string = databasehandler.getCommonInfoEncodedString(generateId);
        getSupportActionBar().setTitle("QR_CODE");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    bitmap = TextToImageEncode(string);

                    setImageView(bitmap);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    static void setCommonInfoId(int id){
        generateId = id;
    }
    void setImageView(final Bitmap bitmap){
        qrGenerator.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                imageView.setImageBitmap(bitmap);
                textView.setText("Scan this QR code");
            }
        });

    }
    Bitmap TextToImageEncode(String Value) throws WriterException {
        /**BitMatrix bitMatrix;
         try {
         bitMatrix = new MultiFormatWriter().encode(
         Value,
         BarcodeFormat.DATA_MATRIX.QR_CODE,
         QRcodeWidth, QRcodeWidth, null
         );

         } catch (IllegalArgumentException Illegalargumentexception) {

         return null;
         }
         int bitMatrixWidth = bitMatrix.getWidth();

         int bitMatrixHeight = bitMatrix.getHeight();

         int[] pixels = new int[bitMatrixWidth * bitMatrixHeight];

         for (int y = 0; y < bitMatrixHeight; y++) {
         int offset = y * bitMatrixWidth;

         for (int x = 0; x < bitMatrixWidth; x++) {

         pixels[offset + x] = bitMatrix.get(x, y) ?
         getResources().getColor(R.color.QRCodeBlackColor):getResources().getColor(R.color.QRCodeWhiteColor);
         }
         }
         Bitmap bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.ARGB_4444);

         bitmap.setPixels(pixels, 0, 500, 0, 0, bitMatrixWidth, bitMatrixHeight);
         return bitmap;**/

        String myStringToEncode = Value;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix bitMatrix = multiFormatWriter.encode(myStringToEncode, BarcodeFormat.QR_CODE,500,500);
        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
        Bitmap bitmpap = barcodeEncoder.createBitmap(bitMatrix);
        return bitmpap;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
