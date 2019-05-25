package com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.controllers.Databasehandler;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.ktharsanan.contactmangerversion1.R;
import com.tooltip.Tooltip;

import java.util.ArrayList;

/**
 * Created by kthar on 20/02/2018.
 */

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static ArrayList<DataObjectForCardView> mDataset;
    private static MyClickListener myClickListener;
    private static MyRecyclerViewAdapter myRecyclerViewAdapter;
    private static Context context;
    private static boolean longPressHint = false;
    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView name;
        TextView mail;
        TextView companyName;
        int id  = 0;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = itemView.findViewById(R.id.textView);
            mail = itemView.findViewById(R.id.textView2);
            companyName = itemView.findViewById(R.id.textView3);
            if(longPressHint && !Info.flagForDisplayToolTipOnCardViewContacts) {
                Tooltip tooltipCommonInfo = new Tooltip.Builder(mail).setDismissOnClick(true)
                        .setText(ConstantClass.ToolTip.TOOLTIP_FOR_FIRST_CARD).setGravity(Gravity.BOTTOM).setBackgroundColor(itemView.getResources().getColor(R.color.color_background_tooltip))
                        .show();
                longPressHint = false;
                Info.flagForDisplayToolTipOnCardViewContacts = true;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if(id!=0){
                        DisplayContact.setToBeDispalyedId(id);
                        Intent displayIntent = new Intent(view.getContext(), DisplayContact.class);
                        view.getContext().startActivity(displayIntent);
                    //}
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View view) {
                    int temp = 0;
                    String name = "";
                    for(int i = 0; i<mDataset.size(); i++ ){
                        if(mDataset.get(i).getId() == id){
                            temp = i;
                            name = mDataset.get(i).getmText1();
                        }
                    }
                    final int toBeDeleted = temp;
                    if(mDataset.get(toBeDeleted).getId() == 1){
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Intent qrGeneratorIntent = new Intent(view.getContext(), QRGenerator.class);
                                        QRGenerator.setContactId(mDataset.get(toBeDeleted).getId());
                                        view.getContext().startActivity(qrGeneratorIntent);
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setMessage("Share your details ").setPositiveButton("Share", dialogClickListener)
                                .setNegativeButton("Cancel", dialogClickListener).show();
                    }
                    else {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked

                                        myRecyclerViewAdapter.deleteItem(toBeDeleted);

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                    case DialogInterface.BUTTON_NEUTRAL:
                                        Intent qrGeneratorIntent = new Intent(view.getContext(), QRGenerator.class);
                                        QRGenerator.setContactId(mDataset.get(toBeDeleted).getId());
                                        view.getContext().startActivity(qrGeneratorIntent);
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setMessage("Are you sure you want to delete " + name + "?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).setNeutralButton("Share", dialogClickListener).show();
                    }
                    return true;
                }
            });
        }

        @Override
        public void onClick(View v) {

            //myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataObjectForCardView> myDataset) {
        mDataset = myDataset;
        myRecyclerViewAdapter = this;
        if(mDataset.size()==1){
            longPressHint = true;
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        holder.name.setText(mDataset.get(position).getmText1());
        holder.mail.setText(mDataset.get(position).getmText2());
        holder.companyName.setText(mDataset.get(position).getmText3());
        holder.id = mDataset.get(position).getId();
    }

    public void addItem(DataObjectForCardView dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(context);
        //Databasehandler databasehandler = new Databasehandler(context);
        boolean bool = databasehandler.deleteThisObject(mDataset.get(index));
        if(bool){
            mDataset.remove(index);
            notifyItemRemoved(index);
        }

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }
}