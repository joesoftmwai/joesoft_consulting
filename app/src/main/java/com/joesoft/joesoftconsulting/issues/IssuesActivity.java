package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Project;

import java.util.ArrayList;


public class IssuesActivity extends AppCompatActivity implements IIssues {
    private static final String TAG = "IssuesActivity";
    public static final int ISSUES_FRAGMENT = 0;
    public static final int PROJECTS_FRAGMENT = 1;

    // widgets
    private ViewPager mViewPager;
    private ProgressBar mProgressBar;

    // vars
    private IssuesFragment mIssuesFragment;
    private  ProjectsFragment mProjectsFragment;
    private IssuesPageAdapter mIssuesPageAdapter;
    private ArrayList<Project> mProjects = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        mViewPager = findViewById(R.id.view_pager);
        mProgressBar = findViewById(R.id.progress_bar_vp);

        setUpViewPager();

    }



    private void queryProjects() {
        showProgressBar();

        if (mProjects != null) {
            if (mProjects.size() > 0) {
                mProjects.clear();
            }
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(getString(R.string.collection_projects))
                .orderBy("time_created", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                Project project = documentSnapshot.toObject(Project.class);
                                mProjects.add(project);
                            }
                        } else {
                            Log.d(TAG, "onComplete: error retrieving projects " + task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "error retrieving projects ", Toast.LENGTH_SHORT).show();
                        }

                        hideProgressBar();
                        updateFragments();
                    }
                });
    }

    private void updateFragments() {
        mIssuesFragment.updateProjectsList(mProjects);
        mProjectsFragment.updateProjectsList(mProjects);
    }

    private void setUpViewPager() {
        mIssuesFragment = new IssuesFragment();
        mProjectsFragment = new ProjectsFragment();

        mIssuesPageAdapter = new IssuesPageAdapter(getSupportFragmentManager());
        mIssuesPageAdapter.addFragment(mIssuesFragment);
        mIssuesPageAdapter.addFragment(mProjectsFragment);

        mViewPager.setAdapter(mIssuesPageAdapter);

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(ISSUES_FRAGMENT).setText(getString(R.string.issues_fragment));
        tabLayout.getTabAt(PROJECTS_FRAGMENT).setText(getString(R.string.projects_fragment));
    }

    @Override
    public void showProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void getProjects() {
        queryProjects();
    }
}
