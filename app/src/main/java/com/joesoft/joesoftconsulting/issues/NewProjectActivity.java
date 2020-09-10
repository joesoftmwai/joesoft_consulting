package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.joesoft.joesoftconsulting.R;
import com.joesoft.joesoftconsulting.models.Project;

public class NewProjectActivity extends AppCompatActivity {
    private TextInputEditText mtietProjectName;
    private TextInputEditText mtietDescripton;
    private ProgressBar mProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        mtietProjectName = findViewById(R.id.tiet_project_name);
        mtietDescripton = findViewById(R.id.tiet_description);
        mProgressBar = findViewById(R.id.progress_bar_np);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.new_project_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_project_save) {
            saveNewProject();
            return true;
        } else if (id == R.id.action_project_cancel) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveNewProject() {
        if (!mtietProjectName.getText().toString().isEmpty()
                && !mtietDescripton.getText().toString().isEmpty()) {
            // showProgressBar
            mProgressBar.setVisibility(View.VISIBLE);

            FirebaseFirestore firestoreDb = FirebaseFirestore.getInstance();
            DocumentReference newProjectRef =
                    firestoreDb.collection(getString(R.string.collection_projects))
                    .document();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Project project = new Project(
                    mtietProjectName.getText().toString(),
                    mtietDescripton.getText().toString(),
                    userId,
                    null,
                    "",
                    newProjectRef.getId()
            );

            newProjectRef.set(project).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "Created new project", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Failed creating new project", Toast.LENGTH_SHORT).show();
                    }

                    // hideProgressBar()
                    mProgressBar.setVisibility(View.INVISIBLE);
                }
            });




        } else {
            Toast.makeText(NewProjectActivity.this,
                    "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
