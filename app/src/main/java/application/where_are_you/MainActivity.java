package application.where_are_you;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import application.where_are_you.Model.Post;
import java.lang.reflect.Array;




public class MainActivity extends AppCompatActivity {
    private Toolbar app_toolbar;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private FloatingActionButton addPostBtn;
    private String user_id;
    private RecyclerView recyclerView;
    private String TAG="birbir";
    private Post[] posts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        user_id = mAuth.getCurrentUser().getUid();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.posts_recycler_view);
        addPostBtn = findViewById(R.id.add_post_btn);
        Log.d("frara","Name");
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (e!=null) {
                    Log.d(TAG,"Error: "+ e.getMessage());
                }
                for (DocumentSnapshot doc: documentSnapshots) {
                    Post post = new Post(doc.getData());

                }
            }
        });
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
                startActivity(newPostIntent);

            }
        });
        // getting v7 toolbar from view
        app_toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(app_toolbar);
        getSupportActionBar().setTitle("Depot");
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            goLogin();
        }else {
            firebaseFirestore.collection("Users").document(currentUser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                     if (task.isSuccessful()) {
                        if (!task.getResult().exists()){
                            goSetup();
                        } else {

                        }
                     }else {
                         String error = task.getException().getMessage();
                         Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG).show();
                     }
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        switch (item.getItemId()) {
            case R.id.action_logout:
                logOut();
                return true;
            case R.id.action_settings:
                goSetup();
                return true;
            default:
                return false;
        }
    }

    private void logOut() {
        mAuth.signOut();
        goLogin();
    }

    private void goNewPost() {
        Intent newPostIntent = new Intent(MainActivity.this, NewPostActivity.class);
        startActivity(newPostIntent);
        finish();
    }

    private void goLogin() {
        Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void goSetup() {
        Intent setupIntent = new Intent(MainActivity.this, SetupActivity.class);
        startActivity(setupIntent);
        finish();
    }
}
