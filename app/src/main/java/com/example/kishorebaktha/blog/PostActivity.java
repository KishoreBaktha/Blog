package com.example.kishorebaktha.blog;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Random;

/**
 * Created by KISHORE BAKTHA on 7/30/2017.
 */

public class PostActivity extends AppCompatActivity {
    private ImageButton img;
    private static int GALLERY_INTENT=101;
    EditText mtitle,mdesc;
    Button submit;
    Uri uri=null;
    private ProgressDialog mProgress;
    private StorageReference mStorage;
    private DatabaseReference mDatabase,mDatabase2;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        img=(ImageButton)findViewById(R.id.imageButton);
        mtitle=(EditText)findViewById(R.id.editText);
        mStorage= FirebaseStorage.getInstance().getReference();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Blog");
        mDatabase2= FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseUser=FirebaseAuth.getInstance().getCurrentUser();
        mdesc=(EditText)findViewById(R.id.editText2);
        submit=(Button)findViewById(R.id.button2);
        mProgress=new ProgressDialog(this);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_INTENT);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPosting();
            }
        });

    }
    private void startPosting() {
        mProgress.setMessage("Posting to blog");
        mProgress.show();
        final String title_val=mtitle.getText().toString();
        final String desc_val=mdesc.getText().toString();
        if(!TextUtils.isEmpty(title_val)&&!TextUtils.isEmpty(desc_val)&&uri!=null)
        {
            StorageReference file_path=mStorage.child("image_blog").child(uri.getLastPathSegment());
            // StorageReference file_path=mStorage.child("image_blog").child(random());
            file_path.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                     Uri downloadUrl=taskSnapshot.getDownloadUrl();
                    DatabaseReference newPost=mDatabase.push();
                    String likevalue=String.valueOf(0);
                    newPost.child("title").setValue(title_val);
                    newPost.child("desc").setValue(desc_val);
                    newPost.child("like").setValue(likevalue);
                    newPost.child("image").setValue(downloadUrl.toString());
                    newPost.child("email").setValue(FirebaseAuth.getInstance().getCurrentUser().getEmail());
                    mtitle.setText("");
                    mdesc.setText("");
                    Toast.makeText(getApplicationContext(),"UPLOAD DONE",Toast.LENGTH_SHORT).show();
                    mProgress.dismiss();
                }
            });
        }
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(10);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(requestCode==GALLERY_INTENT&&resultCode==RESULT_OK)
        {
            uri=data.getData();
            img.setImageURI(uri);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
