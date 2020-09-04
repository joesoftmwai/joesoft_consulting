package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.joesoft.joesoftconsulting.R;

public class NewIssueActivity extends AppCompatActivity {
    private TextInputEditText mtietAssignToProject, mtietSummary, mtietIssueDescription;
    private Spinner mSpinnerIssueType, mSpinnerPriority;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);
        mtietAssignToProject = findViewById(R.id.tiet_assign_to_project);
        mtietIssueDescription = findViewById(R.id.tiet_issue_description);
        mtietSummary = findViewById(R.id.tiet_summary);
        mSpinnerIssueType = findViewById(R.id.spinner_issue_type);
        mSpinnerPriority = findViewById(R.id.spinner_priority);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_issue_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_issue_save) {
            saveNewIssue();
            return true;
        } else if (id == R.id.action_issue_cancel) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewIssue() {
        if (!mtietAssignToProject.getText().toString().isEmpty() && !mtietSummary.getText().toString().isEmpty()
                && !mtietIssueDescription.getText().toString().isEmpty()) {

        } else {
            Toast.makeText(NewIssueActivity.this,
                    "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
