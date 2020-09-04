package com.joesoft.joesoftconsulting.issues;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.joesoft.joesoftconsulting.R;

public class NewProjectActivity extends AppCompatActivity {
    private TextInputEditText mtietProjectName;
    private TextInputEditText mtietDescripton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        mtietProjectName = findViewById(R.id.tiet_project_name);
        mtietDescripton = findViewById(R.id.tiet_description);
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

        } else {
            Toast.makeText(NewProjectActivity.this,
                    "Please fill all fields", Toast.LENGTH_SHORT).show();
        }
    }
}
