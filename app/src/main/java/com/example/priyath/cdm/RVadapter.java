package com.example.priyath.cdm;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by priyath on 2/4/16.
 */
public class RVadapter extends RecyclerView.Adapter<RVadapter.DataUsage>{

    List<usage> usages;


    public RVadapter(List<usage> usages){
        this.usages=usages;

    }

    @Override
    public DataUsage onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        return new DataUsage(v);
    }

    @Override
    public void onBindViewHolder(DataUsage holder, int position) {


        holder.tview1.setText(usages.get(position).name);
        holder.tview2.setText(String.valueOf(usages.get(position).usage));
        holder.iview.setImageDrawable(usages.get(position).drawable);
        holder.tview3.setText(String.valueOf(usages.get(position).i));

    }

    @Override
    public int getItemCount() {
        return usages.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public static class DataUsage extends RecyclerView.ViewHolder {

         CardView cv;
         TextView tview1;
         TextView tview2;
         ImageView iview;
         TextView tview3;


        public DataUsage(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cardView);
            tview1 = (TextView)itemView.findViewById(R.id.appdataUsage);
            tview2 = (TextView)itemView.findViewById(R.id.appName);
            iview = (ImageView)itemView.findViewById((R.id.image));
            tview3 = (TextView)itemView.findViewById(R.id.appno);

        }


    }
}
