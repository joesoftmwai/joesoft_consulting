package com.joesoft.joesoftconsulting.issues;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Issue;
import com.joesoft.joesoftconsulting.models.Project;

import java.util.ArrayList;

public class IssuesFragment extends Fragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener, IssuesRecyclerViewAdapter.CustomClickListener {
    private static final String TAG = "IssuesFragment";
    private Spinner mProjectsSpinner;
    private RecyclerView mRecyclerIssues;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout mAppBarLayout;
    private ConstraintLayout mMainContent;

    private IssuesRecyclerViewAdapter mIssuesRecyclerAdapter;
    private ArrayList<Issue> mIssues = new ArrayList<>();
    private ArrayList<Project> mProjects = new ArrayList<>();
    private IIssues mIIssues;
    private Project mSelectedProject;

    private ActionMode mActionMode;
    private CoordinatorLayout.LayoutParams mLayoutParams;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_issues, container, false);
        ImageView addNewIssue = view.findViewById(R.id.add_new_issue);
        mProjectsSpinner = view.findViewById(R.id.spinner_projects);
        mRecyclerIssues = view.findViewById(R.id.rv_issues);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mAppBarLayout = view.findViewById(R.id.appbar);
        mMainContent = view.findViewById(R.id.main_content_container);

        // get layout params
        mLayoutParams = (CoordinatorLayout.LayoutParams) mMainContent.getLayoutParams();

        addNewIssue.setOnClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        getProjects();

        return view;
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
                initProjectsSpinner();
                initIssuesRecyclerView();
            }
        }
    }

    private void initProjectsSpinner() {
        String[] projects = new String[mProjects.size()];
        for (int i=0; i<mProjects.size(); i++) {
            projects[i] = mProjects.get(i).getName();
        }

        ArrayAdapter<String> projectsAdapter = new ArrayAdapter<String>(
                getActivity(), R.layout.support_simple_spinner_dropdown_item, projects);
        mProjectsSpinner.setAdapter(projectsAdapter);

        mProjectsSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> view, View view1, int i, long l) {
                mSelectedProject = mProjects.get(i);
                getIssues();
            }

            @Override
            public void onNothingSelected(AdapterView<?> view) {

            }
        });

        if(mProjects.size() > 0){
            mSelectedProject = mProjects.get(0);
        }
    }

    private void getIssues() {
        if (mSelectedProject != null) {
            mIIssues.showProgressBar();

            if (mIssues != null) {
                if (mIssues.size() > 0) {
                    mIssues.clear();
                }
            }

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection(getString(R.string.collection_projects))
                    .document(mSelectedProject.getProject_id())
                    .collection(getString(R.string.collection_issues))
                    .orderBy("priority", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                    Issue issue = documentSnapshot.toObject(Issue.class);
                                    mIssues.add(issue);
                                }
                            } else {
                                Log.d(TAG, "onComplete: error getting issues for project id "
                                        + mSelectedProject.getProject_id());
                                Toast.makeText(getActivity(), "Error getting issues for that project",
                                        Toast.LENGTH_SHORT).show();
                            }

                            mIIssues.hideProgressBar();
                            mIssuesRecyclerAdapter.notifyDataSetChanged();
                        }
                    });

        }
    }


    private void initIssuesRecyclerView() {
        int issueIcons[] = {R.drawable.ic_assignment_24dp, R.drawable.ic_bug_report_24dp};
        mRecyclerIssues.setLayoutManager(new LinearLayoutManager(getActivity()));
        mIssuesRecyclerAdapter = new IssuesRecyclerViewAdapter(mIssues, issueIcons, this);
        mRecyclerIssues.setAdapter(mIssuesRecyclerAdapter);
    }

    private void getProjects() {
        mIIssues.getProjects();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.add_new_issue) {
            Intent intent = new Intent(getActivity(), NewIssueActivity.class);
            startActivity(intent);
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
    public void onResume() {
        super.onResume();
        onRefresh();
    }

    @Override
    public void onRefresh() {
        getIssues();
        onItemsLoadComplete();
    }

    private void onItemsLoadComplete(){
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onItemLongClicked(int position) {
        Toast.makeText(getActivity(), "Selected item at position: " + position,
                Toast.LENGTH_SHORT).show();
        if (mActionMode != null) {
           return false;
        }

        mActionMode = getActivity().startActionMode(mActionModeCallback);

        return true;
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.selected_item_context_menu, menu);
            hideAppBarLayout();
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                //
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            showAppBarLayout();
        }
    };

    private void showAppBarLayout() {
        mLayoutParams.setBehavior(new AppBarLayout.ScrollingViewBehavior());
        mAppBarLayout.setVisibility(View.VISIBLE);
    }

    private void hideAppBarLayout() {
        mAppBarLayout.setVisibility(View.GONE);
        mLayoutParams.setBehavior(null);
        mMainContent.requestLayout();
    }

    private void toggleSelection(int position) {

    }


 }
