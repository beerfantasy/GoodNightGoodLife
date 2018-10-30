package com.kmutt.cs.goodnightgoodlife;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ConcurrentSkipListSet;

public class ActivityInListAdapter extends RecyclerView.Adapter<ActivityInListAdapter.ActivityInListViewHolder> {

    private Context mCtx;
    private List<ActivityInList> activityList;


    public ActivityInListAdapter(Context mCtx, List<ActivityInList> activityList) {
        this.mCtx = mCtx;
        this.activityList = activityList;
    }

    @NonNull
    @Override
    public ActivityInListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
         View view = inflater.inflate(R.layout.layout_activity_list, null);

         return new ActivityInListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityInListViewHolder holder,final int position) {

        ActivityInList activityInList = activityList.get(position);

        holder.activity.setText(activityInList.getActivity());

        holder.parentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(mCtx, "Measure relaxation during " + activityList.get(position).getActivity() + ".", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(mCtx, MeasurementActivity.class);
                intent.putExtra("activity_cur", activityList.get(position).getActivity());
                mCtx.startActivity(intent);
            }
        });



    }

    @Override
    public int getItemCount() {
        return activityList.size();
    }

    public class ActivityInListViewHolder extends RecyclerView.ViewHolder {

        TextView activity;
        LinearLayout parentLayout;

        public ActivityInListViewHolder(View itemView) {

            super(itemView);

            activity = itemView.findViewById(R.id.activity_text);
            parentLayout = itemView.findViewById(R.id.parent_layout);

        }

    }

}
