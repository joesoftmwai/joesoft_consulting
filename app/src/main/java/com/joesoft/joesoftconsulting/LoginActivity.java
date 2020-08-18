 package com.joesoft.joesoftconsulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

 public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
     private static final String TAG = "LoginActivity";
     // firebase
     private FirebaseAuth.AuthStateListener mAuthStateListener;
     private ImageView mlImageLogo;
     private EditText mlEditEmail, mlEditPassword;
     private TextView mlTextRegister, mlTextForgotPassword, mlTextResendVerification;
     private Button mlBtnSignIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setUpFirebaseAuth();

        mlImageLogo = findViewById(R.id.ivl_logo);
        mlEditEmail = findViewById(R.id.etl_email);
        mlEditPassword = findViewById(R.id.etl_password);
        mlBtnSignIn = findViewById(R.id.btnl_signin);
        mlTextRegister = findViewById(R.id.tvl_register);
        mlTextForgotPassword = findViewById(R.id.tvl_forgot_password);
        mlTextResendVerification = findViewById(R.id.tvl_resend_verification_email);

        loadAppLogo();

        mlTextRegister.setOnClickListener(this);
        mlBtnSignIn.setOnClickListener(this);
        mlTextResendVerification.setOnClickListener(this);
    }

     private void loadAppLogo() {
         Picasso.get().load(R.drawable.consultinglogo).into(mlImageLogo);
     }

     @Override
     public void onClick(View view) {
       if (view.getId() == R.id.tvl_register) {
           Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
           startActivity(intent);
       }
       if (view.getId() == R.id.btnl_signin) {
           if (!mlEditEmail.getText().toString().isEmpty() && !mlEditPassword.getText().toString().isEmpty()) {
               Log.d(TAG, "onClick: Attempting to login");
               FirebaseAuth.getInstance()
                       .signInWithEmailAndPassword(mlEditEmail.getText().toString(), mlEditPassword.getText().toString())
                       .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                           @Override
                           public void onComplete(@NonNull Task<AuthResult> task) {
                               if (task.isSuccessful()) {
                                   Toast.makeText(getApplicationContext(), "Signed in", Toast.LENGTH_SHORT).show();
                               }
                           }
                       }).addOnFailureListener(new OnFailureListener() {
                   @Override
                   public void onFailure(@NonNull Exception e) {
                       Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                   }
               });
           } else {
               Toast.makeText(getApplicationContext(), "You didn't fill all fields", Toast.LENGTH_SHORT).show();
           }
       }

       if (view.getId() == R.id.tvl_resend_verification_email) {
           openDialog();
       }
     }

     private void openDialog() {
        ResendVerificationDialog verificationDialog = new ResendVerificationDialog();
        verificationDialog.show(getSupportFragmentManager(), "Resend Verification Dialog");
     }

     // set up firebase auth
     private void setUpFirebaseAuth() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth auth) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    if (user.isEmailVerified()) {
                        Log.d(TAG, "onAuthStateChanged: signed_in " + user.getUid());
                        Toast.makeText(getApplicationContext(), "Authenticated with"
                                + user.getEmail(), Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this, SignedInActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "Check your email box for a verification link"
                                + user.getEmail(), Toast.LENGTH_SHORT).show();
                        FirebaseAuth.getInstance().signOut();

                    }
                } else {
                    Log.d(TAG, "onAuthStateChanged: Signed_out");
                }
            }
        };
     }

     @Override
     protected void onStart() {
         super.onStart();
         FirebaseAuth.getInstance().addAuthStateListener(mAuthStateListener);
     }

     @Override
     protected void onStop() {
         super.onStop();
         if (mAuthStateListener != null) {
             FirebaseAuth.getInstance().removeAuthStateListener(mAuthStateListener);
         }

     }

 }
