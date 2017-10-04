package com.example.kishorebaktha.blog;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
public class View extends AppCompatActivity {
        private DatabaseReference mDataBase,mDataBase2;
        TextView t1,t2;
        private ListView mUserList;
        private ArrayList<String> mUserName=new ArrayList<>();
        private ArrayList<String > mKeya=new ArrayList<>();
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.retrieve);
            Intent intent=getIntent();
            final String post_key=intent.getStringExtra("key");
            mDataBase= FirebaseDatabase.getInstance().getReference().child("Likes").getRef();
            mUserList=(ListView)findViewById(R.id.list);
            final ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,R.layout.support_simple_spinner_dropdown_item,mUserName);
            mUserList.setAdapter(arrayAdapter);
            mDataBase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value=dataSnapshot.child(post_key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(String.class);
                    mUserName.add(value);
                    //String key=dataSnapshot.getKey();
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            mDataBase.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    String value=dataSnapshot.child(post_key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).getValue(String.class);
                    mUserName.add(value);
                    //String key=dataSnapshot.getKey();
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    String value=dataSnapshot.getValue(String.class);
                    //to get position where value changed-
                    String key=dataSnapshot.getKey();
                    int index=mKeya.indexOf(key);
                    mUserName.set(index,value);
                    arrayAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }
