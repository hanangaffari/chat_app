    package com.example.chat_app;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static androidx.core.content.ContextCompat.getSystemService;

    public class chatfragment extends Fragment {


    private FirebaseFirestore firebaseFirestore;
    LinearLayoutManager linearLayoutManager;
    private FirebaseAuth firebaseAuth;
    firebasemodel firebasemodel;

    private String imageURIchat;
    int a = 0;
    int b;

    private Handler mhandler = new Handler();

    String pp;
    String jp;


    Button tes;
    ImageView mimageviewofuser;
    ImageButton mbruh;
    RecyclerView mrecyclerView;

    private DocumentReference noti;

    private NoteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chatfragment,container,false);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        //Query query=firebaseFirestore.collection("Users");

        Query query= firebaseFirestore.collection("Users").whereNotEqualTo("uid",firebaseAuth.getUid());
        FirestoreRecyclerOptions<firebasemodel> allusername = new FirestoreRecyclerOptions.Builder<firebasemodel>()
                .setQuery(query,firebasemodel.class)
                .build();



        adapter = new NoteAdapter(allusername);
        mrecyclerView= v.findViewById(R.id.recyclerview);
        mrecyclerView.setHasFixedSize(true);
        linearLayoutManager=new LinearLayoutManager(getContext());
        mrecyclerView.setLayoutManager(linearLayoutManager);
        mrecyclerView.setAdapter(adapter);
        tes=v.findViewById(R.id.test);


        tes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });



        return v;
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
       adapter.startListening();

       CollectionReference dex = firebaseFirestore.collection("Users").document(firebaseAuth.getUid())
               .collection("pesanbaru");

       dex.whereEqualTo("s","latest").addSnapshotListener(new EventListener<QuerySnapshot>() {
           @Override
           public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
          if(error != null){
              return; }
          for (DocumentChange dc : value.getDocumentChanges()){
                          DocumentSnapshot  documentSnapshot = dc.getDocument();
                          pp = documentSnapshot.getString("name");
                          jp = documentSnapshot.getString("s");
           }
          a++;

          if(a == 2){
              mhandler.postDelayed(mtoast,1000);
              a=0;
          }

       }

    });

    }

    private Runnable mtoast = new Runnable() {
        @Override
        public void run() {
            //Toast.makeText(getActivity(), jp+pp, Toast.LENGTH_SHORT).show();

            createNotificationChannels();

        }
    };

    private void createNotificationChannels(){
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                NotificationChannel channel1 = new NotificationChannel(
                        "channel1",
                        "channel1",
                        NotificationManager.IMPORTANCE_HIGH
                );
                channel1.setDescription("this is chnnal");

                NotificationChannel channel2 = new NotificationChannel(
                        "channel1",
                        "channel1",
                        NotificationManager.IMPORTANCE_LOW
                );
                channel2.setDescription("this is chnnal 2");

                NotificationManager manager = getActivity().getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel1);
                manager.createNotificationChannel(channel2);
            }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity(),"n")
                .setContentTitle("Code not")
                .setSmallIcon(R.drawable.ic_baseline_add_24)
                .setAutoCancel(true)
                .setContentText("new message from " + pp);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(getActivity());
        managerCompat.notify(999,builder.build());
        }

        @Override
        public void onStop() {
            super.onStop();
            DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
            documentReference.update("status","offline").addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                }
            });
            if (adapter!= null){
                adapter.stopListening();
            }
        }

    }


