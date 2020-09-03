package com.joesoft.joesoftconsulting;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.joesoft.joesoftconsulting.models.User;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountSettingsActivity extends AppCompatActivity
        implements View.OnClickListener, ChangePhotoDialog.OnPhotoReceivedListener {
    private static final String TAG = "AccountSettingsActivity";
    public static final int REQUEST_CODE = 123;
    public static final double MB = 1000000.0;
    public static final double MB_THRESHHOLD = 5.0;

    private ImageView mImageAccountSettings;
    private EditText masEditName, masEditPhone, masEditEmail, masEditConfirmPassword;
    private Button masBtnSave;
    private TextView masTextChangePassword;
    private ProgressBar masProgressBar;

    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private boolean mStoragePermission;
    private Bitmap mSelectedImageBitmap;
    private Uri mSelectedImageUri;
    private byte[] mBytes;
    private double mProgress;

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
        masProgressBar = findViewById(R.id.progress_bar_as);

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
                    String imageUrl = user.getProfile_image();
                    if (imageUrl != null && !imageUrl.isEmpty())
                        Picasso.get().load(imageUrl).fit().centerCrop().into(mImageAccountSettings);

                    Log.d(TAG, "onDataChange: image url " + user.getProfile_image());
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
            //----------- change name -------------
            if (!masEditName.getText().toString().equals("")) {
                reference.child(getString(R.string.node_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_name))
                        .setValue(masEditName.getText().toString());
            }
            //----------- change phone ------------
            if (!masEditPhone.getText().toString().equals("")) {
                reference.child(getString(R.string.node_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.field_phone))
                        .setValue(masEditPhone.getText().toString());
            }
            Toast.makeText(AccountSettingsActivity.this, "Saved", Toast.LENGTH_SHORT).show();

            //---------- Upload new photo ----------
            if (mSelectedImageUri != null) {
                uploadNewPhoto(mSelectedImageUri);
            } else if (mSelectedImageBitmap != null) {
                uploadNewPhoto(mSelectedImageBitmap);
            }
            
        }

        if (view.getId() == R.id.tvasChangePassword) {
            Toast.makeText(getApplicationContext(),
                    "Change password clicked", Toast.LENGTH_SHORT).show();
        }

        if (view.getId() == R.id.imageAccountSettings) {
            if (mStoragePermission) {

                ChangePhotoDialog changePhotoDialog = new ChangePhotoDialog();
                changePhotoDialog.show(getSupportFragmentManager(), "Change Photo Dialog");
            } else {
                verifyStoragePermission();
            }
        }
    }

    private void uploadNewPhoto(Uri imageUri) {
        Log.d(TAG, "uploadNewPhoto: Uploading new profile image to firebase storage");
        // Only accept images that are compressed to under 5MB. If that is not possible
        // Do not allow image to upload
        BackgroundImageResize resize = new BackgroundImageResize(null);
        resize.execute(imageUri);
    }

    private void uploadNewPhoto(Bitmap imageBitmap) {
        Log.d(TAG, "uploadNewPhoto: Uploading new profile image to firebase storage");
        // Only accept images that are compressed to under 5MB. If that is not possible
        // Do not allow image to upload
        BackgroundImageResize resize = new BackgroundImageResize(imageBitmap);
        Uri uri = null;
        resize.execute(uri);
    }


    public class BackgroundImageResize extends AsyncTask<Uri, Integer, byte[]> {

        Bitmap mBitmap;

        public BackgroundImageResize(Bitmap bitmap) {
            if (bitmap != null)
                mBitmap = bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showProgressBar();
            Toast.makeText(AccountSettingsActivity.this, "compressing image", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected byte[] doInBackground(Uri... uris) {
            Log.d(TAG, "doInBackground: started");
            Uri uri = uris[0];

            if (mBitmap == null) {
                try {
                    mBitmap = MediaStore.Images.Media.getBitmap(AccountSettingsActivity.this.getContentResolver(), uri);
                    Log.d(TAG, "doInBackground: bitmap size megabytes: " + mBitmap.getByteCount()/ MB + " MB");
                } catch (Exception e) {
                    Log.d(TAG, "doInBackground: exception " + e.getCause());
                }
            }

            byte[] bytes = null;
            for (int i = 1; i < 11; i++) {
//                if (i == 10) {
////                    Toast.makeText(AccountSettingsActivity.this,
////                            "That image is too large", Toast.LENGTH_SHORT).show();
//                    Log.d(TAG, "doInBackground: That image is too large");
//                    break;
//                }
                bytes = getBytesFromBitMap(mBitmap,100/i);
                Log.d(TAG, "doInBackground: megabytes(" + (11-i) + "0%) " + bytes.length/MB + " MB");
                if (bytes.length/MB < MB_THRESHHOLD) {
                    return bytes;
                }
            }

            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);
            hideProgressBar();
            mBytes = bytes;

            executeUploadTask();
        }
    }

    private void executeUploadTask() {
        showProgressBar();
        // specify where the photo will be stored
        StorageReference storageRef = FirebaseStorage.getInstance().getReference()
                .child("images/users/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + "/profile_image");

        if (mBytes.length/MB < MB_THRESHHOLD) {
            // create file metadata include content type
            StorageMetadata metadata = new StorageMetadata.Builder()
                    .setContentType("image/jpeg")
                    .setContentLanguage("en")
                    .setCustomMetadata("Joesoft's special metadata", "mJ get the job done")
                    .build();
            // if the image size is valid then we can submit to database
            UploadTask uploadTask = null;
            uploadTask = storageRef.putBytes(mBytes);
            // uploadTask = storageRef.putBytes(mBytes, metadata);

            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot snapshot) {
                    snapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Uri firebaseURL = uri;
                            Toast.makeText(getApplicationContext(), "Upload success", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "onSuccess: firebase download url: " + firebaseURL.getPath());
                            // update photo_url in real time database
                            FirebaseDatabase.getInstance().getReference()
                                    .child(getString(R.string.node_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.field_profile_image))
                                    .setValue(firebaseURL.toString());
                            hideProgressBar();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: Could not upload photo; " + e.getCause());
                    Toast.makeText(getApplicationContext(), "Could not upload photo", Toast.LENGTH_SHORT).show();
                    hideProgressBar();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot snapshot) {
                    double currentProgress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                    if (currentProgress > (mProgress + 15)) {
                        mProgress = (100 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount();
                        Log.d(TAG, "onProgress: Upload is " + mProgress + "% done");
                        Toast.makeText(getApplicationContext(),
                                mProgress + "%", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    }

    //--------- convert from bitmap to byte array --------
    private byte[] getBytesFromBitMap(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }


    private void editUserEmail() {
        showProgressBar();
        // TODO : implement edit user email
    }

    private boolean validateEmail(EditText email) {
        String emailInput = email.getText().toString();
        if (Patterns.EMAIL_ADDRESS.matcher(emailInput).matches())
            return true;

        masEditEmail.setError("Invalid email address");
        return false;
    }

    private void showProgressBar() {
        masProgressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar() {
        if (masProgressBar.getVisibility() == View.VISIBLE) masProgressBar.setVisibility(View.INVISIBLE);
    }

    /**
     * --------- General method for requesting permissions -----------
     */
    public void verifyStoragePermission() {
        String[] permissions = {
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
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
                            "User has been granted permission to access: " + permissions[0]);
                }
                break;
        }
    }

    @Override
    public void getImagePath(Uri imagePath) {
        mSelectedImageBitmap = null;
        mSelectedImageUri = imagePath;
        Log.d(TAG, "getImagePath: got image path " + mSelectedImageUri);

        Picasso.get().load(mSelectedImageUri.toString()).fit().centerCrop().into(mImageAccountSettings);
    }

    @Override
    public void getImageBitmap(Bitmap bitmap) {
        mSelectedImageUri = null;
        mSelectedImageBitmap = bitmap;
        Log.d(TAG, "getImageBitmap: got image bitmap " + mSelectedImageBitmap);

        mImageAccountSettings.setImageBitmap(mSelectedImageBitmap);
    }

}
