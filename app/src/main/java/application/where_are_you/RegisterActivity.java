package application.where_are_you;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private EditText register_email;
    private EditText register_password;
    private EditText register_password_conf;
    private Button register_submit;
    private Button register_have_account;
    private ProgressBar register_loader;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();

        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_pass);
        register_password_conf = findViewById(R.id.register_pass_conf);
        register_submit = findViewById(R.id.register_submit);
        register_have_account = findViewById(R.id.register_have_account);
        register_loader = findViewById(R.id.register_loader);

        register_have_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goLogin();
            }
        });

        register_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String register_email_text = register_email.getText().toString();
                String register_password_text = register_password.getText().toString();
                String register_password_conf_text = register_password_conf.getText().toString();

                if (!TextUtils.isEmpty(register_password_conf_text) &&
                    !TextUtils.isEmpty(register_email_text) &&
                    !TextUtils.isEmpty(register_password_text)){
                    if (Objects.equals(register_password_conf_text, register_password_text)){
                        register_loader.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(register_email_text, register_password_text)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            register_loader.setVisibility(View.INVISIBLE);
                                            goSetup();
                                        } else {
                                            register_loader.setVisibility(View.INVISIBLE);
                                            Exception exception =  task.getException();
                                            String error = "Error: ";
                                            if (exception != null){
                                                error = exception.getMessage();
                                            }
                                            Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        Toast.makeText(RegisterActivity.this, "passwords doesn't match", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(RegisterActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                }
            }


        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            goDashboard();
        }
    }

    private void goLogin() {
        Intent mainIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private void goDashboard() {
        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    private void goSetup() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        startActivity(setupIntent);
        finish();
    }
}
