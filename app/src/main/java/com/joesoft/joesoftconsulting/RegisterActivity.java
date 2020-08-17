package com.joesoft.joesoftconsulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private TextView mrTextEmail;
    private TextView mrTextPassword;
    private TextView mrTextConfirmPassword;
    private Button mrBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mrTextEmail = findViewById(R.id.etr_email);
        mrTextPassword = findViewById(R.id.etr_password);
        mrTextConfirmPassword = findViewById(R.id.etr_confirm_password);
        mrBtnRegister = findViewById(R.id.btnr_register);

        mrBtnRegister.setOnClickListener(this);


    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnr_register) {
            Log.d(TAG, "onClick: Attempting to register");
            if (!mrTextEmail.getText().toString().isEmpty() && !mrTextPassword.getText().toString().isEmpty()
                    && !mrTextConfirmPassword.getText().toString().isEmpty()) {

                if(validateEmail(mrTextEmail)){
                    // check if passwords match
                    if (mrTextPassword.getText().toString().equals(mrTextConfirmPassword.getText().toString())){
                        registerNewEmail(mrTextEmail.getText().toString(), mrTextPassword.getText().toString());
                    } else {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Toast.makeText(RegisterActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void registerNewEmail(String email, String password) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "onComplete: " + task.isSuccessful());
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: AuthState " + FirebaseAuth.getInstance().getCurrentUser().getUid());
                        Toast.makeText(RegisterActivity.this, "User Registered successfully", Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();
                        finish();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
        });
    }

    private boolean validateEmail(TextView email) {
        String emailInput = email.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
            return true;

        mrTextEmail.setError("Invalid email address");
        return false;
    }




}
