package application.where_are_you;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class LoginActivity extends AppCompatActivity {

    private EditText login_email;
    private EditText login_password;
    private Button login_submit;
    private Button login_need_account;
    private ProgressBar login_loader;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // creating Firebase Authentication instance
        mAuth = FirebaseAuth.getInstance();

        // assigning view elements to variables
        login_email = findViewById(R.id.register_email);
        login_password = findViewById(R.id.register_pass);
        login_submit = findViewById(R.id.login_submit);
        login_need_account = findViewById(R.id.login_need_account);
        login_loader = findViewById(R.id.login_loader);

        login_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String login_email_text = login_email.getText().toString();
                String login_password_text = login_password.getText().toString();
                if (!TextUtils.isEmpty(login_email_text) && !TextUtils.isEmpty(login_email_text)) {
                    // inputs are not empty
                    login_loader.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(login_email_text, login_password_text)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // logged in
                                        login_loader.setVisibility(View.INVISIBLE);
                                        goDashboard();
                                    } else {
                                        // not logged in
                                        login_loader.setVisibility(View.INVISIBLE);
                                        String error = task.getException().getMessage();
                                        Toast.makeText(LoginActivity.this, error, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
        login_need_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goRegister();
            }
        });
    }

    private void goDashboard() {
        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void goRegister() {
        Intent mainIntent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            goDashboard();
        }
    }
}
