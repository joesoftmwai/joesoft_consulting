package com.joesoft.joesoftconsulting.issues;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.google.android.material.tabs.TabLayout;
import com.joesoft.joesoftconsulting.R;


public class IssuesActivity extends AppCompatActivity {
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issues);
        mViewPager = findViewById(R.id.view_pager);
        mProgressBar = findViewById(R.id.progress_bar_vp);

        setUpViewPager();
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
}
