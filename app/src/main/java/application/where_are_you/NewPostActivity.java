package application.where_are_you;

import android.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class NewPostActivity extends AppCompatActivity {

    private Toolbar newPostToolbar;
    private EditText newPostTitle;
    private EditText newPostDescription;
    private ImageView newPostImage;
    private Button newPostBtn;
    private Uri newPostImageUri;
    private ProgressBar progressBar;
    private boolean isChanged;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseAuth firebaseAuth;
    private boolean is_posting = false;
    private boolean force_cancel = false;
    private UploadTask uploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);

        //  Firebase ->
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        newPostToolbar = findViewById(R.id.new_post_toolbar);
        newPostTitle = findViewById(R.id.new_post_title);
        newPostDescription = findViewById(R.id.new_post_decription);
        newPostImage = findViewById(R.id.new_post_image);
        newPostBtn = findViewById(R.id.add_post_btn);
        progressBar = findViewById(R.id.new_post_loader);

        setSupportActionBar(newPostToolbar);
        ActionBar mActionBar =  getSupportActionBar();
        mActionBar.setTitle("Add New Post");
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setDisplayShowHomeEnabled(true);

        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                    if(ContextCompat.checkSelfPermission(NewPostActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(NewPostActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

                    } else {

                        BringImagePicker();

                    }

                } else {

                    BringImagePicker();

                }
            }
        });

        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newPostTitleText = newPostTitle.getText().toString();
                String newPostDescriptionText = newPostDescription.getText().toString();
            if (!TextUtils.isEmpty(newPostTitleText) && !TextUtils.isEmpty(newPostDescriptionText) && newPostImageUri!=null){
                progressBar.setVisibility(View.VISIBLE);
                postFirebaseStorage(newPostTitleText, newPostDescriptionText, newPostImageUri);
                }else {
                Toast.makeText(NewPostActivity.this , "fill all fields", Toast.LENGTH_LONG).show();
            }
            }
        });
    }

    private void postFirebaseStorage(final String title, final String description, Uri post_image_uri) {
        String image_name;
        if (post_image_uri.toString().length() == 8) {
            image_name = title + post_image_uri.toString();
        } else if (post_image_uri.toString().length() > 8) {
            image_name = title + post_image_uri.toString().substring(post_image_uri.toString().length() - 8);
        } else {
            image_name = title + post_image_uri.toString();
        }
        is_posting = true;
        uploadTask = storageReference.child("post_covers").child(image_name).putFile(post_image_uri);
        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if (task.isSuccessful()){
                    Uri download_uri = task.getResult().getDownloadUrl();
                    postFirebaseFirestore(title, description, download_uri);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    is_posting = false;
                    String error = task.getException().getMessage();
                    Toast.makeText(NewPostActivity.this , error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postFirebaseFirestore(String title, String description, Uri uri) {
        Map<String, String> post = new HashMap<>();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        post.put("title", title);
        post.put("description", description);
        post.put("image_uri", uri.toString());
        post.put("user_id", user_id);
        firebaseFirestore.collection("Posts").document(title).set(post).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.INVISIBLE);
                    goDashboard();
                    Toast.makeText(NewPostActivity.this , "New post added", Toast.LENGTH_SHORT).show();
                }else {
                    String error = task.getException().getMessage();
                    Toast.makeText(NewPostActivity.this , error, Toast.LENGTH_SHORT).show();
                }
                is_posting = false;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        if (is_posting){
            onBackPressed();
        } else {
            super.onBackPressed();
        }
        return true;
    }

    private void BringImagePicker() {

        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setAspectRatio(1, 1)
                .start(NewPostActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                newPostImageUri = result.getUri();
                newPostImage.setImageURI(newPostImageUri);

                isChanged = true;

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (is_posting) {
            new AlertDialog.Builder(NewPostActivity.this)
                    .setTitle("Are you sure?")
                    .setMessage("Post won't be published")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            uploadTask.cancel();
                            NewPostActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(android.R.string.no, null).show();
        }else {
            super.onBackPressed();
        }
    }

    private void goDashboard() {
        Intent mainIntent = new Intent(NewPostActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
