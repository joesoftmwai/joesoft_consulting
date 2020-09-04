package com.joesoft.joesoftconsulting.issues;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Parcelable;

import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Issue;

public class IssueDetailActivity extends AppCompatActivity {
    public static final String ISSUE
            = "com.joesoft.joesoftconsulting.issues.ISSUE";
    private Issue mIssue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        
        getSelectedIssue();

    }

    private void getSelectedIssue() {
        mIssue = getIntent().getParcelableExtra(ISSUE);
    }




}
