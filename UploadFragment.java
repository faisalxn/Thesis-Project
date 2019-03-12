package com.example.user.companycommunity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import org.apache.commons.io.FilenameUtils;

import java.util.ArrayList;


public class UploadFragment extends Fragment {
    private boolean flag ;
    private String companyId,uId;
    private View view ;
    private ImageView upload_public_imageView,upload_private_imageView ;
    private final static int gallery_pick = 1;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private StorageReference userPrivateDataStorageRef;
    private StorageReference userPublicDataStorageRef;
    private DatabaseReference privateURLDatabaseNameRef;
    private DatabaseReference publicURLDatabaseNameRef;
    private StorageReference storageReference;
    private DatabaseReference databaseReference,notification;
    private ArrayList<String> strings ;


    @SuppressLint("ValidFragment")
    public UploadFragment(String companyId) {
        this.companyId = companyId;
    }

    public UploadFragment(){ }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //view = super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.fragment_upload, container, false);

        upload_public_imageView = (ImageView) view.findViewById(R.id.upload_public_imageView);
        upload_private_imageView = (ImageView) view.findViewById(R.id.upload_private_imageView);

        mAuth = FirebaseAuth.getInstance();
        uId = mAuth.getCurrentUser().getUid();

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        userPrivateDataStorageRef = storageReference.child(companyId).child(uId);
        userPublicDataStorageRef = storageReference.child(companyId).child("public_storage");

        privateURLDatabaseNameRef = databaseReference.child("URLs").child(companyId).child(uId);
        publicURLDatabaseNameRef = databaseReference.child("URLs").child(companyId).child("public_files");

        progressDialog = new ProgressDialog(getActivity());

        strings = new ArrayList<>();
        databaseReference.child("notifications").child(companyId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                strings.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String key = snapshot.getKey();
                    if(key.equals(uId)==false) {
                        strings.add(key);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        upload_private_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false ;

                Intent galleryIntent = new Intent();
                galleryIntent.setType("*/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, gallery_pick);



            }
        });

        upload_public_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = true;
                Intent galleryIntent = new Intent();
                galleryIntent.setType("*/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(galleryIntent, gallery_pick);


            }
        });

        return view ;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==gallery_pick && resultCode== Activity.RESULT_OK && data!=null && flag==false){
            progressDialog.setTitle("Please wait..");
            progressDialog.setMessage("Uploading the selected file to your private storage");
            progressDialog.show();

            Uri fileURI = data.getData();

            final String fileName = fileURI.getLastPathSegment(); //getting exact file name with extension

            StorageReference filePath = userPrivateDataStorageRef.child(fileName);
            filePath.putFile(fileURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {

                        //saving the file URL into real-time database
                        //String downloadURL = task.getResult().getDownloadUrl().toString(); //getting the file URL
                        //String fileNameWithoutExt = FilenameUtils.removeExtension(fileName);
                        //privateURLDatabaseLinkRef.child(fileNameWithoutExt).setValue(downloadURL);
                        String key = privateURLDatabaseNameRef.push().getKey();

                        privateURLDatabaseNameRef.child(key).child("file_name").setValue(fileName)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(getActivity(), "Upload successful!", Toast.LENGTH_LONG).show();

                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Something wrong!", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }
        if(requestCode==gallery_pick && resultCode== Activity.RESULT_OK && data!=null && flag == true) {

            progressDialog.setTitle("Please wait..");
            progressDialog.setMessage("Uploading the selected file to the public storage");
            progressDialog.show();

            Uri fileURI = data.getData();

            final String fileName = fileURI.getLastPathSegment();  //getting exact file name with extension

            StorageReference filePath = userPublicDataStorageRef.child(fileName);
            filePath.putFile(fileURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful()) {

                        //saving the file URL into real-time database
                        //String downloadURL = task.getResult().getDownloadUrl().toString(); //getting the file URL
                        //String fileNameWithoutExt = FilenameUtils.removeExtension(fileName); //firebase database doesn't support special character "." in the node name
                        //publicURLDatabaseLinkRef.child(fileNameWithoutExt).setValue(downloadURL);
                        String key = publicURLDatabaseNameRef.push().getKey();


                        publicURLDatabaseNameRef.child(key).child("file_name").setValue(fileName)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        notification = databaseReference.child("notifications").child(companyId);
                                        for(String s : strings){
                                            String key = notification.child(s).push().getKey();

                                            notification.child(s).child(key).child("name").setValue("A new file has been uploaded in public space!");

                                        }

                                        Toast.makeText(getActivity(), "Upload successful!", Toast.LENGTH_LONG).show();
                                    }
                                });
                    } else {
                        Toast.makeText(getActivity(), "Something wrong!", Toast.LENGTH_LONG).show();
                    }
                    progressDialog.dismiss();
                }
            });
        }


    }
}
