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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {

    TextInputEditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword;
    Button registerButton;
    TextView registerText, alreadyHaveAnAccount;
    ImageView goToLogin;
    ProgressBar registerProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        preferenceManager = new PreferenceManager(getApplicationContext());

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPPassword);
        registerButton = findViewById(R.id.registerButton);
        registerProgressBar = findViewById(R.id.registerProgressBar);

        registerButton.setOnClickListener(view -> {
            if (inputFirstName.getText().toString().trim().isEmpty()) {
                Toasty.warning(RegisterActivity.this, "Enter first name", Toast.LENGTH_SHORT).show();
            } else if (inputLastName.getText().toString().trim().isEmpty()) {
                Toasty.warning(RegisterActivity.this, "Enter last name", Toast.LENGTH_SHORT).show();
            } else if (inputEmail.getText().toString().trim().isEmpty()) {
                Toasty.warning(RegisterActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            }else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()){
                Toasty.error(RegisterActivity.this, "Enter valid email", Toast.LENGTH_SHORT).show();
            } else if (inputPassword.getText().toString().trim().isEmpty()) {
                Toasty.warning(RegisterActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else if (inputConfirmPassword.getText().toString().trim().isEmpty()) {
                Toasty.warning(RegisterActivity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
            } else if (!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                Toasty.error(RegisterActivity.this, "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
            } else {
                signUp();
            }
        });

        registerText = findViewById(R.id.registerText);
        String text = "Register";
        registerText.setText(text);

        goToLogin = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(view -> goToLoginActivity());

        alreadyHaveAnAccount = findViewById(R.id.alreadyHaveAnAccount);
        alreadyHaveAnAccount.setOnClickListener(view -> goToLoginActivity());
    }

    public void signUp() {
        registerButton.setVisibility(View.INVISIBLE);
        registerProgressBar.setVisibility(View.VISIBLE);

        FirebaseFirestore database = FirebaseFirestore.getInstance();

        HashMap<String, Object> user = new HashMap<>();
        user.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
        user.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
        user.put(Constants.KEY_EMAIL, inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, inputPassword.getText().toString());

        database.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toasty.success(this, "User Registered", Toast.LENGTH_SHORT).show();
                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                    preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                    preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                    preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> {
                    registerProgressBar.setVisibility(View.INVISIBLE);
                    registerButton.setVisibility(View.VISIBLE);
                    Toasty.error(RegisterActivity.this, "Error:" + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    public void goToLoginActivity() {
        Intent intent = new Intent(this, loginActivity.class);
        startActivity(intent);
    }
}