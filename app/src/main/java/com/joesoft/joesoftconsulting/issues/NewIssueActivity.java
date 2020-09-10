package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Issue;
import com.joesoft.joesoftconsulting.models.Project;
import com.joesoft.joesoftconsulting.utility.SpinnerResource;

import java.util.ArrayList;

public class NewIssueActivity extends AppCompatActivity implements View.OnTouchListener {
    public static final String TAG = NewIssueActivity.class.getSimpleName();
    private AutoCompleteTextView mactvAssignToProject;
    private TextInputEditText mtietSummary, mtietIssueDescription;
    private Spinner mSpinnerIssueType, mSpinnerPriority;
    private ProgressBar mProgressBar;

    ArrayList<Project> mProjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_issue);
        mactvAssignToProject = findViewById(R.id.actv_assign_to_project);
        mtietIssueDescription = findViewById(R.id.tiet_issue_description);
        mtietSummary = findViewById(R.id.tiet_summary);
        mSpinnerIssueType = findViewById(R.id.spinner_issue_type);
        mSpinnerPriority = findViewById(R.id.spinner_priority);
        mProgressBar = findViewById(R.id.progress_bar_ni);

        mactvAssignToProject.setOnTouchListener(this);

        initIssueTypeSpinner();
        initPrioritySpinner();
        initProjectAutoCompleteTextView();
    }

    private void initProjectAutoCompleteTextView() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference projectsRef = firestore.collection(getString(R.string.collection_projects));

        projectsRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @SuppressLint("ResourceType")
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Got the project");
                            int i = 0;
                            String[] projects = new String[task.getResult().size()];
                            for (QueryDocumentSnapshot documentSnapshot: task.getResult()) {
                                Project project = documentSnapshot.toObject(Project.class);
                                mProjects.add(project);
                                projects[i] = project.getName();
                                i++;
                            }

                            ArrayAdapter<String> projectsAdapter = new ArrayAdapter<String>(
                                    getApplicationContext(), android.R.layout.simple_list_item_1, projects);
                            mactvAssignToProject.setAdapter(projectsAdapter);

                            // enhance user experience
                            initTextWatcher();

                        } else {
                            Log.d(TAG, "onComplete: error getting projects " + task.getException());
                            Toast.makeText(getApplicationContext(),
                                    "Error getting projects", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void initTextWatcher() {
        mactvAssignToProject.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence sequence, int i, int i1, int i2) {
                // ToDo: add implementation
            }

            @Override
            public void onTextChanged(CharSequence sequence, int i, int i1, int i2) {
                // ToDo: add implementation
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().equals("")) {
                    mactvAssignToProject.setError("Select a project");
                } else {
                    mactvAssignToProject.setError(null);
                }
            }
        });
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
        if (mactvAssignToProject.getText().toString().isEmpty()) {
            mactvAssignToProject.setError("Select a project");
        } else if (mtietSummary.getText().toString().isEmpty()) {
            mtietSummary.setError("Required");
        } else {
            mProgressBar.setVisibility(View.VISIBLE);
            // find project id
            String id = "";
            for (Project project: mProjects) {
                if (project.getName().equals(mactvAssignToProject.getText().toString())) {
                    id = project.getProject_id();
                    break;
                }
            }
            final String projectId = id;

            if (projectId.equals("")) {
                Toast.makeText(this, "Select a valid project", Toast.LENGTH_SHORT).show();
                mactvAssignToProject.setError("Select a project");
                mProgressBar.setVisibility(View.INVISIBLE);
            } else {
                // get document reference
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                DocumentReference newIssueRef = firestore
                        .collection(getString(R.string.collection_projects))
                        .document(projectId)
                        .collection(getString(R.string.collection_issues))
                        .document();

                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                String issueId = newIssueRef.getId();

                Issue issue = new Issue();
                issue.setAssignee("none");
                issue.setDescription(mtietIssueDescription.getText().toString());
                issue.setIssue_id(issueId);
                issue.setIssue_type(((SpinnerAdapter)mSpinnerIssueType.getAdapter()).getSelectedText());
                issue.setPriority(issue.getPriorityInteger(((SpinnerAdapter)mSpinnerPriority.getAdapter()).getSelectedText()));
                issue.setReporter(userId);
                issue.setStatus(Issue.IDLE);
                issue.setSummary(mtietSummary.getText().toString());
                issue.setProject_id(projectId);

                newIssueRef.set(issue)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mProgressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(),
                                        "Created new issue", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: failed to add new issue; " + e.getCause());
                                Toast.makeText(getApplicationContext(),
                                        "Failed Creating new issue", Toast.LENGTH_SHORT).show();
                            }
                        });

            }

        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if (view.getId() == R.id.actv_assign_to_project) {
            showSoftKeyboard(view);
            mactvAssignToProject.showDropDown();
            return true;
        }
        return false;
    }

    public void showSoftKeyboard(View view) {
        if (view.requestFocus()) {
            InputMethodManager imm = (InputMethodManager)
                    getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void hideSoftKeyboard(){
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}


