package com.example.videomeet.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.videomeet.R;
import com.example.videomeet.utilities.Constants;
import com.example.videomeet.utilities.PreferenceManager;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import es.dmoral.toasty.Toasty;

public class loginActivity extends AppCompatActivity {

    TextInputEditText inputEmail, inputPassword;
    Button loginButton;
    TextView loginText, newUser;
    ImageView addImage;
    ProgressBar loginProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferenceManager = new PreferenceManager(getApplicationContext());

        if (preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        loginButton = findViewById(R.id.loginButton);
        loginProgressBar = findViewById(R.id.loginProgressBar);

        loginButton.setOnClickListener(view -> {
            if (inputEmail.getText().toString().trim().isEmpty()){
                Toasty.warning(loginActivity.this, "Enter email", Toast.LENGTH_SHORT).show();
            }
            else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                Toasty.error(loginActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            }
            else if (inputPassword.getText().toString().trim().isEmpty()){
                Toasty.warning(loginActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            }
            else {
                signIn();
            }
        });

        loginText = findViewById(R.id.loginText);
        String text = "Login";
        loginText.setText(text);

        newUser = findViewById(R.id.newUser);
        newUser.setOnClickListener(view -> goToRegisterActivity());

        addImage = findViewById(R.id.addImage);
        addImage.setOnClickListener(view -> goToRegisterActivity());
    }

    public void  signIn() {
        loginButton.setVisibility(View.INVISIBLE);
        loginProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL, inputEmail.getText().toString())
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                    else {
                        loginButton.setVisibility(View.VISIBLE);
                        loginProgressBar.setVisibility(View.INVISIBLE);
                        Toasty.error(loginActivity.this, "Unable to login", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void goToRegisterActivity() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }
}