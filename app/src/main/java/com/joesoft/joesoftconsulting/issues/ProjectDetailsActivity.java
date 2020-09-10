package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Project;

import java.util.HashMap;

public class ProjectDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "ProjectDetailsActivity";
    public static final String PROJECT
            = "com.joesoft.joesoftconsulting.issues.PROJECT";
    private Project mProject;

    private TextInputEditText mtietProjectName, mtietDescription, mtietDateCreated;
    private ImageView mivProjectAvatar;
    private Button mBtnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details);
        mtietProjectName = findViewById(R.id.tiet_pd_project_name);
        mtietDescription = findViewById(R.id.tiet_pd_description);
        mtietDateCreated = findViewById(R.id.tiet_pd_date_created);
        mivProjectAvatar = findViewById(R.id.iv_pd_avator);
        mBtnSave = findViewById(R.id.btn_pd_save);

        mBtnSave.setOnClickListener(this);
        mivProjectAvatar.setOnClickListener(this);

        getSelectedProject();
        setProjectDetails();

    }

    private void setProjectDetails() {
        mtietProjectName.setText(mProject.getName());
        mtietDescription.setText(mProject.getDescription());
        mtietDateCreated.setText(mProject.getTime_created().toString());
    }

    private void getSelectedProject() {
        mProject = getIntent().getParcelableExtra(PROJECT);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_pd_save) {
            updateProject();
        } else if (view.getId() == R.id.iv_pd_avator) {

        }
    }

    private void updateProject() {

        if (mtietProjectName.equals("") || mtietDescription.equals("")) {
            Toast.makeText(getApplicationContext(), "Fill in the fields", Toast.LENGTH_SHORT).show();
        } else {
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();

            HashMap<String, Object> projectUpdates = new HashMap<>();
            projectUpdates.put("name", mtietProjectName.getText().toString());
            projectUpdates.put("description", mtietDescription.getText().toString());

            firestore.collection(getString(R.string.collection_projects))
                    .document(mProject.getProject_id())
                    .update(projectUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: project updated");
                                Toast.makeText(getApplicationContext(),
                                        "Project updated", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.d(TAG, "onComplete: Failed to update the project");
                                Toast.makeText(getApplicationContext(),
                                        "Failed to update the project", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}
