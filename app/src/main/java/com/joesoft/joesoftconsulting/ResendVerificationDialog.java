package com.joesoft.joesoftconsulting;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ResendVerificationDialog extends AppCompatDialogFragment {
    private static final String TAG = "ResendVerificationDialog";
    private EditText mrvEditEmail, mrvEditPassword;
    private View mView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.resend_verification, null);


        mrvEditEmail = mView.findViewById(R.id.et_resend_email);
        mrvEditPassword = mView.findViewById(R.id.et_resend_password);

        builder.setView(mView)
                .setTitle("Resend Verification Email")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        // dismiss dialog
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        if (!mrvEditEmail.getText().toString().isEmpty()
                                && !mrvEditPassword.getText().toString().isEmpty()) {

                            authenticateAndResendEmail(
                                    mrvEditEmail.getText().toString(),
                                    mrvEditPassword.getText().toString());

                        } else {
                            Toast.makeText(getActivity(), "All fields must be filled out",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }

    private void authenticateAndResendEmail(String email, String password) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        FirebaseAuth.getInstance().signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            sendVerificationEmail();
                            FirebaseAuth.getInstance().signOut();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mView.getContext(), e.getMessage(),
                                Toast.LENGTH_LONG).show();


                    }
                });

    }


    private void sendVerificationEmail() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()) {
                        Toast.makeText(mView.getContext(),"Sent verification email", Toast.LENGTH_LONG).show();
                    } else  {
                        Toast.makeText(mView.getContext(), "Could not Sent verification email", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
