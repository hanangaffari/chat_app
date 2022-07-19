package com.example.chat_app.status;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.chat_app.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class statusviewActivity extends AppCompatActivity {

    private androidx.appcompat.widget.Toolbar mtoolbarofstatuslayout;
    private ImageButton mbackbuttonofstatuslayout;
    private ImageButton mdeletestatus;
    private ImageView mImageviewofstatususer;
    private ImageView mstatuspost;
    private TextView mnameofStatususer;
    private TextView statustext;
    private String statusname;
    private String statusuid;
    private String statustexxxx;

    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statusview);

        mtoolbarofstatuslayout=findViewById(R.id.toolbarstatusview);
        mbackbuttonofstatuslayout=findViewById(R.id.backbuttonofstatusview);
        mImageviewofstatususer=findViewById(R.id.statususerimageinimageview);
        mnameofStatususer=findViewById(R.id.nameofstausview);
        mstatuspost=findViewById(R.id.statusimg);
        mdeletestatus=findViewById(R.id.status_delete_button);
        statustext=findViewById(R.id.textstatus);

        firebaseFirestore=FirebaseFirestore.getInstance();

        firebaseStorage=FirebaseStorage.getInstance();

        firebaseAuth= FirebaseAuth.getInstance();

        intent=getIntent();

        setSupportActionBar(mtoolbarofstatuslayout);

        mbackbuttonofstatuslayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mdeletestatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
                documentReference.update("statuspost", FieldValue.delete());
                documentReference.update("statusstatus", "doesn't exist ");
                Toast.makeText(statusviewActivity.this, "deleted", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(statusviewActivity.this,statusfragment.class);
                startActivity(intent);
            }
        });

        //isi---------
        statusname = getIntent().getStringExtra("name");
        mnameofStatususer.setText(statusname);

        //image profile-------------
        statusuid = getIntent().getStringExtra("statusuid");

        if(firebaseAuth.getUid().equals(statusuid) ){
            mdeletestatus.setVisibility(View.VISIBLE);
        }else{
            mdeletestatus.setVisibility(View.GONE);
        }


        storageReference=firebaseStorage.getReference();

        storageReference.child("Images").child(statusuid).child("Profile Pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(mImageviewofstatususer);
            }
        });
        //image profile end----------------

        //image status post----------------
        String statusuri=intent.getStringExtra("statuspost");
        if(statusuri.isEmpty()){
            Toast.makeText(this, "status none", Toast.LENGTH_SHORT).show();
        }else{
         Picasso.get().load(statusuri).into(mstatuspost);
        }
        //end-----------------
        //image status text

        statustexxxx = intent.getStringExtra("statustexti");

        if (statustexxxx.equals(null)) {
            statustext.setVisibility(View.GONE);
        }else{
            statustext.setText(statustexxxx);

        }
        }
        //end--------------------------

    }
