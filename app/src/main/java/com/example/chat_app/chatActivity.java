package com.example.chat_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;


public class chatActivity extends AppCompatActivity {

    TabLayout tabLayout;
    TabItem mchat,mcall,mstatus;
    ViewPager viewPager;
    PageAdapter pagerAdapter;
    androidx.appcompat.widget.Toolbar mtoolbar;

    int id=0;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tabLayout=findViewById(R.id.include);
        mchat=findViewById(R.id.chat);
        mcall=findViewById(R.id.calls);
        mstatus=findViewById(R.id.status);
        viewPager=findViewById(R.id.fragmentcontainer);


        firebaseFirestore= FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();

        mtoolbar=findViewById(R.id.toolbar);
        setSupportActionBar(mtoolbar);

        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_baseline_more_vert_24);
        mtoolbar.setOverflowIcon(drawable);

        pagerAdapter=new PageAdapter(getSupportFragmentManager(),tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                 viewPager.setCurrentItem(tab.getPosition());
                 if(tab.getPosition() == 0 || tab.getPosition() == 1 || tab.getPosition() == 2 ){
                     pagerAdapter.notifyDataSetChanged();
                 }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.profile:
                Intent intent = new Intent(chatActivity.this,ProfileActivity.class);
                startActivity(intent);
                break;

            case R.id.settings:
                Toast.makeText(this, "settings is clicked", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","offline").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        DocumentReference documentReference=firebaseFirestore.collection("Users").document(firebaseAuth.getUid());
        documentReference.update("status","online").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
            }
        });

    }
}