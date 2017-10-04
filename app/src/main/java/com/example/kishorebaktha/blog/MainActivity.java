package com.example.kishorebaktha.blog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {
    public RecyclerView blogList;
    DatabaseReference databaseReference,databaseReference2,mDatabase2;
    private static int SIGN_IN_CODE=1;
    FirebaseAuth mAuth;
    static int count=0;
    static boolean flag=false;
    FirebaseAuth.AuthStateListener mAuthListener;
    RelativeLayout activity_main;
    private static boolean mProcessLike=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        activity_main=(RelativeLayout)findViewById(R.id.Activity_main);
        mDatabase2= FirebaseDatabase.getInstance().getReference().child("Users");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Blog");
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Likes");
        blogList = (RecyclerView) findViewById(R.id.blog_list);
        blogList.setHasFixedSize(true);
        blogList.setLayoutManager(new LinearLayoutManager(this));
        check();
    }
    public void check()
    {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_CODE);
        }
        else
        {
            Snackbar.make(activity_main,"Welcome"+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            //load content
            display();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==SIGN_IN_CODE&&resultCode==RESULT_OK)
        {
            Snackbar.make(activity_main,"Successfully signed in",Snackbar.LENGTH_SHORT).show();
            display();
        }
        else
        {
            Snackbar.make(activity_main,"Couldn't sign in...try again later",Snackbar.LENGTH_SHORT).show();
            finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected void display()
    {
        FirebaseRecyclerAdapter<Blog, BlogViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Blog, BlogViewHolder>(
                Blog.class, R.layout.blog_list, BlogViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(final BlogViewHolder viewHolder, Blog model, int position) {
                final String post_key=getRef(position).getKey();
                viewHolder.setEmail(model.getEmail());
                viewHolder.setTitle(model.getTitle());
                viewHolder.setDesc(model.getDesc());
               //final int position2=position;
               // Toast.makeText(getApplicationContext(),String.valueOf(position2),Toast.LENGTH_SHORT).show();
                viewHolder.setImage(getApplicationContext(),model.getImage());
                viewHolder.setTime(model.getMessageTime());
                viewHolder.setLike(model.getLike());
                viewHolder.mDeleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                        remove(viewHolder.setPosition());
                    }
                });
                viewHolder.viewlist.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                       Intent intent=new Intent(getApplicationContext(), com.example.kishorebaktha.blog.View.class);
                        intent.putExtra("key",post_key);
                        startActivity(intent);
                    }
                });
                viewHolder.mLikeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        flag = true;
                            databaseReference2.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (flag) {
                                        if (dataSnapshot.child(post_key).hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                            mDatabase2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                            databaseReference2.child(post_key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).removeValue();
                                            FirebaseDatabase.getInstance().getReference().child("Blog").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                        if (viewHolder.setPosition() == count) {
                                                            String s = snapshot.child("like").getValue().toString();
                                                            int num = Integer.parseInt(s);
                                                            num--;
                                                            String s2 = String.valueOf(num);
                                                            snapshot.getRef().child("like").setValue(s2);
                                                            // Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                                            break;
                                                        }
                                                        count++;
                                                    }
                                                    flag = true;
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                }
                                            });
                                            count = 0;
                                            flag = false;
                                        } else
                                            {
                                                mDatabase2.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        mDatabase2.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            databaseReference2.child(post_key).child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue("Random");
                                            FirebaseDatabase.getInstance().getReference().child("Blog").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        if (viewHolder.setPosition() == count) {
                                            String s = snapshot.child("like").getValue().toString();
                                            int num = Integer.parseInt(s);
                                            num++;
                                            String s2 = String.valueOf(num);
                                            snapshot.getRef().child("like").setValue(s2);
                                            // Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                                            break;
                                        }
                                        count++;
                                    }
                                    flag = true;
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                }
                            });
                                 count = 0;
                                            flag = false;
                                        }
                                    }
                                }
                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                });
            }
        };
        blogList.setAdapter(firebaseRecyclerAdapter);
    }
    public void remove(final int position2)
    {
        FirebaseDatabase.getInstance().getReference().child("Blog").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if(position2==count)
                    {
                        snapshot.getRef().removeValue();
                        // Toast.makeText(getApplicationContext(),"Deleted",Toast.LENGTH_SHORT).show();
                        break;
                    }
                    count++;
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
        count=0;
    }

    public  static class BlogViewHolder extends RecyclerView.ViewHolder
    {
         View item;
        ImageButton mLikeButton,mDeleteButton;
        Button viewlist;
        public BlogViewHolder(View itemView) {
            super(itemView);
            item=itemView;
            // final int position=getAdapterPosition();
            mLikeButton=(ImageButton)item.findViewById(R.id.like);
            mDeleteButton=(ImageButton)item.findViewById(R.id.delete);
            viewlist=(Button)item.findViewById(R.id.viewlist);
        }

        public void setEmail(String email)
        {
            TextView email_txt=(TextView)item.findViewById(R.id.email);
            email_txt.setText(email);
        }
        public void setTitle(String title)
        {
            TextView title_txt=(TextView)item.findViewById(R.id.title);
            title_txt.setText(title);
        }
        public void setDesc(String desc)
        {
            TextView title_desc=(TextView)item.findViewById(R.id.desc);
            title_desc.setText(desc);
        }
        public void setImage(Context context,String image)
        {
            ImageView imageView=(ImageView)item.findViewById(R.id.image);
            Picasso.with(context).load(image).into(imageView);
        }
        public void setTime(long time)
        {
            TextView time_txt=(TextView)item.findViewById(R.id.time);
            time_txt.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)",time));
        }
        public void setLike(String title)
        {
            TextView like=(TextView)item.findViewById(R.id.liketxt);
            like.setText(title);
        }
        public int setPosition()
        {
            return getLayoutPosition();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(getApplicationContext(), PostActivity.class));
        }
        if(item.getItemId()==R.id.signout)
        {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"Successfully signed out",Snackbar.LENGTH_SHORT).show();
                    check();
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
