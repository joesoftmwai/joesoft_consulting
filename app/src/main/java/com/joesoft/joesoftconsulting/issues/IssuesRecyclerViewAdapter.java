package com.joesoft.joesoftconsulting.issues;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Issue;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class IssuesRecyclerViewAdapter extends RecyclerView.Adapter<IssuesRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "IssuesRecyclerAdapter";
    private final ArrayList<Issue> mIssues;
    private int[] mIcons;
    private final CustomClickListener mCustomClickListener;

    public IssuesRecyclerViewAdapter(ArrayList<Issue> issues, int[] icons, CustomClickListener customClickListener) {
        mIssues = issues;
        mIcons = icons;
        mCustomClickListener = customClickListener;
    }

    @NonNull
    @Override
    public IssuesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_issue, parent, false);
        return new ViewHolder(view, mCustomClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesRecyclerViewAdapter.ViewHolder holder, int position) {
        Issue issue = mIssues.get(position);
        holder.mTextIssueSummary.setText(issue.getSummary());
        holder.mTextIssueDateCreated.setText(issue.getTime_reported().toString());

        // set IssueTypeIcons
        int issueIcon;
        if (issue.getIssue_type().equals(Issue.TASK)) {
            issueIcon = mIcons[0];
        } else {
            issueIcon = mIcons[1];
        }

        Picasso.get()
                .load(issueIcon)
                .placeholder(issueIcon)
                .fit().centerCrop()
                .into(holder.mImageIssueType);

        // set PriorityLevelIcons
        switch (issue.getPriority()) {
            case 1: // low priority
                Picasso.get()
                        .load(R.drawable.ic_priority_low_24dp)
                        .placeholder(R.drawable.ic_priority_low_24dp)
                        .fit().centerCrop()
                        .into(holder.mImageIssuePriority);
                break;
            case 2: // medium priority
                Picasso.get()
                        .load(R.drawable.ic_priority_medium_24dp)
                        .placeholder(R.drawable.ic_priority_medium_24dp)
                        .fit().centerCrop()
                        .into(holder.mImageIssuePriority);
                break;
            case 3: // high priority
                Picasso.get()
                        .load(R.drawable.ic_priority_high_24dp)
                        .placeholder(R.drawable.ic_priority_high_24dp)
                        .fit().centerCrop()
                        .into(holder.mImageIssuePriority);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return mIssues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnLongClickListener {
        TextView mTextIssueSummary, mTextIssueDateCreated;
        ImageView mImageIssueType, mImageIssuePriority;
        // custom click listener to pass listeners to issues fragment
        CustomClickListener customListener;

        public ViewHolder(@NonNull View itemView, CustomClickListener customListener) {
            super(itemView);
            mTextIssueSummary = itemView.findViewById(R.id.tv_li_issue_summary);
            mTextIssueDateCreated = itemView.findViewById(R.id.tv_li_issue_date_created);
            mImageIssuePriority = itemView.findViewById(R.id.iv_li_priority_image);
            mImageIssueType = itemView.findViewById(R.id.iv_li_issue_image);
            this.customListener = customListener;

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d(TAG, "onClick: selected issue position: " + position);
            Issue issue = mIssues.get(position);
            Intent intent = new Intent(view.getContext(), IssueDetailActivity.class);
            intent.putExtra(IssueDetailActivity.ISSUE, issue);
            view.getContext().startActivity(intent);
        }

        @Override
        public boolean onLongClick(View view) {
            if (customListener != null) {
                return customListener.onItemLongClicked(getAdapterPosition());
            }
            return false;
        }
    }

    public interface CustomClickListener {
        boolean onItemLongClicked(int position);
    }
}
