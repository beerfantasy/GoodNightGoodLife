package com.kmutt.cs.goodnightgoodlife;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class LogAdapter extends RecyclerView.Adapter<LogAdapter.LogViewHolder> {

    //this context we will use to inflate the layout
    private Context mCtx;

    //we are storing all the products in a list
    private List<Log> logList;

    //getting the context and product list with constructor
    public LogAdapter(Context mCtx, List<Log> productList) {
        this.mCtx = mCtx;
        this.logList = productList;
    }




    @Override
    public LogViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflating and returning our view holder
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        View view = inflater.inflate(R.layout.layout_list, null);
        return new LogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(LogViewHolder holder, int position) {
        //getting the product of the specified position
        Log log = logList.get(position);

        //binding the data with the viewholder views
        holder.textViewDate.setText(log.getDate());
        holder.textViewActivity.setText(log.getActivity());
        holder.textViewDuration.setText(log.getDuration()+"");

        holder.textViewAvgRelax.setText(log.getRelaxation()+ "%");
        if (log.getRelaxation() > 80f) holder.textViewAvgRelax.setTextColor(Color.rgb(88,243,175));
        else holder.textViewAvgRelax.setTextColor(Color.rgb(255,86,86));

        holder.textViewDeepSleep.setText(log.getDeepsleep() + " minutes");
        if (log.getDeepsleep() > 40f) holder.textViewDeepSleep.setTextColor(Color.rgb(88,243,175));
        else holder.textViewDeepSleep.setTextColor(Color.rgb(255,86,86));

    }


    @Override
    public int getItemCount() {
        return logList.size();
    }


    class LogViewHolder extends RecyclerView.ViewHolder {

        TextView textViewDate, textViewActivity, textViewDuration, textViewAvgRelax, textViewDeepSleep;

        public LogViewHolder(View itemView) {
            super(itemView);

            textViewDate = itemView.findViewById(R.id.date_list);
            textViewActivity = itemView.findViewById(R.id.activity_list);
            textViewDuration = itemView.findViewById(R.id.duration_list);
            textViewAvgRelax = itemView.findViewById(R.id.avg_relax_list_num);
            textViewDeepSleep = itemView.findViewById(R.id.deepsleep_list_num);
        }
    }

}
