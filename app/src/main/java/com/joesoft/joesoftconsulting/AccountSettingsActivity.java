package com.joesoft.joesoftconsulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.joesoft.joesoftconsulting.models.User;

public class AccountSettingsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "AccountSettingsActivity";
    public static final int REQUEST_CODE = 123;

    private ImageView mImageAccountSettings;
    private EditText masEditName, masEditPhone, masEditEmail, masEditConfirmPassword;
    private Button masBtnSave;
    private TextView masTextChangePassword;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean mStoragePermission;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkAuthenticationState();
    }

    private void checkAuthenticationState() {
        Log.d(TAG, "checkAuthenticationState: checking auth state");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            Log.d(TAG, "checkAuthenticationState: user is null ridiresct to login screen");

            Intent intent = new Intent(AccountSettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_settings);

        mImageAccountSettings = findViewById(R.id.imageAccountSettings);
        masEditName = findViewById(R.id.etasName);
        masEditPhone = findViewById(R.id.etasPhone);
        masEditEmail = findViewById(R.id.etasEmail);
        masBtnSave = findViewById(R.id.btnasSave);
        masEditConfirmPassword = findViewById(R.id.etasConfirmPassword);
        masTextChangePassword = findViewById(R.id.tvasChangePassword);

        mImageAccountSettings.setOnClickListener(this);
        masTextChangePassword.setOnClickListener(this);
        masBtnSave.setOnClickListener(this);

        verifyStoragePermission();
        setUpFirebaseAuth();
        setUpCurrentEmail();

        getUserAccountDetails();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
    }
    
    private void setUpCurrentEmail() {
        Log.d(TAG, "setUpCurrentEmail: setting current email");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String email = user.getEmail();
            Log.d(TAG, "setUpCurrentEmail: " + email);
            masEditEmail.setText(email);
        }

    }

    private void getUserAccountDetails() {
        Log.d(TAG, "getUserAccountDetails: Retrieve user details");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        // query method 1
        Query query1 = reference.child(getString(R.string.node_users))
                .orderByKey()
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot: snapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: (QUERY METHOD 1) found user: " + user.toString());
                    masEditName.setText(user.getName());
                    masEditPhone.setText(user.getPhone());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        // query method 2
        Query query2 = reference.child(getString(R.string.node_users))
                .orderByChild(getString(R.string.field_user_id))
                .equalTo(FirebaseAuth.getInstance().getCurrentUser().getUid());

        query2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot singleSnapshot: snapshot.getChildren()) {
                    User user = singleSnapshot.getValue(User.class);
                    Log.d(TAG, "onDataChange: (QUERY METHOD 2) found user: " + user.toString());
                    masEditName.setText(user.getName());
                    masEditPhone.setText(user.getPhone());
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        masEditEmail.setText(FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }

    private void setUpFirebaseAuth() {
        Log.d(TAG, "setUpFirebaseAuth: Started");
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    // signed in
                    Log.d(TAG, "onAuthStateChanged: signed_in " + user.getUid());
                } else {
                    // signed out
                    Log.d(TAG, "onAuthStateChanged: signed_out");
                    startActivity(new Intent(AccountSettingsActivity.this, LoginActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnasSave) {
            Log.d(TAG, "onClick: Attempting to save");
            // check if email has been changed
            if (!masEditEmail.getText().toString().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                if (!masEditEmail.getText().toString().isEmpty()
                        && !masEditConfirmPassword.getText().toString().isEmpty()) {
                    // check is email is valid
                    if (validateEmail(masEditEmail)) {
                        editUserEmail();
                    }
                } else {
                    Toast.makeText(AccountSettingsActivity.this,
                            "Email & password must be filled to save", Toast.LENGTH_SHORT).show();
                }
            }

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
            // change name
            if (!masEditName.getText().toString().equals("")) {
                reference.child(getString(R.string.node_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_name))
                        .setValue(masEditName.getText().toString());
                Toast.makeText(getApplicationContext(),
                        "Name updated", Toast.LENGTH_SHORT).show();
            }
            // change phone
            if (!masEditPhone.getText().toString().equals("")) {
                reference.child(getString(R.string.node_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_phone))
                        .setValue(masEditPhone.getText().toString());
                Toast.makeText(getApplicationContext(),
                        "Phone Number updated", Toast.LENGTH_SHORT).show();
            }
        }

        if (view.getId() == R.id.tvasChangePassword) {
            Toast.makeText(getApplicationContext(),
                    "Change password clicked", Toast.LENGTH_SHORT).show();
        }

        if (view.getId() == R.id.imageAccountSettings) {
            if (mStoragePermission) {

            } else {
                verifyStoragePermission();
            }
        }
    }

    private void editUserEmail() {
        // TODO : implement edit user email
    }

    public boolean validateEmail(EditText email) {
        String emailInput = email.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
            return true;

        masEditEmail.setError("Invalid email address");
        return false;
    }

    /**
     * --------- General method for requesting permissions -----------
     */
    public void verifyStoragePermission() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        };

        if (ContextCompat.checkSelfPermission(
                getApplicationContext(), permissions[0]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                getApplicationContext(), permissions[1]) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(
                getApplicationContext(), permissions[2]) == PackageManager.PERMISSION_GRANTED) {
            mStoragePermission = true;
        } else {
            ActivityCompat.requestPermissions(
                    AccountSettingsActivity.this, permissions, REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: Request code: " + requestCode);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "onRequestPermissionsResult: " +
                            "User has been granted permision to access: " + permissions[0]);
                }
                break;
        }
    }
}
