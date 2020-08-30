package com.joesoft.joesoftconsulting;

import android.app.AlertDialog;
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
import androidx.appcompat.app.AppCompatDialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordResetDialog extends AppCompatDialogFragment {
    private static final String TAG = "PasswordResetDialog";
    private View mView;
    private EditText mEditResetEmail;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.reset_password, null);

        mEditResetEmail = mView.findViewById(R.id.etResetEmail);


        builder.setView(mView)
                .setTitle("Reset Password: Email")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        // dismiss dialog
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface anInterface, int i) {
                        if (!mEditResetEmail.getText().toString().isEmpty()) {
                            Log.d(TAG, "onClick: attempting to send reset link "
                                    + mEditResetEmail.getText().toString().isEmpty());
                            sendPasswordResetEmail(mEditResetEmail.getText().toString());
                        }
                        else {
                            Toast.makeText(mView.getContext(), "Enter all fields",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        return builder.create();
    }

    private void sendPasswordResetEmail(String email) {
        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: Password Reset Email sent");
                            Toast.makeText(mView.getContext(), "Password Reset Link Sent to Email",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(TAG, "onComplete: No user associated with that email.");
                            Toast.makeText(mView.getContext(), "No User is Associated with that Email",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


}
