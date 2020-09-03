package com.joesoft.joesoftconsulting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class ChangePhotoDialog extends AppCompatDialogFragment implements View.OnClickListener {

    private AlertDialog mAlertDialog;

    public interface OnPhotoReceivedListener{
        void getImagePath(Uri imagePath);

        void getImageBitmap(Bitmap bitmap);
    }

    private OnPhotoReceivedListener mOnPhotoReceive;

    private static final String TAG = "ChangePhotoDialog";
    public static final int PICK_FILE_REQUEST_CODE = 456;
    public static final int CAMERA_REQUEST_CODE = 789;
    private TextView mcpTextTakePhoto, mcpTextChoosePhoto;
    private View mView;

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            mOnPhotoReceive = (OnPhotoReceivedListener) getActivity(); 
        } catch (Exception e) {
            Log.d(TAG, "onAttach: Exception " + e.getMessage());
        }
        super.onAttach(context);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        mView = inflater.inflate(R.layout.change_photo, null);

        mcpTextTakePhoto = mView.findViewById(R.id.tvTakePhoto);
        mcpTextChoosePhoto = mView.findViewById(R.id.tvChoosePhoto);

        mcpTextChoosePhoto.setOnClickListener(this);
        mcpTextTakePhoto.setOnClickListener(this);

        builder.setView(mView);

        mAlertDialog = builder.create();

        return mAlertDialog;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tvChoosePhoto) {
            // Toast.makeText(mView.getContext(), "Choose Photo selected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onClick: accessing phone memory");
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

            startActivityForResult(intent, PICK_FILE_REQUEST_CODE);
        }

        if (view.getId() == R.id.tvTakePhoto) {
            // Toast.makeText(mView.getContext(), "Take Photo selected", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onClick: taking a photo from camera");
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            Log.d(TAG, "onActivityResult: image: " + selectedImageUri);
            if (selectedImageUri != null)
                mOnPhotoReceive.getImagePath(selectedImageUri);
            mAlertDialog.dismiss();
        } else if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: done taking photo");
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");

            Log.d(TAG, "onActivityResult: bitmap " + bitmap);
            if (bitmap != null)
                mOnPhotoReceive.getImageBitmap(bitmap);
            mAlertDialog.dismiss();
        }
    }
}
