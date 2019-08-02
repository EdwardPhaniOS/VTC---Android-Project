package com.example.flashcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity
{
    private ImageButton cancelButton;
    private Button signUpButton;
    private EditText userEmail;
    private EditText password;
    private EditText confirmPassword;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        setUp();

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToMainPage();
            }
        });
    }

    public void setUp()
    {
        mAuth = FirebaseAuth.getInstance();

        cancelButton = (ImageButton) findViewById(R.id.cancelButton);
        signUpButton = (Button) findViewById(R.id.signUpButton);
        userEmail = (EditText) findViewById(R.id.userEmail);
        password = (EditText) findViewById(R.id.password);
        confirmPassword = (EditText) findViewById(R.id.confirmPassword);
    }

    private void register() {

        String email = userEmail.getText().toString();
        String passwordString = password.getText().toString();
        String confirmPasswordString = confirmPassword.getText().toString();
        final String TAG = "FlashCardAuth";

        if (email.isEmpty() || passwordString.isEmpty() || confirmPasswordString.isEmpty())
        {
            Toast.makeText(RegisterActivity.this, "Please enter your email and password",
                    Toast.LENGTH_SHORT).show();
        } else {
            if (passwordString.matches(confirmPasswordString)) {
                mAuth.createUserWithEmailAndPassword(email, passwordString)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText(RegisterActivity.this, "Success.",
                                            Toast.LENGTH_SHORT).show();
                                    navigateToMainPage();

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }

                                // ...
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this,
                        "Your password and confirm password is not match",
                        Toast.LENGTH_SHORT).show();
            }

        }

    }

    private void navigateToMainPage() {
        final Intent intentToMainPage = new
                Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intentToMainPage);
    }
}
