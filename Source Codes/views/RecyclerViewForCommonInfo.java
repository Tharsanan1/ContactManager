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
import com.ktharsanan.contactmangerversion1.R;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.ConstantClass;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.DataObjectForCardView;
import com.ktharsanan.contactmangerversion1.com.ktharsanan.contactmanagerversion1.models.Info;
import com.tooltip.Tooltip;

import java.util.ArrayList;

/**
 * Created by kthar on 04/05/2018.
 */

public class RecyclerViewForCommonInfo extends RecyclerView
        .Adapter<RecyclerViewForCommonInfo
        .DataObjectHolder> {
    private static ArrayList<DataObjectForCardView> mDataset;
    private static MyClickListener myClickListener;
    private static RecyclerViewForCommonInfo recyclerViewForCommonInfo;
    private static Context context;
    private static boolean isOneCardInView;
    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView infoMain;
        TextView info2;
        TextView info3;
        int id  = 0;

        public DataObjectHolder(final View itemView) {
            super(itemView);
            context = itemView.getContext();
            infoMain = itemView.findViewById(R.id.textView);
            info2 = itemView.findViewById(R.id.textView2);
            info3 = itemView.findViewById(R.id.textView3);
            if(isOneCardInView && !Info.flagForDisplayToolTipOnCardViewCommonInfo) {
                Tooltip tooltipCommonInfo = new Tooltip.Builder(info2).setDismissOnClick(true)
                        .setText(ConstantClass.ToolTip.TOOLTIP_FOR_FIRST_CARD.toUpperCase()).setGravity(Gravity.BOTTOM).setBackgroundColor( itemView.getResources().getColor(R.color.color_background_tooltip))
                        .show();
                isOneCardInView = false;
                Info.flagForDisplayToolTipOnCardViewCommonInfo = true;
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //if(id!=0){
                    Databasehandler databasehandler = Databasehandler.getdatabaseHandler(view.getContext());
                    //Databasehandler databasehandler = new Databasehandler(view.getContext());
                    DisplayCommonInfo.setCommonInfo(databasehandler.getCommonInfo(id));
                    Intent displayIntent = new Intent(view.getContext(), DisplayCommonInfo.class);
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

                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        //Yes button clicked

                                        recyclerViewForCommonInfo.deleteItem(toBeDeleted);

                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        //No button clicked
                                        break;
                                    case DialogInterface.BUTTON_NEUTRAL:
                                        Intent qrGeneratorIntent = new Intent(view.getContext(), QRGenaratorForCommonInfo.class);
                                        QRGenaratorForCommonInfo.setCommonInfoId(mDataset.get(toBeDeleted).getId());
                                        view.getContext().startActivity(qrGeneratorIntent);
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
                        builder.setMessage("Are you sure you want to delete " + name + "?").setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).setNeutralButton("Share", dialogClickListener).show();

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

    public RecyclerViewForCommonInfo(ArrayList<DataObjectForCardView> myDataset) {
        mDataset = myDataset;
        recyclerViewForCommonInfo = this;
        if(myDataset.size()==1){
            isOneCardInView = true;
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
        holder.infoMain.setText(mDataset.get(position).getmText1());
        holder.info2.setText(mDataset.get(position).getmText2());
        holder.info3.setText(mDataset.get(position).getmText3());
        holder.id = mDataset.get(position).getId();
    }

    public void addItem(DataObjectForCardView dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        Databasehandler databasehandler = Databasehandler.getdatabaseHandler(context);
        //Databasehandler databasehandler = new Databasehandler(context);
        boolean bool = databasehandler.deleteThisCommonInfoObject(mDataset.get(index).getId());
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