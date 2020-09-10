package com.joesoft.joesoftconsulting.issues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Project;

import java.util.ArrayList;


public class ProjectsFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private RecyclerView mRecyclerProjects;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    private ProjectsRecyclerViewAdapter mProjectsRecyclerAdapter;
    private ArrayList<Project> mProjects = new ArrayList<>();
    private IIssues mIIssues;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_projects, container, false);
        ImageView addNewProject = view.findViewById(R.id.add_new_project);
        ImageView searchProjects = view.findViewById(R.id.search_projects);
        mRecyclerProjects = view.findViewById(R.id.rv_projects);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        addNewProject.setOnClickListener(this);
        searchProjects.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        initProjectsRecyclerView();

        return view;
    }

    private void getProjects() {
        mIIssues.getProjects();
    }

    public void updateProjectsList(ArrayList<Project> projects) {
        if (mProjects != null) {
            if (mProjects.size() > 0) {
                mProjects.clear();
            }
        }

        if (projects != null) {
            if (projects.size() > 0) {
                mProjects.addAll(projects);
                mProjectsRecyclerAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initProjectsRecyclerView() {
        mRecyclerProjects.setLayoutManager(new LinearLayoutManager(getActivity()));
        mProjectsRecyclerAdapter = new ProjectsRecyclerViewAdapter(mProjects);
        mRecyclerProjects.setAdapter(mProjectsRecyclerAdapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_project) {
            Intent intent = new Intent(getActivity(), NewProjectActivity.class);
            startActivity(intent);
        }
        if (view.getId() == R.id.search_projects) {
            // TODO: search implementation
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mIIssues = (IIssues) getActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onRefresh() {
        getProjects();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete() {
        mSwipeRefreshLayout.setRefreshing(false);
    }
}
