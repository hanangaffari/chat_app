package com.example.chat_app.status;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.R;
import com.example.chat_app.chatActivity;
import com.example.chat_app.chatfragment;
import com.example.chat_app.setProfile;
import com.example.chat_app.specificchat;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.net.InternetDomainName;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;

public class statusfragment extends Fragment {

    public View v;
    public static int PICK_IMG;
    public ImageView mimageView;
    public ImageButton statusadd;
    public ImageButton statusupload;
    public ImageButton test;
    public RelativeLayout rest;
    private Uri imageUri;
    private String ImageUriStatusToken;
    private EditText statustex;

    LinearLayoutManager linearLayoutManager;

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private FirebaseStorage firebaseStorage;
    private FirebaseFirestore firebaseFirestore;

    private statusadapter adapter;

    ImageView imageviewofstatus;
    RecyclerView frecyclerView;

    private String c;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.statusfragment, container, false);

        statusadd = v.findViewById(R.id.statusadd);
        statusupload = v.findViewById(R.id.statusuploadbutton);
        mimageView = v.findViewById(R.id.statusimg);
        statustex = v.findViewById(R.id.statustext);
        rest = v.findViewById(R.id.restatus);
        test=v.findViewById(R.id.test);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        
        firebaseFirestore = FirebaseFirestore.getInstance();
        frecyclerView = v.findViewById(R.id.statusrecyclerview);
        imageviewofstatus = v.findViewById(R.id.imageviewofstatususer);

        //display status

        Query query = firebaseFirestore.collection("Users").whereEqualTo("statusstatus","exist");
        FirestoreRecyclerOptions<firebasemodel> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel>()
                .setQuery(query,firebasemodel.class)
                .build();




        adapter = new statusadapter(allusername);
        frecyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        frecyclerView.setLayoutManager(linearLayoutManager);
        frecyclerView.setAdapter(adapter);


        //kirim status
        statusadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                intent.setType("image/*");
                startActivityForResult(intent, PICK_IMG);
            }
        });
        statusupload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rest.setVisibility(View.GONE);
                sendImageToStorage();
            }
        });
        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK && requestCode == PICK_IMG) {
            imageUri = data.getData();
            mimageView.setImageURI(imageUri);
            rest.setVisibility(View.VISIBLE);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void sendImageToStorage() {
        StorageReference imageref = storageReference.child("status post").child(firebaseAuth.getUid()).child("Profile status");

        //image conversion
        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 25, byteArrayOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();

        //putting image

        UploadTask uploadTask = imageref.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imageref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ImageUriStatusToken = uri.toString();
                        Toast.makeText(getActivity(), "URI get success", Toast.LENGTH_SHORT).show();
                        sendDataToCloudFirestore();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "URI GET FAILED", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(getActivity(), "Image is uploaded", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "image not upload", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendDataToCloudFirestore() {
        DocumentReference documentReference = firebaseFirestore.collection("Users").document(firebaseAuth.getUid())
                .collection("statuspost").document("status");
            Map<String, Object> userdata = new HashMap<>();
                userdata.put("statuspost", ImageUriStatusToken);
                userdata.put("statustext",statustex.getText().toString());

                DocumentReference documentReference1 = firebaseFirestore.collection("Users")
                        .document(firebaseAuth.getUid());
                Map<String, Object> status = new HashMap<>();
                status.put("statusstatus","exist");

                DocumentReference documentReference2 = firebaseFirestore.collection("Users")
                        .document(firebaseAuth.getUid());

                Map<String, Object> statu = new HashMap<>();
                statu.put("statuspost",ImageUriStatusToken);

                documentReference2.set(statu,SetOptions.merge());
                documentReference1.set(status,SetOptions.merge());
                documentReference.set(userdata, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(getActivity(), "data on cloud firestore sent success", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                }
            });
    }






    @Override
    public void onStart() {
        super.onStart();
       adapter.startListening();

    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
           adapter.stopListening();
        }
    }
}


