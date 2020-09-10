package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Issue;
import com.joesoft.joesoftconsulting.models.User;
import com.joesoft.joesoftconsulting.utility.SpinnerResource;

import java.util.ArrayList;

public class IssueDetailActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "IssueDetailActivity";
    public static final String ISSUE = "com.joesoft.joesoftconsulting.issues.ISSUE";
    private TextInputEditText mtietAssignToProject, mtietSummary, mtietDescription;
    private TextView tvAddAttachment;
    private Spinner mSpinnerIssueStatus, mSpinnerPriority, mSpinnerIssueType, mSpinnerAssignee;
    private Button mBtnSave;
    private ProgressBar mProgressBar;

    private Issue mIssue;
    private ArrayList<User> mUsers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_issue_detail);
        mtietAssignToProject = findViewById(R.id.tiet_id_assign_to_project);
        mtietSummary = findViewById(R.id.tiet_id_summary);
        mtietDescription = findViewById(R.id.tiet_id_description);
        tvAddAttachment = findViewById(R.id.tv_id_add_attachment);
        mSpinnerIssueStatus = findViewById(R.id.spinner_id_issue_status);
        mSpinnerPriority = findViewById(R.id.spinner_id_priority);
        mSpinnerIssueType = findViewById(R.id.spinner_id_issue_type);
        mSpinnerAssignee = findViewById(R.id.spinner_id_assignee);
        mBtnSave = findViewById(R.id.btn_id_save);
        mProgressBar = findViewById(R.id.progress_bar_id);

        tvAddAttachment.setOnClickListener(this);
        mBtnSave.setOnClickListener(this);

        
       if (getSelectedIssue()) {
           initIssueTypeSpinner();
           initPrioritySpinner();
           initIssueStatusSpinner();
           getEmployeeList();
           setIssueDetails();
       } else {
           Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
           finish();
       }

    }

    private void setIssueDetails() {
        mtietSummary.setText(mIssue.getSummary());
        mtietDescription.setText(mIssue.getDescription());
        getProjectName();
    }

    private void getProjectName() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection(getString(R.string.collection_projects))
                .document(mIssue.getProject_id())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            String projectName = (String) documentSnapshot.get("name");
                            mtietAssignToProject.setText(projectName);
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    "error getting project name", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onComplete: error getting project name.");
                        }
                    }
                });
    }

    private void getEmployeeList() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child(getString(R.string.node_users));

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                initAssigneeSpinner();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void initAssigneeSpinner() {
        String[] usernames = new String[mUsers.size() + 1];
        String[] userImages = new String[mUsers.size() + 1];
        for(int i = 0; i < mUsers.size(); i++){
            usernames[i] = mUsers.get(i).getName();
            userImages[i] = mUsers.get(i).getProfile_image();
        }
        // set the none / "" option to be available in the spinner
        userImages[mUsers.size()] = "";
        usernames[mUsers.size()] = "none";

//        final SpinnerAdapter adapter = new SpinnerAdapter(this, usernames, userImages);
//        final String[] names = usernames;
//        mSpinnerAssignee.setAdapter(adapter);
//        adapter.setSelectedText(mIssue.getAssignee());
//
//        mSpinnerAssignee.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                adapter.setSelectedText(names[i]);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });


        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, usernames);
        mSpinnerAssignee.setAdapter(adapter);
        setAssigneeSpinner();
    }

    private void setAssigneeSpinner() {
//        if(!mIssue.getAssignee().equals("")){
//            int position = ((SpinnerAdapter)mSpinnerAssignee.getAdapter())
//                    .getPosition(((SpinnerAdapter)mSpinnerAssignee.getAdapter()).getSelectedText());
//            mSpinnerAssignee.setSelection(position);
//        }
//        else{
//            mSpinnerAssignee.setSelection(0);
//        }


    }

    private void initIssueStatusSpinner() {
        final String[] issueStatus = SpinnerResource.issue_status_spinner;
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, issueStatus);
        mSpinnerIssueStatus.setAdapter(adapter);

        setIssueStatusSpinner();
    }

    private void setIssueStatusSpinner() {
        if(mIssue.getStatus().equals(Issue.IN_PROGRESS)){
            mSpinnerIssueStatus.setSelection(0);
        }
        else if(mIssue.getStatus().equals(Issue.DONE)){
            mSpinnerIssueStatus.setSelection(1);
        }
        else if(mIssue.getStatus().equals(Issue.IDLE)){
            mSpinnerIssueStatus.setSelection(2);
        }
        else{
            mSpinnerIssueStatus.setSelection(0);
        }
    }

    private void initPrioritySpinner() {
        final String[] priorityLevels = SpinnerResource.issue_priorities_spinner;
        int[] priorityImages = SpinnerResource.issue_priority_images_spinner;
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, priorityLevels, priorityImages);
        mSpinnerPriority.setAdapter(spinnerAdapter);

        mSpinnerPriority.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> view, View view1, int i, long l) {
                spinnerAdapter.setSelectedText(priorityLevels[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> view) {
                // TODO: nothing
            }
        });

        setPrioritySpinner();
    }

    private void setPrioritySpinner() {
        if(mIssue.getPriorityString().equals(Issue.HIGH)){
            mSpinnerPriority.setSelection(0);
        }
        else if(mIssue.getPriorityString().equals(Issue.MEDIUM)){
            mSpinnerPriority.setSelection(1);
        }
        else if(mIssue.getPriorityString().equals(Issue.LOW)){
            mSpinnerPriority.setSelection(2);
        }
        else{
            mSpinnerPriority.setSelection(0);
        }
    }

    private void initIssueTypeSpinner() {
        final String[] issueTypes = SpinnerResource.issue_types_spinner;
        int[] issueImages = SpinnerResource.issue_type_images_spinner;
        final SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, issueTypes, issueImages);
        mSpinnerIssueType.setAdapter(spinnerAdapter);

        mSpinnerIssueType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> view, View view1, int i, long l) {
                spinnerAdapter.setSelectedText(issueTypes[i]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> view) {
                // TODO: nothing
            }
        });

        setIssueTypeSpinner();
    }

    private void setIssueTypeSpinner() {
        if(mIssue.getIssue_type().equals(Issue.TASK)){
            mSpinnerIssueType.setSelection(0);
        }
        else if(mIssue.getIssue_type().equals(Issue.BUG)){
            mSpinnerIssueType.setSelection(1);
        }
    }

    private boolean getSelectedIssue() {
        if (getIntent().hasExtra(ISSUE)) {
            mIssue = getIntent().getParcelableExtra(ISSUE);
            return true;
        }
        return false;
    }


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_id_add_attachment) {

        } else if (view.getId() == R.id.btn_id_save) {
            if (mtietSummary.getText().toString().equals("")) {
                mtietSummary.setError("Required");
            } else {
                mProgressBar.setVisibility(View.VISIBLE);

                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference updateIssueRef = firestore
                        .collection(getString(R.string.collection_projects))
                        .document(mIssue.getProject_id())
                        .collection(getString(R.string.collection_issues))
                        .document(mIssue.getIssue_id());

                Issue issue = new Issue();
                issue.setAssignee((String) mSpinnerAssignee.getSelectedItem());
                issue.setDescription(mtietDescription.getText().toString());
                issue.setProject_id(mIssue.getProject_id());
                issue.setIssue_id(mIssue.getIssue_id());
                issue.setSummary(mtietSummary.getText().toString());
                issue.setStatus((String) mSpinnerIssueStatus.getSelectedItem());
                issue.setReporter(mIssue.getReporter());
                issue.setTime_reported(mIssue.getTime_reported());
                issue.setIssue_type(((SpinnerAdapter)mSpinnerIssueType.getAdapter()).getSelectedText());
                issue.setPriority(issue.getPriorityInteger(
                        ((SpinnerAdapter)(mSpinnerPriority.getAdapter())).getSelectedText()));

                updateIssueRef.set(issue)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(),
                                            "Issue update success", Toast.LENGTH_SHORT).show();
                                    finish();
                                } else {
                                    Log.d(TAG, "onComplete: Issue update failed " + task.getException());
                                    Toast.makeText(getApplicationContext(),
                                            "Issue update failed", Toast.LENGTH_SHORT).show();
                                }
                                mProgressBar.setVisibility(View.INVISIBLE);
                            }
                        });


            }
        }
    }
}
