package com.example.chat_app;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.squareup.picasso.Picasso;

public class NoteAdapter extends FirestoreRecyclerAdapter<firebasemodel,NoteAdapter.NoteHolder> {

    public NoteAdapter(@NonNull FirestoreRecyclerOptions<firebasemodel> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteHolder noteHolder, int i, @NonNull firebasemodel firebasemodel) {
        noteHolder.particularusername.setText(firebasemodel.getName());
        noteHolder.statusofuser.setText(firebasemodel.getStatus());
        String uri = firebasemodel.getImage();
        Picasso.get().load(uri).into(noteHolder.mimageviewofuser);

        noteHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),specificchat.class);
                intent.putExtra("name",firebasemodel.getName());
                intent.putExtra("receiveruid",firebasemodel.getUid());
                intent.putExtra("imageuri",firebasemodel.getImage());
                v.getContext().startActivity(intent);
            }
        });

        if (firebasemodel.getStatus().equals("online")){
            noteHolder.statusofuser.setText(firebasemodel.getStatus());
            noteHolder.statusofuser.setTextColor(Color.GREEN);

        }else if (firebasemodel.getStatus().equals("offline")){
            noteHolder.statusofuser.setText(firebasemodel.getStatus());
        }
    }

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatviewlayout,
                parent,false);
        return new NoteHolder(v);
    }

    class NoteHolder extends RecyclerView.ViewHolder{

         TextView particularusername;
         TextView statusofuser;
         ImageView mimageviewofuser;

        public NoteHolder(@NonNull View itemView) {
            super(itemView);
            particularusername=itemView.findViewById(R.id.nameofuser);
            mimageviewofuser=itemView.findViewById(R.id.imageviewofuser);
            statusofuser=itemView.findViewById(R.id.statusofuser);
        }
    }
}
