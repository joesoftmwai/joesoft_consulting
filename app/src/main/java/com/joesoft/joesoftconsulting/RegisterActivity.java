package com.joesoft.joesoftconsulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RegisterActivity";
    private FirebaseAuth mAuth;
    private EditText mrEditEmail, mrEditPassword, mrEditConfirmPassword;
    private Button mrBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mrEditEmail = findViewById(R.id.etr_email);
        mrEditPassword = findViewById(R.id.etr_password);
        mrEditConfirmPassword = findViewById(R.id.etr_confirm_password);
        mrBtnRegister = findViewById(R.id.btnr_register);

        mrBtnRegister.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }

    @SuppressLint("ShowToast")
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnr_register) {
            Log.d(TAG, "onClick: Attempting to register");
            if (!mrEditEmail.getText().toString().isEmpty() && !mrEditPassword.getText().toString().isEmpty()
                    && !mrEditConfirmPassword.getText().toString().isEmpty()) {

                if(validateEmail(mrEditEmail)){
                    // check if passwords match
                    if (mrEditPassword.getText().toString().equals(mrEditConfirmPassword.getText().toString())){
                        registerNewEmail(mrEditEmail.getText().toString(), mrEditPassword.getText().toString());
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

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            sendVerificationEmail();
                            mAuth.signOut();

                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                });



    }

    public void sendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,"Sent verification email", Toast.LENGTH_SHORT).show();
                    } else  {
                        Toast.makeText(RegisterActivity.this, "Could not Sent verification email", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
    public boolean validateEmail(TextView email) {
        String emailInput = email.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
            return true;

        mrEditEmail.setError("Invalid email address");
        return false;
    }

//    private void updateUI(FirebaseUser user) {
//        hideProgressDialog();
//        if (user != null) {
//            mStatusTextView.setText(getString(R.string.google_status_fmt, user.getEmail()));
//            mDetailTextView.setText(getString(R.string.firebase_status_fmt, user.getUid()));
//
//            findViewById(R.id.sign_in_button).setVisibility(View.GONE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);
//        } else {
//            mStatusTextView.setText(R.string.signed_out);
//            mDetailTextView.setText(null);
//
//            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
//            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);
//        }
//    }




}
