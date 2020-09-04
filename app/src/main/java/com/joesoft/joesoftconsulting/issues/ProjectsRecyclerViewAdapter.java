package com.joesoft.joesoftconsulting.issues;

import android.annotation.SuppressLint;
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
import com.joesoft.joesoftconsulting.models.Project;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ProjectsRecyclerViewAdapter extends RecyclerView.Adapter<ProjectsRecyclerViewAdapter.ViewHolder> {
    private static final String TAG = "ProjectsRecyclerViewAdapter";
    private final ArrayList<Project> mProjects;

    public ProjectsRecyclerViewAdapter(ArrayList<Project> projects) {
        mProjects = projects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_project, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Project project = mProjects.get(position);
        holder.mTextProjectName.setText(project.getName());
        holder.mTextDateCreated.setText(project.getTime_created().toString());

        Picasso.get().load(project.getAvatar())
                .fit().centerCrop()
                .into(holder.mImageProject);

    }

    @Override
    public int getItemCount() {
        return mProjects.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView mTextProjectName, mTextDateCreated;
        ImageView mImageProject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextProjectName = itemView.findViewById(R.id.tv_li_project_name);
            mTextDateCreated = itemView.findViewById(R.id.tv_li_date_created);
            mImageProject = itemView.findViewById(R.id.iv_li_project);
            itemView.setOnClickListener(this);
        }

        @SuppressLint("LongLogTag")
        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Log.d(TAG, "onClick: selected project position: " + position);
            Project project = mProjects.get(position);
            Intent intent = new Intent(view.getContext(), ProjectDetailsActivity.class);
            intent.putExtra(ProjectDetailsActivity.PROJECT, project);
            view.getContext().startActivity(intent);
        }
    }
}

