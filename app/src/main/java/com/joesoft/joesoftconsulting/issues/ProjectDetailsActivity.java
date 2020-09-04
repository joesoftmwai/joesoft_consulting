package com.joesoft.joesoftconsulting.issues;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Project;

public class ProjectDetailsActivity extends AppCompatActivity {
    public static final String PROJECT
            = "com.joesoft.joesoftconsulting.issues.PROJECT";
    private Project mProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);

        getSelectedProject();

    }

    private void getSelectedProject() {
        mProject = getIntent().getParcelableExtra(PROJECT);
    }
}
