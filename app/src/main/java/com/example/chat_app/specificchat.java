package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class specificchat extends AppCompatActivity {

    EditText mgetmessage;
    ImageButton msendmessagebutton;

    CardView msendmessagecardview;
    androidx.appcompat.widget.Toolbar mtoolbarofspecificchat;
    ImageView mImageviewofspecificuser;
    TextView mnameofspecificuser;

    private String enteredmessage;
    Intent intent;
    String mrecievername,msendername,mrecieveruid,msenderuid;
    private FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private DocumentReference noteref;
    int s = 0;
    String senderr;

    String o;

    String senderroom,recieverroom;
    ImageButton mbackbuttonofspecificchat;
    FirebaseFirestore firebaseFirestore;


    String nm;
    String sa;
    RecyclerView mmessagerecyclerview;

    String currenttime;
    Calendar calendar;
    SimpleDateFormat simpleDateFormat;

    public MessagesAdapter messagesAdapter;
    ArrayList<Messages> messagesArrayList;
    private Handler mHandler = new Handler();

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_specificchat);



        mgetmessage=findViewById(R.id.getmessage);
        msendmessagecardview=findViewById(R.id.cardviewofsendmessage);
        msendmessagebutton=findViewById(R.id.imageviewofsendmessage);
        mtoolbarofspecificchat=findViewById(R.id.toolbarspecificchat);
        mnameofspecificuser=findViewById(R.id.nameofspecificuser);
        mImageviewofspecificuser=findViewById(R.id.specificuserimageinimageview);
        mbackbuttonofspecificchat=findViewById(R.id.backbuttonofspecificchat);

        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        messagesArrayList=new ArrayList<>();
        mmessagerecyclerview=findViewById(R.id.recyclerviewofspecific);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagerecyclerview.setLayoutManager(linearLayoutManager);
        messagesAdapter = new MessagesAdapter(specificchat.this,messagesArrayList);
        mmessagerecyclerview.setAdapter(messagesAdapter);
        intent=getIntent();


        String saya = firebaseAuth.getUid();
        noteref= firebaseFirestore.document("Users/" + saya);

        noteref.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if(documentSnapshot.exists()){
                            senderr = documentSnapshot.getString("name");
                        }else{
                            Toast.makeText(specificchat.this, "none", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

        setSupportActionBar(mtoolbarofspecificchat);
        mtoolbarofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(specificchat.this, "toolbar is clicked", Toast.LENGTH_SHORT).show();
            }
        });

        firebaseDatabase=FirebaseDatabase.getInstance();

        calendar=Calendar.getInstance();
        simpleDateFormat=new SimpleDateFormat("hh:mm a");

        msenderuid=firebaseAuth.getUid();
        mrecieveruid=getIntent().getStringExtra("receiveruid");
        mrecievername=getIntent().getStringExtra("name");

        senderroom=msenderuid+mrecieveruid;
        recieverroom=mrecieveruid+msenderuid;


        DatabaseReference databaseReference= firebaseDatabase.getReference().child("chats").child(senderroom).child("messages");
        messagesAdapter=new MessagesAdapter(specificchat.this,messagesArrayList);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren())
                {
                    Messages messages=snapshot1.getValue(Messages.class);
                    messages.setMessage(messages.getMessage());
                    messagesArrayList.add(messages);
                    mmessagerecyclerview.setVisibility(View.GONE);
                    mmessagerecyclerview.setVisibility(View.VISIBLE);
                }
                messagesAdapter.notifyDataSetChanged();
                mmessagerecyclerview.setVisibility(View.GONE);
                mmessagerecyclerview.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        mbackbuttonofspecificchat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(specificchat.this,chatActivity.class);
            }
        });
        mnameofspecificuser.setText(mrecievername);
        String uri=intent.getStringExtra("imageuri");
        if(uri.isEmpty()){
            Toast.makeText(this, "null is recieve", Toast.LENGTH_SHORT).show();
        }else{
            Picasso.get().load(uri).into(mImageviewofspecificuser);
        }
        messagesAdapter.notifyDataSetChanged();
        mmessagerecyclerview.setVisibility(View.GONE);
        mmessagerecyclerview.setVisibility(View.VISIBLE);
        msendmessagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start pesan baru
                s++;
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(mrecieveruid)
                        .collection("pesanbaru").document(senderr);

                Map<String, Integer> userdata = new HashMap<>();
                userdata.put("pesanbaru " + senderr,s);
                documentReference.set(userdata,SetOptions.merge());


                Map<String,Object> ub = new HashMap<>();
                ub.put("uid",firebaseAuth.getUid());
                ub.put("name",senderr);


                documentReference.set(ub,SetOptions.merge());

                //dapetin uid alamat yang latest sebelumnya

                    Task<QuerySnapshot> q = firebaseFirestore.collection("Users").document(mrecieveruid)
                            .collection("pesanbaru").whereEqualTo("s", "latest").get();
                    q.addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                o = document.getString("uid");
                            }
                        }
                    });

               //--------------------------------------------

                //dapetin nama latest sebelumnya dan hapus
                mHandler.postDelayed(mtoastrunnable,500);
                //=============================================
                //messages firebasedatabase
                enteredmessage=mgetmessage.getText().toString();
                if(enteredmessage.isEmpty()){
                    Toast.makeText(specificchat.this, "enter message first", Toast.LENGTH_SHORT).show();
                }else{
                    Date date = new Date();
                    currenttime=simpleDateFormat.format(calendar.getTime());
                    Messages messages=new Messages(enteredmessage,firebaseAuth.getUid(),date.getTime(),currenttime);
                    firebaseDatabase=FirebaseDatabase.getInstance();
                    firebaseDatabase.getReference().child("chats")
                            .child(senderroom)
                            .child("messages")
                            .push().setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            firebaseDatabase.getReference().child("chats")
                                    .child(recieverroom)
                                    .child("messages")
                                    .push()
                                    .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mmessagerecyclerview.setVisibility(View.GONE);
                                    mmessagerecyclerview.setVisibility(View.VISIBLE);

                                }
                            });
                        }
                    });
                    mgetmessage.setText(null);

                    firebaseDatabase.getReference().child("chats")
                            .child("messages")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(snapshot.exists()){
                                        int id = (int) snapshot.getChildrenCount();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                    //end messages firebase database
                }
            }



        });
    }

    private Runnable mtoastrunnable = new Runnable() {
        @Override
        public void run() {
            if(o!=null){
                DocumentReference documentReference1 =  firebaseFirestore.collection("Users").document(o);
                documentReference1.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.exists()){
                                    nm=documentSnapshot.getString("name");
                                }else{
                                    mHandler.postDelayed(mtoastrunnable2,600);
                                }
                            }
                        });
                mHandler.postDelayed(getMtoastrunnable1,500);}
            else{
                mHandler.postDelayed(mtoastrunnable2,500);
            }
        }
    };

    private Runnable mtoastrunnable1 = new Runnable() {

        @Override
        public void run() {
            DocumentReference documentReference = firebaseFirestore.collection("Users").document(mrecieveruid)
                    .collection("pesanbaru").document(nm);

            HashMap<String,Object> uc = new HashMap<>();
            uc.put("s","outdated");
            documentReference.set(uc,SetOptions.merge());

          mHandler.postDelayed(mtoastrunnable2,1000);
        }
    };

    private  Runnable mtoastrunnable2 = new Runnable() {
        @Override
        public void run() {
            DocumentReference documentReference1 = firebaseFirestore.collection("Users").document(mrecieveruid)
                    .collection("pesanbaru").document(senderr);

            HashMap<String,Object> us = new HashMap<>();
            us.put("s","latest");
            documentReference1.set(us,SetOptions.merge());
        }
    };

    private Runnable getMtoastrunnable3 = new Runnable() {
        @Override
        public void run() {
            if(sa.equals("latest")){
                return;
            }else{
                mHandler.postDelayed(mtoastrunnable2,500);
            }
        }
    };

    private Runnable getMtoastrunnable1 = new Runnable() {
        @Override
        public void run() {
            if(nm == senderr){
                DocumentReference documentReference = firebaseFirestore.collection("Users").document(mrecieveruid)
                        .collection("pesanbaru").document(nm);
                documentReference.get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                sa=documentSnapshot.getString("s");
                            }

                        });
                mHandler.postDelayed(getMtoastrunnable3,500);
            }else{
                mHandler.postDelayed(mtoastrunnable1,500);
            }
        }
    };

    @Override
    public void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
        if (messagesAdapter!= null){
            messagesAdapter.notifyDataSetChanged();

        }

    }

    @Override
    public void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }

        });
        /*
        DocumentReference doc = firebaseFirestore.collection("Users").document(firebaseAuth.getUid())
                .collection("pesanbaru").document("pesanbarutotal");
        doc.update("pesanbaru " + mrecievername,0);

        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                firebaseDatabase.getReference().child("chats").child(senderroom).addChildEventListener(new ChildEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {


                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
                            NotificationManager manager = getSystemService(NotificationManager.class);
                            manager.createNotificationChannel(channel);
                        }
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(specificchat.this,"n")
                                .setContentTitle("Code not")
                                .setSmallIcon(R.drawable.ic_baseline_add_24)
                                .setAutoCancel(true)
                                .setContentText("new message from " + senderr);

                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(specificchat.this);
                        managerCompat.notify(999,builder.build());



                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }


                });


            }
        });
        */
        messagesAdapter.notifyDataSetChanged();
        mmessagerecyclerview.setVisibility(View.GONE);
        mmessagerecyclerview.setVisibility(View.VISIBLE);

    }


}