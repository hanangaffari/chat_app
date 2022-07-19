package com.example.chat_app.status;

import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chat_app.NoteAdapter;
import com.example.chat_app.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

public class statusadapter extends FirestoreRecyclerAdapter<firebasemodel,statusadapter.statusHolder> {

    public statusadapter(@NonNull FirestoreRecyclerOptions<firebasemodel> options) { super(options); }

    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private String o;
    private String a;
    private String b;
    private ImageButton sss;

    @Override
    protected void onBindViewHolder(@NonNull statusHolder statusHolder, int i, @NonNull firebasemodel firebasemodel) {

        firebaseFirestore.collection("Users").document(firebasemodel.getUid()).collection("statuspost")
                .document("status").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                b = documentSnapshot.getString("statustext");
            }
        });


        


        statusHolder.statususername.setText(firebasemodel.getName());
        statusHolder.statusofuser.setText(firebasemodel.getStatus());
        String uri  = firebasemodel.getstatuspost();
        Picasso.get().load(uri).into(statusHolder.mimageviewofstatus);

        statusHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(v.getContext(),statusviewActivity.class);
                intent.putExtra("name",firebasemodel.getName());
                intent.putExtra("statusuid",firebasemodel.getUid());
                intent.putExtra("statususerimage",firebasemodel.getImage());
                intent.putExtra("statuspost",firebasemodel.getstatuspost());
                intent.putExtra("statustexti",b);

                v.getContext().startActivity(intent);
            }
        });

        if(firebasemodel.getStatus().equals("online")){
            statusHolder.statusofuser.setText(firebasemodel.getStatus());
            statusHolder.statusofuser.setTextColor(Color.GREEN);
        }else if (firebasemodel.getStatus().equals("offline")){
            statusHolder.statusofuser.setText(firebasemodel.getStatus());
            statusHolder.statusofuser.setTextColor(Color.GRAY);
        }
    }

    @NonNull
    @Override
    public statusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.statusviewlayout,
                parent,false);
        return new statusHolder(v);
    }

    class statusHolder extends RecyclerView.ViewHolder{
        TextView statususername;
        TextView statusofuser;
        ImageView mimageviewofstatus;

        public statusHolder(@NonNull View itemView) {
            super(itemView);

            statususername=itemView.findViewById(R.id.nameofuserstatus);
            statusofuser=itemView.findViewById(R.id.datestatusofuser);
            mimageviewofstatus=itemView.findViewById(R.id.imageviewofstatususer);
        }
    }
}

