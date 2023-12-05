package com.example.kursach.activity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kursach.R;
import com.example.kursach.model.HashUtils;
import com.example.kursach.model.HelperClass;
import com.example.kursach.viewmodels.SignupViewModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    private TextView textError1, textError2, textError3, textError4;
    private SignupViewModel signupViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupViewModel = new ViewModelProvider(this).get(SignupViewModel.class);

        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);
        textError1 = findViewById(R.id.textError1);
        textError2 = findViewById(R.id.textError2);
        textError3 = findViewById(R.id.textError3);
        textError4 = findViewById(R.id.textError4);

        // Определение наблюдателей для обновления UI в соответствии с результатами валидации
        signupViewModel.getIsNameValid().observe(this, isNameValid -> {
            textError1.setVisibility(isNameValid ? View.GONE : View.VISIBLE);
        });

        signupViewModel.getIsEmailValid().observe(this, isEmailValid -> {
            textError2.setVisibility(isEmailValid ? View.GONE : View.VISIBLE);
        });

        signupViewModel.getIsUsernameValid().observe(this, isUsernameValid -> {
            textError3.setVisibility(isUsernameValid ? View.GONE : View.VISIBLE);
        });

        signupViewModel.getIsPasswordValid().observe(this, isPasswordValid -> {
            textError4.setVisibility(isPasswordValid ? View.GONE : View.VISIBLE);
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    database = FirebaseDatabase.getInstance();
                    reference = database.getReference("users");

                    String name = signupName.getText().toString();
                    String email = signupEmail.getText().toString();
                    String username = signupUsername.getText().toString();
                    String password = signupPassword.getText().toString();

                    String hashedPassword = HashUtils.hashPassword(password);
                    Log.d("HashedPassword", hashedPassword);

                    HelperClass helperClass = new HelperClass(name, email, username, hashedPassword);
                    reference.child(username).setValue(helperClass);

                    Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean validateFields() {
        String name = signupName.getText().toString().trim();
        String email = signupEmail.getText().toString().trim();
        String username = signupUsername.getText().toString().trim();
        String password = signupPassword.getText().toString().trim();

        signupViewModel.validateName(name);
        signupViewModel.validateEmail(email);
        signupViewModel.validateUsername(username);
        signupViewModel.validatePassword(password);

        return signupViewModel.getIsNameValid().getValue() &&
                signupViewModel.getIsEmailValid().getValue() &&
                signupViewModel.getIsUsernameValid().getValue() &&
                signupViewModel.getIsPasswordValid().getValue();
    }
}

