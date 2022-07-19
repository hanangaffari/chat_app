package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class setProfile extends AppCompatActivity {

    private CardView mgetuserimage;
    private ImageView mgetuserimageinImageview;
    private static int PICK_IMG;
    private Uri imagepath;

    private EditText mgetusername;
    private android.widget.Button msaveprofile;
    private FirebaseAuth firebaseAuth;
    private String name;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private String ImageUriAccessToken;
    private FirebaseFirestore firebaseFirestore;
    ProgressBar mprogressbarofsetprofile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_profile);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
        storageReference=firebaseStorage.getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        mgetusername=findViewById(R.id.getusername);
        mgetuserimage=findViewById(R.id.getuserimage);
        mgetuserimageinImageview=findViewById(R.id.getuserimageinimageview);
        msaveprofile=findViewById(R.id.saveprofile);
        mprogressbarofsetprofile=findViewById(R.id.progressbarsaveprofile);

        mgetuserimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent,PICK_IMG);
            }
        });

        msaveprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=mgetusername.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(getApplicationContext(),"name is empty",Toast.LENGTH_SHORT).show();
                }else if(imagepath==null){
                    Toast.makeText(getApplicationContext(),"image is empty",Toast.LENGTH_SHORT).show();
                }else{
                    mprogressbarofsetprofile.setVisibility(View.VISIBLE);
                    sendDataForNewUser();
                    mprogressbarofsetprofile.setVisibility(View.INVISIBLE);
                    Intent intent=new Intent(setProfile.this,chatActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == PICK_IMG && resultCode == RESULT_OK){
            imagepath=data.getData();
            mgetuserimageinImageview.setImageURI(imagepath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
    private void sendDataForNewUser(){
        sendDataToRealTimeDatabase();
    }
    private void sendDataToRealTimeDatabase(){
        name=mgetusername.getText().toString().trim();
        FirebaseDatabase firebaseDatabase=FirebaseDatabase.getInstance();
        DatabaseReference databaseReference=firebaseDatabase.getReference(firebaseAuth.getUid());

        userprofile muserprofile=new userprofile(name,firebaseAuth.getUid());
        databaseReference.setValue(muserprofile);
        Toast.makeText(getApplicationContext(), "user profile added succesfully", Toast.LENGTH_SHORT).show();
        sendImageToStorage();
    }
    private void sendImageToStorage(){
            StorageReference imageref =storageReference.child("Images").child(firebaseAuth.getUid()).child("Profile Pic");

        //image conversion
        Bitmap bitmap=null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imagepath);
        }catch(IOException e){
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,byteArrayOutputStream);
        byte[] data =byteArrayOutputStream.toByteArray();

        //putting image

        UploadTask uploadTask = imageref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriAccessToken=uri.toString();
                        Toast.makeText(getApplicationContext(),"URI get success",Toast.LENGTH_SHORT).show();
                        sendDataToCloudFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "URI GET FAILED", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getApplicationContext(), "Image is uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(setProfile.this, "image not upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void sendDataToCloudFirestore() {
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        Map<String,Object> userdata=new HashMap<>();
        userdata.put("name",name);
        userdata.put("Image",ImageUriAccessToken);
        userdata.put("uid",firebaseAuth.getUid());
        userdata.put("status","online");

        documentReference.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "data on cloud firestore sent success", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
