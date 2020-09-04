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

    public IssuesRecyclerViewAdapter(ArrayList<Issue> issues) {
        mIssues = issues;
    }

    @NonNull
    @Override
    public IssuesRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_issue, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IssuesRecyclerViewAdapter.ViewHolder holder, int position) {
        Issue issue = mIssues.get(position);
        holder.mTextIssueSummary.setText(issue.getSummary());
        holder.mTextIssueDateCreated.setText(issue.getTime_reported().toString());

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextIssueSummary, mTextIssueDateCreated;
        ImageView mImageIssueType, mImageIssuePriority;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextIssueSummary = itemView.findViewById(R.id.tv_li_issue_summary);
            mTextIssueDateCreated = itemView.findViewById(R.id.tv_li_issue_date_created);
            mImageIssuePriority = itemView.findViewById(R.id.iv_li_priority_image);
            mImageIssueType = itemView.findViewById(R.id.iv_li_issue_image);
            itemView.setOnClickListener(this);
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
    }
}
